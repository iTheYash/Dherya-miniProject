package yash.com.miniproject.dherya.app.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.dialogs.CheckCreditPeriodLimitDialogFragment;
import yash.com.miniproject.dherya.app.holders.SelectableCreditCardViewHolder;
import yash.com.miniproject.dherya.app.utils.Constants;
import yash.com.miniproject.dherya.app.utils.DateUtils;
import yash.com.miniproject.dherya.app.utils.SharedPreferencesUtils;
import yash.com.miniproject.dherya.app.utils.TextUtils;
import yash.com.miniproject.dherya.app.views.HorizontalBar;
import yash.com.miniproject.dherya.database.ExpenseManagerDAO;
import yash.com.miniproject.dherya.enums.ExpenseCategory;
import yash.com.miniproject.dherya.exceptions.CreditCardNotFoundException;
import yash.com.miniproject.dherya.exceptions.CreditPeriodNotFoundException;
import yash.com.miniproject.dherya.exceptions.SharedPreferenceNotFoundException;
import yash.com.miniproject.dherya.model.CreditCard;
import yash.com.miniproject.dherya.model.DailyExpense;


public class OverviewFragment extends Fragment {


    private static final String TAG = OverviewFragment.class.getSimpleName();


    int activeCreditCardId = -1;
    CreditCard activeCreditCard = null;
    ExpenseManagerDAO dao;


