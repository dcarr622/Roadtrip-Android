package com.suchroadtrip.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.suchroadtrip.app.R;

/**
 * Created by david on 1/24/14.
 */
public class RoadtripMapFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (!getActivity().getActionBar().isShowing())
            getActivity().getActionBar().show();

        Log.i("MapFragment", "onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_roadtripmap, container, false);
    }

    public static RoadtripMapFragment newInstance() {
        return new RoadtripMapFragment();
    }
}
