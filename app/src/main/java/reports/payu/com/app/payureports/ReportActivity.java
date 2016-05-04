package reports.payu.com.app.payureports;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

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
public class ReportActivity extends AppCompatActivity {

    private BarChart barChart;
    private ReportResults reportsResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        barChart = (BarChart) findViewById(R.id.barChart);
        setDataInBarChart();
    }


    public void setDataInBarChart() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(loadJSONFromAsset());

            reportsResults = (ReportResults) Session.getInstance(this).getParsedResponseFromGSON(jsonObject, Session.dataType.ReportResults);
            ArrayList<BarEntry> barComp1 = new ArrayList<>();

            List<ReportData> list = reportsResults.getList();
            ArrayList<String> xVals = new ArrayList<>();

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

                xVals.add("" + date + " to " + list.get(i).getMaxDate());
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
