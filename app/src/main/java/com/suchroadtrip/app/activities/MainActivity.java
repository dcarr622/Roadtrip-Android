package com.suchroadtrip.app.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.suchroadtrip.app.R;
import com.suchroadtrip.app.data.TripAdapter;
import com.suchroadtrip.app.fragments.NewTripFragment;
import com.suchroadtrip.app.fragments.RoadtripFeedFragment;
import com.suchroadtrip.app.fragments.RoadtripMapFragment;
import com.suchroadtrip.lib.RTApi;
import com.suchroadtrip.lib.RTContentProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity implements ActionBar.OnNavigationListener, LoaderManager.LoaderCallbacks<Cursor>, RTApi.LoginCallback {

    private static final String TAG = "wowsuchtag";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * com.suchroadtrip.app.fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /*For timer*/
    Handler timerHandler = null;
    private Date time = new Date(0);
    private boolean moving = false;
    private double totalDistance = 0.01;
    private Location lastLocation = null;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss", Locale.US);
    private Runnable everySecond = new Runnable() {

        public void run() {
            if(moving) {
                time.setTime(time.getTime() + 1000);
                if (lastLocation == null) {
                    lastLocation = RTApi.getLastLocation();
                }
                if (lastLocation != null) {
                    Location currentLocation = RTApi.getLastLocation();
                    if (currentLocation != null) {
                        totalDistance += lastLocation.distanceTo(currentLocation);
                        lastLocation = currentLocation;
                    }
                }
                setDistTime(timeFormat.format(time), totalDistance);
                timerHandler.postDelayed(this, 1000);
            }
        }

    };
    private static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }

    protected void setDistTime(String time, Double dist) {
        TextView elapsedTime = (TextView) findViewById(R.id.time_elapsed_text_view);
        elapsedTime.setText(time);
        TextView distanceTraveled = (TextView) findViewById(R.id.distance_traveled_text_view);
        dist *= 0.000621371;
        if (dist < 0.01) {
            dist = 0.0;
        }
        distanceTraveled.setText(String.format("%.2g%n", dist));
    }

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    /*For checking user logged-in status*/
    private static final String APP_SHARED_PREFS = "roadtrip_preferences";
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    private static boolean isLoggedIn = false;

    @Override
    protected void onResume() {
        super.onResume();
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Alegreya.ttf");
        int titleId = getResources().getIdentifier("action_bar_title", "id",
                "android");
        TextView actionBarTitle = (TextView) findViewById(titleId);
        actionBarTitle.setTextColor(getResources().getColor(R.color.white));
        actionBarTitle.setTypeface(typeface);
        actionBarTitle.setTextSize(30);
        actionBarTitle.setPadding(0,0,0,10);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        sharedPrefs = getApplicationContext().getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        isLoggedIn = sharedPrefs.getBoolean("userLoggedInState", false);
        //TODO remove this
//        isLoggedIn = true;

        if (!isLoggedIn) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(loginIntent);
        }

        try {
            RTApi.login(this, this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        timerHandler = new Handler();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Alegreya.ttf");
        int titleId = getResources().getIdentifier("action_bar_title", "id",
                "android");
        TextView actionBarTitle = (TextView) findViewById(titleId);
        actionBarTitle.setTextColor(getResources().getColor(R.color.white));
        actionBarTitle.setTypeface(typeface);
        actionBarTitle.setTextSize(30);
        actionBarTitle.setPadding(0,0,0,10);

        getActionBar().setDisplayShowHomeEnabled(false);

        getActionBar().setBackgroundDrawable(new ColorDrawable(0xff006ABD));



        setContentView(R.layout.activity_main);

//        int titleID = getResources().getIdentifier("action_bar_title", "id", "android);");
//        TextView titleTextView = (TextView) findViewById(titleID);
//        Typeface tf = Typeface.createFromAsset(getAssets(), "@asset/Alegreya.ttf");
//       titleTextView.setTypeface(tf);

        getLoaderManager().initLoader(0, null, this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.main_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    public void startTimer() {
        moving = true;
        timerHandler.post(everySecond);
    }

    protected void stopTimer() {
        moving = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        if (sharedPrefs.getBoolean("tripActive", false)) {
            Log.d(TAG, "Trip is active");
            MenuItem stop = menu.findItem(R.id.action_stop);
            stop.setVisible(true);

            MenuItem startnew = menu.findItem(R.id.action_new_trip);
            startnew.setVisible(false);
        }
        else if (sharedPrefs.getBoolean("tripActive", true)) {
            Log.d(TAG, "Trip is not active");
            MenuItem stop = menu.findItem(R.id.action_stop);
            stop.setVisible(false);

            MenuItem startnew = menu.findItem(R.id.action_new_trip);
            startnew.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_new_trip) {
            NewTripFragment newTripDialog = new NewTripFragment();
            newTripDialog.show(getFragmentManager(), "New Trip");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        Log.d(TAG, "selected trip with id = " + itemId);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case 0:
                return new CursorLoader(this, RTContentProvider.TRIP_URI, null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG, "load finished, found " + cursor.getCount() + " results");
        getActionBar().setListNavigationCallbacks(new TripAdapter(this, cursor), this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @Override
    public void onLoginComplete(boolean success) {
        Log.d(TAG, "RTApi login success:" + success);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private RoadtripMapFragment mapFragment;
        private RoadtripFeedFragment feedFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                if(mapFragment == null)
                    mapFragment = RoadtripMapFragment.newInstance();
                return mapFragment;
            }
            if (position == 1) {
                if(feedFragment == null)
                    feedFragment = RoadtripFeedFragment.newInstance("52e4c576bc7b92b20ecef6df"); //TODO read the real id's here
                return feedFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_map).toUpperCase(l);
                case 1:
                    return getString(R.string.title_feed).toUpperCase(l);
            }
            return null;
        }
    }

}
