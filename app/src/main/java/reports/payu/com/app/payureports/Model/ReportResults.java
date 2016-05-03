package reports.payu.com.app.payureports.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagar.chauhan on 5/3/16.
 */
public class ReportResults implements Serializable{

    List<ReportData> displayReportResult;

    public List<ReportData> getList() {
        return displayReportResult;
    }
}
