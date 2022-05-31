package yash.com.miniproject.dherya.app.holders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.activities.ExpenseDetailActivity;
import yash.com.miniproject.dherya.app.adapters.ExpensesAdapter;
import yash.com.miniproject.dherya.app.utils.Constants;
import yash.com.miniproject.dherya.app.utils.DateUtils;
import yash.com.miniproject.dherya.app.utils.ImageUtils;
import yash.com.miniproject.dherya.model.Expense;

public class ExpensesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public ExpensesAdapter mAdapter;
    private Fragment mFragment;
    private ExpenseDeletedListener mListener = null;

    private RelativeLayout mContainer;
    private TextView mAmount;
    private TextView mDescription;
    private TextView mDate;
    private ImageView mImage;
    private TextView mCategory;
    private TextView mType;
    private ImageView mDeleteIcon;

    private Expense mCurrent;
    private int mCreditPeriodId;
    private int mExpensePosition;

    public ExpensesViewHolder(View itemView) {
        super(itemView);

        mContainer = (RelativeLayout) itemView.findViewById(R.id.list_item_expenses_container);
        mAmount = (TextView) itemView.findViewById(R.id.list_item_expenses_txt_amount);
        mDescription = (TextView) itemView.findViewById(R.id.list_item_expenses_txt_description);
        mDate = (TextView) itemView.findViewById(R.id.list_item_expenses_txt_date);
        mImage = (ImageView) itemView.findViewById(R.id.list_item_expenses_img_image);
        mCategory = (TextView) itemView.findViewById(R.id.list_item_expenses_txt_category);
        mType = (TextView) itemView.findViewById(R.id.list_item_expenses_txt_type);
        mDeleteIcon = (ImageView) itemView.findViewById(R.id.list_item_expenses_img_delete);
    }

    public void setData(ExpensesAdapter adapter, Fragment fragment, Expense current, int creditPeriodId, int position) {
        mAdapter = adapter;
        mFragment = fragment;
        this.mCurrent = current;
        mCreditPeriodId = creditPeriodId;
        this.mExpensePosition = position;

        this.mAmount.setText(current.getAmount().toPlainString() + " " + current.getCurrency().getCode());
        this.mDescription.setText(current.getDescription());
        this.mDate.setText(DateUtils.getRelativeTimeSpanString(current.getDate()));

        this.mCategory.setText(current.getExpenseCategory().getFriendlyName());
        ((GradientDrawable)this.mCategory.getBackground()).setColor(ContextCompat.getColor(mFragment.getContext(), current.getExpenseCategory().getColor()));

        this.mType.setText(current.getExpenseType().getShortName());
        ((GradientDrawable)this.mType.getBackground()).setColor(ContextCompat.getColor(mFragment.getContext(), current.getExpenseType().getColor()));


        if(current.getThumbnail() != null && current.getThumbnail().length > 0)
            this.mImage.setImageBitmap(ImageUtils.getBitmap(current.getThumbnail()));
        else
            this.mImage.setImageResource(R.drawable.icon_expense);

    }

    public void setListeners() {
        mDeleteIcon.setOnClickListener(this);
        mContainer.setOnClickListener(this);
    }

    public void setOnExpenseDeletedListener(ExpenseDeletedListener listener) {
        mListener = listener;
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.list_item_expenses_container:
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(mImage, mFragment.getResources().getString(R.string.transition_name_expense_detail_image));

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mFragment.getActivity(), pairs);
                Intent expenseDetailIntent = new Intent(mFragment.getActivity(), ExpenseDetailActivity.class);
                expenseDetailIntent.putExtra(ExpenseDetailActivity.INTENT_EXTRAS_EXPENSE, mCurrent);
                expenseDetailIntent.putExtra(ExpenseDetailActivity.INTENT_EXTRAS_CREDIT_PERIOD_ID, mCreditPeriodId);
                mFragment.startActivityForResult(expenseDetailIntent, Constants.EXPENSE_DETAIL_ACTIVITY_REQUEST_CODE, options.toBundle());

                break;

            case R.id.list_item_expenses_img_delete:

                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                            mListener.OnExpenseDeleted(mExpensePosition);
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mFragment.getActivity());
                builder.setTitle(R.string.dialog_delete_expense_title)
                        .setMessage(R.string.dialog_delete_expense_message)
                        .setPositiveButton((R.string.dialog_delete_expense_button_yes), listener)
                        .setNegativeButton((R.string.dialog_delete_expense_button_no), null)
                        .show();
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    public interface ExpenseDeletedListener {
        void OnExpenseDeleted(int position);
    }
}
