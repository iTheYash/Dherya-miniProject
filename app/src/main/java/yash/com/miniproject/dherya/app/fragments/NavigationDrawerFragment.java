package yash.com.miniproject.dherya.app.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.activities.AddCreditCardActivity;
import yash.com.miniproject.dherya.app.adapters.NavigationDrawerAdapter;
import yash.com.miniproject.dherya.app.dialogs.SelectCreditCardDialogFragment;
import yash.com.miniproject.dherya.app.holders.SelectableCreditCardViewHolder;
import yash.com.miniproject.dherya.app.utils.Constants;
import yash.com.miniproject.dherya.app.utils.SharedPreferencesUtils;
import yash.com.miniproject.dherya.database.ExpenseManagerDAO;
import yash.com.miniproject.dherya.exceptions.CreditCardNotFoundException;
import yash.com.miniproject.dherya.exceptions.SharedPreferenceNotFoundException;
import yash.com.miniproject.dherya.model.CreditCard;
import yash.com.miniproject.dherya.model.NavigationDrawerItem;

public class NavigationDrawerFragment extends Fragment {


    int mActiveCreditCardID = -1;
    private CreditCard mActiveCreditCard = null;
    private ExpenseManagerDAO mDao;


    SelectableCreditCardViewHolder holder;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mHeaderContainer;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_navigation_drawer, container);

        refreshData();
        setUpRecyclerView(rootView);
        refreshDrawerHeader(rootView);

        return rootView;
    }

    private void refreshData() {

        if(mDao == null)
            mDao = new ExpenseManagerDAO(getActivity().getApplicationContext());

        try {
            mActiveCreditCardID = SharedPreferencesUtils.getInt(getContext(), Constants.ACTIVE_CC_ID);
            mActiveCreditCard = mDao.getCreditCard(mActiveCreditCardID);
        }catch (SharedPreferenceNotFoundException | CreditCardNotFoundException e) {}
    }


    private void refreshDrawerHeader(View rootView) {

        mHeaderContainer = (RelativeLayout) rootView.findViewById(R.id.nav_drawer_header_container);
        View headerCreditCardContainer = rootView.findViewById(R.id.list_item_credit_card_container);
        View errNoCC = rootView.findViewById(R.id.nav_drawer_err_no_cc);
        View errSelectACC = rootView.findViewById(R.id.nav_drawer_err_select_a_cc);


        headerCreditCardContainer.setVisibility(View.GONE);
        errNoCC.setVisibility(View.GONE);
        errSelectACC.setVisibility(View.GONE);


        if(mActiveCreditCard != null) {

            headerCreditCardContainer.setVisibility(View.VISIBLE);
            holder = new SelectableCreditCardViewHolder(headerCreditCardContainer);
            holder.setData(getContext(), mActiveCreditCard, 0);
        } else if (mDao.getCreditCardList().size() > 0) {
            errSelectACC.setVisibility(View.VISIBLE);

        } else {
            errNoCC.setVisibility(View.VISIBLE);

            mHeaderContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent addCCIntent = new Intent(getActivity(), AddCreditCardActivity.class);

                    startActivity(addCCIntent);
                }
            });


        }

        if(mDao.getCreditCardList().size() > 0) {
            mHeaderContainer = (RelativeLayout) rootView.findViewById(R.id.nav_drawer_header_container);
            mHeaderContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SelectCreditCardDialogFragment dialog = SelectCreditCardDialogFragment.newInstance(mDao.getCreditCardList());
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            refreshData();
                            holder.setData(getContext(), mActiveCreditCard, 0);
                            closeDrawer();
                        }
                    });
                    dialog.show(getFragmentManager(), "fragment_dialog_select_credit_card");
                }
            });

        }

    }

    private void setUpRecyclerView(View view) {

        List<NavigationDrawerItem> data = new ArrayList<>();
        data.add(new NavigationDrawerItem(getResources().getString(R.string.fragment_name_overview), R.drawable.icon_overview));
        data.add(new NavigationDrawerItem("Notes", R.drawable.icon_about));
        data.add(new NavigationDrawerItem("Recipe", R.drawable.icon_about));
        data.add(new NavigationDrawerItem("Grocery", R.drawable.icon_about));
        data.add(new NavigationDrawerItem(getResources().getString(R.string.fragment_name_expense_list), R.drawable.icon_expenses));
        data.add(new NavigationDrawerItem(getResources().getString(R.string.fragment_name_graphs), R.drawable.icon_chart));
        data.add(new NavigationDrawerItem(getResources().getString(R.string.fragment_name_credit_cards), R.drawable.icon_credit_card));
        data.add(new NavigationDrawerItem(getResources().getString(R.string.fragment_name_preferences), R.drawable.icon_settings));
        data.add(new NavigationDrawerItem(getResources().getString(R.string.fragment_name_about), R.drawable.icon_about));


        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.nad_drawer_recyclerview_list);

        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(this, data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;
    }

    public void closeDrawer() {
        if(mDrawerLayout != null)
            mDrawerLayout.closeDrawer(GravityCompat.START);
    }


}
