package reports.payu.com.app.payureports.Model;

import java.util.ArrayList;

/**
 * Created by sagar.chauhan on 5/9/16.
 */
public class ReportList {
    private int status;

    public int getStatus() {
        return status;
    }

    public ArrayList<ListItem> getList() {
        return list;
    }

    ArrayList<ListItem> list;

    public class ListItem {
        String id;

        public String getId() {
            return id;
        }

        public String getHeading() {
            return heading;
        }

        String heading;
    }
}
