package com.suchroadtrip.app.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;

import android.database.Cursor;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.suchroadtrip.app.R;
import com.suchroadtrip.lib.RTContentProvider;
import com.suchroadtrip.lib.RTOpenHelper;

import java.util.ArrayList;

/**
 * Created by david on 1/24/14.
 */
public class RoadtripMapFragment extends Fragment implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private LocationClient locationClient = null;
    private GoogleMap map = null;

    private boolean initedView = false;
    private String preTripId = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (!getActivity().getActionBar().isShowing())
            getActivity().getActionBar().show();

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_roadtripmap, container, false);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        map = mapFragment.getMap();
        map.getUiSettings().setAllGesturesEnabled(true);
        map.setMyLocationEnabled(true);

        locationClient = new LocationClient(getActivity(), this, this);
        locationClient.connect();

        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(false);

        Button myLocationButton = (Button) v.findViewById(R.id.my_location_button);
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Location myLoc = map.getMyLocation();
                if (myLoc == null) {
                    LocationManager mgr = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    String provider = mgr.getBestProvider(criteria, true);
                    myLoc = mgr.getLastKnownLocation(provider);
                }
                map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myLoc.getLatitude(), myLoc.getLongitude())));
            }
        });

        initedView = true;

        if (preTripId != null)
            setTrip(preTripId);

        return v;
    }

    public static RoadtripMapFragment newInstance() {
        return new RoadtripMapFragment();
    }

    public void setTrip(String tripId) {
        if (initedView) {
            map.clear();
            new DrawPath().execute(tripId);
        } else {
            preTripId = tripId;
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Location loc = locationClient.getLastLocation();
        if (loc != null) {
            LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
        locationClient.disconnect();
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class DrawPath extends AsyncTask<String, Void, ArrayList<LatLng>> {

        @Override
        protected ArrayList<LatLng> doInBackground(String... params) {
            ArrayList<LatLng> points = new ArrayList<LatLng>();
            Cursor c = getActivity().getContentResolver().query(Uri.withAppendedPath(RTContentProvider.LOCATION_URI, params[0]), null, null, null, null);
            while (c.moveToNext()) {
                double lat = c.getDouble(c.getColumnIndex(RTOpenHelper.KEY_LAT));
                double lng = c.getDouble(c.getColumnIndex(RTOpenHelper.KEY_LNG));
                points.add(new LatLng(lat, lng));
            }
            return points;
        }

        @Override
        protected void onPostExecute(ArrayList<LatLng> points) {
            Log.d("Map", "drawing " + points + " on the map");
            PolylineOptions options = new PolylineOptions();
            options.addAll(points);
            map.addPolyline(options);
        }
    }
}
