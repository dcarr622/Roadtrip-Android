package com.suchroadtrip.lib;

import android.content.ContentValues;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by vmagro on 1/25/14.
 */
public class RTApi {

    private static final String TAG = "wowsuchapi";

    private static final String BASE_URL = "http://suchroadtrip.com/";

    private static URL START_TRIP_URL;
    private static URL LOGIN_URL;

    static {
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        try {
            START_TRIP_URL = new URL(BASE_URL + "trips");
            LOGIN_URL = new URL(BASE_URL + "users/login");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void login(final String username, final String password, final LoginCallback cb) throws IOException {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) LOGIN_URL.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json");

                    try {
                        JSONObject json = new JSONObject();
                        json.put("username", username);
                        json.put("password", password);
                        connection.getOutputStream().write(json.toString().getBytes());

                        Log.d(TAG, "login response code: " + connection.getResponseCode());

                        JSONObject response = readJson(connection.getInputStream());

                        Log.d(TAG, "login response: " + response.toString());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    return connection.getResponseCode() == 200; //the server will only return a 200 if the user successfully logged in
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                cb.onLoginComplete(result);
            }
        }.execute();


    }

    public static void login(Context context, LoginCallback cb) throws IOException {
        login(getUsername(context), getPassword(context), cb);
    }

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

    public static void startTrip(final Context context, final String name, final Location start, final String end, final StartTripCallback cb) {
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... voids) {
                JSONObject json = new JSONObject();
                try {
                    json.put("name", name);
                    json.put("startLat", start.getLatitude());
                    json.put("startLng", start.getLongitude());

                    Geocoder geocoder = new Geocoder(context);

                    List<Address> addresses = geocoder.getFromLocation(start.getLatitude(), start.getLongitude(), 1);
                    if (addresses.size() > 0)
                        json.put("startCity", addresses.get(0).getLocality());

                    json.put("endCity", end);
                    addresses = geocoder.getFromLocationName(end, 1);
                    if (addresses.size() > 0) {
                        Address endCity = addresses.get(0);
                        json.put("endLat", endCity.getLatitude());
                        json.put("endLng", endCity.getLongitude());
                    }

                    HttpURLConnection connection = (HttpURLConnection) START_TRIP_URL.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.getOutputStream().write(json.toString().getBytes());

                    //try to relogin
                    if (connection.getResponseCode() == 401) {
                        login(context, new LoginCallback() {
                            @Override
                            public void onLoginComplete(boolean success) {
                                if (success)
                                    startTrip(context, name, start, end, cb);
                            }
                        });
                    }

                    JSONObject response = readJson(connection.getInputStream());

                    Log.d(TAG, "trip response:" + response.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer result) {
                if (result != null)
                    cb.tripStarted(result);
                else
                    cb.tripStarted(-1);
            }

        }.execute();
    }

    public static JSONObject readJson(InputStream inputStream) throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        String data = "";
        while ((line = reader.readLine()) != null) {
            data += line;
        }

        return new JSONObject(data);
    }

    private static String getUsername(Context ctx) {
        return ctx.getSharedPreferences("roadtrip_preferences", Context.MODE_PRIVATE).getString("username", null);
    }

    private static String getPassword(Context ctx) {
        return ctx.getSharedPreferences("roadtrip_preferences", Context.MODE_PRIVATE).getString("password", null);
    }

    public static interface StartTripCallback {
        public void tripStarted(int id);
    }

    public static interface LoginCallback {
        public void onLoginComplete(boolean success);
    }

}
