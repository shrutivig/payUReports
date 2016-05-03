package reports.payu.com.app.payureports;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

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
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import reports.payu.com.app.payureports.Model.ReportData;
import reports.payu.com.app.payureports.Model.ReportResults;

public class HomeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private LineChart lineChart;
    private BarChart barChart;
    private PieChart pieChart;
    private ReportResults reportsResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Button signOutButton = (Button) findViewById(R.id.sign_out_button);
        lineChart = (LineChart) findViewById(R.id.chart);
        barChart = (BarChart) findViewById(R.id.chart2);
        pieChart = (PieChart) findViewById(R.id.chart3);
        setDataInChart();

        if (signOutButton != null) {
            signOutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //signOut();
                    // LoginSignUpActivity.signOut();
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(Status status) {
                                    launchLoginSignupActivity();
                                }
                            });
                }
            });
        }
        findViewById(R.id.disconnect_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void launchLoginSignupActivity() {

        Intent i = new Intent(this, LoginSignUpActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public void setDataInChart() {

        ArrayList<Entry> listSuccess = new ArrayList<>();
        ArrayList<Entry> listFailed = new ArrayList<>();
        ArrayList<Entry> listDropped = new ArrayList<>();
        ArrayList<Entry> listBounced = new ArrayList<>();
        ArrayList<Entry> listUserCancelled = new ArrayList<>();
        ArrayList<Entry> listOther = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset());
            reportsResults = (ReportResults) Session.getInstance(this).getParsedResponseFromGSON(jsonObject, Session.dataType.ReportResults);
            List<ReportData> list = reportsResults.getList();
            for (int i = 0 ; i < list.size() ; i++) {
                ReportData temp = list.get(i);
                Entry success = new Entry(temp.getSuccess(), i);
                listSuccess.add(success);
                Entry failed = new Entry(temp.getFailed(), i);
                listFailed.add(failed);
                Entry dropped = new Entry(temp.getDropped(), i);
                listDropped.add(dropped);
                Entry bounced = new Entry(temp.getBounced(), i);
                listBounced.add(bounced);
                Entry userCancelled = new Entry(temp.getUserCancelled(), i);
                listUserCancelled.add(userCancelled);
                Entry others = new Entry(temp.getPending(), i);
                listOther.add(others);

            }
            LineDataSet setComp1 = new LineDataSet(listSuccess, "success");
            LineDataSet setComp2 = new LineDataSet(listFailed, "failed");
            LineDataSet setComp3 = new LineDataSet(listDropped, "dropped");
            LineDataSet setComp4 = new LineDataSet(listBounced, "bounced");
            LineDataSet setComp5 = new LineDataSet(listUserCancelled, "userCancelled");
            LineDataSet setComp6 = new LineDataSet(listOther, "other");


            /*Entry c1e1 = new Entry(100.000f, 0); // 0 == quarter 1
            valsComp1.add(c1e1);
            Entry c1e2 = new Entry(50.000f, 1); // 1 == quarter 2 ...
            valsComp1.add(c1e2);
            Entry c1e3 = new Entry(70.000f, 2); // 0 == quarter 1
            valsComp1.add(c1e3);
            Entry c1e4 = new Entry(60.000f, 3); // 1 == quarter 2 ...
            valsComp1.add(c1e4);
            // and so on ...

            Entry c2e1 = new Entry(120.000f, 0); // 0 == quarter 1
            valsComp2.add(c2e1);
            Entry c2e2 = new Entry(110.000f, 1); // 1 == quarter 2 ...
            valsComp2.add(c2e2);
            Entry c2e3 = new Entry(100.000f, 2); // 0 == quarter 1
            valsComp2.add(c2e3);
            Entry c2e4 = new Entry(150.000f, 3); // 1 == quarter 2 ...
            valsComp2.add(c2e4);*/


            setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
            setComp1.setCircleColor(ContextCompat.getColor(this, R.color.black));
            setComp1.setColor(ContextCompat.getColor(this, android.R.color.holo_green_dark ));

            setComp2.setAxisDependency(YAxis.AxisDependency.LEFT);
            setComp2.setCircleColor(ContextCompat.getColor(this, R.color.black));
            setComp2.setColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));

            setComp3.setAxisDependency(YAxis.AxisDependency.LEFT);
            setComp3.setCircleColor(ContextCompat.getColor(this, R.color.black));
            setComp3.setColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));

            setComp4.setAxisDependency(YAxis.AxisDependency.LEFT);
            setComp4.setCircleColor(ContextCompat.getColor(this, R.color.black));
            setComp4.setColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark));

            setComp5.setAxisDependency(YAxis.AxisDependency.LEFT);
            setComp2.setCircleColor(ContextCompat.getColor(this, R.color.black));
            setComp2.setColor(ContextCompat.getColor(this, android.R.color.darker_gray));

            setComp6.setAxisDependency(YAxis.AxisDependency.LEFT);
            setComp6.setCircleColor(ContextCompat.getColor(this, R.color.black));
            setComp6.setColor(ContextCompat.getColor(this, android.R.color.black));

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(setComp1);
            dataSets.add(setComp2);
            dataSets.add(setComp3);
            dataSets.add(setComp4);
            dataSets.add(setComp5);
            dataSets.add(setComp6);

            ArrayList<String> xVals = new ArrayList<>();
            xVals.add("Success");
            xVals.add("Failed");
            xVals.add("Dropped");
            xVals.add("Bounced");
            xVals.add("User Cancelled");
            xVals.add("Others");

            LineData mData = new LineData(xVals, dataSets);
            lineChart.setData(mData);

            lineChart.animateY(2000);


            ArrayList<BarEntry> barComp1 = new ArrayList<BarEntry>();
            ArrayList<BarEntry> barComp2 = new ArrayList<BarEntry>();

            BarEntry b1 = new BarEntry(100.000f, 0); // 0 == quarter 1
            barComp1.add(b1);
            BarEntry b2 = new BarEntry(50.000f, 1); // 1 == quarter 2 ...
            barComp1.add(b2);
            BarEntry b3 = new BarEntry(70.000f, 2); // 0 == quarter 1
            barComp1.add(b3);
            BarEntry b4 = new BarEntry(60.000f, 3); // 1 == quarter 2 ...
            barComp1.add(b4);

            BarEntry b11 = new BarEntry(120.000f, 0); // 0 == quarter 1
            barComp2.add(b11);
            BarEntry b12 = new BarEntry(110.000f, 1); // 1 == quarter 2 ...
            barComp2.add(b12);
            BarEntry b13 = new BarEntry(100.000f, 2); // 0 == quarter 1
            barComp2.add(b13);
            BarEntry b14 = new BarEntry(150.000f, 3); // 1 == quarter 2 ...
            barComp2.add(b14);


            BarDataSet setBar1 = new BarDataSet(barComp1, "Company1");
            setBar1.setAxisDependency(YAxis.AxisDependency.LEFT);
            setBar1.setColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));

            BarDataSet setBar2 = new BarDataSet(barComp2, "Company 2");
            setBar2.setAxisDependency(YAxis.AxisDependency.LEFT);
            setBar2.setColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));

            ArrayList<IBarDataSet> dataSet2 = new ArrayList<IBarDataSet>();
            dataSet2.add(setBar1);
            dataSet2.add(setBar2);

            BarData mData2 = new BarData(xVals, dataSet2);
            barChart.setData(mData2);
            barChart.animateY(2000);

            PieDataSet setPie1 = new PieDataSet(listSuccess, "Company 1");
            setPie1.setAxisDependency(YAxis.AxisDependency.LEFT);
            setPie1.setColors(ColorTemplate.COLORFUL_COLORS);

            PieData mData3 = new PieData(xVals, setPie1);
            pieChart.setData(mData3);
            pieChart.animateY(3000);
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
