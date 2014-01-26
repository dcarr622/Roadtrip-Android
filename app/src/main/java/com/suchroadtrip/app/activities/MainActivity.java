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
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
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

        sharedPrefs = getApplicationContext().getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        sharedPrefs.edit().putString("username", "vmagro").putString("password", "wowsuchapp").commit();
        isLoggedIn = sharedPrefs.getBoolean("userLoggedInState", false);
        //TODO remove this
//        isLoggedIn = true;

        if (!isLoggedIn) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(loginIntent);
        }

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Alegreya.ttf");
        int titleId = getResources().getIdentifier("action_bar_title", "id",
                "android");
        TextView actionBarTitle = (TextView) findViewById(titleId);
        actionBarTitle.setTextColor(getResources().getColor(R.color.white));
        actionBarTitle.setTypeface(typeface);
        actionBarTitle.setTextSize(30);
        actionBarTitle.setPadding(0,0,0,10);

        getActionBar().setDisplayShowHomeEnabled(false);

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

        try {
            RTApi.login(this, this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
                    mapFragment = (RoadtripMapFragment) RoadtripMapFragment.newInstance();
                return mapFragment;
            }
            if (position == 1) {
                if(feedFragment == null)
                    feedFragment = RoadtripFeedFragment.newInstance(1); //TODO read the real id's here
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
