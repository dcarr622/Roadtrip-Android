package com.suchroadtrip.app.data;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.suchroadtrip.lib.RTOpenHelper;

/**
 * Created by david on 1/25/14.
 */
public class TripEventAdapter extends CursorAdapter {

    private static final String TAG = "TripEventAdapter";

    public TripEventAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        Log.d(TAG, "loaded " + cursor.getCount() + " events");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        TextView tv = new TextView(context);
        tv.setText("blahblahblah");

        if (cursor.getColumnIndex(RTOpenHelper.KEY_SOCIAL_SERVICE) != -1) {
            Log.d(TAG, "social event");
        } else if (cursor.getColumnIndex(RTOpenHelper.KEY_PHOTO_URI) != -1) {
            Log.d(TAG, "photo event");
        } else if (cursor.getColumnIndex(RTOpenHelper.KEY_NAME) != -1) {
            Log.d(TAG, "location event");
        }
        return tv;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
