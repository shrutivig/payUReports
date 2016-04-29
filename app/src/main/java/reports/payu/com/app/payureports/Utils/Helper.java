package reports.payu.com.app.payureports.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


/**
 * Created by sagar.chauhan on 3/14/16.
 */
public class Helper {

    private static long mLastClickTime = 0;
    private static ProgressDialog progressDialog;

    public static boolean isConnected(Context c) {
        ConnectivityManager conMgr = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr != null) {
            NetworkInfo resultTypeMobile = conMgr.getNetworkInfo(0);
            NetworkInfo resultTypeWifi = conMgr.getNetworkInfo(1);
            if (((resultTypeMobile != null && resultTypeMobile.isConnectedOrConnecting())) || (resultTypeWifi != null && resultTypeWifi.isConnectedOrConnecting())) {
                return true;
            } else
                return false;
        } else {
            return false;
        }
    }


    public static void dismissProgressDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
        progressDialog = null;
    }

    public static void showProgressDialog(Context mActivity, String strMessage, boolean isCancellable) {
        if (progressDialog != null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        } else {
            progressDialog = new ProgressDialog(mActivity);
        }
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setMessage((strMessage.equals(null) ? "Loading..." : strMessage));
        progressDialog.setCancelable(isCancellable);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public static boolean isValidClick() {

        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return false;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        return true;
    }


    public static String getAndroidID(Context context) {

        if (context == null)
            return "";
        String device_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return device_id;
    }

    public static String getAppVersion(Context mContext) {

        try {
            PackageInfo pInfo = mContext.getApplicationContext().getPackageManager().getPackageInfo(mContext.getApplicationContext().getPackageName(), 0);
            String currentVersion = pInfo.versionName;
            return currentVersion;
        } catch (Exception e) {
//Start the next activity
            return "";
        }

    }

    public static void hideKeyboardIfShown(Activity ctx) {

        InputMethodManager inputMethodManager = (InputMethodManager) ctx.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = ctx.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(ctx);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboardIfShown(Activity act, View view) {

        InputMethodManager inputMethodManager = (InputMethodManager) act.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
