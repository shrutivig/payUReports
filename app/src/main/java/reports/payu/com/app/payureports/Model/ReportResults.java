package reports.payu.com.app.payureports.Model;

import java.io.Serializable;

/**
 * Created by sagar.chauhan on 5/3/16.
 */
public class ReportResults implements Serializable {

    DisplayReportResults displayReportResult;
    String reportID;
    int status;

    public DisplayReportResults getDisplayReportResult() {
        return displayReportResult;
    }

    public int getStatus() {
        return status;
    }

    public String getReportID() {
        return reportID;
    }
}
