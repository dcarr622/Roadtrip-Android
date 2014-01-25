package com.suchroadtrip.app.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.suchroadtrip.app.R;
import com.suchroadtrip.lib.CircleTransform;
import com.suchroadtrip.lib.RTOpenHelper;
import com.suchroadtrip.lib.Util;

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
        View v = null;
        if (cursor.getColumnIndex(RTOpenHelper.KEY_SOCIAL_SERVICE) != -1) {
            v = LayoutInflater.from(context).inflate(R.layout.feed_social, null);
            v.setTag(new EventTag(EventTag.Type.SOCIAL, v));
            Log.d(TAG, "social event");
        } else if (cursor.getColumnIndex(RTOpenHelper.KEY_PHOTO_URI) != -1) {
            v = LayoutInflater.from(context).inflate(R.layout.feed_media, null);
            v.setTag(new EventTag(EventTag.Type.PHOTO, v));
            Log.d(TAG, "photo event");
        } else if (cursor.getColumnIndex(RTOpenHelper.KEY_NAME) != -1) {
            v = LayoutInflater.from(context).inflate(R.layout.feed_stop, null);
            v.setTag(new EventTag(EventTag.Type.LOCATION, v));
            Log.d(TAG, "location event");
        }
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor c) {
        if (view.getTag() != null && view.getTag() instanceof EventTag) {
            EventTag tag = (EventTag) view.getTag();

            Picasso picasso = Picasso.with(context);

            if (tag.map != null){
                String url = Util.buildMapUrl(c.getDouble(c.getColumnIndex(RTOpenHelper.KEY_LAT)), c.getDouble(c.getColumnIndex(RTOpenHelper.KEY_LNG)));
                Log.d(TAG, url);
                picasso.load(url).transform(new CircleTransform()).into(tag.map);
            }

            switch (tag.getType()) {
                case SOCIAL:
                    tag.postText.setText(c.getString(c.getColumnIndex(RTOpenHelper.KEY_TEXT)));
                    tag.authorText.setText(c.getString(c.getColumnIndex(RTOpenHelper.KEY_POSTER)));
                    tag.networkText.setText("via " + c.getString(c.getColumnIndex(RTOpenHelper.KEY_SOCIAL_SERVICE)));
                    break;
                case PHOTO:
                    picasso.load(Uri.parse(c.getString(c.getColumnIndex(RTOpenHelper.KEY_PHOTO_URI)))).resize(100, 100).into(tag.mediaImage);
                    break;
            }
        }
    }

    private static class EventTag {
        public static enum Type {
            SOCIAL, PHOTO, LOCATION
        }

        private Type type;

        public ImageView map;

        public TextView postText;
        public TextView networkText;
        public TextView authorText;

        public TextView mediaText;
        public ImageView mediaImage;

        public EventTag(Type type, View v) {
            this.type = type;

            map = (ImageView) v.findViewById(R.id.mapImage);

            switch (type) {
                case SOCIAL:
                    postText = (TextView) v.findViewById(R.id.postText);
                    networkText = (TextView) v.findViewById(R.id.networkText);
                    authorText = (TextView) v.findViewById(R.id.authorText);
                    break;
                case PHOTO:
                    authorText = (TextView) v.findViewById(R.id.authorText);
                    mediaText = (TextView) v.findViewById(R.id.mediaText);
                    mediaImage = (ImageView) v.findViewById(R.id.mediaImage);
                    break;
            }

        }

        public Type getType() {
            return type;
        }

    }

}
