package reports.payu.com.app.payureports;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import reports.payu.com.app.payureports.Model.ReportList;

public class HomeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private final String REPORT_TYPE_CUSTOM = "custom";
    private final String REPORT_TYPE_GENERIC = "generic";
    private GoogleApiClient mGoogleApiClient;
    ProgressDialog ringProgressDialog;
    public String email;
    private ReportList parsedReportList;
    private ListView reportListView;

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeLoginCall();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ringProgressDialog = ProgressDialog.show(this, "Please wait ...", "Authenticating with server", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.setCanceledOnTouchOutside(false);

        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            email = getIntent().getStringExtra(Constants.EMAIL);
            if (!Fabric.isInitialized())
                Fabric.with(this, new Crashlytics());
            Crashlytics.setUserEmail(email);
        }

        reportListView = (ListView) findViewById(R.id.report_list);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Button signOutButton = (Button) findViewById(R.id.filter_button);

        if (signOutButton != null) {
            signOutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(HomeActivity.this)
                            .setIcon(R.drawable.alert)
                            .setTitle("Sign-Out")
                            .setCancelable(false)
                            .setMessage("Are you sure you want to sign-out from this application?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    logoutUserFromApp();
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });
        }

        findViewById(R.id.disconnect_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        reportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!isInternetConnected(HomeActivity.this)) {
                    Snackbar snack = Snackbar.make(findViewById(R.id.home_layout), "The Internet connection appears to be offline.", Snackbar.LENGTH_SHORT);
                    View snackview = snack.getView();
                    TextView tv = (TextView) snackview.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(ContextCompat.getColor(HomeActivity.this, android.R.color.white));
                    snack.show();
                } else {
                    Intent intent = null;
                    if (parsedReportList.getList().get(position).getReportType().equals(REPORT_TYPE_CUSTOM))
                        intent = new Intent(HomeActivity.this, ReportActivity.class);
                    else if (parsedReportList.getList().get(position).getReportType().equals(REPORT_TYPE_GENERIC))
                        intent = new Intent(HomeActivity.this, TableReportActivity.class);
                    if (intent != null) {
                        intent.putExtra(Constants.REPORT_ID, parsedReportList.getList().get(position).getId());
                        intent.putExtra(Constants.EMAIL, email);
                        intent.putExtra(Constants.REPORT_NAME, parsedReportList.getList().get(position).getHeading());
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void makeLoginCall() {

        if (!isInternetConnected(this)) {
            Snackbar snack = Snackbar.make(findViewById(R.id.home_layout), "The Internet connection appears to be offline.", Snackbar.LENGTH_SHORT);
            View snackview = snack.getView();
            TextView tv = (TextView) snackview.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(ContextCompat.getColor(HomeActivity.this, android.R.color.white));
            snack.show();
        } else
            Session.getInstance(this).login(email);
    }

    private void logoutUserFromApp() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        launchLoginSignupActivity();
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void launchLoginSignupActivity() {

        Intent i = new Intent(this, LoginSignUpActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Subscribe
    public void onEventMainThread(CobbocEvent event) {
        switch (event.getType()) {
            case CobbocEvent.LOGIN:
                ringProgressDialog.dismiss();

                if (event.getStatus()) {
                    JSONObject jsonObject = (JSONObject) event.getValue();
                    parsedReportList = (ReportList) Session.getInstance(this).getParsedResponseFromGSON(jsonObject, Session.dataType.ReportList);

                    if (parsedReportList != null) {
                        List reportList = parsedReportList.getList();
                        ArrayList<String> reportsName = new ArrayList<>();
                        for (int i = 0; i < reportList.size(); i++) {
                            reportsName.add(((ReportList.ListItem) reportList.get(i)).getHeading());
                        }
                        ArrayAdapter<String> reportsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, reportsName);
                        reportListView.setAdapter(reportsAdapter);
                    }

                } else {
                    if (event.getValue() instanceof String) {
                        handleStatus("XYZ", event.getValue().toString());
                    } else {
                        JSONObject temp = (JSONObject) event.getValue();

                        String errorMessage = null;
                        String errorCode = null;
                        try {
                            errorCode = temp.optString(Constants.ERROR_CODE, temp.getString("errorCode").toString());
                            errorMessage = temp.optString(Constants.MESSAGE, temp.getString("msg").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            errorCode = "XYZ";
                            errorMessage = "XYZ";
                        }
                        handleStatus(errorCode, errorMessage);
                    }
                }
                break;

            default:
                // we don't do anything else here
        }
    }

    private void handleStatus(String errorCode, String errorMessage) {

        switch (errorCode) {
            case "ER101":
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                logoutUserFromApp();
                break;
            case "ER102":
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                logoutUserFromApp();
                break;
            case "ER103":
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                logoutUserFromApp();
                break;
            case "ER104":
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                logoutUserFromApp();
                break;
            case "ER105":
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                logoutUserFromApp();
                break;
            case "ER106":
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                logoutUserFromApp();
                break;
            default:
                Toast.makeText(this, "Login Unsuccessful!", Toast.LENGTH_SHORT).show();
                logoutUserFromApp();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean isInternetConnected(Context c) {
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

}
