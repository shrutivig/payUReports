package reports.payu.com.app.payureports;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
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
import com.github.mikephil.charting.utils.ColorTemplate;

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

import reports.payu.com.app.payureports.Model.DisplayReportResults;
import reports.payu.com.app.payureports.Model.ReportData;
import reports.payu.com.app.payureports.Model.ReportResults;

/**
 * Created by shruti.vig on 5/4/16.
 */
public class ReportActivity extends HomeActivity {

    private HorizontalBarChart barChart;
    private ReportResults reportsResults;
    private LineChart lineChart;
    private PieChart pieChart;
    private int[] colors = new int[]{Color.rgb(106, 150, 31), Color.rgb(193, 37, 82), Color.rgb(245, 199, 0), Color.rgb(255, 102, 0),
            Color.rgb(179, 100, 53), Color.rgb(255, 32, 34)};
    private TextView startDateFilter;
    private TextView endDateFilter;
    private Date START_DATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final LinearLayout filterLayout = (LinearLayout) findViewById(R.id.filter_layout);
        Button filter = (Button) toolbar.findViewById(R.id.sign_out_button);
        filter.setText(getString(R.string.filter));
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filterLayout.getVisibility() == View.VISIBLE)
                    filterLayout.setVisibility(View.GONE);
                else
                    filterLayout.setVisibility(View.VISIBLE);
            }
        });

        startDateFilter = (TextView) findViewById(R.id.filter_start_date);
        startDateFilter.setOnClickListener(selectDateChooserClickListener);

        endDateFilter = (TextView) findViewById(R.id.filter_end_date);
        endDateFilter.setOnClickListener(selectDateChooserClickListener);

        barChart = (HorizontalBarChart) findViewById(R.id.barChart);
        lineChart = (LineChart) findViewById(R.id.lineChart);
        pieChart = (PieChart) findViewById(R.id.pieChart);
        setDataInChart();
    }

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
                                    startDateFilter.setText(dayOfMonth + " - "
                                            + (monthOfYear + 1) + " - " + year);
                                    START_DATE = new Date(year - 1900, monthOfYear, dayOfMonth);
                                    break;
                                case R.id.filter_end_date:
                                    endDateFilter.setError(null);
                                    endDateFilter.setText(dayOfMonth + " - "
                                            + (monthOfYear + 1) + " - " + year);
                                    break;
                            }
                        }

                    }, mYear, mMonth, mDay);
            datePicker.show();
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);

            if (textView.getId() == R.id.filter_end_date) {
                datePicker.getDatePicker().setMinDate(START_DATE.getTime());
                datePicker.updateDate(START_DATE.getYear(), START_DATE.getMonth(), START_DATE.getDay());
            }
        }
    };

    private void setDataInPieChart(ReportData l) {
        /*Pie Chart Start*/
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
        pieChart.animateY(3000);
        /*Pie Chart End*/
    }

    private void setDataInBarChart(List<ReportData> list) {


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
                Date date1 = dateFormat.parse(list.get(i).getMinDate());
                Date date2 = dateFormat.parse(list.get(i).getMaxDate());
                date = (String) android.text.format.DateFormat.format("dd", date1);
                toDate = (String) android.text.format.DateFormat.format("dd-MMM ''yy", date2);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            xVals.add("" + date + " - " + toDate);

            BarEntry tempBarEntry = new BarEntry(new float[]{tempSuccess, tempFailure, tempDropped, tempBounced, tempUserCancelled, tempPending}, i);
            barComp1.add(tempBarEntry);
        }

        BarDataSet setBar1 = new BarDataSet(barComp1, "");
        setBar1.setAxisDependency(YAxis.AxisDependency.LEFT);
// setBar1.setColors(ColorTemplate.PASTEL_COLORS);
        setBar1.setColors(new int[]{Color.rgb(106, 150, 31), Color.rgb(193, 37, 82), Color.rgb(245, 199, 0), Color.rgb(255, 102, 0),
                Color.rgb(179, 100, 53), Color.rgb(0, 0, 0)});
        setBar1.setStackLabels(new String[]{"Success", "Failed", "Dropped", "Bounced", "User Cancelled", "Pending"});
        setBar1.setValueTextSize(0f);
        ArrayList<IBarDataSet> dataSet2 = new ArrayList<>();
        dataSet2.add(setBar1);
        BarData mData2 = new BarData(xVals, dataSet2);
        barChart.getXAxis().setTextSize(2f);
        barChart.setData(mData2);
        barChart.animateY(2000);
    }

    private void setDataInLineChart(List<ReportData> l) {

        /*Line Chart Start*/
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
                Date date1 = dateFormat.parse(temp.getMinDate());
                Date date2 = dateFormat.parse(temp.getMaxDate());
                date = (String) android.text.format.DateFormat.format("dd", date1);
                toDate = (String) android.text.format.DateFormat.format("dd-MMM ''yy", date2);
            } catch (ParseException e) {
                e.printStackTrace();
            }

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
        lineChart.animateY(2000);
        /*Line Chart End*/

    }

    public void setDataInChart() {
        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset());
            reportsResults = (ReportResults) Session.getInstance(this).getParsedResponseFromGSON(jsonObject, Session.dataType.ReportResults);
            List<ReportData> mDay = reportsResults.getDisplayReportResult().getDay();
            List<ReportData> mWeek = reportsResults.getDisplayReportResult().getWeek();
            List<ReportData> mMonth = reportsResults.getDisplayReportResult().getMonth();
            ReportData mOverall = reportsResults.getDisplayReportResult().getOverall();
            setDataInPieChart(mOverall);
            setDataInLineChart(mDay);
            setDataInBarChart(mDay);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.report_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        barChart.setVisibility(View.GONE);
        lineChart.setVisibility(View.GONE);
        pieChart.setVisibility(View.GONE);
        switch (item.getItemId()) {

            case R.id.bar:
                barChart.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        barChart.animateY(2000);
                    }
                }, 100);
                return true;
            case R.id.line:
                lineChart.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lineChart.animateX(2000);
                    }
                }, 100);
                return true;
            case R.id.pie:
                pieChart.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pieChart.animateY(2000);
                    }
                }, 100);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
