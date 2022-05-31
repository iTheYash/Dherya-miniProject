package yash.com.miniproject.dherya.app.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.activities.OcrCreateExpenseActivity;
import yash.com.miniproject.dherya.app.adapters.ExpensesAdapter;
import yash.com.miniproject.dherya.app.dialogs.CreateOrEditExpenseDialogFragment;
import yash.com.miniproject.dherya.app.holders.ExpensesViewHolder;
import yash.com.miniproject.dherya.app.utils.Constants;
import yash.com.miniproject.dherya.app.utils.SharedPreferencesUtils;
import yash.com.miniproject.dherya.database.ExpenseManagerDAO;
import yash.com.miniproject.dherya.exceptions.CouldNotDeleteDataException;
import yash.com.miniproject.dherya.exceptions.CreditCardNotFoundException;
import yash.com.miniproject.dherya.exceptions.CreditPeriodNotFoundException;
import yash.com.miniproject.dherya.exceptions.SharedPreferenceNotFoundException;
import yash.com.miniproject.dherya.model.CreditCard;
import yash.com.miniproject.dherya.model.Expense;

public class ExpenseListFragment extends Fragment {


    int activeCreditCardId = -1;
    CreditCard activeCreditCard = null;
    List<Expense> creditCardExpenses = new ArrayList<>();
    ExpenseManagerDAO mDao;


    RecyclerView recyclerViewExpenses;
    LinearLayoutManager mLayoutManager;
    ExpensesAdapter mAdapter;
    FloatingActionMenu fabMenu;
    FloatingActionButton fabNewExpense;
    FloatingActionButton fabNewExpenseCamera;
    SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout noCCContainer;

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.fragment_name_expense_list));
        Spinner mSpinner = getActivity().findViewById(R.id.spinner_tagss);
        mSpinner.setVisibility(View.GONE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadDao();

        try {
            activeCreditCardId = SharedPreferencesUtils.getInt(getContext(), Constants.ACTIVE_CC_ID);
            try {
                refreshData();
            }catch (CreditCardNotFoundException e ) {
                Toast.makeText(getActivity(), getResources().getString(R.string.err_problem_loading_card_or_no_card_exists), Toast.LENGTH_SHORT).show();
            }catch (CreditPeriodNotFoundException e) {
                Toast.makeText(getActivity(), getResources().getString(R.string.err_problem_loading_credit_period), Toast.LENGTH_SHORT).show();
            }
        }catch(SharedPreferenceNotFoundException e) {

        }
    }

    @Override
    public void onResume() {
        refreshRecyclerView();
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expense_list, container, false);

        recyclerViewExpenses = (RecyclerView) rootView.findViewById(R.id.home_recycler_expenses);
        noCCContainer = (RelativeLayout) rootView.findViewById(R.id.home_err_no_cc_container);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.home_swipe_refresh);
        fabMenu = (FloatingActionMenu) rootView.findViewById(R.id.home_fab_menu);
        fabNewExpense = (FloatingActionButton) rootView.findViewById(R.id.home_fab_new_expense);
        fabNewExpenseCamera = (FloatingActionButton) rootView.findViewById(R.id.home_fab_new_expense_camera);

        if(activeCreditCard != null) {
            setUpRecyclerView(rootView);
            setUpSwipeRefresh(rootView);

            setUpFab(rootView);
        }
        else {
            noCCContainer.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
            fabMenu.setVisibility(View.GONE);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.EXPENSE_DETAIL_ACTIVITY_REQUEST_CODE && resultCode == Constants.RESULT_REFRESH_DATA) {
            refreshRecyclerView();
        }

    }


    private void loadDao() {
        if(mDao == null)
            mDao = new ExpenseManagerDAO(getActivity().getApplicationContext());
    }



    private void setUpRecyclerView(View rootView) {


        ExpensesViewHolder.ExpenseDeletedListener listener = new ExpensesViewHolder.ExpenseDeletedListener() {
            @Override
            public void OnExpenseDeleted(int position) {
                try {

                    mDao.deleteExpense(creditCardExpenses.get(position).getId());
                    creditCardExpenses.remove(position);
                    mAdapter.notifyItemRemoved(position);
                    mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
                }catch (CouldNotDeleteDataException e) {
                    Toast.makeText(getActivity(), "There was an error deleting the expense!", Toast.LENGTH_SHORT).show();
               }

            }
        };
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mAdapter = new ExpensesAdapter(this, creditCardExpenses, activeCreditCard.getCreditPeriods().get(0).getId(), listener);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerViewExpenses.getContext(), mLayoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.item_decoration_half_line));


        recyclerViewExpenses.addItemDecoration(itemDecoration);
        recyclerViewExpenses.setLayoutManager(mLayoutManager);

        recyclerViewExpenses.setAdapter(mAdapter);
    }

    private void setUpSwipeRefresh(View rootView) {
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
        fabMenu.setClosedOnTouchOutside(true);


        fabNewExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);
                showCreateExpenseDialog();
            }
        });
        fabNewExpenseCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);
                Intent intent = new Intent(getActivity(), OcrCreateExpenseActivity.class);
                intent.putExtra(OcrCreateExpenseActivity.TAG_EXTRA_PERIOD_ID, activeCreditCard.getCreditPeriods().get(0).getId());
                intent.putExtra(OcrCreateExpenseActivity.TAG_EXTRA_CURRENCY, activeCreditCard.getCurrency());
                startActivity(intent);
            }
        });

    }


    public void refreshData() throws CreditCardNotFoundException, CreditPeriodNotFoundException {
        activeCreditCard = mDao.getCreditCardWithCreditPeriod(activeCreditCardId, 0);

        creditCardExpenses.clear();
        creditCardExpenses.addAll(activeCreditCard.getCreditPeriods().get(0).getExpenses());

    }


    public void refreshRecyclerView() {

        if(activeCreditCard != null) {
            loadDao();

            int oldExpensesCount = creditCardExpenses.size();
            try {
                refreshData();
            }catch (CreditCardNotFoundException e ) {
                Toast.makeText(getActivity(), getResources().getString(R.string.err_problem_loading_card_or_no_card_exists), Toast.LENGTH_SHORT).show();
                return;
            }catch (CreditPeriodNotFoundException e) {
                Toast.makeText(getActivity(), getResources().getString(R.string.err_problem_loading_credit_period), Toast.LENGTH_SHORT).show();
                return;
            }

            int newExpensesCount = creditCardExpenses.size();

            if(newExpensesCount == oldExpensesCount+1) {
                mAdapter.notifyItemInserted(0);
                mAdapter.notifyItemRangeChanged(1, activeCreditCard.getCreditPeriods().get(0).getExpenses().size()-1);
                mLayoutManager.scrollToPosition(0);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }

    }


    private void showCreateExpenseDialog() {
        FragmentManager fm = getFragmentManager();
        CreateOrEditExpenseDialogFragment dialog = CreateOrEditExpenseDialogFragment.newInstance(
                mDao,
                activeCreditCard.getCreditPeriods().get(0).getId(),
                activeCreditCard.getCurrency(),
                null);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                refreshRecyclerView();

            }
        });
        dialog.show(fm, "fragment_dialog_create_expense");
    }


}
