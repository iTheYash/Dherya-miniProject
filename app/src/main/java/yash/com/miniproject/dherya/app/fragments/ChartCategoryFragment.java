package yash.com.miniproject.dherya.app.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;
import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.utils.Constants;
import yash.com.miniproject.dherya.app.utils.SharedPreferencesUtils;
import yash.com.miniproject.dherya.database.ExpenseManagerDAO;
import yash.com.miniproject.dherya.enums.ExpenseCategory;
import yash.com.miniproject.dherya.exceptions.CreditCardNotFoundException;
import yash.com.miniproject.dherya.exceptions.CreditPeriodNotFoundException;
import yash.com.miniproject.dherya.exceptions.SharedPreferenceNotFoundException;
import yash.com.miniproject.dherya.model.CreditCard;
import yash.com.miniproject.dherya.model.CreditPeriod;

/**
 * Created by abice on 30/1/2017.
 */

public class ChartCategoryFragment extends Fragment {


    //UI
    private PieChartView chart;
    private RelativeLayout mNoExpensesContainer;

    //DATA
    private boolean chartIsVisible = false;
    private int activeCreditCardId;
    private ExpenseManagerDAO dao;
    private PieChartData data;
    CreditPeriod creditPeriod;
    CreditCard creditCard;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_chart_categories, container, false);
        chart = (PieChartView) rootView.findViewById(R.id.chart_categories_piechart);
        mNoExpensesContainer = (RelativeLayout) rootView.findViewById(R.id.chart_categories_no_expenses_container);


        try {
            activeCreditCardId = SharedPreferencesUtils.getInt(getContext(), Constants.ACTIVE_CC_ID);

        }catch(SharedPreferenceNotFoundException e) {
            //This shouldn't happen
        }

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !chartIsVisible) {
            refreshData();
            chartIsVisible = true;
        }
        else {
        }
    }



    public void refreshData() {
        if(dao == null)
            dao = new ExpenseManagerDAO(getActivity());

        //Refresh list from DB
        try {
            creditCard = dao.getCreditCardWithCreditPeriod(activeCreditCardId, 0);
            creditPeriod = creditCard.getCreditPeriods().get(0);
        }catch(CreditCardNotFoundException | CreditPeriodNotFoundException e) {
            Toast.makeText(getActivity(), getResources().getString(R.string.err_problem_loading_card_or_no_card_exists), Toast.LENGTH_SHORT).show();
        }

        //Check if there are no expenses in this period or there is no active credit card
        if(creditCard == null || creditPeriod.getExpensesTotal().equals(BigDecimal.ZERO)) {
            chart.setVisibility(View.GONE);
            mNoExpensesContainer.setVisibility(View.VISIBLE);
            return;
        }

        int numCategories = ExpenseCategory.values().length;
        List<BigDecimal> expenseByCategory = creditPeriod.getExpensesByCategory();
        List<SliceValue> sliceValues = new ArrayList<>();


        for (int i = 0; i < numCategories; ++i) {
            //SliceValue sliceValue = new SliceValue(expenseByCategory[i].floatValue(), ContextCompat.getColor(getContext(), ExpenseCategory.values()[i].getColor() ));
            SliceValue sliceValue = new SliceValue(20, ContextCompat.getColor(getContext(), ExpenseCategory.values()[i].getColor() ));
            sliceValue.setTarget(expenseByCategory.get(i).floatValue());
            sliceValue.setLabel(getExpenseLabel(expenseByCategory.get(i), ExpenseCategory.values()[i].getFriendlyName()));
            sliceValues.add(sliceValue);
        }


        //Setup chart
        data = new PieChartData(sliceValues);
        data.setHasLabels(true);
        //data.setHasLabelsOutside(true);
        chart.setPieChartData(data);

        chart.startDataAnimation(10000);

    }

    private String getExpenseLabel(BigDecimal expenseValue, String expenseName) {
        BigDecimal aux;
        BigDecimal expenseTotal = new BigDecimal(expenseValue.toPlainString());
        BigDecimal total = creditPeriod.getExpensesTotal();

        if(!total.equals(BigDecimal.ZERO)) {
            if(!expenseValue.equals((BigDecimal.ZERO))) {
                aux = expenseTotal.divide(total, 3, RoundingMode.CEILING);
                aux = aux.multiply(new BigDecimal(100));
                return String.format(Locale.getDefault(), "%1$s %2$s%%", expenseName, new DecimalFormat("#0.#").format(aux));
            }
        }

        return "";
    }

}

