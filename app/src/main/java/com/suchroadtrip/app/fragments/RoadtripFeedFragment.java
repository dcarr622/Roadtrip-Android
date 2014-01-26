package com.suchroadtrip.app.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.suchroadtrip.app.R;
import com.suchroadtrip.app.data.TripEventAdapter;
import com.suchroadtrip.lib.RTApi;
import com.suchroadtrip.lib.RTContentProvider;

/**
 * Created by david on 1/25/14.
 */
public class RoadtripFeedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_TRIP_ID = "trip";

    public static RoadtripFeedFragment newInstance(String tripId) {
        Bundle args = new Bundle();
        args.putString(ARG_TRIP_ID, tripId);
        RoadtripFeedFragment fragment = new RoadtripFeedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private String tripId = null;

    public void setTrip(String id){
        this.tripId = id;
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setTrip(getArguments().getString(ARG_TRIP_ID));


        if (!getActivity().getActionBar().isShowing())
            getActivity().getActionBar().show();

        RTApi.loadMedia(getActivity(), tripId);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_roadtripfeed, container, false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case 0:
                return new CursorLoader(getActivity(), Uri.withAppendedPath(RTContentProvider.ALL_EVENTS_URI, tripId), null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        TripEventAdapter adapter = new TripEventAdapter(getActivity(), cursor);
        ((ListView) getView().findViewById(R.id.feed)).setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
