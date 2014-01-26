package com.suchroadtrip.lib;

import android.location.Location;

/**
 * Created by vmagro on 1/25/14.
 */
public class Util {

    private static final String FORMAT_MAP_URL = "http://maps.googleapis.com/maps/api/staticmap?center=%f,%f&markers=%f,%f&zoom=13&size=400x400&key=AIzaSyCAJbA5io-Jb97Y-vwSNJI6y64DcCyAZoE&sensor=false";

    public static String buildMapUrl(Location loc) {
        String url = String.format(FORMAT_MAP_URL, loc.getLatitude(), loc.getLongitude(), loc.getLatitude(), loc.getLongitude());
        return url;
    }

    public static String buildMapUrl(double lat, double lng) {
        Location loc = new Location("RoadTrip");
        loc.setLatitude(lat);
        loc.setLongitude(lng);
        return buildMapUrl(loc);
    }

}
