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

    private void setBackgroundForButton(TextView text) {
        if (android.os.Build.VERSION.SDK_INT >= 16)
            text.setBackground(ContextCompat.getDrawable(this, R.drawable.marker_background));
        else
            text.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.marker_background));
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
        headerColumn1.setGravity(Gravity.CENTER);
        headerColumn1.setTextColor(Color.WHITE);
        headerColumn1.setPadding(5, 5, 5, 5);
        setBackgroundForButton(headerColumn1);
        tableHeader.addView(headerColumn1);

        TextView headerColumn2 = new TextView(this);
        headerColumn2.setText("SUCCESSFUL TX 1");
        headerColumn2.setTextColor(Color.WHITE);
        headerColumn2.setPadding(5, 5, 5, 5);
        setBackgroundForButton(headerColumn2);
        tableHeader.addView(headerColumn2);

        TextView headerColumn3 = new TextView(this);
        headerColumn3.setText("SUCCESSFUL TX 2");
        headerColumn3.setTextColor(Color.WHITE);
        headerColumn3.setPadding(5, 5, 5, 5);
        setBackgroundForButton(headerColumn3);
        tableHeader.addView(headerColumn3);

        TextView headerColumn4 = new TextView(this);
        headerColumn4.setText("SUCCESSFUL TX 3");
        headerColumn4.setTextColor(Color.WHITE);
        setBackgroundForButton(headerColumn4);
        headerColumn4.setPadding(5, 5, 5, 5);
        tableHeader.addView(headerColumn4);

        TextView headerColumn5 = new TextView(this);
        headerColumn5.setText("SUCCESSFUL TX 4");
        headerColumn5.setTextColor(Color.WHITE);
        headerColumn5.setPadding(5, 5, 5, 5);
        setBackgroundForButton(headerColumn5);
        tableHeader.addView(headerColumn5);

        TextView headerColumn6 = new TextView(this);
        headerColumn6.setText("SUCCESSFUL TX 5");
        headerColumn6.setTextColor(Color.WHITE);
        headerColumn6.setPadding(5, 5, 5, 5);
        setBackgroundForButton(headerColumn6);
        tableHeader.addView(headerColumn6);

        TextView headerColumn7 = new TextView(this);
        headerColumn7.setText("SUCCESSFUL TX 6");
        headerColumn7.setTextColor(Color.WHITE);
        headerColumn7.setPadding(5, 5, 5, 5);
        setBackgroundForButton(headerColumn7);
        tableHeader.addView(headerColumn7);

        TextView headerColumn8 = new TextView(this);
        headerColumn8.setText("SUCCESSFUL TX 7");
        headerColumn8.setTextColor(Color.WHITE);
        headerColumn8.setPadding(5, 5, 5, 5);
        setBackgroundForButton(headerColumn8);
        tableHeader.addView(headerColumn8);

        TextView headerColumn9 = new TextView(this);
        headerColumn9.setText("SUCCESSFUL TX 8");
        headerColumn9.setTextColor(Color.WHITE);
        headerColumn9.setPadding(5, 5, 5, 5);
        setBackgroundForButton(headerColumn9);
        tableHeader.addView(headerColumn9);

        reportsTable.addView(tableHeader, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        reportsTable.setStretchAllColumns(true);

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
            setBackgroundForButton(column1);
            column1.setPadding(5, 5, 5, 5);
            column1.setGravity(Gravity.CENTER);
            tableRow.addView(column1);

            TextView column2 = new TextView(this);
            column2.setText("" + (i + 1));
            column2.setTextColor(Color.BLACK);
            column2.setPadding(5, 5, 5, 5);
            column2.setGravity(Gravity.CENTER);
            setBackgroundForButton(column2);
            tableRow.addView(column2);

            TextView column3 = new TextView(this);
            column3.setText("" + (i + 1));
            column3.setTextColor(Color.BLACK);
            column3.setPadding(5, 5, 5, 5);
            setBackgroundForButton(column3);
            column3.setGravity(Gravity.CENTER);
            tableRow.addView(column3);

            TextView column4 = new TextView(this);
            column4.setText("" + (i + 1));
            column4.setTextColor(Color.BLACK);
            column4.setPadding(5, 5, 5, 5);
            column4.setGravity(Gravity.CENTER);
            setBackgroundForButton(column4);
            tableRow.addView(column4);

            TextView column5 = new TextView(this);
            column5.setText("" + (i + 1));
            column5.setTextColor(Color.BLACK);
            column5.setPadding(5, 5, 5, 5);
            column5.setGravity(Gravity.CENTER);
            setBackgroundForButton(column5);
            tableRow.addView(column5);

            TextView column6 = new TextView(this);
            column6.setText("" + (i + 1));
            column6.setTextColor(Color.BLACK);
            column6.setPadding(5, 5, 5, 5);
            column6.setGravity(Gravity.CENTER);
            setBackgroundForButton(column6);
            tableRow.addView(column6);

            TextView column7 = new TextView(this);
            column7.setText("" + (i + 1));
            column7.setTextColor(Color.BLACK);
            column7.setPadding(5, 5, 5, 5);
            column7.setGravity(Gravity.CENTER);
            setBackgroundForButton(column7);
            tableRow.addView(column7);

            TextView column8 = new TextView(this);
            column8.setText("" + (i + 1));
            column8.setTextColor(Color.BLACK);
            column8.setPadding(5, 5, 5, 5);
            column8.setGravity(Gravity.CENTER);
            setBackgroundForButton(column8);
            tableRow.addView(column8);

            TextView column9 = new TextView(this);
            column9.setText("" + (i + 1));
            column9.setTextColor(Color.BLACK);
            column9.setPadding(5, 5, 5, 5);
            column9.setGravity(Gravity.CENTER);
            setBackgroundForButton(column9);
            tableRow.addView(column9);

            reportsTable.addView(tableRow, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            //    reportsTable.addView(v);
        }
    }
}
