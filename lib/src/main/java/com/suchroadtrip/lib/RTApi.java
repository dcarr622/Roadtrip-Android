package com.suchroadtrip.lib;

import android.content.ContentValues;
import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;

/**
 * Created by vmagro on 1/25/14.
 */
public class RTApi {

    private static final String BASE_URL = "http://suchroadtrip.com/api/";

    public static void updateLocation(Location loc) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                return null;
            }

        };
    }

    public static void addPicture(Context ctx, Uri pic, Location loc) {
        ContentValues cv = new ContentValues();
        cv.put(RTOpenHelper.KEY_PHOTO_URI, pic.toString());
        cv.put(RTOpenHelper.KEY_LAT, loc.getLatitude());
        cv.put(RTOpenHelper.KEY_LNG, loc.getLongitude());
        cv.put(RTOpenHelper.KEY_TRIP_ID, 1);
        ctx.getContentResolver().insert(RTContentProvider.PHOTO_URI, cv);
    }

}
