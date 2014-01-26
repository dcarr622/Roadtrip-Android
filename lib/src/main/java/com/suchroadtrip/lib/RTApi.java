package com.suchroadtrip.lib;

import android.content.ContentValues;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
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
    private static URL ADD_SPOT_URL;
    private static URL GRANT_ACCESS_URL;
    private static String GET_MEDIA_URL = BASE_URL + "trips/media?id=";
    private static String GET_USER_INFO_URL = BASE_URL + "user/info?id=";

    private static Location lastLocation = null;

    static {
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        try {
            START_TRIP_URL = new URL(BASE_URL + "trips");
            LOGIN_URL = new URL(BASE_URL + "users/login");
            ADD_SPOT_URL = new URL(BASE_URL + "trips/addSpot");
            GRANT_ACCESS_URL = new URL(BASE_URL + "trips/addFriend");
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
                if (cb != null)
                    cb.onLoginComplete(result);
            }
        }.execute();


    }

    public static void login(Context context, LoginCallback cb) throws IOException {
        login(getUsername(context), getPassword(context), cb);
    }

    public static Location getLastLocation() {
        return lastLocation;
    }

    public static void updateLocation(Context context, final String tripId, final Location loc) {
        lastLocation = loc;
        ContentValues cv = new ContentValues();
        cv.put(RTOpenHelper.KEY_TRIP_ID, tripId);
        cv.put(RTOpenHelper.KEY_LAT, loc.getLatitude());
        cv.put(RTOpenHelper.KEY_LNG, loc.getLongitude());
        context.getContentResolver().insert(RTContentProvider.LOCATION_URI, cv);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    JSONObject json = new JSONObject();
                    json.put("id", tripId);
                    json.put("lat", loc.getLatitude());
                    json.put("lng", loc.getLongitude());

                    HttpURLConnection connection = (HttpURLConnection) ADD_SPOT_URL.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.getOutputStream().write(json.toString().getBytes());

                    Log.d(TAG, "addSpot response: " + connection.getResponseCode());

                    Log.d(TAG, "uploading spot: " + json.toString());

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

        }.execute();
    }

    public static void addPicture(Context ctx, String tripId, Uri pic, Location loc) {
        ContentValues cv = new ContentValues();
        cv.put(RTOpenHelper.KEY_PHOTO_URI, pic.toString());
        cv.put(RTOpenHelper.KEY_LAT, loc.getLatitude());
        cv.put(RTOpenHelper.KEY_LNG, loc.getLongitude());
        cv.put(RTOpenHelper.KEY_TRIP_ID, tripId);
        ctx.getContentResolver().insert(RTContentProvider.PHOTO_URI, cv);

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                return null;
            }

            @Override
            protected void onPostExecute(Boolean result) {

            }
        };
    }

    public static void startTrip(final Context context, final String name, final Location start, final String end, final StartTripCallback cb) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
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

                    String id = response.getJSONObject("result").getString("id");
                    return id;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (cb != null) {
                    if (result != null)
                        cb.tripStarted(result);
                    else
                        cb.tripStarted(null);
                }
            }

        }.execute();
    }

    public static void grantAccess(final String tripId, final String username) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    JSONObject json = new JSONObject();
                    json.put("id", tripId);
                    json.put("friendname", username);

                    HttpURLConnection connection = (HttpURLConnection) GRANT_ACCESS_URL.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.getOutputStream().write(json.toString().getBytes());

                    Log.d(TAG, "grantAccess request: " + json.toString());

                    Log.d(TAG, "grantAccess response: " + connection.getResponseCode());

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

        }.execute();
    }

    public static void loadMedia(final Context context, final String id) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    URL url = new URL(GET_MEDIA_URL + id);
                    Log.d(TAG, url.toString());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Content-Type", "application/json");

                    JSONObject response = readJson(connection.getInputStream());

                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject json = results.getJSONObject(i);
                        final ContentValues cv = new ContentValues();
                        cv.put(RTOpenHelper.KEY_SOCIAL_SERVICE, json.getString("medium"));
                        JSONArray latlng = json.getJSONArray("latlng");
                        if (latlng.length() > 0) {
                            cv.put(RTOpenHelper.KEY_LAT, latlng.getDouble(0));
                            cv.put(RTOpenHelper.KEY_LNG, latlng.getDouble(1));
                        }
                        cv.put(RTOpenHelper.KEY_TEXT, json.getString("text"));
                        cv.put(RTOpenHelper.KEY_TRIP_ID, id);
                        cv.put(RTOpenHelper.KEY_SOCIAL_ID, json.getString("_id"));

                        getUserInfo(json.getString("user"), new UserCallback() {
                            @Override
                            public void onUserInfo(JSONObject userJson) {
                                try {
                                    cv.put(RTOpenHelper.KEY_AUTHOR, userJson.getString("username"));
                                    cv.put(RTOpenHelper.KEY_AUTHOR_PIC, userJson.getString("picture"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                context.getContentResolver().insert(RTContentProvider.SOCIAL_URI, cv);
                            }
                        });
                    }

                    Log.d(TAG, "loadMedia response: " + connection.getResponseCode());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

        }.execute();
    }

    public static void getUserInfo(final String uid, final UserCallback cb) {
        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected JSONObject doInBackground(Void... voids) {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(GET_USER_INFO_URL + uid).openConnection();

                    JSONObject response = readJson(connection.getInputStream());

                    JSONObject results = response.getJSONObject("results");

                    return results;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                if (cb != null) {
                    cb.onUserInfo(result);
                }
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
        public void tripStarted(String id);
    }

    public static interface LoginCallback {
        public void onLoginComplete(boolean success);
    }

    public static interface UserCallback {
        public void onUserInfo(JSONObject userJson);
    }

}
