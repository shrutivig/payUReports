package reports.payu.com.app.payureports;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import reports.payu.com.app.payureports.Model.ReportList;

public class HomeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private final String REPORT_TYPE_CUSTOM = "custom";
    private final String REPORT_TYPE_GENERIC = "generic";
    private GoogleApiClient mGoogleApiClient;
    ProgressDialog ringProgressDialog;
    public String email;
    private Button signOutButton;
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
        ringProgressDialog.setCancelable(true);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent() != null) {
            email = getIntent().getStringExtra(Constants.EMAIL);
        }
        //       makeLoginCall();

        reportListView = (ListView) findViewById(R.id.report_list);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signOutButton = (Button) findViewById(R.id.filter_button);

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

                Intent intent = null;
                if (parsedReportList.getList().get(position).getReportType().equals(REPORT_TYPE_CUSTOM))
                    intent = new Intent(HomeActivity.this, ReportActivity.class);
                else if (parsedReportList.getList().get(position).getReportType().equals(REPORT_TYPE_GENERIC))
                    intent = new Intent(HomeActivity.this, TableReportActivity.class);
                intent.putExtra(Constants.REPORT_ID, parsedReportList.getList().get(position).getId());
                intent.putExtra(Constants.EMAIL, email);
                intent.putExtra(Constants.REPORT_NAME, parsedReportList.getList().get(position).getHeading());
                startActivity(intent);

            }
        });
    }

    private void makeLoginCall() {

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


                    if (event.getValue().toString().contains("Server error")) {
                        handleStatus("XYZ", event.getValue().toString());
                    } else {
                        JSONObject temp = (JSONObject) event.getValue();
                        String errorCode = temp.optString(Constants.ERROR_CODE, "XYZ");
                        String errorMessage = temp.optString(Constants.MESSAGE, "XYZ");
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
                Toast.makeText(this, "Login Unsuccessful!", Toast.LENGTH_SHORT).show();
                logoutUserFromApp();
                break;
            case "ER102":
                Toast.makeText(this, "Login Unsuccessful!", Toast.LENGTH_SHORT).show();
                logoutUserFromApp();
                break;
            case "ER103":
                Toast.makeText(this, "Login Unsuccessful!", Toast.LENGTH_SHORT).show();
                logoutUserFromApp();
                break;
            case "ER104":
                Toast.makeText(this, "Login Unsuccessful!", Toast.LENGTH_SHORT).show();
                logoutUserFromApp();
                break;
            case "ER105":
                Toast.makeText(this, "Login Unsuccessful!", Toast.LENGTH_SHORT).show();
                logoutUserFromApp();
                break;
            case "ER106":
                Toast.makeText(this, "Login Unsuccessful!", Toast.LENGTH_SHORT).show();
                logoutUserFromApp();
                break;
            default:
                Toast.makeText(this, "Login Unsuccessful!", Toast.LENGTH_SHORT).show();
                logoutUserFromApp();
                break;
        }


    }

}
