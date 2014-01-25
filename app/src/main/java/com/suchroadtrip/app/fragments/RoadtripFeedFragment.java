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

import com.suchroadtrip.app.R;
import com.suchroadtrip.lib.RTContentProvider;
import com.suchroadtrip.lib.RTOpenHelper;

/**
 * Created by david on 1/25/14.
 */
public class RoadtripFeedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getLoaderManager().initLoader(0, null, this);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_roadtripfeed, container, false);
    }

    public static Fragment newInstance() {
        return new RoadtripFeedFragment();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        /*switch (id){
            case 0:
                return new CursorLoader(getActivity(), Uri.withAppendedPath(RTContentProvider., null, null, null, null);
        }*/
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
