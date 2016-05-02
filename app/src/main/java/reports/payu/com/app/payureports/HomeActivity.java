package reports.payu.com.app.payureports;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;

import reports.payu.com.app.payureports.Utils.ChartMarker;

public class HomeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private LineChart chart;
    private BarChart chart2;
    private PieChart chart3;

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
        chart = (LineChart) findViewById(R.id.chart);
        chart2 = (BarChart) findViewById(R.id.chart2);
        chart3 = (PieChart) findViewById(R.id.chart3);
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

        Intent i = new Intent(this,LoginSignUpActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public void setDataInChart() {

        ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp2 = new ArrayList<Entry>();

        Entry c1e1 = new Entry(100.000f, 0); // 0 == quarter 1
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
        valsComp2.add(c2e4);


        LineDataSet setComp1 = new LineDataSet(valsComp1, "Company 1");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setCircleColor(ContextCompat.getColor(this, R.color.black));
        setComp1.setColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        LineDataSet setComp2 = new LineDataSet(valsComp2, "Company 2");
        setComp2.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp2.setCircleColor(ContextCompat.getColor(this, R.color.black));
        setComp2.setColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1);
        dataSets.add(setComp2);

        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("Quarter1");
        xVals.add("Quarter2");
        xVals.add("Quarter3");
        xVals.add("Quarter4");

        LineData mData = new LineData(xVals, dataSets);
        chart.setData(mData);

        chart.animateY(2000);


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
        chart2.setData(mData2);
        chart2.animateY(2000);

        PieDataSet setPie1 = new PieDataSet(valsComp1, "Company 1");
        setPie1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setPie1.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData mData3 = new PieData(xVals, setPie1);
        chart3.setData(mData3);
        chart3.animateY(3000);
    }
}
