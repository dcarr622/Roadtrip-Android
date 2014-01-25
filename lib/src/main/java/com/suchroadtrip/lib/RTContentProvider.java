package com.suchroadtrip.lib;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by vmagro on 1/24/14.
 */
public class RTContentProvider extends ContentProvider {

    private static final String TAG = "suchcontent";

    public static final String AUTHORITY = "com.suchroadtrip.provider";

    private static final Uri baseUri = Uri.parse("content://" + AUTHORITY + "/");

    public static final Uri TRIP_URI = Uri.withAppendedPath(baseUri, RTOpenHelper.TABLE_TRIPS);
    public static final Uri SOCIAL_URI = Uri.withAppendedPath(baseUri, RTOpenHelper.TABLE_SOCIAL);
    public static final Uri LOCATION_URI = Uri.withAppendedPath(baseUri, RTOpenHelper.TABLE_LOCATION);
    public static final Uri PHOTO_URI = Uri.withAppendedPath(baseUri, RTOpenHelper.TABLE_PHOTO);
    public static final Uri ALL_EVENTS_URI = Uri.withAppendedPath(baseUri, "events");

    private static final int MATCH_TRIP = 1;
    private static final int MATCH_SOCIAL_UPDATE_TRIP = 2;
    private static final int MATCH_PHOTO_TRIP = 3;
    private static final int MATCH_LOCATION_TRIP = 4;

    private static final int MATCH_SOCIAL = 5;
    private static final int MATCH_PHOTO = 6;
    private static final int MATCH_LOCATION = 7;

