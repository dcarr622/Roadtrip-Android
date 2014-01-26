package com.suchroadtrip.app.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.suchroadtrip.app.R;
import com.suchroadtrip.app.activities.LoginActivity;
import com.suchroadtrip.app.activities.LoginToTwitter;
import com.suchroadtrip.app.activities.MainActivity;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

/**
 * Created by david on 1/25/14.
 */
public class SocialFragment extends Fragment {

    /* Twitter */

    public static Twitter twitter;

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
        return view;
    }

    private void loginToTwitter() {
        GetRequestTokenTask getRequestTokenTask = new GetRequestTokenTask();
        getRequestTokenTask.execute();
    }

    public static Fragment newInstance() {
        return new SocialFragment();
    }

    public class GetRequestTokenTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPostExecute(final Boolean success) {
            getActivity().finish();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
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
            return true;
        }
    }


    private void launchLoginWebView(RequestToken requestToken) {
        Log.d("Twitter", "launchLoginWebView");
        Intent intent = new Intent(getActivity(), LoginToTwitter.class);
        intent.putExtra(LoginActivity.AUTHENTICATION_URL_KEY, requestToken.getAuthenticationURL());
        startActivityForResult(intent, LoginActivity.LOGIN_TO_TWITTER_REQUEST);
    }



}
