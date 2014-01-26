package com.suchroadtrip.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.suchroadtrip.app.R;

/**
 * Created by david on 1/24/14.
 */
public class RoadtripMapFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("MapFragment", "onCreateView");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_roadtripmap, container, false);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        GoogleMap map = mapFragment.getMap();
        map.setMyLocationEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        return v;
    }

    public static RoadtripMapFragment newInstance() {
        return new RoadtripMapFragment();
    }
}
