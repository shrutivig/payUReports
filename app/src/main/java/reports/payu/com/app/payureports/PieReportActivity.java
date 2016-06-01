package reports.payu.com.app.payureports;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
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

import java.math.BigDecimal;
import java.util.ArrayList;

import reports.payu.com.app.payureports.Model.ReportResults;

/**
 * Created by shruti.vig on 5/26/16.
 */
public class PieReportActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private String reportId, email;
    private JSONObject reportsResults;
    ProgressDialog ringProgressDialog;
    private float totalDataSum = 0.0f;
    private GoogleApiClient mGoogleApiClient;
    private int[] colors = new int[]{Color.rgb(28, 148, 36), Color.rgb(217, 58, 33), Color.rgb(253, 152, 39), Color.rgb(54, 105, 201),
            Color.rgb(151, 20, 151), Color.rgb(24, 153, 196), Color.rgb(170, 160, 57), Color.rgb(138, 19, 77), Color.rgb(153, 217, 255), Color.rgb(255, 102, 102)};

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
        Session.getInstance(this).fetchReportData(email, reportId, null);
    }

    private void setUpPieCharts(JSONArray pieData, PieChart pieChartLayout, int pieChartIndex) {
        ringProgressDialog.dismiss();

        totalDataSum = 0.0f;

        ArrayList<Entry> data = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < pieData.length(); i++) {
            try {
                JSONObject pieObject = (JSONObject) pieData.get(i);
                if (pieObject.has("count") && !pieObject.isNull("count")) {
                    String value = pieObject.getString("count");
                    totalDataSum = totalDataSum + Float.parseFloat(value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        for (int i = 0; i < pieData.length(); i++) {

            try {
                JSONObject pieObject = (JSONObject) pieData.get(i);
                if (pieObject.has("count") && !pieObject.isNull("count")) {
                    String value = pieObject.getString("count");
                    Entry entry = new Entry(Float.parseFloat(value), i);
                    data.add(entry);
                }

                if (pieObject.has("status") && !pieObject.isNull("status"))
                    labels.add(pieObject.getString("status"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        PieDataSet setPie1 = new PieDataSet(data, "");
        setPie1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setPie1.setColors(colors);

        PieData mData3 = new PieData(labels, setPie1);
        mData3.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

                float percentValue = (entry.getVal() / totalDataSum) * 100;
                if (percentValue < 1f)
                    return "";
                else
                    return String.format("%.2f", percentValue) + "%";
            }
        });
        mData3.setValueTextSize(8f);
        pieChartLayout.setData(mData3);
        pieChartLayout.setDescription("Data Set " + pieChartIndex);
        pieChartLayout.setDescriptionTextSize(20f);
        pieChartLayout.setHoleRadius(24f);
        pieChartLayout.setDrawSliceText(false);
        pieChartLayout.setTransparentCircleRadius(27f);
        pieChartLayout.setUsePercentValues(true);
        pieChartLayout.getLegend().setPosition(Legend.LegendPosition.LEFT_OF_CHART_CENTER);
        pieChartLayout.animateY(2000);

    }

    private void initPieLayout(JSONArray displayReportresult) {
        int numberOfTables = displayReportresult.length();
        try {
            while (numberOfTables > 0) {
                switch (numberOfTables) {
                    case 1:
                        JSONArray dataForTable1 = displayReportresult.getJSONArray(0);
                        PieChart piechart1 = (PieChart) findViewById(R.id.pieChart1);
                        setUpPieCharts(dataForTable1, piechart1, 1);
                        piechart1.setVisibility(View.VISIBLE);
                        numberOfTables--;
                        break;
                    case 2:
                        JSONArray dataForTable2 = displayReportresult.getJSONArray(1);
                        PieChart piechart2 = (PieChart) findViewById(R.id.pieChart2);
                        setUpPieCharts(dataForTable2, piechart2, 2);
                        piechart2.setVisibility(View.VISIBLE);
                        numberOfTables--;
                        break;
                    case 3:
                        JSONArray dataForTable3 = displayReportresult.getJSONArray(2);
                        PieChart piechart3 = (PieChart) findViewById(R.id.pieChart3);
                        setUpPieCharts(dataForTable3, piechart3, 3);
                        piechart3.setVisibility(View.VISIBLE);
                        numberOfTables--;
                        break;
                    case 4:
                        JSONArray dataForTable4 = displayReportresult.getJSONArray(3);
                        PieChart piechart4 = (PieChart) findViewById(R.id.pieChart4);
                        setUpPieCharts(dataForTable4, piechart4, 4);
                        piechart4.setVisibility(View.VISIBLE);
                        numberOfTables--;
                        break;
                    case 5:
                        JSONArray dataForTable5 = displayReportresult.getJSONArray(4);
                        PieChart piechart5 = (PieChart) findViewById(R.id.pieChart5);
                        setUpPieCharts(dataForTable5, piechart5, 5);
                        piechart5.setVisibility(View.VISIBLE);
                        numberOfTables--;
                        break;
                    case 6:
                        JSONArray dataForTable6 = displayReportresult.getJSONArray(5);
                        PieChart piechart6 = (PieChart) findViewById(R.id.pieChart6);
                        setUpPieCharts(dataForTable6, piechart6, 6);
                        piechart6.setVisibility(View.VISIBLE);
                        numberOfTables--;
                        break;
                    case 7:
                        JSONArray dataForTable7 = displayReportresult.getJSONArray(6);
                        PieChart piechart7 = (PieChart) findViewById(R.id.pieChart7);
                        setUpPieCharts(dataForTable7, piechart7, 7);
                        piechart7.setVisibility(View.VISIBLE);
                        numberOfTables--;
                        break;
                    case 8:
                        JSONArray dataForTable8 = displayReportresult.getJSONArray(7);
                        PieChart piechart8 = (PieChart) findViewById(R.id.pieChart8);
                        setUpPieCharts(dataForTable8, piechart8, 8);
                        piechart8.setVisibility(View.VISIBLE);
                        numberOfTables--;
                        break;
                    case 9:
                        JSONArray dataForTable9 = displayReportresult.getJSONArray(8);
                        PieChart piechart9 = (PieChart) findViewById(R.id.pieChart9);
                        setUpPieCharts(dataForTable9, piechart9, 8);
                        piechart9.setVisibility(View.VISIBLE);
                        numberOfTables--;
                        break;
                    case 10:
                        JSONArray dataForTable10 = displayReportresult.getJSONArray(9);
                        PieChart piechart10 = (PieChart) findViewById(R.id.pieChart10);
                        setUpPieCharts(dataForTable10, piechart10, 10);
                        piechart10.setVisibility(View.VISIBLE);
                        numberOfTables--;
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                            initPieLayout(displayReportresult);
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