    SelectableCreditCardViewHolder holder;
    View headerCreditCardContainer;
    HorizontalBar creditDatePeriodBar;
    HorizontalBar creditBalanceBar;
    TextView extraInfo;
    ScrollView scrollViewContainer;
    View errNoCC;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.fragment_name_overview));
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
                activeCreditCard = dao.getCreditCardWithCreditPeriod(activeCreditCardId, 0);
            }catch (CreditCardNotFoundException e ) {
                Toast.makeText(getActivity(), getResources().getString(R.string.err_problem_loading_card_or_no_card_exists), Toast.LENGTH_SHORT).show();
            }catch (CreditPeriodNotFoundException e) {
                createACurrentCreditPeriod();
            }
        }catch(SharedPreferenceNotFoundException e) {

        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        headerCreditCardContainer = view.findViewById(R.id.list_item_credit_card_container);
        creditDatePeriodBar = (HorizontalBar) view.findViewById(R.id.frag_overview_credit_date_period_bar);
        creditBalanceBar = (HorizontalBar) view.findViewById(R.id.frag_overview_credit_balance_bar);
        extraInfo = (TextView) view.findViewById(R.id.frag_overview_extra_info);
        scrollViewContainer = (ScrollView) view.findViewById(R.id.frag_overview_body_scroll_view_container);
        errNoCC = view.findViewById(R.id.frag_overview_err_no_cc);

        refreshUI();

        return view;
    }

    private void loadDao() {
        if(dao == null)
            dao = new ExpenseManagerDAO(getActivity().getApplicationContext());
    }


    private void refreshUI() {
        loadDao();


        scrollViewContainer.setVisibility(View.GONE);
        headerCreditCardContainer.setVisibility(View.GONE);
        errNoCC.setVisibility(View.GONE);

        if (activeCreditCard != null) {
            try {

                scrollViewContainer.setVisibility(View.VISIBLE);
                headerCreditCardContainer.setVisibility(View.VISIBLE);

                /* DatePeriod bar */
                Calendar today = Calendar.getInstance();
                Calendar startDate = activeCreditCard.getCreditPeriods().get(0).getStartDate();
                Calendar endDate = activeCreditCard.getCreditPeriods().get(0).getEndDate();
                int daysBetweenStartAndToday = DateUtils.getDaysBetween(startDate, today);
                int daysInPeriod = activeCreditCard.getCreditPeriods().get(0).getTotalDaysInPeriod();
                int datePeriodPercentage;
                if(daysInPeriod > 0)
                    datePeriodPercentage = (int)(100*((float)daysBetweenStartAndToday/daysInPeriod));
                else
                    datePeriodPercentage = 0;

                creditDatePeriodBar.setProgressPercentage(datePeriodPercentage);
                creditDatePeriodBar.setTextLo(DateUtils.getDayShortMonthString(startDate));
                creditDatePeriodBar.setTextHi(DateUtils.getDayShortMonthString(endDate));
                if(today.getTimeInMillis() <= endDate.getTimeInMillis()) {
                    creditDatePeriodBar.setTextBar(DateUtils.getDayShortMonthString(today));
                }


                int creditLimit = activeCreditCard.getCreditPeriods().get(0).getCreditLimit().toBigInteger().intValue();
                int expensesTotal = activeCreditCard.getCreditPeriods().get(0).getExpensesTotal().toBigInteger().intValue();
                String currencyCode = activeCreditCard.getCurrency().getCode();
                int balancePercentage;
                if(creditLimit > 0)
                    balancePercentage = (int)(100*((float)expensesTotal/creditLimit));
                else
                    balancePercentage = 0;

                creditBalanceBar.setProgressPercentage(balancePercentage);
                creditBalanceBar.setTextHi(creditLimit + " " + currencyCode);
                if(expensesTotal > 0)
                    creditBalanceBar.setTextBar(Integer.toString(expensesTotal) + " " + currencyCode);
                creditBalanceBar.setTextLo("0 " + currencyCode);


                extraInfo.setText(TextUtils.fromHtml(generateExtraInfo()));


                holder = new SelectableCreditCardViewHolder(headerCreditCardContainer);
                holder.setData(getContext(), activeCreditCard, 0);
            }catch (Exception e) {
                Toast.makeText(getActivity(), "Problem refreshing card data", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

        } else {
            errNoCC.setVisibility(View.VISIBLE);
        }
    }

    private String generateExtraInfo() {


        List<String> extraInfos = new ArrayList<>();
        String result = "";
        String info1 = getResources().getString(R.string.fragment_overview_credit_extra_info_spendiest_day);
        String info2 = getResources().getString(R.string.fragment_overview_credit_extra_info_spendiest_category);
        String info3 = getResources().getString(R.string.fragment_overview_credit_extra_info_average_per_day);
        String info4 = getResources().getString(R.string.fragment_overview_credit_extra_info_to_spend_average);

        BigDecimal maxDailyExpense = new BigDecimal(0);
        String maxDailyExpenseDate = null;
        List<DailyExpense> dailyExpenses = activeCreditCard.getCreditPeriods().get(0).getDailyExpenses();
        for(DailyExpense de : dailyExpenses) {
            if(de.getAmount().compareTo(maxDailyExpense) == 1) {
                maxDailyExpense = de.getAmount();
                maxDailyExpenseDate = de.getFormattedDate();
            }
        }

        if(maxDailyExpenseDate != null)
            extraInfos.add(String.format(Locale.getDefault(), info1, maxDailyExpenseDate, maxDailyExpense.toBigInteger().toString(), activeCreditCard.getCurrency().getCode()));





        List<BigDecimal> expensesByCategory = activeCreditCard.getCreditPeriods().get(0).getExpensesByCategory();
        BigDecimal expenseTotal = activeCreditCard.getCreditPeriods().get(0).getExpensesTotal();
        BigDecimal maxCategory = new BigDecimal(0);
        String maxCategoryName = "";



        if(expenseTotal.compareTo(BigDecimal.ZERO) == 1) {

            for(int i = 0; i < ExpenseCategory.values().length; i++) {
                if(expensesByCategory.get(i).compareTo(maxCategory) == 1) {
                    maxCategory = expensesByCategory.get(i);
                    maxCategoryName = ExpenseCategory.getByExpenseCategoryId(i).getFriendlyName();
                }
            }

            BigDecimal percentOfTotalMaxCategory = maxCategory.divide(expenseTotal, 2, RoundingMode.HALF_UP);
            percentOfTotalMaxCategory = percentOfTotalMaxCategory.multiply(new BigDecimal(100));
            extraInfos.add(String.format(Locale.getDefault(), info2, maxCategoryName,  percentOfTotalMaxCategory.toBigInteger().toString()));
        }



        Calendar today = Calendar.getInstance();
        Calendar startDate = activeCreditCard.getCreditPeriods().get(0).getStartDate();
        Calendar endDate = activeCreditCard.getCreditPeriods().get(0).getEndDate();
        int daysBetweenStartAndToday = DateUtils.getDaysBetween(startDate, today);
        int daysBetweenTodayAndEnd = DateUtils.getDaysBetween(today, endDate);
        BigDecimal expensesTotal = activeCreditCard.getCreditPeriods().get(0).getExpensesTotal();
        BigDecimal creditLimit = activeCreditCard.getCreditPeriods().get(0).getCreditLimit();
        BigDecimal creditToSpend = creditLimit.subtract(expensesTotal);
        String currencyCode = activeCreditCard.getCurrency().getCode();

        if(expensesTotal.compareTo(BigDecimal.ZERO) == 1 && daysBetweenStartAndToday > 0) {
            BigDecimal average = expensesTotal.divide(new BigDecimal(daysBetweenStartAndToday), 1, RoundingMode.HALF_UP);
            average = average.setScale(1, RoundingMode.HALF_UP);
            extraInfos.add(String.format(Locale.getDefault(), info3, average.toPlainString(), currencyCode));
        }

        if(creditToSpend.compareTo(BigDecimal.ZERO) == 1 && daysBetweenTodayAndEnd > 0) {
            BigDecimal averageToSpend = creditToSpend.divide(new BigDecimal(daysBetweenTodayAndEnd), 1, RoundingMode.HALF_UP);
            averageToSpend = averageToSpend.setScale(1, RoundingMode.HALF_UP);
            creditToSpend = creditToSpend.setScale(1, RoundingMode.HALF_UP);
            extraInfos.add(String.format(Locale.getDefault(), info4, creditToSpend.toPlainString(), averageToSpend.toPlainString(), currencyCode));
        }





        if(extraInfos.size() > 0) {
            result += "&#8226; " + extraInfos.get(0);
            for (int i = 1; i < extraInfos.size(); i++) {
                result += "<br/>&#8226; " + extraInfos.get(i);
            }
        }

        return result;
    }


    private void createACurrentCreditPeriod() {


        try {

            CreditCard cc = dao.getCreditCard(activeCreditCardId);
            BigDecimal previousCreditPeriodLimit = dao.getCreditPeriodListFromCard(activeCreditCardId).get(0).getCreditLimit();


            FragmentManager fm = getActivity().getSupportFragmentManager();
            CheckCreditPeriodLimitDialogFragment dialog = CheckCreditPeriodLimitDialogFragment.newInstance(
                    dao,
                    cc,
                    previousCreditPeriodLimit);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    try {
                        activeCreditCard = dao.getCreditCardWithCreditPeriod(activeCreditCardId, 0);
                        refreshUI();
                    }catch (Exception e) {
                        Toast.makeText(getActivity(), "Error refreshing credit card data", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show(fm, "fragment_dialog_new_credit_period");

        } catch (CreditCardNotFoundException e) {
            Toast.makeText(getActivity(), "Error getting credit card data", Toast.LENGTH_SHORT).show();
        }

    }

}