    private static final int MATCH_ALL_EVENTS = 8;

    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, RTOpenHelper.TABLE_TRIPS, MATCH_TRIP);

        uriMatcher.addURI(AUTHORITY, RTOpenHelper.TABLE_SOCIAL + "/#", MATCH_SOCIAL_UPDATE_TRIP);
        uriMatcher.addURI(AUTHORITY, RTOpenHelper.TABLE_PHOTO + "/#", MATCH_PHOTO_TRIP);
        uriMatcher.addURI(AUTHORITY, RTOpenHelper.TABLE_LOCATION + "/#", MATCH_LOCATION_TRIP);

        uriMatcher.addURI(AUTHORITY, RTOpenHelper.TABLE_SOCIAL, MATCH_SOCIAL);
        uriMatcher.addURI(AUTHORITY, RTOpenHelper.TABLE_PHOTO, MATCH_PHOTO);
        uriMatcher.addURI(AUTHORITY, RTOpenHelper.TABLE_LOCATION, MATCH_LOCATION);

        uriMatcher.addURI(AUTHORITY, "events/#", MATCH_ALL_EVENTS);
    }

    private RTOpenHelper dbHelper = null;

    @Override
    public boolean onCreate() {
        dbHelper = new RTOpenHelper(getContext());
        Log.i(TAG, "onCreate");
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Uri notificationUri = null;
        String table = null;
        switch (uriMatcher.match(uri)) {
            case MATCH_TRIP:
                return dbHelper.getReadableDatabase().query(RTOpenHelper.TABLE_TRIPS, projection, selection, selectionArgs, null, null, sortOrder);
            case MATCH_SOCIAL_UPDATE_TRIP:
                table = RTOpenHelper.TABLE_SOCIAL;
                notificationUri = SOCIAL_URI;
                break;
            case MATCH_LOCATION_TRIP:
                table = RTOpenHelper.TABLE_LOCATION;
                notificationUri = LOCATION_URI;
                break;
            case MATCH_PHOTO_TRIP:
                table = RTOpenHelper.TABLE_PHOTO;
                notificationUri = PHOTO_URI;
                break;
            case MATCH_ALL_EVENTS:
                String id = uri.getLastPathSegment();
                Log.d(TAG, "looking for all events for " + id);
                Log.d(TAG, Uri.withAppendedPath(SOCIAL_URI, id).toString());
                Cursor socialCursor = query(Uri.withAppendedPath(SOCIAL_URI, id), projection, selection, selectionArgs, sortOrder);
                Log.d(TAG, "found " + socialCursor.getCount() + " social events");
                Cursor locationCursor = query(Uri.withAppendedPath(LOCATION_URI, id), projection, selection, selectionArgs, sortOrder);
                Cursor photoCursor = query(Uri.withAppendedPath(PHOTO_URI, id), projection, selection, selectionArgs, sortOrder);
                Cursor[] cursors = new Cursor[]{socialCursor, locationCursor, photoCursor};
                notificationUri = ALL_EVENTS_URI;
                MergeCursor mergeCursor = new MergeCursor(cursors);
                mergeCursor.setNotificationUri(getContext().getContentResolver(), notificationUri);
                return mergeCursor;
        }

        //this section is to make the query only include the results for that trip id
        if (selection == null)
            selection = "";
        selection += " " + RTOpenHelper.KEY_TRIP_ID + " = ?";
        if (selectionArgs == null)
            selectionArgs = new String[0];
        String[] newSelectionArgs = Arrays.copyOf(selectionArgs, selectionArgs.length + 1);
        newSelectionArgs[newSelectionArgs.length - 1] = uri.getLastPathSegment();
        //end trip id selection section

        Log.d(TAG, "querying " + table + " for event " + uri.getLastPathSegment());

        if (table != null) {
            Cursor c = dbHelper.getReadableDatabase().query(table, projection, selection, newSelectionArgs, null, null, sortOrder);
            c.setNotificationUri(getContext().getContentResolver(), notificationUri);
            return c;
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case MATCH_TRIP:
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + ".trip";
            case MATCH_SOCIAL_UPDATE_TRIP:
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + ".social";
            case MATCH_LOCATION_TRIP:
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + ".location";
            case MATCH_PHOTO_TRIP:
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + ".photo";
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        String table = null;
        switch (uriMatcher.match(uri)) {
            case MATCH_TRIP:
                table = RTOpenHelper.TABLE_TRIPS;
                break;
            case MATCH_SOCIAL_UPDATE_TRIP:
            case MATCH_SOCIAL:
                table = RTOpenHelper.TABLE_SOCIAL;
                break;
            case MATCH_LOCATION_TRIP:
            case MATCH_LOCATION:
                table = RTOpenHelper.TABLE_LOCATION;
                break;
            case MATCH_PHOTO_TRIP:
            case MATCH_PHOTO:
                table = RTOpenHelper.TABLE_PHOTO;
        }
        if (table != null) {
            Uri result = Uri.withAppendedPath(baseUri, table +
                    dbHelper.getWritableDatabase().insert(table, null, contentValues));
            getContext().getContentResolver().notifyChange(Uri.withAppendedPath(baseUri, table), null);
            return result;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String table = null;
        switch (uriMatcher.match(uri)) {
            case MATCH_TRIP:
                table = RTOpenHelper.TABLE_TRIPS;
                break;
            case MATCH_SOCIAL_UPDATE_TRIP:
            case MATCH_SOCIAL:
                table = RTOpenHelper.TABLE_SOCIAL;
                break;
            case MATCH_LOCATION_TRIP:
            case MATCH_LOCATION:
                table = RTOpenHelper.TABLE_LOCATION;
                break;
            case MATCH_PHOTO_TRIP:
            case MATCH_PHOTO:
                table = RTOpenHelper.TABLE_PHOTO;
        }
        if (table != null) {
            int result = dbHelper.getWritableDatabase().delete(table, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(Uri.withAppendedPath(baseUri, table), null);
            return result;
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        String table = null;
        switch (uriMatcher.match(uri)) {
            case MATCH_TRIP:
                table = RTOpenHelper.TABLE_TRIPS;
                break;
            case MATCH_SOCIAL_UPDATE_TRIP:
            case MATCH_SOCIAL:
                table = RTOpenHelper.TABLE_SOCIAL;
                break;
            case MATCH_LOCATION_TRIP:
            case MATCH_LOCATION:
                table = RTOpenHelper.TABLE_LOCATION;
                break;
            case MATCH_PHOTO_TRIP:
            case MATCH_PHOTO:
                table = RTOpenHelper.TABLE_PHOTO;
        }
        if (table != null) {
            int result = dbHelper.getWritableDatabase().update(table, contentValues, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(Uri.withAppendedPath(baseUri, table), null);
            return result;
        }
        return 0;
    }

}
