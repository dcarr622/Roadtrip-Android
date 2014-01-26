package com.suchroadtrip.app.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.suchroadtrip.app.R;
import com.suchroadtrip.app.activities.LoginToTwitter;
import com.suchroadtrip.app.activities.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Created by david on 1/25/14.
 */
public class SocialFragment extends Fragment {

    /* Twitter */

    public static final String TAG = "SocialFragment";

    public static Twitter twitter;
    public static final String AUTHENTICATION_URL_KEY = "AUTHENTICATION_URL_KEY";
    public static final int LOGIN_TO_TWITTER_REQUEST = 0;

    /*Shared prefs*/
    private static final String APP_SHARED_PREFS = "roadtrip_preferences";
    public static SharedPreferences sharedPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_social, container, false);

        view.findViewById(R.id.twitter_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginToTwitter();
            }
        });

        view.findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainActivityIntent = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(mainActivityIntent);
            }
        });

        /*Get shared prefs*/
        sharedPrefs = getActivity().getApplicationContext().getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);

        return view;
    }

    private void loginToTwitter() {
        GetRequestTokenTask getRequestTokenTask = new GetRequestTokenTask();
        getRequestTokenTask.execute();
    }

    public static Fragment newInstance() {
        return new SocialFragment();
    }

    public class GetRequestTokenTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("Twitter", "doInBackground");
            twitter = TwitterFactory.getSingleton();
            twitter.setOAuthConsumer(
                    getString(R.string.TWITTER_CONSUMER_KEY),
                    getString(R.string.TWITTER_CONSUMER_SECRET));

            try {
                RequestToken requestToken = twitter.getOAuthRequestToken(
                        getString(R.string.TWITTER_CALLBACK_URL));
                launchLoginWebView(requestToken);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("SocialFragment", "onActivityResult");
        if (requestCode == LOGIN_TO_TWITTER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                getAccessToken(data.getStringExtra(LoginToTwitter.CALLBACK_URL_KEY));
            }
        }
    }


    private void launchLoginWebView(RequestToken requestToken) {
        Log.d("Twitter", "launchLoginWebView");
        Intent intent = new Intent(getActivity(), LoginToTwitter.class);
        intent.putExtra(AUTHENTICATION_URL_KEY, requestToken.getAuthenticationURL());
        startActivityForResult(intent, LOGIN_TO_TWITTER_REQUEST);
    }

    // getAccessToken also has to be handle in AsyncTask
    public void getAccessToken(String callbackUrl) {
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
