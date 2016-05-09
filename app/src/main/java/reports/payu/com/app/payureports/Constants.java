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

    /**
     * for session time out
     * //300000; // 5 min = 5 * 60 * 1000 ms
     */
    public static final long SESSION_TIMEOUT = 30000;

    // values based on string: @string/minimum_amount_string
    public static final int MINIMUM_PAYABLE_AMOUNT_START_OFFSET = 22;
    public static final int MINIMUM_PAYABLE_AMOUNT_START_OFFSET_WITH_RS = 26;
    public static final int MINIMUM_PAYABLE_AMOUNT_START_OFFSET_FOR_DATE = 3;

    // values based on string: @string/emi_conversion_text
    public static final int NUMBER_OF_TRANSACTIONS_FOR_EMI_END_OFFSET = 13;
    public static final int NUMBER_OF_TRANSACTIONS_FOR_EMI_START_OFFSET = 0;

    public static final int EMI_AMOUNT_FOR_TRANSACTION_DETAIL_START_OFFSET = 45;
    public static final int EMI_AMOUNT_FOR_TRANSACTION_DETAIL_END_OFFSET = 63;

    // values based on string: @string/last_bill_due_day_text
    public static final int DUE_DAYS_START_OFFSET = 6;

    public static final String EMAIL = "email";
    public static final String MOBILE = "mobile";
    public static final String PASSWORD = "password";
    public static final String DEVICE_ID = "deviceId";
    public static final String DEVICE_TYPE = "deviceType";
    public static final String ANDROID = "android";
    public static final String STATUS = "status";
    public static final String MESSAGE = "msg";
    public static final String CREDIT_INFO = "credit_info_data";

    public static final String CAT_SHOPPING = "shopping";
    public static final String CAT_CLOTHING = "Clothing";

    public static final String NULL = "null";
    public static final String OTHERS = "others";
    public static final String TYPE_REFUND = "refund";

    public static final String USER_ID = "user_id";
    public static final Object OTP = "otp";
    public static final String RESULT = "result";
    public static final String BODY = "body";

    public static final String TRANSACTION = "cc_transaction_detail";


    public static final String SECRET_MESSAGE = "Very secret message";
    public static final String KEY_NAME = "my_key";

    /*
     * Preference keys for the project
     */
    public static final String PREFERENCE_NOTIFICATION = "preference_notification";
    public static final String PREFERENCE_INACTIVITY_TIMEOUT = "preference_inactivity_timeout";
    public static final String PREFERENCE_FINGERPRINT = "preference_fingerprint";


    public static final String CARD_NUMBER = "card_number";
    public static final String FLOW_TYPE = "flow_type";
    public static final String FLOW_TYPE_SIGN_UP = "sign_up";
    public static final String FLOW_TYPE_FORGOT_PIN = "forgot_pin";
    public static final String FLOW_TYPE_FORGOT_PASSWORD = "forgot_password";
    public static final String PIN = "pin";
    public static final String QUESTIONS_ARRAY = "questions";
    public static final String QUESTION1 = "question1";
    public static final String QUESTION2 = "question2";
    public static final String QUESTION3 = "question3";
    public static final String ANSWER1 = "answer1";
    public static final String ANSWER2 = "answer2";
    public static final String ANSWER3 = "answer3";
    public static final String QUESTION = "security_question";
    public static final String ANSWER = "security_questions_answer";
    public static final String SENDER_ID = "92853391562";

    public static final String LOGIN_SUCCESSFUL = "login_success";
    public static final String EVENT_FLAG = "eventFlag";
    public static final String EVENT_1 = "1";
    public static final String EVENT_2 = "2";
    public static final String REPORT_ID = "reportId";
    public static final String DURATION = "params";
    public static final String ERROR_CODE = "errorCode";
}
