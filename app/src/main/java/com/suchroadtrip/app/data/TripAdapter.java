package com.suchroadtrip.app.data;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.suchroadtrip.lib.RTOpenHelper;

/**
 * Created by vmagro on 1/25/14.
 */
public class TripAdapter extends CursorAdapter {

    public TripAdapter(Context context, Cursor c) {
        super(context, c, 0);
        Log.d("wowsuchadapter", "loaded " + c.getCount() + " results");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, null);
        String name = cursor.getString(cursor.getColumnIndex(RTOpenHelper.KEY_NAME));
        ((TextView) v.findViewById(android.R.id.text1)).setText(name);
        Log.d("wowsoload", "loaded trip " + cursor.getInt(cursor.getColumnIndex(RTOpenHelper.KEY_ID)));
        v.setTag(new TripTag(cursor.getInt(cursor.getColumnIndex(RTOpenHelper.KEY_ID)), name));
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }

    public static class TripTag {

        private String name;
        private int id;

        public TripTag(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

    }

}
