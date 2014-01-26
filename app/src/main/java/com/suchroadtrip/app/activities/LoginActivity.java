package com.suchroadtrip.app.activities;

import android.app.ActionBar;
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
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;

import com.suchroadtrip.app.R;
import com.suchroadtrip.app.fragments.LoginFragment;
import com.suchroadtrip.app.fragments.RegisterFragment;
import com.suchroadtrip.app.fragments.SocialFragment;

import java.util.Locale;

import twitter4j.auth.AccessToken;

import static com.suchroadtrip.app.fragments.SocialFragment.twitter;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity implements ActionBar.TabListener {

    /**
     * The default email to populate the email field with.
     */

    public static final String AUTHENTICATION_URL_KEY = "AUTHENTICATION_URL_KEY";
    public static final int LOGIN_TO_TWITTER_REQUEST = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v13.app.FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    /*Shared prefs*/
    private static final String APP_SHARED_PREFS = "roadtrip_preferences";
    public static SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        /*Get shared prefs*/
        sharedPrefs = getApplicationContext().getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.login_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    // add onActivityResult method to MainActivity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    /**
     * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0) {
                return LoginFragment.newInstance();
            }
            if (position == 1) {
                return RegisterFragment.newInstance();
            }
            if (position == 2) {
                return SocialFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.login_title).toUpperCase(l);
                case 1:
                    return getString(R.string.register_title).toUpperCase(l);
                case 2:
                    return getString(R.string.social_title).toUpperCase(l);
            }
            return null;
        }

    }



    public class GetAccessTokenTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            String verifier = strings[0];
            try {
                AccessToken accessToken = twitter.getOAuthAccessToken(verifier);
                Log.d("LoginActivity-Twitter", accessToken.getToken());
                Log.d("LoginActivity-Twitter", accessToken.getScreenName());
            } catch (Exception e) {
                // handle exceptions
            }
            return null;
        }
    }

}
