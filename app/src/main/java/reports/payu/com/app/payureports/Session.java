package reports.payu.com.app.payureports;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.conn.ConnectTimeoutException;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import reports.payu.com.app.payureports.Utils.Helper;
import reports.payu.com.app.payureports.Utils.Logger;
import reports.payu.com.app.payureports.Utils.SharedPrefsUtils;


/**
 * Created by sagar.chauhan on 3/14/16.
 */
public class Session {

    public static final String TAG = Session.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private final Handler handler;
    private final EventBus eventBus;
    private final Context mContext;
    private static Session INSTANCE = null;
    private final SessionData mSessionData = new SessionData();
    Long start = null, end = null, diff = null;

    private Session(Context context) //Set Token and User from SharedPrefs in constructor, very clever actually :P
    {
        eventBus = EventBus.getDefault();
        // the handler ensures that all operations happen in the ui thread and
        // not in the background thread. This may very have changed after the
        // removal of dependence on Tasks Class and usage of EventBus, but it
        // hasn't been verified.
        handler = new Handler(Looper.getMainLooper());
        mContext = context;
        String mUserId = SharedPrefsUtils.getStringPreference(mContext, Constants.USER_ID, Constants.SP_USER_NAME);
        if (mUserId != null) {
            mSessionData.setUserId(mUserId);
        }
    }

    public static synchronized Session getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new Session(context);
        }
        return INSTANCE;
    }

    public interface Task {
        void onSuccess(JSONObject object);

        void onSuccess(String response);

        void onError(Throwable throwable);

        void onProgress(int percent);
    }

    private class SessionData {
        private String token, userId = null;

        public SessionData() {
            reset();
        }

        public String getUser() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public void reset() {
            token = null;
        }

    }


    private static String getAbsoluteUrl(String relativeUrl) {
        if (relativeUrl.equals("/payuPaisa/up.php"))
            return Constants.BASE_URL_IMAGE + relativeUrl;
        else
            return Constants.BASE_URL + relativeUrl;
    }

    public RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TextUtils.isEmpty(Session.TAG) ? TAG : Session.TAG);
        getRequestQueue(mContext).add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public String getToken() {
        return mSessionData.getToken();
    }

    public void setUserId(String user) {
        mSessionData.setUserId(user);
    }

    public String getUserId() {
        return mSessionData.getUser();
    }


    /**
     * Get the cached login state
     */
    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public void setToken(String token) {
        mSessionData.setToken(token);
    }


    public void reset() {
        mSessionData.reset();
    }

    public void postFetch(final String url, final Map<String, String> params, final Task task, final int method) {

        if (Constants.DEBUG) {
            Logger.d(Session.TAG, "SdkSession.postFetch: " + url + " " + params + " " + method);
        }

        StringRequest myRequest = new StringRequest(method, getAbsoluteUrl(url), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                diff = System.currentTimeMillis() - start;

                Logger.i(Session.TAG, "URL=" + url + "Time=" + diff);

                if (Constants.DEBUG) {
                    Logger.d(Session.TAG, "SdkSession.postFetch.onSuccess: " + url + " " + params + " " + method + ": " + response);
                }

                try {
                    JSONObject object = new JSONObject(response);
                    if (object.has("error")) {
                        onFailure(object.getString("error"), new Throwable(object.getString("error")));
                    } else {
                        runSuccessOnHandlerThread(task, object);
                    }
                } catch (JSONException e) {
                    // maybe this is a string?
                    onFailure(e.getMessage(), e);
                }
            }

            public void onFailure(String msg, Throwable e) {
                if (Constants.DEBUG) {
                    Log.e(Session.TAG, "Session...new JsonHttpResponseHandler() {...}.onFailure: " + e.getMessage() + " " + msg);
                }
                if (msg.contains("401")) {
                    /*not required in app*/

                    // logout("force");
                    cancelPendingRequests(TAG);

                }
                runErrorOnHandlerThread(task, e);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (Constants.DEBUG) {
                    Log.e(Session.TAG, "Session...new JsonHttpResponseHandler() {...}.onFailure: " + error.getMessage());
                }
                if (error != null && error.networkResponse != null && error.networkResponse.statusCode == 401) {

                    //  logout("force");

                }
                runErrorOnHandlerThread(task, error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                params.put(Constants.DEVICE_ID, Helper.getAndroidID(mContext));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "PayUMoneyAPP");
                if (getToken() != null) {
                    params.put("Authorization", "Bearer " + getToken());
                } else {
                    params.put("Accept", "*/*;");
                }
                return params;
            }

            @Override
            public String getBodyContentType() {
                if (getToken() == null) {
                    return "application/x-www-form-urlencoded";
                } else {
                    return super.getBodyContentType();
                }
            }
        };
        myRequest.setShouldCache(false);
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(myRequest);
        start = System.currentTimeMillis();

    }

    private String getParameters(Map<String, String> params) {
        String parameters = "?";
        Iterator it = params.entrySet().iterator();
        boolean isFirst = true;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (isFirst) {
                parameters = parameters.concat(pair.getKey() + "=" + pair.getValue());
            } else {
                parameters = parameters.concat("&" + pair.getKey() + "=" + pair.getValue());
            }
            isFirst = false;
            it.remove();
        }
        return parameters;
    }

    private void runErrorOnHandlerThread(final Task task, final Throwable e) {
        if (e instanceof ConnectTimeoutException || e instanceof SocketTimeoutException) {
            final Throwable x = new Throwable("time out error");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    task.onError(x);
                }
            });
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    task.onError(e);
                }
            });
        }
    }

    private void runSuccessOnHandlerThread(final Task task, final JSONObject jsonObject) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                task.onSuccess(jsonObject);
            }
        });
    }

    private void runSuccessOnHandlerThread(final Task task, final String response) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                task.onSuccess(response);
            }
        });
    }

    public void login(String email, String pin) {

        final Map p = new HashMap<>();
        p.put(Constants.EMAIL, email);
        p.put(Constants.PASSWORD, pin);

        postFetch("/auth/payu/login", p, new Task() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    int status = jsonObject.getInt(Constants.STATUS);
                    if (status < 0) {
                        eventBus.post(new CobbocEvent(CobbocEvent.LOGIN, false, jsonObject));
                    } else {
                        String token = jsonObject.getJSONObject(Constants.RESULT).getJSONObject(Constants.BODY).getString(Constants.ACCESS_TOKEN);
                        /*Token will be alive till the session is alive*/
                        setToken(token);
                        eventBus.post(new CobbocEvent(CobbocEvent.LOGIN, true, jsonObject.getString(Constants.MESSAGE)));
                    }
                } catch (JSONException e) {
                    eventBus.post(new CobbocEvent(CobbocEvent.LOGIN, false));
                }
            }

            @Override
            public void onSuccess(String response) {

            }

            @Override
            public void onError(Throwable throwable) {
                eventBus.post(new CobbocEvent(CobbocEvent.LOGIN, false, "An error occurred while trying to login. Please try again later."));
            }

            @Override
            public void onProgress(int percent) {

            }
        }, Request.Method.POST);
    }




}
