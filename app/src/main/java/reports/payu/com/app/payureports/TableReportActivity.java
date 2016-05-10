package reports.payu.com.app.payureports;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by shruti.vig on 5/10/16.
 */
public class TableReportActivity extends AppCompatActivity {

    private TableLayout reportsTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_report);
        reportsTable = (TableLayout) findViewById(R.id.report_table);

        setHeadersInReportsTable();
        setDataInReportsTable();
    }

    private void setHeadersInReportsTable() {

        View v = new View(this);
        v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
        v.setBackgroundColor(Color.rgb(51, 51, 51));

        TableRow tableHeader = new TableRow(this);
        tableHeader.setBackgroundColor(Color.GRAY);
        tableHeader.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        TextView headerColumn1 = new TextView(this);
        headerColumn1.setText("DATE");
        headerColumn1.setTextColor(Color.WHITE);
        headerColumn1.setPadding(5, 5, 5, 5);
        tableHeader.addView(headerColumn1);

        TextView headerColumn2 = new TextView(this);
        headerColumn2.setText("SUCCESSFUL TX 1");
        headerColumn2.setTextColor(Color.WHITE);
        headerColumn2.setPadding(5, 5, 5, 5);
        tableHeader.addView(headerColumn2);

        TextView headerColumn3 = new TextView(this);
        headerColumn3.setText("SUCCESSFUL TX 2");
        headerColumn3.setTextColor(Color.WHITE);
        headerColumn3.setPadding(5, 5, 5, 5);
        tableHeader.addView(headerColumn3);

        TextView headerColumn4 = new TextView(this);
        headerColumn4.setText("SUCCESSFUL TX 3");
        headerColumn4.setTextColor(Color.WHITE);
        headerColumn4.setPadding(5, 5, 5, 5);
        tableHeader.addView(headerColumn4);

        TextView headerColumn5 = new TextView(this);
        headerColumn5.setText("SUCCESSFUL TX 4");
        headerColumn5.setTextColor(Color.WHITE);
        headerColumn5.setPadding(5, 5, 5, 5);
        tableHeader.addView(headerColumn5);

        reportsTable.addView(tableHeader, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        reportsTable.addView(v);

    }

    private void setDataInReportsTable() {

        for (int i = 0; i < 11; i++) {
            View v = new View(this);
            v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
            v.setBackgroundColor(Color.rgb(51, 51, 51));

            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            TextView column1 = new TextView(this);
            column1.setText("" + (i + 1));
            column1.setTextColor(Color.BLACK);
            column1.setPadding(5, 5, 5, 5);
            tableRow.addView(column1);

            TextView column2 = new TextView(this);
            column2.setText("" + (i + 1));
            column2.setTextColor(Color.BLACK);
            column2.setPadding(5, 5, 5, 5);
            tableRow.addView(column2);

            TextView column3 = new TextView(this);
            column3.setText("" + (i + 1));
            column3.setTextColor(Color.BLACK);
            column3.setPadding(5, 5, 5, 5);
            tableRow.addView(column3);

            TextView column4 = new TextView(this);
            column4.setText("" + (i + 1));
            column4.setTextColor(Color.BLACK);
            column4.setPadding(5, 5, 5, 5);
            tableRow.addView(column4);

            TextView column5 = new TextView(this);
            column5.setText("" + (i + 1));
            column5.setTextColor(Color.BLACK);
            column5.setPadding(5, 5, 5, 5);
            tableRow.addView(column5);

            reportsTable.addView(tableRow, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            reportsTable.addView(v);
        }
    }
}
