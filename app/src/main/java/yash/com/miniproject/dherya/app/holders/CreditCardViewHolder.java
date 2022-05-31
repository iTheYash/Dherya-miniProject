package yash.com.miniproject.dherya.app.holders;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.model.CreditCard;

public class CreditCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context mContext;
    private Fragment mFragment;
    CreditCardSelectedListener mListener = null;

    private CreditCard mCurrent;
    private int mPosition;

    private RelativeLayout container;
    private TextView bankName;
    private TextView alias;
    private TextView currency;
    private TextView cardNumber;
    private TextView cardExpiration;


    private RelativeLayout ccContainer;
    private TextView ccAlias;
    private TextView ccNumber;
    private ImageView ccCardType;
    private ImageView ccCardChip;


    public CreditCardViewHolder(View itemView) {
        super(itemView);

        container = (RelativeLayout) itemView.findViewById(R.id.list_item_credit_card_container);
        alias = (TextView) itemView.findViewById(R.id.list_item_credit_card_alias);
        cardNumber = (TextView) itemView.findViewById(R.id.list_item_credit_card_number);
        cardExpiration = (TextView) itemView.findViewById(R.id.list_item_credit_card_expiration);
        bankName = (TextView) itemView.findViewById(R.id.list_item_credit_card_bank_name);
        currency = (TextView) itemView.findViewById(R.id.list_item_credit_card_currency);

        ccContainer = (RelativeLayout) itemView.findViewById(R.id.list_item_credit_card_cc_container);
        ccAlias = (TextView) itemView.findViewById(R.id.list_item_credit_card_cc_alias);
        ccNumber = (TextView) itemView.findViewById(R.id.list_item_credit_card_cc_number);
        ccCardType = (ImageView) itemView.findViewById(R.id.list_item_credit_card_cc_type);
        ccCardChip = (ImageView) itemView.findViewById(R.id.list_item_credit_card_cc_chip);
    }


    public void setData(Context context, Fragment fragment, CreditCard current, int position) {
        mContext = context;
        mFragment = fragment;
        mCurrent = current;
        mPosition = position;


        alias.setText(current.getCardAlias());
        cardNumber.setText(current.getCardNumber());
        cardExpiration.setText("EXP " + current.getShortCardExpirationString());
        bankName.setText(current.getBankName());
        currency.setText(current.getCurrency().getCode());

        ccContainer.setBackground(current.getCreditCardBackground().getBackgroundDrawable(context));
        ccAlias.setText(current.getCardAlias());
        ccAlias.setTextColor(current.getCreditCardBackground().getTextColor(context));
        ccNumber.setText(current.getCardNumber());
        ccNumber.setTextColor(current.getCreditCardBackground().getTextColor(context));

        switch(current.getCardType()) {
            case MASTERCARD:
                ccCardType.setImageResource(R.drawable.logo_mastercard);
                break;
            case VISA:
                ccCardType.setImageResource(R.drawable.logo_visa);
                break;
        }

    }

    public void setListeners() {
        container.setOnClickListener(this);
    }

    public void setOnCreditCardSelectedListener(CreditCardSelectedListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id) {
            case R.id.list_item_credit_card_container:
                if(mListener != null)
                    mListener.OnCreditCardSelected(mCurrent);
                break;
        }
    }


    public interface CreditCardSelectedListener {
        void OnCreditCardSelected(CreditCard creditCard);
    }

}

