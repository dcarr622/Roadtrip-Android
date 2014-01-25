package com.suchroadtrip.lib;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vmagro on 1/24/14.
 */
public class RTOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SuchRoadtrip";
    private static final int DATABASE_VERSION = 1;

    private static String KEY_ID = "_id";
    private static String KEY_TIME = "time";
    private static String KEY_LAT = "lat";
    private static String KEY_LNG = "lng";

    protected static final String TABLE_SOCIAL = "social";

    private static final String KEY_SOCIAL_SERVICE = "service";
    private static final String KEY_TEXT = "text";

    private static final String CREATE_TABLE_SOCIAL = "CREATE TABLE " + TABLE_SOCIAL + " (" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_TIME + " DATETIME," +
            KEY_LAT + " REAL," +
            KEY_LNG + "REAL," +
            KEY_TEXT + "TEXT," +
            KEY_SOCIAL_SERVICE + "TEXT" +
            ");";

    protected static final String TABLE_PHOTO = "photo";

    private static final String KEY_PHOTO_URI = "photo";

    private static final String CREATE_TABLE_PHOTO = "CREATE TABLE " + TABLE_PHOTO + " (" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_TIME + " DATETIME," +
            KEY_LAT + " REAL," +
            KEY_LNG + "REAL," +
            KEY_PHOTO_URI + " STRING" +
            ");";


    protected static final String TABLE_LOCATION = "location";

    private static final String CREATE_TABLE_LOCATION = "CREATE TABLE " + TABLE_LOCATION + " (" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_TIME + " DATETIME," +
            KEY_LAT + " REAL," +
            KEY_LNG + "REAL," +
            ");";

    public RTOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SOCIAL);
        db.execSQL(CREATE_TABLE_PHOTO);
        db.execSQL(CREATE_TABLE_LOCATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

}
