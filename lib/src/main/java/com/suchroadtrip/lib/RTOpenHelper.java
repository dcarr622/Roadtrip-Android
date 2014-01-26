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
    public static final String KEY_NAME = "name";

    protected static final String TABLE_TRIPS = "trip";

    public static final String KEY_START_LAT = "start_lat";
    public static final String KEY_START_LNG = "start_lng";
    public static final String KEY_END_LAT = "end_lat";
    public static final String KEY_END_LNG = "end_lng";
    public static final String KEY_START_CITY = "start_city";
    public static final String KEY_END_CITY = "end_city";

    private static final String CREATE_TABLE_TRIPS = "CREATE TABLE " + TABLE_TRIPS + " (" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_NAME + " TEXT," +
            KEY_START_LAT + " REAL," +
            KEY_START_LNG + " REAL," +
            KEY_END_LAT + " REAL," +
            KEY_END_LNG + " REAL," +
            KEY_START_CITY + " TEXT," +
            KEY_END_CITY + " TEXT" +
            ");";

    protected static final String TABLE_SOCIAL = "social";

    public static final String KEY_SOCIAL_SERVICE = "service";
    public static final String KEY_TEXT = "text";
    public static final String KEY_POSTER = "poster";

    private static final String CREATE_TABLE_SOCIAL = "CREATE TABLE " + TABLE_SOCIAL + " (" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_TRIP_ID + " TEXT NOT NULL," +
            KEY_TIME + " DATETIME," +
            KEY_LAT + " REAL," +
            KEY_LNG + " REAL," +
            KEY_TEXT + " TEXT," +
            KEY_SOCIAL_SERVICE + " TEXT," +
            KEY_POSTER + " TEXT" +
            ");";

    protected static final String TABLE_PHOTO = "photo";

    public static final String KEY_PHOTO_URI = "photo";

    private static final String CREATE_TABLE_PHOTO = "CREATE TABLE " + TABLE_PHOTO + " (" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_TRIP_ID + " TEXT NOT NULL," +
            KEY_TIME + " DATETIME ," +
            KEY_LAT + " REAL," +
            KEY_LNG + " REAL," +
            KEY_PHOTO_URI + " TEXT" +
            ");";


    protected static final String TABLE_LOCATION = "location";

    private static final String CREATE_TABLE_LOCATION = "CREATE TABLE " + TABLE_LOCATION + " (" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_TRIP_ID + " TEXT NOT NULL," +
            KEY_TIME + " DATETIME," +
            KEY_NAME + " TEXT," +
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

        values.clear();

        values.put(KEY_TRIP_ID, 1);
        values.put(KEY_LAT, "34.044022");
        values.put(KEY_LNG, "-118.26641549999998");

        values.put(KEY_SOCIAL_SERVICE, "Twitter");
        values.put(KEY_TEXT, "Foss is Boss");
        values.put(KEY_POSTER, "David Carr");
        db.insert(TABLE_SOCIAL, null, values);

        values.clear();

        values.put(KEY_TRIP_ID, 1);
        values.put(KEY_LAT, "34.044022");
        values.put(KEY_LNG, "-118.26641549999998");

        values.put(KEY_PHOTO_URI, "http://vinnie.io/favicon.ico");
        db.insert(TABLE_PHOTO, null, values);

        values.clear();

        values.put(KEY_TRIP_ID, 1);
        values.put(KEY_LAT, "34.044022");
        values.put(KEY_LNG, "-118.26641549999998");

        values.put(KEY_NAME, "In-n-Out Burger");
        db.insert(TABLE_LOCATION, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

}
