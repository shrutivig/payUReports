package reports.payu.com.app.payureports.Utils;

import android.content.Context;
import android.util.Log;

import java.util.Map;


/**
 * Created by shruti.vig on 3/14/16.
 */

public class Logger {

    private static String LOG_PREFIX = "PAYU_CC_";
    private static final boolean LOG_ENABLE = true;
    private static final boolean DETAIL_ENABLE = false;
    private static int EVENT_NUMBER = -1;

    private static final int MAX_LOG_TAG_LENGTH = 23;
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();

    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }
        return LOG_PREFIX + str;
    }

    /**
     * Don't use this when obfuscating class names!
     */
    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }

    private Logger() {
    }

    public static void setTAG(String TAG) {
        LOG_PREFIX = TAG;
    }

    private static String buildMsg(String msg) {
        StringBuilder buffer = new StringBuilder();

        if (DETAIL_ENABLE) {
            final StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];

            buffer.append("[ ");
            buffer.append(Thread.currentThread().getName());
            buffer.append(": ");
            buffer.append(stackTraceElement.getFileName());
            buffer.append(": ");
            buffer.append(stackTraceElement.getLineNumber());
            buffer.append(": ");
            buffer.append(stackTraceElement.getMethodName());
        }

        buffer.append("() ] --> ");

        buffer.append(msg);

        return buffer.toString();
    }


    public static void v(String msg) {

        if (LOG_ENABLE && Log.isLoggable(LOG_PREFIX, Log.VERBOSE)) {
            Log.v(LOG_PREFIX, buildMsg(msg));
        }
    }


    public static void d(String msg) {

        if (LOG_ENABLE && Log.isLoggable(LOG_PREFIX, Log.DEBUG)) {
            Log.d(LOG_PREFIX, buildMsg(msg));
        }
    }


    public static void i(String msg) {

        if (LOG_ENABLE && Log.isLoggable(LOG_PREFIX, Log.INFO)) {
            Log.i(LOG_PREFIX, buildMsg(msg));
        }
    }


    public static void w(String msg) {

        if (LOG_ENABLE && Log.isLoggable(LOG_PREFIX, Log.WARN)) {
            Log.w(LOG_PREFIX, buildMsg(msg));
        }
    }


    public static void w(String msg, Exception e) {

        if (LOG_ENABLE && Log.isLoggable(LOG_PREFIX, Log.WARN)) {
            Log.w(LOG_PREFIX, buildMsg(msg), e);
        }
    }


    public static void e(String msg) {

        if (LOG_ENABLE && Log.isLoggable(LOG_PREFIX, Log.ERROR)) {
            Log.e(LOG_PREFIX, buildMsg(msg));
        }
    }


    public static void e(String msg, Exception e) {

        if (LOG_ENABLE && Log.isLoggable(LOG_PREFIX, Log.ERROR)) {
            Log.e(LOG_PREFIX, buildMsg(msg), e);
        }
    }

    public static void v(String TAG, String msg) {

        if (LOG_ENABLE && Log.isLoggable(makeLogTag(TAG), Log.VERBOSE)) {
            Log.v(makeLogTag(TAG), buildMsg(msg));
        }
    }


    public static void d(String TAG, String msg) {

        if (LOG_ENABLE && Log.isLoggable(makeLogTag(TAG), Log.DEBUG)) {
            Log.d(makeLogTag(TAG), buildMsg(msg));
        }
    }


    public static void i(String TAG, String msg) {

        if (LOG_ENABLE && Log.isLoggable(makeLogTag(TAG), Log.INFO)) {
            Log.i(makeLogTag(TAG), buildMsg(msg));
        }
    }


    public static void w(String TAG, String msg) {

        if (LOG_ENABLE && Log.isLoggable(makeLogTag(TAG), Log.WARN)) {
            Log.w(makeLogTag(TAG), buildMsg(msg));
        }
    }


    public static void w(String TAG, String msg, Exception e) {

        if (LOG_ENABLE && Log.isLoggable(makeLogTag(TAG), Log.WARN)) {

            Log.w(makeLogTag(TAG), buildMsg(msg), e);
        }
    }


    public static void e(String TAG, String msg) {

        if (LOG_ENABLE && Log.isLoggable(makeLogTag(TAG), Log.ERROR)) {
            Log.e(makeLogTag(TAG), buildMsg(msg));
        }
    }


    public static void e(String TAG, String msg, Exception e) {

        if (LOG_ENABLE && Log.isLoggable(makeLogTag(TAG), Log.ERROR)) {
            Log.e(makeLogTag(TAG), buildMsg(msg), e);
        }
    }

    /**
     * This method receives values to be saved as server logs in the application.
     * The value gets saved in ServerLogSharedPreference file.
     *
     * @param context Calling activity context
     * @param value   String value to be saved
     * @return void
     */

    /**
     * This method dumps all the values present in shared preference in the form of a Map of String key-values.
     * Also, clears the shared Preference file after dumping data.
     *
     * @param context Calling activity context
     * @return Map<String, String>
     */

    public static Map<String, String> getServerLogFromCCLogger(Context context) {
        return null;
    }


}
