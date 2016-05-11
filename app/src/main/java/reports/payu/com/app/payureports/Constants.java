package reports.payu.com.app.payureports;

import java.util.regex.Pattern;

import de.keyboardsurfer.android.widget.crouton.Configuration;

/**
 * Created by sagar.chauhan on 3/14/16.
 */
public class Constants {

    /**
     * Tag for logging
     */
    public static final String TAG = "CCPayU";
    public static final boolean DEBUG = false;
    public static final String HOME = "HOME";  //CCHomeActivity

    public static final boolean CC_TRANSACTION_HISTORY_SHOW_FOOTER = true;


    /*
    ** Shared Preference File Names
    */
    public static final String SP_SERVER_NAME = "ServerLogSharedPreference";
    public static final String SP_USER_NAME = "UserSessionSharedPreference";

    public static final Configuration CONFIGURATION_INFINITE = new Configuration.Builder().setDuration(Configuration.DURATION_INFINITE).build();
    public static final Configuration CONFIGURATION_LONG = new Configuration.Builder().setDuration(Configuration.DURATION_LONG).build();
    public static final Configuration CONFIGURATION_SHORT = new Configuration.Builder().setDuration(Configuration.DURATION_SHORT).build();


    public static final String BASE_URL_IMAGE = "";
    public static final String BASE_URL = "";

    public static final String ACCESS_TOKEN = "token";
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern PHONE_PATTERN = Pattern.compile("[\\d]{10}$");

    public static final String EMAIL = "email";
    public static final String DEVICE_ID = "deviceId";
    public static final String DEVICE_TYPE = "deviceType";
    public static final String ANDROID = "android";
    public static final String STATUS = "status";
    public static final String MESSAGE = "msg";

    public static final String USER_ID = "user_id";

    public static final String EVENT_FLAG = "eventFlag";
    public static final String EVENT_1 = "1";
    public static final String EVENT_2 = "2";
    public static final String REPORT_ID = "reportId";
    public static final String DURATION = "params";
    public static final String ERROR_CODE = "errorCode";
    public static final String START_DATE = "startDate";

    public static final String END_DATE = "endDate";
    public static final String REPORT_NAME = "reportName";
}
