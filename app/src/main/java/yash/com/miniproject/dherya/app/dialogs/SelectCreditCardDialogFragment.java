package yash.com.miniproject.dherya.app.dialogs;


import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.adapters.SelectableCreditCardAdapter;
import yash.com.miniproject.dherya.app.fragments.OverviewFragment;
import yash.com.miniproject.dherya.app.holders.SelectableCreditCardViewHolder;
import yash.com.miniproject.dherya.app.utils.Constants;
import yash.com.miniproject.dherya.app.utils.SharedPreferencesUtils;
import yash.com.miniproject.dherya.model.CreditCard;

/**
 * Created by Alex on 30/8/2016.
 */
public class SelectCreditCardDialogFragment extends AppCompatDialogFragment {

    //Constants
    private static final String TAG = "SelectCCDialogFrag";

    //UI
    private DialogInterface.OnDismissListener mOnDismissListener = null;
    private RecyclerView mRecyclerView;
    private SelectableCreditCardAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    //DATA
    private List<CreditCard> mCreditCardList;
    private CreditCard selectedCreditCard = null;


    public SelectCreditCardDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static SelectCreditCardDialogFragment newInstance(List<CreditCard> creditCardList) {
        SelectCreditCardDialogFragment frag = new SelectCreditCardDialogFragment();
        frag.setCreditCardList(creditCardList);
        return frag;
    }

    private void setCreditCardList(List<CreditCard> creditCardList) {
        mCreditCardList = creditCardList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_select_credit_card, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpCCRecyclerView(view);
    }


    private void setUpCCRecyclerView(View view) {

        mRecyclerView = (RecyclerView) view.findViewById(R.id.dialog_select_cc_recycler);

        SelectableCreditCardViewHolder.SelectableCreditCardSelectedListener ccSelectedListener = new SelectableCreditCardViewHolder.SelectableCreditCardSelectedListener() {
            @Override
            public void OnCreditCardSelected(CreditCard creditCard) {

                SharedPreferencesUtils.setInt(getContext(), Constants.ACTIVE_CC_ID, creditCard.getId());
                getFragmentManager().beginTransaction().replace(R.id.home_content_frame, new OverviewFragment()).commit();
                dismiss();
            }
        };

        mAdapter = new SelectableCreditCardAdapter(getContext().getApplicationContext(), mCreditCardList);
        mAdapter.setCreditCardSelectedListener(ccSelectedListener);

        mRecyclerView.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(getContext().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }


    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        mOnDismissListener = listener;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
    }

}

