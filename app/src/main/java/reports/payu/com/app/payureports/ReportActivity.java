package reports.payu.com.app.payureports;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
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
import java.util.TimeZone;

import reports.payu.com.app.payureports.Model.ReportData;
import reports.payu.com.app.payureports.Model.ReportResults;

/**
 * Created by shruti.vig on 5/4/16.
 */
public class ReportActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private final int FLAG_FILTER_DAY = 0;
    private final int FLAG_FILTER_WEEK = 1;
    private final int FLAG_FILTER_MONTH = 2;
    private GoogleApiClient mGoogleApiClient;

    private HorizontalBarChart barChart;
    private ReportResults reportsResults;
    private LineChart lineChart;
    private PieChart pieChart;
    private int[] colors = new int[]{Color.rgb(28, 148, 36), Color.rgb(217, 58, 33), Color.rgb(253, 152, 39), Color.rgb(54, 105, 201),
            Color.rgb(151, 20, 151), Color.rgb(24, 153, 196)};
    private TextView startDateFilter;
    private TextView endDateFilter;
    private Calendar startDate, endDate;
    private boolean mFilterStartDateEntered = false;
    private boolean mFilterEndDateEntered = false;
    private Button filterDay, filterWeek, filterMonth, filterAll, filterBar, filterLine, filterPie;
    private LinearLayout filterLayout;
    ProgressDialog ringProgressDialog;
    private String reportId, email;
    private String submitStartDate, submitEndDate;
    private List<ReportData> mDay, mWeek, mMonth;
    private ReportData mOverall;

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ringProgressDialog = ProgressDialog.show(this, "Please wait ...", "Fetching Data", true);
        ringProgressDialog.setCancelable(true);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView titleText = (TextView) toolbar.findViewById(R.id.title);

        if (getIntent() != null) {
            reportId = getIntent().getStringExtra(Constants.REPORT_ID);
            email = getIntent().getStringExtra(Constants.EMAIL);
            titleText.setText(getIntent().getStringExtra(Constants.REPORT_NAME));
        }

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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

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
                    JSONObject dateJson = new JSONObject();
                    try {
                        dateJson.put(Constants.START_DATE, submitStartDate);
                        dateJson.put(Constants.END_DATE, submitEndDate);
                    } catch (JSONException e) {

                    }
                    if (!ringProgressDialog.isShowing()) {
                        ringProgressDialog.show();
                    }
                    fetchReportData(dateJson);
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
        fetchReportData(null);
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

                                    mFilterStartDateEntered = true;

                                    startDateFilter.setText(dayOfMonth + " - "
                                            + (monthOfYear + 1) + " - " + year);
                                    submitStartDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

                                    if (mFilterEndDateEntered) {
                                        endDate = null;
                                        mFilterEndDateEntered = false;
                                        endDateFilter.setText("");
                                        submitEndDate = null;
                                    }

                                    startDate = Calendar.getInstance();
                                    startDate.set(Calendar.YEAR, year);
                                    startDate.set(Calendar.MONTH, monthOfYear);
                                    startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                                    break;
                                case R.id.filter_end_date:
                                    if (mFilterStartDateEntered) {

                                        endDate = Calendar.getInstance();
                                        endDate.set(Calendar.YEAR, year);
                                        endDate.set(Calendar.MONTH, monthOfYear);
                                        endDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                        if (startDate.getTimeInMillis() > endDate.getTimeInMillis()) {
                                            showSnackBar("End Date cannot be before Start Date.");
                                            endDate = null;
                                            mFilterEndDateEntered = false;
                                            endDateFilter.setText("");
                                            submitEndDate = null;
                                        } else {
                                            mFilterEndDateEntered = true;
                                            endDateFilter.setText(dayOfMonth + " - "
                                                    + (monthOfYear + 1) + " - " + year);
                                            submitEndDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                        }
                                    } else {
                                        showSnackBar("Start Date cannot be empty.");
                                    }

                                    break;
                            }
                        }

                    }, mYear, mMonth, mDay);
            datePicker.show();
            Calendar minDateToBeSelected = Calendar.getInstance();
            minDateToBeSelected.set(Calendar.YEAR, 2014);
            minDateToBeSelected.set(Calendar.MONTH, 3);
            minDateToBeSelected.set(Calendar.DAY_OF_MONTH, 25);
            datePicker.getDatePicker().setMinDate(minDateToBeSelected.getTimeInMillis());
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);


            switch (textView.getId()) {
                case R.id.filter_start_date:
                    if (mFilterStartDateEntered && startDate != null)
                        datePicker.updateDate(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
                    break;
                case R.id.filter_end_date:
                    if (mFilterEndDateEntered && endDate != null)
                        datePicker.updateDate(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));
                    break;

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

                    setDataInPieChart(mOverall);
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

    private void fetchReportData(JSONObject duration) {

        Session.getInstance(this).fetchReportData(email, reportId, duration);
    }

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

    private void resetVisibilityForAllButtons() {
        setVisibilityForButton(filterDay, false);
        setVisibilityForButton(filterWeek, false);
        setVisibilityForButton(filterMonth, false);
        setVisibilityForButton(filterAll, false);
        initializeButtonsVisibilityAsPerData();

    }

    private void initializeButtonsVisibilityAsPerData() {

        if (mDay != null)
            filterDay.setVisibility(View.VISIBLE);
        if (mWeek != null)
            filterWeek.setVisibility(View.VISIBLE);
        if (mMonth != null)
            filterMonth.setVisibility(View.VISIBLE);
        if (mDay == null && mWeek == null && mMonth == null) {
            filterBar.setVisibility(View.GONE);
            filterLine.setVisibility(View.GONE);
        } else {
            filterBar.setVisibility(View.VISIBLE);
            filterLine.setVisibility(View.VISIBLE);
        }
        if (mOverall == null) {
            pieChart.setVisibility(View.GONE);
        }

    }

    private void setVisibilityForButton(Button button, boolean isVisible) {


        if (isVisible) {
            switch (button.getId()) {
                case R.id.button_day:
                    if (mDay != null)
                        button.setVisibility(View.VISIBLE);
                    break;
                case R.id.button_week:
                    if (mWeek != null)
                        button.setVisibility(View.VISIBLE);
                    break;
                case R.id.button_month:
                    if (mMonth != null)
                        button.setVisibility(View.VISIBLE);
                    break;
                case R.id.button_all:
                    if (mOverall != null)
                        button.setVisibility(View.VISIBLE);
                    break;
            }
        } else
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
        setPie1.setValueTextSize(5f);

        xValsForPieChart.add("Success");
        xValsForPieChart.add("Failed");
        xValsForPieChart.add("Dropped");
        xValsForPieChart.add("Bounced");
        xValsForPieChart.add("User Cancelled");
        xValsForPieChart.add("Others");

        PieData mData3 = new PieData(xValsForPieChart, setPie1);
        mData3.setValueTextSize(5f);
        pieChart.setData(mData3);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");
        try {
            Date fromDate = dateFormat.parse(mOverall.getMinDate());
            Date toDate = dateFormat.parse(mOverall.getMaxDate());
            String from = (String) android.text.format.DateFormat.format("dd-MMM ''yy", fromDate);
            String to = (String) android.text.format.DateFormat.format("dd-MMM ''yy", toDate);
            pieChart.setDescription(from + " to " + to);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        pieChart.setDescriptionTextSize(20f);
        pieChart.setHoleRadius(24f);
        pieChart.setTransparentCircleRadius(27f);
        pieChart.animateY(2000);
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
        setBar1.setStackLabels(new String[]{"Success", "Failed", "Dropped", "Bounced", "User Cancelled", "Others"});
        setBar1.setValueTextSize(0f);
        ArrayList<IBarDataSet> dataSet2 = new ArrayList<>();
        dataSet2.add(setBar1);
        BarData mData2 = new BarData(xVals, dataSet2);
        barChart.getXAxis().setTextSize(2f);
        barChart.setData(mData2);
        barChart.setDescription("");
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
        lineChart.setDescription("");
        lineChart.animateX(2000);

    }

    public void setVisibilityOfButtons() {

        resetVisibilityForAllButtons();
        filterBar.callOnClick();

        if (mDay == null) {
            if (mWeek != null)
                filterWeek.callOnClick();
            else if (mMonth != null)
                filterMonth.callOnClick();
            else if (mOverall != null)
                filterPie.callOnClick();
        } else
            filterDay.callOnClick();

    }

    public void setDataInChart(int flag) {

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
            default:
                break;

        }
    }

    public void setDataInChartByDay() {

        if (mDay != null) {
            setDataInLineChart(mDay, true);
            setDataInBarChart(mDay, true);
        }
    }

    public void setDataInChartByWeek() {

        if (mWeek != null) {
            setDataInLineChart(mWeek, false);
            setDataInBarChart(mWeek, false);
        }
    }

    public void setDataInChartByMonth() {

        if (mMonth != null) {
            setDataInLineChart(mMonth, false);
            setDataInBarChart(mMonth, false);
        }

    }

    private void showSnackBar(String snackbarText) {
        Snackbar snack = Snackbar.make(findViewById(R.id.report_layout), snackbarText, Snackbar.LENGTH_SHORT);
        View view = snack.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(ReportActivity.this, android.R.color.white));
        snack.show();
    }

    @Subscribe
    public void onEventMainThread(CobbocEvent event) {
        switch (event.getType()) {
            case CobbocEvent.REPORT:

                if (event.getStatus()) {

                    JSONObject jsonObject = (JSONObject) event.getValue();
                    reportsResults = (ReportResults) Session.getInstance(this).getParsedResponseFromGSON(jsonObject, Session.dataType.ReportResults);
                    mOverall = reportsResults.getDisplayReportResult().getOverall();
                    mDay = reportsResults.getDisplayReportResult().getDay();
                    mWeek = reportsResults.getDisplayReportResult().getWeek();
                    mMonth = reportsResults.getDisplayReportResult().getMonth();

                    setVisibilityOfButtons();
                    if (ringProgressDialog.isShowing())
                        ringProgressDialog.dismiss();

                } else {
                    if (ringProgressDialog.isShowing())
                        ringProgressDialog.dismiss();
                    if (event.getValue().toString().contains("Server error")) {
                        handleStatus("XYZ", event.getValue().toString());
                    } else {
                        JSONObject temp = (JSONObject) event.getValue();
                        String errorCode = temp.optString(Constants.ERROR_CODE, "XYZ");
                        String errorMessage = temp.optString(Constants.MESSAGE, "XYZ");
                        handleStatus(errorCode, errorMessage);
                    }
                }
                break;

            default:
        }
    }

    private void handleStatus(String errorCode, String errorMessage) {

        switch (errorCode) {
            case "ER101":
                Toast.makeText(this, "Login Unsuccessful!", Toast.LENGTH_SHORT).show();
                logoutUser();
                break;
            case "ER102":
                Toast.makeText(this, "Login Unsuccessful!", Toast.LENGTH_SHORT).show();
                logoutUser();
                break;
            case "ER103":
                Toast.makeText(this, "Login Unsuccessful!", Toast.LENGTH_SHORT).show();
                logoutUser();
                break;
            case "ER104":
                Toast.makeText(this, "Login Unsuccessful!", Toast.LENGTH_SHORT).show();
                logoutUser();
                break;
            case "ER105":
                Toast.makeText(this, "Login Unsuccessful!", Toast.LENGTH_SHORT).show();
                logoutUser();
                break;
            case "ER106":
                Toast.makeText(this, "Login Unsuccessful!", Toast.LENGTH_SHORT).show();
                logoutUser();
                break;
            case "ER107":
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                break;
            //logoutUser();
            default:
                Toast.makeText(this, "Login Unsuccessful!", Toast.LENGTH_SHORT).show();
                logoutUser();
                break;
        }


    }

    private void logoutUser() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        launchLoginSignupActivity();
                    }
                });
    }

    private void launchLoginSignupActivity() {

        Intent i = new Intent(this, LoginSignUpActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
