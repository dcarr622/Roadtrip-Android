package com.suchroadtrip.lib;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by vmagro on 1/24/14.
 */
public class RTContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.suchroadtrip.provider";

    private static final Uri baseUri = Uri.parse("content://" + AUTHORITY + "/");

    private static final int MATCH_SOCIAL_UPDATE = 1;
    private static final int MATCH_PHOTO = 2;
    private static final int MATCH_LOCATION = 3;

    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, RTOpenHelper.TABLE_SOCIAL, MATCH_SOCIAL_UPDATE);
        uriMatcher.addURI(AUTHORITY, RTOpenHelper.TABLE_PHOTO, MATCH_PHOTO);
        uriMatcher.addURI(AUTHORITY, RTOpenHelper.TABLE_LOCATION, MATCH_LOCATION);
    }

    private RTOpenHelper dbHelper = null;

    @Override
    public boolean onCreate() {
        dbHelper = new RTOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String table = null;
        switch (uriMatcher.match(uri)) {
            case MATCH_SOCIAL_UPDATE:
                table = RTOpenHelper.TABLE_SOCIAL;
                break;
            case MATCH_LOCATION:
                table = RTOpenHelper.TABLE_LOCATION;
                break;
            case MATCH_PHOTO:
                table = RTOpenHelper.TABLE_PHOTO;
        }
        if (table != null)
            return dbHelper.getReadableDatabase().query(table, projection, selection, selectionArgs, null, null, sortOrder);
        return null;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case MATCH_SOCIAL_UPDATE:
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + ".social";
            case MATCH_LOCATION:
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + ".location";
            case MATCH_PHOTO:
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + ".photo";
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        String table = null;
        switch (uriMatcher.match(uri)) {
            case MATCH_SOCIAL_UPDATE:
                table = RTOpenHelper.TABLE_SOCIAL;
                break;
            case MATCH_LOCATION:
                table = RTOpenHelper.TABLE_LOCATION;
                break;
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
            case MATCH_SOCIAL_UPDATE:
                table = RTOpenHelper.TABLE_SOCIAL;
                break;
            case MATCH_LOCATION:
                table = RTOpenHelper.TABLE_LOCATION;
                break;
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
            case MATCH_SOCIAL_UPDATE:
                table = RTOpenHelper.TABLE_SOCIAL;
                break;
            case MATCH_LOCATION:
                table = RTOpenHelper.TABLE_LOCATION;
                break;
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
