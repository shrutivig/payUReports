package reports.payu.com.app.payureports.Model;

import java.io.Serializable;

/**
 * Created by sagar.chauhan on 5/3/16.
 */
public class ReportData implements Serializable{

    private String minDate,maxDate;
    private Float success,total,pending,bounced,dropped,failed,userCancelled;

    public String getMinDate() {
        return minDate;
    }

    public String getMaxDate() {
        return maxDate;
    }

    public Float getTotal() {
        return total;
    }

    public Float getPending() {
        return pending;
    }

    public Float getUserCancelled() {
        return userCancelled;
    }

    public Float getSuccess() {
        return success;
    }

    public Float getFailed() {
        return failed;
    }

    public Float getDropped() {
        return dropped;
    }

    public Float getBounced() {
        return bounced;
    }
}
