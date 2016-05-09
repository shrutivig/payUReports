package reports.payu.com.app.payureports;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import reports.payu.com.app.payureports.Model.ReportData;
import reports.payu.com.app.payureports.Model.ReportResults;

/**
 * Created by shruti.vig on 5/4/16.
 */
public class ReportActivity extends HomeActivity {

    private final int FLAG_FILTER_DAY = 0;
    private final int FLAG_FILTER_WEEK = 1;
    private final int FLAG_FILTER_MONTH = 2;

    private HorizontalBarChart barChart;
    private ReportResults reportsResults;
    private LineChart lineChart;
    private PieChart pieChart;
    private int[] colors = new int[]{Color.rgb(106, 150, 31), Color.rgb(193, 37, 82), Color.rgb(245, 199, 0), Color.rgb(255, 102, 0),
            Color.rgb(179, 100, 53), Color.rgb(255, 32, 34)};
    private TextView startDateFilter;
    private TextView endDateFilter;
    private Date startDate;
    private boolean mFilterStartDateEntered = false;
    private boolean mFilterEndDateEntered = false;
    private Button filterDay, filterWeek, filterMonth, filterAll, filterBar, filterLine, filterPie;
    private LinearLayout filterLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        filterLayout = (LinearLayout) findViewById(R.id.filter_layout);
        Button filter = (Button) toolbar.findViewById(R.id.filter_button);
        filter.setText(getString(R.string.filter));
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filterLayout.getVisibility() == View.VISIBLE) {
                    filterLayout.setVisibility(View.GONE);
                } else {
                    filterLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        startDateFilter = (TextView) findViewById(R.id.filter_start_date);
        startDateFilter.setOnClickListener(selectDateChooserClickListener);

        endDateFilter = (TextView) findViewById(R.id.filter_end_date);
        endDateFilter.setOnClickListener(selectDateChooserClickListener);

        findViewById(R.id.filter_submit_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mFilterStartDateEntered) {
                    //      Crouton.makeText(ReportActivity.this, "Start date cannot be empty.", Style.ALERT, R.id.filter_layout).setConfiguration(Constants.CONFIGURATION_SHORT).show();
                    showSnackBar("Start Date cannot be empty.");
                } else if (!mFilterEndDateEntered) {
                    //             Crouton.makeText(ReportActivity.this, "End date cannot be empty.", Style.ALERT, R.id.filter_layout).setConfiguration(Constants.CONFIGURATION_SHORT).show();
                    showSnackBar("End Date cannot be empty.");
                } else if (mFilterStartDateEntered && mFilterEndDateEntered) {
                    //submmit api call
                }
            }
        });

        filterDay = (Button) findViewById(R.id.button_day);
        filterWeek = (Button) findViewById(R.id.button_week);
        filterMonth = (Button) findViewById(R.id.button_month);
        filterAll = (Button) findViewById(R.id.button_all);
        filterBar = (Button) findViewById(R.id.button_bar);
        filterLine = (Button) findViewById(R.id.button_line);
        filterPie = (Button) findViewById(R.id.button_pie);

        barChart = (HorizontalBarChart) findViewById(R.id.barChart);
        lineChart = (LineChart) findViewById(R.id.lineChart);
        pieChart = (PieChart) findViewById(R.id.pieChart);

        barChart.setOnTouchListener(onMapTouchListener);
        lineChart.setOnTouchListener(onMapTouchListener);
        pieChart.setOnTouchListener(onMapTouchListener);

        setOnClickListenersForButtons();
        setBackgroundForButton(filterDay, true);
        setBackgroundForButton(filterBar, true);
        setVisibilityForButton(filterAll, false);
        setDataInChart(FLAG_FILTER_DAY);
    }

    View.OnTouchListener onMapTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (filterLayout.getVisibility() == View.VISIBLE)
                filterLayout.setVisibility(View.GONE);

            return false;
        }
    };
    View.OnClickListener selectDateChooserClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            final Calendar c = Calendar.getInstance();
            final int mYear = c.get(Calendar.YEAR);
            final int mMonth = c.get(Calendar.MONTH);
            final int mDay = c.get(Calendar.DAY_OF_MONTH);

            final View textView = v;
            DatePickerDialog datePicker = new DatePickerDialog(ReportActivity.this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                            switch (textView.getId()) {
                                case R.id.filter_start_date:
                                    startDateFilter.setError(null);
                                    mFilterStartDateEntered = true;
                                    startDateFilter.setText(dayOfMonth + " - "
                                            + (monthOfYear + 1) + " - " + year);
                                    startDate = new Date(year - 1900, monthOfYear, dayOfMonth);
                                    break;
                                case R.id.filter_end_date:
                                    if (mFilterStartDateEntered) {
                                        endDateFilter.setError(null);
                                        mFilterEndDateEntered = true;
                                        endDateFilter.setText(dayOfMonth + " - "
                                                + (monthOfYear + 1) + " - " + year);
                                    } else {
                                        showSnackBar("Start Date cannot be empty.");
                                    }

                                    break;
                            }
                        }

                    }, mYear, mMonth, mDay);
            datePicker.show();
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);

            if (textView.getId() == R.id.filter_end_date && mFilterStartDateEntered) {
                datePicker.getDatePicker().setMinDate(startDate.getTime());
                datePicker.updateDate(startDate.getYear(), startDate.getMonth(), startDate.getDay());
            }
        }
    };

    View.OnClickListener viewByButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            resetAllViewByBackgroundsToDefault();
            switch (v.getId()) {
                case R.id.button_day:
                    setBackgroundForButton(filterDay, true);
                    setDataInChart(FLAG_FILTER_DAY);
                    break;
                case R.id.button_week:
                    setBackgroundForButton(filterWeek, true);
                    setDataInChart(FLAG_FILTER_WEEK);
                    break;
                case R.id.button_month:
                    setBackgroundForButton(filterMonth, true);
                    setDataInChart(FLAG_FILTER_MONTH);
                    break;
                case R.id.button_all:
                    setBackgroundForButton(filterAll, true);
                    break;
            }
        }
    };

    View.OnClickListener chartTypeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            resetAllChartTypeBackgroundsToDefault();
            barChart.setVisibility(View.GONE);
            lineChart.setVisibility(View.GONE);
            pieChart.setVisibility(View.GONE);
            switch (v.getId()) {
                case R.id.button_bar:
                    setBackgroundForButton(filterBar, true);
                    setVisibilityForButton(filterDay, true);
                    setVisibilityForButton(filterWeek, true);
                    setVisibilityForButton(filterMonth, true);
                    setVisibilityForButton(filterAll, false);

                    barChart.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            barChart.animateY(2000);
                        }
                    }, 100);
                    break;
                case R.id.button_line:
                    setBackgroundForButton(filterLine, true);
                    setVisibilityForButton(filterDay, true);
                    setVisibilityForButton(filterWeek, true);
                    setVisibilityForButton(filterMonth, true);
                    setVisibilityForButton(filterAll, false);

                    lineChart.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lineChart.animateX(2000);
                        }
                    }, 100);
                    break;
                case R.id.button_pie:
                    setBackgroundForButton(filterPie, true);
                    setVisibilityForButton(filterDay, false);
                    setVisibilityForButton(filterWeek, false);
                    setVisibilityForButton(filterMonth, false);
                    setVisibilityForButton(filterAll, true);
                    setBackgroundForButton(filterAll, true);

                    pieChart.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pieChart.animateY(2000);
                        }
                    }, 100);
                    break;
            }
        }
    };

    private void setOnClickListenersForButtons() {
        filterDay.setOnClickListener(viewByButtonListener);
        filterWeek.setOnClickListener(viewByButtonListener);
        filterMonth.setOnClickListener(viewByButtonListener);
        filterAll.setOnClickListener(viewByButtonListener);

        filterBar.setOnClickListener(chartTypeButtonListener);
        filterLine.setOnClickListener(chartTypeButtonListener);
        filterPie.setOnClickListener(chartTypeButtonListener);
    }

    private void resetAllViewByBackgroundsToDefault() {
        setBackgroundForButton(filterDay, false);
        setBackgroundForButton(filterWeek, false);
        setBackgroundForButton(filterMonth, false);
        setBackgroundForButton(filterAll, false);
    }

    private void resetAllChartTypeBackgroundsToDefault() {
        setBackgroundForButton(filterBar, false);
        setBackgroundForButton(filterLine, false);
        setBackgroundForButton(filterPie, false);
    }

    private void setVisibilityForButton(Button button, boolean isVisible) {
        if (isVisible)
            button.setVisibility(View.VISIBLE);
        else
            button.setVisibility(View.GONE);
    }

    private void setBackgroundForButton(Button button, boolean isSelected) {
        if (isSelected) {
            if (android.os.Build.VERSION.SDK_INT >= 16)
                button.setBackground(ContextCompat.getDrawable(this, R.drawable.filter_button_background_selected));
            else
                button.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.filter_button_background_selected));
        } else {
            if (android.os.Build.VERSION.SDK_INT >= 16)
                button.setBackground(ContextCompat.getDrawable(this, R.drawable.filter_button_background));
            else
                button.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.filter_button_background));
        }
    }

    private void setDataInPieChart(ReportData l) {

        ArrayList<Entry> listForPieChart = new ArrayList<>();
        ArrayList<String> xValsForPieChart = new ArrayList<>();

        float successTotal = l.getSuccess();
        float failedTotal = l.getFailed();
        float droppedTotal = l.getDropped();
        float bouncedTotal = l.getBounced();
        float userCancelledTotal = l.getUserCancelled();
        float pendingTotal = l.getPending();

        listForPieChart.add(new Entry(successTotal, 0));
        listForPieChart.add(new Entry(failedTotal, 1));
        listForPieChart.add(new Entry(pendingTotal, 2));
        listForPieChart.add(new Entry(droppedTotal, 3));
        listForPieChart.add(new Entry(userCancelledTotal, 4));
        listForPieChart.add(new Entry(bouncedTotal, 5));

        PieDataSet setPie1 = new PieDataSet(listForPieChart, "");
        setPie1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setPie1.setColors(colors);

        xValsForPieChart.add("Success");
        xValsForPieChart.add("Failed");
        xValsForPieChart.add("Pending ");
        xValsForPieChart.add("Dropped ");
        xValsForPieChart.add("User Cancelled");
        xValsForPieChart.add("Bounced");

        PieData mData3 = new PieData(xValsForPieChart, setPie1);
        pieChart.setData(mData3);
        pieChart.setHoleRadius(20f);
        pieChart.setTransparentCircleRadius(25f);
        pieChart.animateY(3000);
    }

    private void setDataInBarChart(List<ReportData> list, boolean isDaySelected) {


        ArrayList<BarEntry> barComp1 = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            float tempSuccess = list.get(i).getSuccess();
            float tempFailure = list.get(i).getFailed();
            float tempDropped = list.get(i).getDropped();
            float tempBounced = list.get(i).getBounced();
            float tempUserCancelled = list.get(i).getUserCancelled();
            float tempPending = list.get(i).getPending();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");
            String date = null;
            String toDate = null;
            try {
                if (isDaySelected) {
                    Date date1 = dateFormat.parse(list.get(i).getMinDate());
                    date = (String) android.text.format.DateFormat.format("dd-MMM ''yy", date1);
                } else {
                    Date date1 = dateFormat.parse(list.get(i).getMinDate());
                    date = (String) android.text.format.DateFormat.format("dd", date1);
                    Date date2 = dateFormat.parse(list.get(i).getMaxDate());
                    toDate = (String) android.text.format.DateFormat.format("dd-MMM ''yy", date2);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (isDaySelected)
                xVals.add(date);
            else
                xVals.add("" + date + " - " + toDate);


            BarEntry tempBarEntry = new BarEntry(new float[]{tempSuccess, tempFailure, tempDropped, tempBounced, tempUserCancelled, tempPending}, i);
            barComp1.add(tempBarEntry);
        }

        BarDataSet setBar1 = new BarDataSet(barComp1, "");
        setBar1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setBar1.setColors(colors);
        setBar1.setStackLabels(new String[]{"Success", "Failed", "Dropped", "Bounced", "User Cancelled", "Pending"});
        setBar1.setValueTextSize(0f);
        ArrayList<IBarDataSet> dataSet2 = new ArrayList<>();
        dataSet2.add(setBar1);
        BarData mData2 = new BarData(xVals, dataSet2);
        barChart.getXAxis().setTextSize(2f);
        barChart.setData(mData2);
        barChart.animateY(2000);
    }

    private void setDataInLineChart(List<ReportData> l, boolean isDaySelected) {

        ArrayList<Entry> listSuccess = new ArrayList<>();
        ArrayList<Entry> listFailed = new ArrayList<>();
        ArrayList<Entry> listDropped = new ArrayList<>();
        ArrayList<Entry> listBounced = new ArrayList<>();
        ArrayList<Entry> listUserCancelled = new ArrayList<>();
        ArrayList<Entry> listOther = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();

        List<ReportData> list = l;
        for (int i = 0; i < list.size(); i++) {

            ReportData temp = list.get(i);

            float tempSuccessValue = temp.getSuccess();
            Entry success = new Entry(tempSuccessValue, i);
            listSuccess.add(success);

            float tempFailedValue = temp.getFailed();
            Entry failed = new Entry(tempFailedValue, i);
            listFailed.add(failed);

            float tempDroppedValue = temp.getDropped();
            Entry dropped = new Entry(tempDroppedValue, i);
            listDropped.add(dropped);

            float tempBouncedValue = temp.getBounced();
            Entry bounced = new Entry(tempBouncedValue, i);
            listBounced.add(bounced);

            float tempUserCancelledValue = temp.getUserCancelled();
            Entry userCancelled = new Entry(tempUserCancelledValue, i);
            listUserCancelled.add(userCancelled);

            float tempPendingValue = temp.getPending();
            Entry others = new Entry(tempPendingValue, i);
            listOther.add(others);


            SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");
            String date = null;
            String toDate = null;
            try {

                if (isDaySelected) {
                    Date date1 = dateFormat.parse(temp.getMinDate());
                    date = (String) android.text.format.DateFormat.format("dd-MMM ''yy", date1);
                } else {
                    Date date1 = dateFormat.parse(temp.getMinDate());
                    date = (String) android.text.format.DateFormat.format("dd", date1);
                    Date date2 = dateFormat.parse(temp.getMaxDate());
                    toDate = (String) android.text.format.DateFormat.format("dd-MMM ''yy", date2);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (isDaySelected)
                xVals.add(date);
            else
                xVals.add("" + date + " - " + toDate);

        }
        LineDataSet setComp1 = new LineDataSet(listSuccess, "Success");
        LineDataSet setComp2 = new LineDataSet(listFailed, "Failed");
        LineDataSet setComp3 = new LineDataSet(listDropped, "Dropped");
        LineDataSet setComp4 = new LineDataSet(listBounced, "Bounced");
        LineDataSet setComp5 = new LineDataSet(listUserCancelled, "User Cancelled");
        LineDataSet setComp6 = new LineDataSet(listOther, "Others");

        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setCircleColor(ContextCompat.getColor(this, R.color.black));
        setComp1.setColor(colors[0]);
        setComp1.setLineWidth(2);

        setComp2.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp2.setCircleColor(ContextCompat.getColor(this, R.color.black));
        setComp2.setColor(colors[1]);
        setComp2.setLineWidth(2);

        setComp3.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp3.setCircleColor(ContextCompat.getColor(this, R.color.black));
        setComp3.setColor(colors[2]);
        setComp3.setLineWidth(2);

        setComp4.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp4.setCircleColor(ContextCompat.getColor(this, R.color.black));
        setComp4.setColor(colors[3]);
        setComp4.setLineWidth(2);

        setComp5.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp5.setCircleColor(ContextCompat.getColor(this, R.color.black));
        setComp5.setColor(colors[4]);
        setComp5.setLineWidth(2);

        setComp6.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp6.setCircleColor(ContextCompat.getColor(this, R.color.black));
        setComp6.setColor(colors[5]);
        setComp6.setLineWidth(2);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(setComp1);
        dataSets.add(setComp2);
        dataSets.add(setComp3);
        dataSets.add(setComp4);
        dataSets.add(setComp5);
        dataSets.add(setComp6);

        LineData mData = new LineData(xVals, dataSets);
        lineChart.setData(mData);
        lineChart.animateX(2000);

    }

    public void setDataInChart(int flag) {
        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset());
            reportsResults = (ReportResults) Session.getInstance(this).getParsedResponseFromGSON(jsonObject, Session.dataType.ReportResults);

            ReportData mOverall = reportsResults.getDisplayReportResult().getOverall();
            setDataInPieChart(mOverall);

            switch (flag) {
                case FLAG_FILTER_DAY:
                    setDataInChartByDay();
                    break;

                case FLAG_FILTER_WEEK:
                    setDataInChartByWeek();
                    break;

                case FLAG_FILTER_MONTH:
                    setDataInChartByMonth();
                    break;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setDataInChartByDay() {

        List<ReportData> mDay = reportsResults.getDisplayReportResult().getDay();
        setDataInLineChart(mDay, true);
        setDataInBarChart(mDay, true);
    }

    public void setDataInChartByWeek() {
        List<ReportData> mWeek = reportsResults.getDisplayReportResult().getWeek();
        setDataInLineChart(mWeek, false);
        setDataInBarChart(mWeek, false);

    }

    public void setDataInChartByMonth() {
        List<ReportData> mMonth = reportsResults.getDisplayReportResult().getMonth();
        setDataInLineChart(mMonth, false);
        setDataInBarChart(mMonth, false);

    }

    public String loadJSONFromAsset() {

        String json;
        try {
            InputStream is = this.getAssets().open("reportData.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void showSnackBar(String snackbarText) {
        Snackbar snack = Snackbar.make(findViewById(R.id.report_layout), snackbarText, Snackbar.LENGTH_SHORT);
        View view = snack.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(ReportActivity.this, android.R.color.white));
        snack.show();
    }
}
