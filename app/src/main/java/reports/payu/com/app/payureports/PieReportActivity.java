package reports.payu.com.app.payureports;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by shruti.vig on 5/26/16.
 */
public class PieReportActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private String reportId, email;
    private JSONObject reportsResults;
    ProgressDialog ringProgressDialog;
    private GoogleApiClient mGoogleApiClient;

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
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.setCanceledOnTouchOutside(false);
        setContentView(R.layout.activity_pie_report);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView titleText = null;
        if (toolbar != null) {
            titleText = (TextView) toolbar.findViewById(R.id.title);
            toolbar.findViewById(R.id.filter_button).setVisibility(View.GONE);
        }

        if (getIntent() != null) {
            reportId = getIntent().getStringExtra(Constants.REPORT_ID);
            email = getIntent().getStringExtra(Constants.EMAIL);
            if (titleText != null)
                titleText.setText(getIntent().getStringExtra(Constants.REPORT_NAME));
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        fetchPieChartData();
    }

    private void fetchPieChartData() {
        //  Session.getInstance(this).fetchPieReportData(email, reportId);
        setUpPieCharts(null, 1);
        setUpPieCharts(null, 2);
        setUpPieCharts(null, 3);
    }

    private void setUpPieCharts(JSONObject pieData, int pieIndex) {
        ringProgressDialog.dismiss();
        PieChart pieChartLayout = null;
        switch (pieIndex) {
            case 1:
                pieChartLayout = (PieChart) findViewById(R.id.pieChart1);
                break;
            case 2:
                pieChartLayout = (PieChart) findViewById(R.id.pieChart2);
                break;
            case 3:
                pieChartLayout = (PieChart) findViewById(R.id.pieChart3);
                break;
        }

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

        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("Quarter1");
        xVals.add("Quarter2");
        xVals.add("Quarter3");
        xVals.add("Quarter4");


        PieDataSet setPie1 = new PieDataSet(valsComp1, "Company 1");
        setPie1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setPie1.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData mData3 = new PieData(xVals, setPie1);
        mData3.setValueFormatter(new PercentFormatter());
        mData3.setValueTextSize(10f);
        pieChartLayout.setData(mData3);
        pieChartLayout.setDescriptionTextSize(20f);
        pieChartLayout.setHoleRadius(24f);
        pieChartLayout.setDrawSliceText(false);
        pieChartLayout.setTransparentCircleRadius(27f);
        pieChartLayout.getLegend().setPosition(Legend.LegendPosition.LEFT_OF_CHART_CENTER);
        pieChartLayout.animateY(2000);


    }

    private void initPieLayout(JSONArray displayReportresult) {

    }


    @Subscribe
    public void onEventMainThread(CobbocEvent event) {
        switch (event.getType()) {
            case CobbocEvent.REPORT:
                if (event.getStatus()) {
                    try {
                        reportsResults = (JSONObject) event.getValue();

                        if (reportsResults != null && reportsResults.has("displayReportResult") && !reportsResults.isNull("displayReportResult")) {
                            JSONArray displayReportresult = reportsResults.getJSONArray("displayReportResult");

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (ringProgressDialog.isShowing())
                        ringProgressDialog.dismiss();

                    if (event.getValue().toString().contains("Server error")) {
                        handleStatus("XYZ", event.getValue().toString());
                    } else {
                        JSONObject temp = (JSONObject) event.getValue();
                        String errorMessage = null;
                        String errorCode = null;
                        try {
                            errorCode = temp.optString(Constants.ERROR_CODE, temp.getString("errorCode").toString());
                            errorMessage = temp.optString(Constants.MESSAGE, temp.getString("msg").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            errorCode = "XYZ";
                            errorMessage = "XYZ";
                        }
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
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                logoutUser();
                break;
            case "ER102":
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                logoutUser();
                break;
            case "ER103":
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                logoutUser();
                break;
            case "ER104":
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                logoutUser();
                break;
            case "ER105":
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                logoutUser();
                break;
            case "ER106":
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                logoutUser();
                break;
            case "ER107":
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                finish();
                break;
            //logoutUser();
            case "ER108":
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                finish();
                break;
            case "ER109":
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                finish();
                break;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
