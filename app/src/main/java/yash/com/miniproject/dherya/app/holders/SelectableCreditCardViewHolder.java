package yash.com.miniproject.dherya.app.holders;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.model.CreditCard;

public class SelectableCreditCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context mContext;
    SelectableCreditCardSelectedListener mListener = null;

    private CreditCard mCurrent;
    private int mPosition;

    private RelativeLayout container;
    private TextView bankName;
    private TextView alias;
    private TextView currency;
    private TextView cardNumber;
    private TextView creditCardLabel;
    private TextView cardExpirationLabel;
    private TextView cardExpiration;
    private ImageView cardType;
    private ImageView cardChip;


    public SelectableCreditCardViewHolder(View itemView) {
        super(itemView);

        container = (RelativeLayout) itemView.findViewById(R.id.list_item_credit_card_container);
        bankName = (TextView) itemView.findViewById(R.id.list_item_credit_card_bank_name);
        alias = (TextView) itemView.findViewById(R.id.list_item_credit_card_alias);
        currency = (TextView) itemView.findViewById(R.id.list_item_credit_card_currency);
        cardNumber = (TextView) itemView.findViewById(R.id.list_item_credit_card_number);
        creditCardLabel = (TextView) itemView.findViewById(R.id.list_item_credit_card_label);
        cardExpirationLabel = (TextView) itemView.findViewById(R.id.list_item_credit_card_expiration_label);
        cardExpiration = (TextView) itemView.findViewById(R.id.list_item_credit_card_expiration);
        cardType = (ImageView) itemView.findViewById(R.id.list_item_credit_card_type);
        cardChip = (ImageView) itemView.findViewById(R.id.list_item_credit_card_chip);



    }


    public void setData(Context context, CreditCard current, int position) {
        mContext = context;
        mCurrent = current;
        mPosition = position;

        bankName.setText(current.getBankName());
        alias.setText(current.getCardAlias());
        cardNumber.setText(current.getCardNumber());
        currency.setText(current.getCurrency().getCode());
        cardExpiration.setText(current.getShortCardExpirationString());

        container.setBackground(current.getCreditCardBackground().getBackgroundDrawable(context));
        bankName.setTextColor(current.getCreditCardBackground().getTextColor(context));
        alias.setTextColor(current.getCreditCardBackground().getTextColor(context));
        currency.setTextColor(current.getCreditCardBackground().getTextColor(context));
        cardNumber.setTextColor(current.getCreditCardBackground().getTextColor(context));
        cardExpiration.setTextColor(current.getCreditCardBackground().getTextColor(context));
        creditCardLabel.setTextColor(current.getCreditCardBackground().getTextColor(context));
        cardExpirationLabel.setTextColor(current.getCreditCardBackground().getTextColor(context));

        switch(current.getCardType()) {
            case MASTERCARD:
                cardType.setImageResource(R.drawable.mastercard_logo);
                break;
            case VISA:
                cardType.setImageResource(R.drawable._8482363cef1014c0b5e49c1);
                break;
        }


    }

    public void setListeners() {
        container.setOnClickListener(this);
    }

    public void setOnCreditCardSelectedListener(SelectableCreditCardSelectedListener listener) {
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


    public interface SelectableCreditCardSelectedListener {
        void OnCreditCardSelected(CreditCard creditCard);
    }

}
