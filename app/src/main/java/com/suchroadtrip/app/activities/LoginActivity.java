package com.suchroadtrip.app.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.suchroadtrip.app.R;
import com.suchroadtrip.app.fragments.LoginFragment;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import twitter4j.auth.AccessToken;

import static com.suchroadtrip.app.fragments.SocialFragment.twitter;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {

    /**
     * The default email to populate the email field with.
     */

    public static final String AUTHENTICATION_URL_KEY = "AUTHENTICATION_URL_KEY";
    public static final int LOGIN_TO_TWITTER_REQUEST = 0;

    /*Shared prefs*/
    private static final String APP_SHARED_PREFS = "roadtrip_preferences";
    public static SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        /*Get shared prefs*/
        sharedPrefs = getApplicationContext().getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);

        FragmentManager fragmentManager = getFragmentManager();
        Fragment login = LoginFragment.newInstance();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(android.R.id.content, login);
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("LoginActivity", "onActivityResult");
        if (requestCode == LOGIN_TO_TWITTER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                getAccessToken(data.getStringExtra(LoginToTwitter.CALLBACK_URL_KEY));
            }
        }
    }

    // getAccessToken also has to be handle in AsyncTask
    private void getAccessToken(String callbackUrl) {
        Uri uri = Uri.parse(callbackUrl);
        String verifier = uri.getQueryParameter("oauth_verifier");

        GetAccessTokenTask getAccessTokenTask = new GetAccessTokenTask();
        getAccessTokenTask.execute(verifier);
    }

    public class GetAccessTokenTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            String verifier = strings[0];
            try {
                AccessToken accessToken = twitter.getOAuthAccessToken(verifier);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
                nameValuePairs.add(new BasicNameValuePair("twitterId", String.valueOf(accessToken.getUserId())));
                nameValuePairs.add(new BasicNameValuePair("twitterUsername", accessToken.getScreenName()));
                nameValuePairs.add(new BasicNameValuePair("userId", sharedPrefs.getString("_id", "null")));
                nameValuePairs.add(new BasicNameValuePair("oauthToken", accessToken.getToken()));
                nameValuePairs.add(new BasicNameValuePair("oauthSecret", accessToken.getTokenSecret()));

                HttpPost postRequest = new HttpPost(getString(R.string.twitter_url));
                DefaultHttpClient httpClient = new DefaultHttpClient();

                HttpResponse response = null;
                postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                response = httpClient.execute(postRequest);

                Log.d("TwitterPost", String.valueOf(response.getStatusLine().getStatusCode()));


            } catch (Exception e) {
                // handle exceptions
            }
            return null;
        }
    }

}
