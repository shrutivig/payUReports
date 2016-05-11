package reports.payu.com.app.payureports;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Iterator;

/**
 * Created by shruti.vig on 5/10/16.
 */
public class TableReportActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

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
        ringProgressDialog.setCancelable(true);
        setContentView(R.layout.activity_table_report);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView titleText = (TextView) toolbar.findViewById(R.id.title);
        toolbar.findViewById(R.id.filter_button).setVisibility(View.GONE);

        if (getIntent() != null) {
            reportId = getIntent().getStringExtra(Constants.REPORT_ID);
            email = getIntent().getStringExtra(Constants.EMAIL);
            titleText.setText(getIntent().getStringExtra(Constants.REPORT_NAME));
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        fetchReportData(null);
    }

    private void setBackgroundForButton(TextView text) {
        if (android.os.Build.VERSION.SDK_INT >= 16)
            text.setBackground(ContextCompat.getDrawable(this, R.drawable.marker_background));
        else
            text.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.marker_background));
    }

    private void fetchReportData(JSONObject duration) {

        Session.getInstance(this).fetchReportData(email, reportId, duration);
    }


    private void setDataInReportsTable(JSONArray tableData, int tableIndex) {

        TableLayout reportsTable = null;
        TextView emptyTableText = null;
        try {
            switch (tableIndex) {
                case 1:
                    reportsTable = (TableLayout) findViewById(R.id.report_table1);
                    emptyTableText = (TextView) findViewById(R.id.empty_table1);
                    break;
                case 2:
                    reportsTable = (TableLayout) findViewById(R.id.report_table2);
                    emptyTableText = (TextView) findViewById(R.id.empty_table2);
                    break;
                case 3:
                    reportsTable = (TableLayout) findViewById(R.id.report_table3);
                    emptyTableText = (TextView) findViewById(R.id.empty_table3);
                    break;
                case 4:
                    reportsTable = (TableLayout) findViewById(R.id.report_table4);
                    emptyTableText = (TextView) findViewById(R.id.empty_table4);
                    break;
                case 5:
                    reportsTable = (TableLayout) findViewById(R.id.report_table5);
                    emptyTableText = (TextView) findViewById(R.id.empty_table5);
                    break;
                case 6:
                    reportsTable = (TableLayout) findViewById(R.id.report_table6);
                    emptyTableText = (TextView) findViewById(R.id.empty_table6);
                    break;
                case 7:
                    reportsTable = (TableLayout) findViewById(R.id.report_table7);
                    emptyTableText = (TextView) findViewById(R.id.empty_table7);
                    break;
                case 8:
                    reportsTable = (TableLayout) findViewById(R.id.report_table8);
                    emptyTableText = (TextView) findViewById(R.id.empty_table8);
                    break;
                case 9:
                    reportsTable = (TableLayout) findViewById(R.id.report_table9);
                    emptyTableText = (TextView) findViewById(R.id.empty_table9);
                    break;
                case 10:
                    reportsTable = (TableLayout) findViewById(R.id.report_table10);
                    emptyTableText = (TextView) findViewById(R.id.empty_table10);
                    break;
            }

            TableRow headerRow = new TableRow(this);
            headerRow.setBackgroundColor(ContextCompat.getColor(this, R.color.background_blue));
            headerRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            if (tableData == null || tableData.length() == 0) {
                emptyTableText.setVisibility(View.VISIBLE);
            } else {
                Iterator headerValues = ((JSONObject) tableData.get(0)).keys();

                ArrayList<String> headers = new ArrayList();
                while (headerValues.hasNext()) {
                    String tempHeader = headerValues.next().toString();
                    TextView column = new TextView(this);
                    column.setText(tempHeader);
                    column.setTypeface(null, Typeface.BOLD);
                    column.setAllCaps(true);
                    column.setTextColor(Color.BLACK);
                    setBackgroundForButton(column);
                    column.setPadding(20, 20, 20, 20);
                    column.setGravity(Gravity.CENTER);
                    headerRow.addView(column);

                    headers.add(tempHeader);

                }
                reportsTable.addView(headerRow, new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));

                for (int i = 0; i < tableData.length(); i++) {

                    TableRow tableRow = new TableRow(this);
                    tableRow.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));

                    JSONObject rowDataValue = (JSONObject) tableData.get(i);
                    for (int j = 0; j < rowDataValue.length(); j++) {
                        TextView column = new TextView(this);
                        column.setText(rowDataValue.getString(headers.get(j)));
                        column.setTextColor(Color.BLACK);
                        setBackgroundForButton(column);
                        column.setPadding(10, 10, 10, 10);
                        column.setGravity(Gravity.CENTER);
                        tableRow.addView(column);
                    }

                    reportsTable.addView(tableRow, new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));
                }
            }
        } catch (JSONException e) {
            ringProgressDialog.dismiss();
            e.printStackTrace();
        }
    }

    private void initLayout(JSONArray displayReportresult) {
        int numberOfTables = displayReportresult.length();
        try {
            while (numberOfTables > 0) {
                switch (numberOfTables) {
                    case 1:
                        JSONArray dataForTable1 = displayReportresult.getJSONArray(0);
                        setDataInReportsTable(dataForTable1, 1);
                        findViewById(R.id.layoutForTable1).setVisibility(View.VISIBLE);
                        numberOfTables--;
                        break;
                    case 2:
                        JSONArray dataForTable2 = displayReportresult.getJSONArray(1);
                        setDataInReportsTable(dataForTable2, 2);
                        findViewById(R.id.layoutForTable2).setVisibility(View.VISIBLE);
                        numberOfTables--;
                        break;
                    case 3:
                        JSONArray dataForTable3 = displayReportresult.getJSONArray(2);
                        setDataInReportsTable(dataForTable3, 3);
                        findViewById(R.id.layoutForTable3).setVisibility(View.VISIBLE);
                        numberOfTables--;
                        break;
                    case 4:
                        JSONArray dataForTable4 = displayReportresult.getJSONArray(3);
                        setDataInReportsTable(dataForTable4, 4);
                        findViewById(R.id.layoutForTable4).setVisibility(View.VISIBLE);
                        numberOfTables--;
                        break;
                    case 5:
                        JSONArray dataForTable5 = displayReportresult.getJSONArray(4);
                        setDataInReportsTable(dataForTable5, 5);
                        findViewById(R.id.layoutForTable5).setVisibility(View.VISIBLE);
                        numberOfTables--;
                        break;
                    case 6:
                        JSONArray dataForTable6 = displayReportresult.getJSONArray(5);
                        setDataInReportsTable(dataForTable6, 6);
                        findViewById(R.id.layoutForTable6).setVisibility(View.VISIBLE);
                        numberOfTables--;
                        break;
                    case 7:
                        JSONArray dataForTable7 = displayReportresult.getJSONArray(6);
                        setDataInReportsTable(dataForTable7, 7);
                        findViewById(R.id.layoutForTable7).setVisibility(View.VISIBLE);
                        numberOfTables--;
                        break;
                    case 8:
                        JSONArray dataForTable8 = displayReportresult.getJSONArray(7);
                        setDataInReportsTable(dataForTable8, 8);
                        findViewById(R.id.layoutForTable8).setVisibility(View.VISIBLE);
                        numberOfTables--;
                        break;
                    case 9:
                        JSONArray dataForTable9 = displayReportresult.getJSONArray(8);
                        setDataInReportsTable(dataForTable9, 9);
                        findViewById(R.id.layoutForTable9).setVisibility(View.VISIBLE);
                        numberOfTables--;
                        break;
                    case 10:
                        JSONArray dataForTable10 = displayReportresult.getJSONArray(9);
                        setDataInReportsTable(dataForTable10, 10);
                        findViewById(R.id.layoutForTable10).setVisibility(View.VISIBLE);
                        numberOfTables--;
                        break;
                }
            }
            if (ringProgressDialog.isShowing())
                ringProgressDialog.dismiss();
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
                            initLayout(displayReportresult);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (ringProgressDialog.isShowing())
                        ringProgressDialog.dismiss();
                    JSONObject temp = null;

                    if (event.getValue().toString().contains("Server error")) {
                        handleStatus("XYZ", "XYZ");
                    } else {
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
