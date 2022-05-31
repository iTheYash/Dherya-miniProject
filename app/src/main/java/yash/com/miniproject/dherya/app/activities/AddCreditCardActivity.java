package yash.com.miniproject.dherya.app.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.adapters.SelectableCreditCardAdapter;
import yash.com.miniproject.dherya.app.holders.SelectableCreditCardViewHolder;
import yash.com.miniproject.dherya.app.utils.Constants;
import yash.com.miniproject.dherya.app.utils.SharedPreferencesUtils;
import yash.com.miniproject.dherya.database.ExpenseManagerDAO;
import yash.com.miniproject.dherya.enums.CreditCardBackground;
import yash.com.miniproject.dherya.enums.CreditCardType;
import yash.com.miniproject.dherya.enums.Currency;
import yash.com.miniproject.dherya.exceptions.CouldNotInsertDataException;
import yash.com.miniproject.dherya.model.CreditCard;

public class AddCreditCardActivity extends AppCompatActivity {


    public static final String CAME_FROM_WELCOME_ACTIVITY_INTENT = "CAME_FROM_WELCOME_ACTIVITY_INTENT";


    private static final String CALENDAR_EXPIRATION_TAG = "1";


    boolean mCameFromWelcomeScreen = false;
    Calendar cardExpirationCal = null;
    List<Currency> currencies;
    List<CreditCardType> cardTypes;
    List<Integer> days;
    List<CreditCard> mCreditCardList;
    CreditCardBackground selectedCreditCardBackground = null;


