package reports.payu.com.app.payureports;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import reports.payu.com.app.payureports.Model.ReportResults;

/**
 * Created by shruti.vig on 5/10/16.
 */
public class TableReportActivity extends AppCompatActivity {

    private String reportId, email;
    private JSONObject reportsResults;
    ProgressDialog ringProgressDialog;


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

        if (getIntent() != null) {
            reportId = getIntent().getStringExtra(Constants.REPORT_ID);
            email = getIntent().getStringExtra(Constants.EMAIL);
        }
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
        try {
            switch (tableIndex) {
                case 1:
                    reportsTable = (TableLayout) findViewById(R.id.report_table);
                    break;
                case 2:
                    reportsTable = (TableLayout) findViewById(R.id.report_table2);
                    break;
                case 3:
                    reportsTable = (TableLayout) findViewById(R.id.report_table3);
                    break;
            }

            TableRow headerRow = new TableRow(this);
            headerRow.setBackgroundColor(Color.GRAY);
            headerRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));


            Iterator headerValues = ((JSONObject) tableData.get(0)).keys();
            ArrayList<String> headers = new ArrayList();
            while (headerValues.hasNext()) {
                String tempHeader = headerValues.next().toString();
                TextView column1 = new TextView(this);
                column1.setText(tempHeader);
                column1.setTextColor(Color.BLACK);
                setBackgroundForButton(column1);
                column1.setPadding(5, 5, 5, 5);
                column1.setGravity(Gravity.CENTER);
                headerRow.addView(column1);

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
                    TextView column1 = new TextView(this);
                    column1.setText(rowDataValue.getString(headers.get(j)));
                    column1.setTextColor(Color.BLACK);
                    setBackgroundForButton(column1);
                    column1.setPadding(5, 5, 5, 5);
                    column1.setGravity(Gravity.CENTER);
                    tableRow.addView(column1);
                }

                reportsTable.addView(tableRow, new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));
                ringProgressDialog.dismiss();
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
                        //findViewById(R.id.layoutForTable3).setVisibility(View.VISIBLE);
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
                            initLayout(displayReportresult);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ringProgressDialog.dismiss();
                    Toast.makeText(this, event.getValue().toString(), Toast.LENGTH_SHORT).show();
                }
                break;

            default:
        }
    }
}
