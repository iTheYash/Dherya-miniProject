package yash.com.miniproject.dherya.app.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.utils.Constants;
import yash.com.miniproject.dherya.app.utils.SharedPreferencesUtils;
import yash.com.miniproject.dherya.database.ExpenseManagerDAO;
import yash.com.miniproject.dherya.exceptions.CreditCardNotFoundException;
import yash.com.miniproject.dherya.exceptions.CreditPeriodNotFoundException;
import yash.com.miniproject.dherya.exceptions.SharedPreferenceNotFoundException;
import yash.com.miniproject.dherya.model.CreditCard;
import yash.com.miniproject.dherya.model.CreditPeriod;
import yash.com.miniproject.dherya.model.DailyExpense;

/**
 * Created by Alex on 17/8/2016.
 */
public class ChartExpenseFragment extends Fragment {

    //UI
    private LineChartView chart;
    private RelativeLayout mNoExpensesContainer;

    //DATA
    private boolean chartIsVisible = false;
    private int activeCreditCardId;
    private ExpenseManagerDAO dao;
    private LineChartData data;
    CreditPeriod creditPeriod;
    CreditCard creditCard;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_chart_expenses, container, false);
        chart = (LineChartView) rootView.findViewById(R.id.chart_expenses_linechart);
        mNoExpensesContainer = (RelativeLayout) rootView.findViewById(R.id.chart_expenses_no_expenses_container);

        try {
            activeCreditCardId = SharedPreferencesUtils.getInt(getContext(), Constants.ACTIVE_CC_ID);
        }catch(SharedPreferenceNotFoundException e) {
            //This shouldn't happen
            //Toast.makeText(getActivity(), "Megapeo en oncreate, SharedPreferenceNotFoundException CreditCardNotFoundException", Toast.LENGTH_SHORT).show();
        }

        refreshData();
        return rootView;
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



        //Convert the data to PointValues, add those to lines
        List<Line> lines = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<PointValue> accumulatedValues = new ArrayList<>();
        List<PointValue> dailyValues = new ArrayList<>();
        List<PointValue> maxValue = new ArrayList<>();

        //Set maxValue (CreditLimit)
        maxValue.add(new PointValue(0, creditPeriod.getCreditLimit().floatValue()));
        maxValue.add(new PointValue(creditPeriod.getTotalDaysInPeriod(), creditPeriod.getCreditLimit().floatValue()));


        //Set daily and accumulated values.
        List<DailyExpense> dailyExpenses = creditPeriod.getDailyExpenses();
        List<DailyExpense> accumulatedDailyExpenses = creditPeriod.getAccumulatedDailyExpenses();
        for (int i = 0; i < creditPeriod.getTotalDaysInPeriod(); ++i) {

            PointValue aux = new PointValue(i, 1);
            //PointValue aux = new PointValue(i, accumulatedDailyExpenses.get(i).getAmount().floatValue());
            aux.setTarget(i, accumulatedDailyExpenses.get(i).getAmount().floatValue());
            accumulatedValues.add(aux);

            dailyValues.add(new PointValue(i, dailyExpenses.get(i).getAmount().floatValue()));
            axisValues.add(new AxisValue(i).setLabel(accumulatedDailyExpenses.get(i).getFormattedDate()));
        }

        //Add accumulatedValues line
        Line line = new Line(accumulatedValues);
        line.setColor(ChartUtils.COLOR_BLUE);
        //line.setCubic(true);
        line.setFilled(true);
        line.setHasPoints(false);
        lines.add(line);

        //Add maxValue line
        line = new Line(maxValue);
        line.setColor(ChartUtils.COLOR_RED);
        line.setHasPoints(false);
        lines.add(line);


        //Add dailyValues line
        line = new Line(dailyValues);
        line.setColor(ChartUtils.COLOR_ORANGE);
        line.setPointRadius(3);
        line.setHasLines(false);
        lines.add(line);


        //Add lines to chart data
        data = new LineChartData(lines);
        data.setBaseValue(Float.NEGATIVE_INFINITY);

        //Setup axis
        Axis axisX = new Axis(axisValues)
                .setHasTiltedLabels(true)
                .setName("Days");

        Axis axisY = new Axis().setHasLines(true)
                .setHasTiltedLabels(true)
                .setName("Money (" + creditCard.getCurrency().getCode() + ")")
                .setFormatter(new MoneyFormatter());

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);


        //Setup chart
        chart.setLineChartData(data);
        chart.setZoomType(ZoomType.VERTICAL);
        chart.setMaxZoom(5);
        chart.setViewportCalculationEnabled(false);

        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.top *= 1.3;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);


        chart.startDataAnimation(1000);
    }
}

/**
 * Recalculated height values to display on axis. For this example I use auto-generated height axis so I
 * override only formatAutoValue method.
 */
class MoneyFormatter extends SimpleAxisValueFormatter {

    public MoneyFormatter() {}

    @Override
    public int formatValueForAutoGeneratedAxis(char[] formattedValue, float value, int autoDecimalDigits) {

        if(value > 1000000) {
            value = value/1000000;
            String s = String.format("%.1f", value);
            value = Float.valueOf(s);
            setAppendedText("M".toCharArray());
        }
        else if(value > 1000) {
            value = value/1000;
            String s = String.format("%.1f", value);
            value = Float.valueOf(s);
            setAppendedText("k".toCharArray());
        } else {
            setAppendedText("".toCharArray());
        }
        return super.formatValueForAutoGeneratedAxis(formattedValue, value, autoDecimalDigits);
    }
}