    Toolbar toolbar;
    EditText cardBankName;
    EditText cardAlias;
    EditText cardNumber;
    EditText creditLimit;
    EditText cardExpiration;
    Spinner cardCurrency;
    Spinner cardType;
    Spinner cardClosingDay;
    Spinner cardDueDay;
    Button buttonAddCreditCard;
    RecyclerView mRecyclerView;
    SelectableCreditCardAdapter mAdapter;
    LinearLayoutManager mLayoutManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credit_card);

        toolbar = (Toolbar) findViewById(R.id.add_cc_toolbar);
        cardBankName = (EditText) findViewById(R.id.add_cc_edit_bank);
        cardAlias = (EditText) findViewById(R.id.add_cc_edit_alias);
        cardNumber = (EditText) findViewById(R.id.add_cc_edit_number);
        creditLimit = (EditText) findViewById(R.id.add_cc_edit_credit_limit);
        cardExpiration = (EditText) findViewById(R.id.add_cc_edit_expiration);
        cardCurrency = (Spinner) findViewById(R.id.add_cc_spinner_currency);
        cardType = (Spinner) findViewById(R.id.add_cc_spinner_type);
        cardClosingDay = (Spinner) findViewById(R.id.add_cc_edit_closing);
        cardDueDay = (Spinner) findViewById(R.id.add_cc_edit_due);
        mRecyclerView = (RecyclerView) findViewById(R.id.add_cc_recycler);
        buttonAddCreditCard = (Button) findViewById(R.id.add_cc_button_add_cc);
        buttonAddCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleNewCardCreation();
            }
        });

        setUpToolbar();
        setUpPickers();
        setUpSpinners();
        setUpCCRecyclerView();
        setUpRecyclerUpdater();


        mCameFromWelcomeScreen = getIntent().getBooleanExtra(CAME_FROM_WELCOME_ACTIVITY_INTENT, false);
    }


    private void setUpToolbar() {
        toolbar.setTitle(getResources().getString(R.string.activity_add_new_cc_title));
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icon_back_material));
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mCameFromWelcomeScreen) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.activity_add_new_cc_exit_dialog_title))
                    .setMessage(getResources().getString(R.string.activity_add_new_cc_exit_dialog_message))
                    .setPositiveButton(getResources().getString(R.string.activity_add_new_cc_exit_dialog_button_exit),  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.activity_add_new_cc_exit_dialog_button_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            dialog.show();
        }
        else
            super.onBackPressed();
    }

    private void setUpPickers() {
        cardExpiration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                            @Override
                            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                                if(cardExpirationCal == null) {
                                    cardExpirationCal = Calendar.getInstance();
                                    cardExpirationCal.set(Calendar.HOUR_OF_DAY, 0);
                                    cardExpirationCal.set(Calendar.MINUTE, 0);
                                    cardExpirationCal.set(Calendar.SECOND, 0);
                                    cardExpirationCal.set(Calendar.MILLISECOND, 0);
                                }

                                cardExpirationCal.set(year, monthOfYear, dayOfMonth);
                                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                                cardExpiration.setText(formatter.format(cardExpirationCal.getTime()));
                            }
                        })
                        .setFirstDayOfWeek(Calendar.SUNDAY)
                        .setDoneText(getResources().getString(R.string.activity_add_new_cc_expiration_datepicker_button_select))
                        .setCancelText(getResources().getString(R.string.activity_add_new_cc_expiration_datepicker_button_cancel));
                cdp.show(getSupportFragmentManager(), CALENDAR_EXPIRATION_TAG);
            }
        });
    }

    private void setUpSpinners() {
        currencies = new ArrayList<>(Arrays.asList(Currency.values()));
        ArrayAdapter currencyAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, currencies);
        currencyAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        cardCurrency.setAdapter(currencyAdapter);

        cardTypes = new ArrayList<>(Arrays.asList(CreditCardType.values()));
        ArrayAdapter cardTypeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, cardTypes);
        cardTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        cardType.setAdapter(cardTypeAdapter);

        days = new ArrayList<>();
        for(int i=1; i<=28;i++) {
            days.add(new Integer(i));
        }

        ArrayAdapter closingDayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, days);
        closingDayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        cardClosingDay.setAdapter(closingDayAdapter);

        ArrayAdapter dueDayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, days);
        dueDayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        cardDueDay.setAdapter(dueDayAdapter);
    }

    private void setUpCCRecyclerView() {

        mCreditCardList = CreditCard.getCreditCardBackgroundTypesList(this);
        SelectableCreditCardViewHolder.SelectableCreditCardSelectedListener ccSelectedListener = new SelectableCreditCardViewHolder.SelectableCreditCardSelectedListener() {
            @Override
            public void OnCreditCardSelected(CreditCard creditCard) {
                Toast.makeText(AddCreditCardActivity.this, "Background selected", Toast.LENGTH_SHORT).show();
                selectedCreditCardBackground = creditCard.getCreditCardBackground();
            }
        };

        mAdapter = new SelectableCreditCardAdapter(getApplicationContext(), mCreditCardList);
        mAdapter.setCreditCardSelectedListener(ccSelectedListener);
        mRecyclerView.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

    }


    private void setUpRecyclerUpdater() {

        cardBankName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                for (CreditCard c : mCreditCardList) {
                    c.setBankName(cardBankName.getText().toString());
                }
                mAdapter.notifyDataSetChanged();
            }
        });

        cardAlias.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                for (CreditCard c : mCreditCardList) {
                    c.setCardAlias(cardAlias.getText().toString());
                }
                mAdapter.notifyDataSetChanged();
            }
        });

        cardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                for (CreditCard c : mCreditCardList) {
                    c.setCardNumber(cardNumber.getText().toString());
                }
                mAdapter.notifyDataSetChanged();
            }
        });

        cardExpiration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(cardExpirationCal != null) {
                    for (CreditCard c : mCreditCardList) {
                        c.setCardExpiration(cardExpirationCal);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });


        cardCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                for (CreditCard c : mCreditCardList) {
                    c.setCurrency(currencies.get(cardCurrency.getSelectedItemPosition()));
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        cardType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                for (CreditCard c : mCreditCardList) {
                    c.setCardType(cardTypes.get(cardType.getSelectedItemPosition()));
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

    }


    private void handleNewCardCreation() {
        String alias = cardAlias.getText().toString();
        if(alias.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.activity_add_new_cc_error_bad_alias), Toast.LENGTH_SHORT).show();
            return;
        }

        try{
            Double.parseDouble(creditLimit.getText().toString());
        }catch (NumberFormatException e) {
            Toast.makeText(this, getResources().getString(R.string.activity_add_new_cc_error_bad_credit_limit), Toast.LENGTH_SHORT).show();
            return;
        }

        if(cardExpirationCal == null) {
            Toast.makeText(this, getResources().getString(R.string.activity_add_new_cc_error_bad_expiration), Toast.LENGTH_SHORT).show();
            return;
        }

        int closing = days.get(cardClosingDay.getSelectedItemPosition());
        int due = days.get(cardDueDay.getSelectedItemPosition());
        if(closing == due) {
            Toast.makeText(this, getResources().getString(R.string.activity_add_new_cc_error_bad_closing_due_days), Toast.LENGTH_SHORT).show();
            return;
        }

        if(selectedCreditCardBackground == null) {
            Toast.makeText(this, getResources().getString(R.string.activity_add_new_cc_error_cc_background_not_selected), Toast.LENGTH_SHORT).show();
            return;
        }

        String bankName = cardBankName.getText().toString();
        String number = cardNumber.getText().toString();
        BigDecimal firstCreditPeriodLimit = new BigDecimal(creditLimit.getText().toString());
        firstCreditPeriodLimit = firstCreditPeriodLimit.setScale(2, BigDecimal.ROUND_DOWN);

        Currency currency = currencies.get(cardCurrency.getSelectedItemPosition());
        CreditCardType type = cardTypes.get(cardType.getSelectedItemPosition());


        ExpenseManagerDAO dao = new ExpenseManagerDAO(this);
        try {
            int creditCardId = (int) dao.insertCreditCard(new CreditCard(alias, bankName, number, currency, type, cardExpirationCal, closing, due, selectedCreditCardBackground), firstCreditPeriodLimit);
            SharedPreferencesUtils.setInt(getApplicationContext(), Constants.ACTIVE_CC_ID,  creditCardId);

        }catch(CouldNotInsertDataException e) {
            Toast.makeText(this, "There was a problem inserting the credit card!", Toast.LENGTH_SHORT).show();
        }
        Intent goHomeIntent = new Intent(this, HomeActivity.class);
        startActivity(goHomeIntent);
        finish();
    }
}
