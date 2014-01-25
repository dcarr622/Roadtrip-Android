package com.suchroadtrip.lib;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vmagro on 1/24/14.
 */
public class RTOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SuchRoadtrip";
    private static final int DATABASE_VERSION = 1;

    public static final String KEY_ID = "_id";
    public static final String KEY_TIME = "time";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LNG = "lng";
    public static final String KEY_TRIP_ID = "trip";

    protected static final String TABLE_TRIPS = "trip";

    public static final String KEY_NAME = "name";

    private static final String CREATE_TABLE_TRIPS = "CREATE TABLE " + TABLE_TRIPS + " (" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_NAME + " TEXT" +
            ");";

    protected static final String TABLE_SOCIAL = "social";

    public static final String KEY_SOCIAL_SERVICE = "service";
    public static final String KEY_TEXT = "text";

    private static final String CREATE_TABLE_SOCIAL = "CREATE TABLE " + TABLE_SOCIAL + " (" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_TRIP_ID + " INTEGER" +
            KEY_TIME + " DATETIME," +
            KEY_LAT + " REAL," +
            KEY_LNG + " REAL," +
            KEY_TEXT + " TEXT," +
            KEY_SOCIAL_SERVICE + "TEXT" +
            ");";

    protected static final String TABLE_PHOTO = "photo";

    public static final String KEY_PHOTO_URI = "photo";

    private static final String CREATE_TABLE_PHOTO = "CREATE TABLE " + TABLE_PHOTO + " (" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_TRIP_ID + " INTEGER" +
            KEY_TIME + " DATETIME," +
            KEY_LAT + " REAL," +
            KEY_LNG + " REAL," +
            KEY_PHOTO_URI + " TEXT" +
            ");";


    protected static final String TABLE_LOCATION = "location";

    private static final String CREATE_TABLE_LOCATION = "CREATE TABLE " + TABLE_LOCATION + " (" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_TRIP_ID + " INTEGER" +
            KEY_TIME + " DATETIME," +
            KEY_LAT + " REAL," +
            KEY_LNG + " REAL" +
            ");";

    public RTOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TRIPS);
        db.execSQL(CREATE_TABLE_SOCIAL);
        db.execSQL(CREATE_TABLE_PHOTO);
        db.execSQL(CREATE_TABLE_LOCATION);

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, "Hello world");
        db.insert(TABLE_TRIPS, null, values);
        values.clear();
        values.put(KEY_NAME, "Trip 2");
        db.insert(TABLE_TRIPS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

}
