package com.suchroadtrip.app.fragments;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.suchroadtrip.app.R;

/**
 * Created by david on 1/24/14.
 */
public class RoadtripMapFragment extends Fragment implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private LocationClient locationClient = null;
    private GoogleMap map = null;

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

        return v;
    }

    public static RoadtripMapFragment newInstance() {
        return new RoadtripMapFragment();
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
}
