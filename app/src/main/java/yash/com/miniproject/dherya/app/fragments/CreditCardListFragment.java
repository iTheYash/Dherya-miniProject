package yash.com.miniproject.dherya.app.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.activities.AddCreditCardActivity;
import yash.com.miniproject.dherya.app.adapters.CreditCardAdapter;
import yash.com.miniproject.dherya.app.dialogs.EditOrDeleteCreditCardDialogFragment;
import yash.com.miniproject.dherya.app.holders.CreditCardViewHolder;
import yash.com.miniproject.dherya.database.ExpenseManagerDAO;
import yash.com.miniproject.dherya.exceptions.CreditCardNotFoundException;
import yash.com.miniproject.dherya.exceptions.CreditPeriodNotFoundException;
import yash.com.miniproject.dherya.model.CreditCard;

public class CreditCardListFragment extends Fragment {


    List<CreditCard> creditCards = new ArrayList<>();
    ExpenseManagerDAO dao;


    RecyclerView recycler;
    LinearLayoutManager layoutManager;
    CreditCardAdapter adapter;
    FloatingActionButton fabNewCreditCard;
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.fragment_name_credit_cards));
        Spinner mSpinner = getActivity().findViewById(R.id.spinner_tagss);
        mSpinner.setVisibility(View.GONE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadDao();
        creditCards.addAll(dao.getCreditCardList());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_credit_card_list, container, false);

        setUpRecyclerView(rootView);
        setUpSwipeRefresh(rootView);
        setUpFab(rootView);

        return rootView;
    }


    private void loadDao() {
        if(dao == null)
            dao = new ExpenseManagerDAO(getActivity().getApplicationContext());
    }


    private void setUpRecyclerView(View rootView) {

        recycler = (RecyclerView) rootView.findViewById(R.id.ccl_recycler);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recycler.addItemDecoration(new DividerItemDecoration(recycler.getContext(), layoutManager.getOrientation()));
        recycler.setLayoutManager(layoutManager);

        adapter = new CreditCardAdapter(getActivity(), this, creditCards);
        adapter.setCreditCardSelectedListener(new CreditCardViewHolder.CreditCardSelectedListener() {
            @Override
            public void OnCreditCardSelected(CreditCard creditCard) {
                showEditOrDeleteCCDialog(creditCard);
            }
        });
        recycler.setAdapter(adapter);

    }

    private void setUpSwipeRefresh(View rootView) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.ccl_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.swipe_refresh_green, R.color.swipe_refresh_red, R.color.swipe_refresh_yellow);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                    @Override
                                                    public void onRefresh() {
                                                        refreshRecyclerView();
                                                        swipeRefreshLayout.setRefreshing(false);
                                                    }
                                                }
        );
    }


    private void setUpFab(View rootView) {
        fabNewCreditCard = (FloatingActionButton) rootView.findViewById(R.id.ccl_fab_add_cc);

        fabNewCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addCCIntent = new Intent(getActivity(), AddCreditCardActivity.class);
                startActivity(addCCIntent);
            }
        });
    }


    public void refreshData() throws CreditCardNotFoundException, CreditPeriodNotFoundException {

        creditCards.clear();
        creditCards.addAll(dao.getCreditCardList());
    }


    public void refreshRecyclerView() {
        loadDao();

        int oldCount = creditCards.size();
        try {
            refreshData();
        }catch (CreditCardNotFoundException e ) {
            Toast.makeText(getActivity(), getResources().getString(R.string.err_problem_loading_card_or_no_card_exists), Toast.LENGTH_SHORT).show();
            return;
        }catch (CreditPeriodNotFoundException e) {
            Toast.makeText(getActivity(), getResources().getString(R.string.err_problem_loading_credit_period), Toast.LENGTH_SHORT).show();
            return;
        }

        int newCount = creditCards.size();


        if(newCount == oldCount+1) {
            adapter.notifyItemInserted(0);
            adapter.notifyItemRangeChanged(1, creditCards.size()-1);
            layoutManager.scrollToPosition(0);
        } else {
            adapter.notifyDataSetChanged();
        }

    }


    private void showEditOrDeleteCCDialog(CreditCard selectedCreditCard) {
        FragmentManager fm = getFragmentManager();
        EditOrDeleteCreditCardDialogFragment dialog = EditOrDeleteCreditCardDialogFragment.newInstance(
                dao,
                selectedCreditCard);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                refreshRecyclerView();
            }
        });
        dialog.show(fm, "fragment_dialog_edit_delete_cc");
    }


}
