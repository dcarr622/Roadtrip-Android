package com.suchroadtrip.app.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.PersistentCookieStore;
import com.suchroadtrip.app.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
* Created by david on 1/25/14.
*/
public class RegisterFragment extends Fragment {
    public static Fragment newInstance() {
        return new RegisterFragment();
    }

    // Values for email and password at the time of the login attempt.
    private String mUser = "";
    private String mEmail = "";
    private String mPassword = "";

    private UserRegisterTask mAuthTask = null;

    /*Shared prefs*/
    private static final String APP_SHARED_PREFS = "roadtrip_preferences";
    public static SharedPreferences sharedPrefs;


    // UI references.
    private EditText mEmailView;
    private EditText mUserView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;

    /* HTTP stuff */

    DefaultHttpClient httpClient;
    PersistentCookieStore cookieStore;

    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Set up the login form.
        mUserView = (EditText) view.findViewById(R.id.register_user);
        mUserView.setText(mUser);

          /*Get shared prefs*/
        sharedPrefs = getActivity().getApplicationContext().getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);


        mEmailView = (EditText) view.findViewById(R.id.register_email);
        mEmailView.setText(mEmail);

        mPasswordView = (EditText) view.findViewById(R.id.register_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });


        httpClient = new DefaultHttpClient();
        cookieStore = new PersistentCookieStore(getActivity().getApplicationContext());
        httpClient.setCookieStore(cookieStore);

        view.findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mLoginFormView = view.findViewById(R.id.register_form);
        mLoginStatusView = view.findViewById(R.id.register_status);
        mLoginStatusMessageView = (TextView) view.findViewById(R.id.register_status_message);


        return view;
    }

    public void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserView.setError(null);
        mPasswordView.setError(null);
        mEmailView.setError(null);

        // Store values at the time of the login attempt.
        mUser = mUserView.getText().toString();
        mPassword = mPasswordView.getText().toString();
        mEmail = mEmailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!mEmail.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
            mAuthTask = new UserRegisterTask();
            mAuthTask.execute((Void) null);
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            HttpPost postRequest = new HttpPost(getString(R.string.roadtrip_signup_url));

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("username", mUser));
            nameValuePairs.add(new BasicNameValuePair("password", mPassword));
            nameValuePairs.add(new BasicNameValuePair("email", mEmail));

            editor = sharedPrefs.edit();
            editor.putString("username", mUser);
            editor.putString("password", mPassword);
            editor.commit();

            HttpResponse response = null;
            HttpEntity entity = null;
            List<Cookie> cookiejar = null;

            try {
                postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                response = httpClient.execute(postRequest);
                entity = response.getEntity();
                cookiejar = httpClient.getCookieStore().getCookies();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (entity != null) {
                String retSrc = null;
                String status = null;
                try {
                    retSrc = EntityUtils.toString(entity);
                    JSONObject result = new JSONObject(retSrc); //Convert String to JSON Object
                    status = result.getString("status");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // parsing JSON
                if (status.equals("ok")) {
                    Log.v("Login", "ok");
                    editor = sharedPrefs.edit();
                    editor.putString("userCookie", cookiejar.get(0).toString());
                    editor.commit();

                    HttpGet httpget = new HttpGet(getString(R.string.roadtrip_user_check));

                    try {
                        HttpResponse userCheckResponse = httpClient.execute(httpget);
                        String userCheckresp = EntityUtils.toString(userCheckResponse.getEntity());
                        JSONObject getResult = new JSONObject(userCheckresp);
                        String username = getResult.getString("username");
                        String userID = getResult.getString("_id");
                        editor = sharedPrefs.edit();
                        editor.putString("_id", userID);
                        editor.commit();
                        Log.v("usernameCheck", username);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                else if (status.equals("err")) {
                    Log.v("Login", "err");
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                editor = sharedPrefs.edit();
                editor.putBoolean("userLoggedInState", true);
//                editor.putInt("currentLoggedInUserId", userId);
                editor.commit();
                FragmentManager fragmentManager = getFragmentManager();
                Fragment socialFragment = SocialFragment.newInstance();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(android.R.id.content, socialFragment);
                fragmentTransaction.commit();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

    }

}
