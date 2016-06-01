package reports.payu.com.app.payureports.Model;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by sagar.chauhan on 5/4/16.
 */
public class DisplayReportResults {

    List<ReportData> Day;
    List<ReportData> Week;
    List<ReportData> Month;
    ReportData Overall;

    public List<ReportData> getDay() {
        return Day;
    }

    public ReportData getOverall() {
        return Overall;
    }

    public List<ReportData> getWeek() {
        return Week;
    }

    public List<ReportData> getMonth() {
        return Month;
    }

}
