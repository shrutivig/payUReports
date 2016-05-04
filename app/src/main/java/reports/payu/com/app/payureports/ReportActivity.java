package reports.payu.com.app.payureports;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.github.mikephil.charting.charts.BarChart;
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
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import reports.payu.com.app.payureports.Model.ReportData;
import reports.payu.com.app.payureports.Model.ReportResults;

/**
 * Created by shruti.vig on 5/4/16.
 */
public class ReportActivity extends HomeActivity {

    private BarChart barChart;
    private ReportResults reportsResults;
    private LineChart lineChart;
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        barChart = (BarChart) findViewById(R.id.barChart);
        lineChart = (LineChart) findViewById(R.id.lineChart);
        pieChart = (PieChart) findViewById(R.id.pieChart);
        setDataInChart();
    }


    public void setDataInChart() {

        ArrayList<Entry> listSuccess = new ArrayList<>();
        ArrayList<Entry> listFailed = new ArrayList<>();
        ArrayList<Entry> listDropped = new ArrayList<>();
        ArrayList<Entry> listBounced = new ArrayList<>();
        ArrayList<Entry> listUserCancelled = new ArrayList<>();
        ArrayList<Entry> listOther = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();

        ArrayList<Entry> listForPieChart = new ArrayList<>();
        double successTotal = 0.0;
        double failedTotal = 0.0;
        double droppedTotal = 0.0;
        double bouncedTotal = 0.0;
        double userCancelledTotal = 0.0;
        double pendingTotal = 0.0;
        ArrayList<String> xValsForPieChart = new ArrayList<>();
        /*Pie Chart End*/

        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset());
            reportsResults = (ReportResults) Session.getInstance(this).getParsedResponseFromGSON(jsonObject, Session.dataType.ReportResults);

            /*Line Chart Start*/
            List<ReportData> list = reportsResults.getList();
            for (int i = 0; i < list.size(); i++) {

                ReportData temp = list.get(i);

                float tempSuccessValue = temp.getSuccess();
                successTotal += tempSuccessValue;
                Entry success = new Entry(tempSuccessValue, i);
                listSuccess.add(success);

                float tempFailedValue = temp.getFailed();
                failedTotal += tempFailedValue;
                Entry failed = new Entry(tempFailedValue, i);
                listFailed.add(failed);

                float tempDroppedValue = temp.getDropped();
                droppedTotal += tempDroppedValue;
                Entry dropped = new Entry(tempDroppedValue, i);
                listDropped.add(dropped);

                float tempBouncedValue = temp.getBounced();
                bouncedTotal += tempBouncedValue;
                Entry bounced = new Entry(tempBouncedValue, i);
                listBounced.add(bounced);

                float tempUserCancelledValue = temp.getUserCancelled();
                userCancelledTotal += tempUserCancelledValue;
                Entry userCancelled = new Entry(tempUserCancelledValue, i);
                listUserCancelled.add(userCancelled);

                float tempPendingValue = temp.getPending();
                pendingTotal += tempPendingValue;
                Entry others = new Entry(tempPendingValue, i);
                listOther.add(others);

                xVals.add("" + temp.getMinDate() + " to " + temp.getMaxDate());

            }
            LineDataSet setComp1 = new LineDataSet(listSuccess, "Success");
            LineDataSet setComp2 = new LineDataSet(listFailed, "Failed");
            LineDataSet setComp3 = new LineDataSet(listDropped, "Dropped");
            LineDataSet setComp4 = new LineDataSet(listBounced, "Bounced");
            LineDataSet setComp5 = new LineDataSet(listUserCancelled, "User Cancelled");
            LineDataSet setComp6 = new LineDataSet(listOther, "Others");

            setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
            setComp1.setCircleColor(ContextCompat.getColor(this, R.color.black));
            setComp1.setColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
            setComp1.setLineWidth(2);

            setComp2.setAxisDependency(YAxis.AxisDependency.LEFT);
            setComp2.setCircleColor(ContextCompat.getColor(this, R.color.black));
            setComp2.setColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            setComp2.setLineWidth(2);

            setComp3.setAxisDependency(YAxis.AxisDependency.LEFT);
            setComp3.setCircleColor(ContextCompat.getColor(this, R.color.black));
            setComp3.setColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
            setComp3.setLineWidth(2);

            setComp4.setAxisDependency(YAxis.AxisDependency.LEFT);
            setComp4.setCircleColor(ContextCompat.getColor(this, R.color.black));
            setComp4.setColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark));
            setComp4.setLineWidth(2);

            setComp5.setAxisDependency(YAxis.AxisDependency.LEFT);
            setComp5.setCircleColor(ContextCompat.getColor(this, R.color.black));
            setComp5.setColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            setComp5.setLineWidth(2);

            setComp6.setAxisDependency(YAxis.AxisDependency.LEFT);
            setComp6.setCircleColor(ContextCompat.getColor(this, R.color.black));
            setComp6.setColor(ContextCompat.getColor(this, android.R.color.black));
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

            ArrayList<BarEntry> barComp1 = new ArrayList<>();

            for (int i = 0; i < list.size(); i++) {
                float tempSuccess = list.get(i).getSuccess();
                float tempFailure = list.get(i).getFailed();
                float tempDropped = list.get(i).getDropped();
                float tempBounced = list.get(i).getBounced();
                float tempUserCancelled = list.get(i).getUserCancelled();
                float tempPending = list.get(i).getPending();

                BarEntry tempBarEntry = new BarEntry(new float[]{tempSuccess, tempFailure, tempDropped, tempBounced, tempUserCancelled, tempPending}, i);
                barComp1.add(tempBarEntry);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss.SSSSSS'Z'");
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                Date date = null;
                try {
                    date = dateFormat.parse(list.get(i).getMinDate());
                } catch (ParseException e) {
                    e.printStackTrace();

                }
            }

            BarDataSet setBar1 = new BarDataSet(barComp1, "");
            setBar1.setAxisDependency(YAxis.AxisDependency.LEFT);
            //       setBar1.setColors(ColorTemplate.PASTEL_COLORS);
            setBar1.setColors(new int[]{Color.rgb(106, 150, 31), Color.rgb(193, 37, 82), Color.rgb(245, 199, 0), Color.rgb(255, 102, 0),
                    Color.rgb(179, 100, 53), Color.rgb(0, 0, 0)});
            setBar1.setStackLabels(new String[]{"Success", "Failed", "Dropped", "Bounced", "User Cancelled", "Pending"});
            setBar1.setValueTextSize(0f);
            ArrayList<IBarDataSet> dataSet2 = new ArrayList<>();
            dataSet2.add(setBar1);
            BarData mData2 = new BarData(xVals, dataSet2);
            barChart.setData(mData2);
            barChart.animateY(2000);


            /*Pie Chart Start*/
            xValsForPieChart.add("Success");
            xValsForPieChart.add("Failed");
            xValsForPieChart.add("Pending ");
            xValsForPieChart.add("Dropped ");
            xValsForPieChart.add("User Cancelled");
            xValsForPieChart.add("Bounced");

            listForPieChart.add(new Entry((float) successTotal, 0));
            listForPieChart.add(new Entry((float) failedTotal, 1));
            listForPieChart.add(new Entry((float) pendingTotal, 2));
            listForPieChart.add(new Entry((float) droppedTotal, 3));
            listForPieChart.add(new Entry((float) userCancelledTotal, 4));
            listForPieChart.add(new Entry((float) bouncedTotal, 5));

            PieDataSet setPie1 = new PieDataSet(listForPieChart, "");
            setPie1.setAxisDependency(YAxis.AxisDependency.LEFT);
            setPie1.setColors(new int[]{Color.rgb(106, 150, 31), Color.rgb(193, 37, 82), Color.rgb(245, 199, 0), Color.rgb(255, 102, 0),
                    Color.rgb(179, 100, 53), Color.rgb(255, 255, 255)});
            PieData mData3 = new PieData(xValsForPieChart, setPie1);
            pieChart.setData(mData3);
            pieChart.animateY(3000);
            /*Pie Chart End*/
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
}
