package yash.com.miniproject.dherya.app.adapters;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.holders.ExpensesViewHolder;
import yash.com.miniproject.dherya.model.Expense;

/**
 * Created by Alex on 7/8/2016.
 */
public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesViewHolder> {

    private List<Expense> mExpenses;
    private int mCreditPeriodId;
    private LayoutInflater mInflater;
    private Fragment mFragment;
    private ExpensesViewHolder.ExpenseDeletedListener mListener;

    public ExpensesAdapter(Fragment fragment, List<Expense> expenses, int creditPeriodId, ExpensesViewHolder.ExpenseDeletedListener listener) {
        mFragment = fragment;
        mExpenses = expenses;
        mCreditPeriodId = creditPeriodId;
        mInflater = LayoutInflater.from(mFragment.getContext());
        mListener = listener;
    }

    @Override
    public ExpensesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = mInflater.inflate(R.layout.list_item_expenses, parent, false);
        return new ExpensesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExpensesViewHolder holder, int position) {
        Expense current = mExpenses.get(position);
        holder.setData(this, mFragment, current, mCreditPeriodId, position);
        holder.setListeners();
        holder.setOnExpenseDeletedListener(mListener);
    }

    @Override
    public int getItemCount() {
        return mExpenses.size();
    }

    /*public void removeExpense(int position) {
        mExpenses.remove(position);
    }*/
}
