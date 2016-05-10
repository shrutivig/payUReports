package reports.payu.com.app.payureports.Model;

import java.util.ArrayList;

/**
 * Created by sagar.chauhan on 5/9/16.
 */
public class ReportList {

    public ArrayList<ListItem> getList() {
        return reportList;
    }

    ArrayList<ListItem> reportList;

    public class ListItem {
        String id;
        String heading;
        String reportType;

        public String getReportType() {
            return reportType;
        }

        public String getId() {
            return id;
        }

        public String getHeading() {
            return heading;
        }


    }
}
