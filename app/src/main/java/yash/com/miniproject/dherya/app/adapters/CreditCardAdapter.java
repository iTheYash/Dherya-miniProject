package yash.com.miniproject.dherya.app.adapters;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.holders.CreditCardViewHolder;
import yash.com.miniproject.dherya.model.CreditCard;

/**
 * Created by Alex on 30/8/2016.
 */
public class CreditCardAdapter extends RecyclerView.Adapter<CreditCardViewHolder>  {

    private List<CreditCard> mCreditCards;
    private LayoutInflater mInflater;
    private Context mContext;
    private Fragment mFragment;
    private CreditCardViewHolder.CreditCardSelectedListener mCCSelectedListener;

    public CreditCardAdapter(Context context, Fragment fragment, List<CreditCard> creditCards) {
        mContext = context;
        mFragment = fragment;
        mCreditCards = creditCards;
        mInflater = LayoutInflater.from(context);
    }

    public void setCreditCardSelectedListener(CreditCardViewHolder.CreditCardSelectedListener ccSelectedListener) {
        mCCSelectedListener = ccSelectedListener;
    }


    @Override
    public CreditCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_credit_card, parent, false);
        return new CreditCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CreditCardViewHolder holder, int position) {
        CreditCard current = mCreditCards.get(position);
        holder.setData(mContext, mFragment, current, position);

        if(mCCSelectedListener != null) {
            holder.setListeners();
            holder.setOnCreditCardSelectedListener(mCCSelectedListener);
        }
        else
            Toast.makeText(mContext, "Warning: mCCSelectedListener == null!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return mCreditCards.size();
    }
}
