package reports.payu.com.app.payureports;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import reports.payu.com.app.payureports.Model.ReportList;

public class HomeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    ProgressDialog ringProgressDialog;
    public String email;
    private Button signOutButton;
    private ReportList parsedReportList;

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
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
        makeLoginCall();

       /* ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);*/

        findViewById(R.id.report1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ReportActivity.class));
            }
        });
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
                    //signOut();
                    // LoginSignUpActivity.signOut();
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(Status status) {
                                    launchLoginSignupActivity();
                                }
                            });
                }
            });
        }
        findViewById(R.id.disconnect_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void makeLoginCall() {

        Session.getInstance(this).login(email);
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
                JSONObject temp = (JSONObject) event.getValue();
                String errorCode = temp.optString(Constants.ERROR_CODE, "XYZ");
                String errorMessage = temp.optString(Constants.MESSAGE, "XYZ");
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

                        ((ListView) findViewById(R.id.report_list)).setAdapter(reportsAdapter);
                    }

                } else {
                    handleStatus(errorCode, errorMessage);
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
                signOutButton.callOnClick();
                break;
            case "ER102":
                Toast.makeText(this, "Login Unsuccessful!", Toast.LENGTH_SHORT).show();
                signOutButton.callOnClick();
                break;
            case "ER103":
                Toast.makeText(this, "Login Unsuccessful!", Toast.LENGTH_SHORT).show();
                signOutButton.callOnClick();
                break;
            case "ER104":
                Toast.makeText(this, "Login Unsuccessful!", Toast.LENGTH_SHORT).show();
                signOutButton.callOnClick();
                break;
            case "ER105":
                Toast.makeText(this, "Login Unsuccessful!", Toast.LENGTH_SHORT).show();
                signOutButton.callOnClick();
                break;
            case "ER106":
                Toast.makeText(this, "Login Unsuccessful!", Toast.LENGTH_SHORT).show();
                signOutButton.callOnClick();
                break;
            default:
                Toast.makeText(this, "Login Unsuccessful!", Toast.LENGTH_SHORT).show();
                signOutButton.callOnClick();
                break;
        }


    }


}
