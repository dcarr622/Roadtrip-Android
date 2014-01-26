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
import android.widget.LinearLayout;
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
        View v = LayoutInflater.from(context).inflate(R.layout.card, null);

        if (cursor.getColumnIndex(RTOpenHelper.KEY_SOCIAL_SERVICE) != -1) {
            v.setTag(new EventTag(context, EventTag.Type.SOCIAL, v));
            Log.d(TAG, "social event");
        } else if (cursor.getColumnIndex(RTOpenHelper.KEY_PHOTO_URI) != -1) {
            v.setTag(new EventTag(context, EventTag.Type.PHOTO, v));
            Log.d(TAG, "photo event");
        } else if (cursor.getColumnIndex(RTOpenHelper.KEY_NAME) != -1) {
            v.setTag(new EventTag(context, EventTag.Type.LOCATION, v));
            Log.d(TAG, "location event");
        }
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor c) {
        if (view.getTag() != null && view.getTag() instanceof EventTag) {
            EventTag tag = (EventTag) view.getTag();

            Picasso picasso = Picasso.with(context);

            if (tag.map != null) {
                String url = Util.buildMapUrl(c.getDouble(c.getColumnIndex(RTOpenHelper.KEY_LAT)), c.getDouble(c.getColumnIndex(RTOpenHelper.KEY_LNG)));
                Log.d(TAG, url);
                picasso.load(url).into(tag.map);
            }

            switch (tag.getType()) {
                case SOCIAL:
                    tag.socialPostText.setText(c.getString(c.getColumnIndex(RTOpenHelper.KEY_TEXT)));
                    tag.socialAuthorText.setText(c.getString(c.getColumnIndex(RTOpenHelper.KEY_POSTER)));
                    tag.socialNetworkText.setText("via " + c.getString(c.getColumnIndex(RTOpenHelper.KEY_SOCIAL_SERVICE)));
                    break;
                case PHOTO:
                    picasso.load(Uri.parse(c.getString(c.getColumnIndex(RTOpenHelper.KEY_PHOTO_URI)))).into(tag.photo);
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

        public LinearLayout detailLayout;

        public ImageView author;

        public ImageView photo;

        public TextView socialPostText, socialAuthorText, socialNetworkText;

        public EventTag(Context context, Type type, View v) {
            this.type = type;

            map = (ImageView) v.findViewById(R.id.map_image);
            detailLayout = (LinearLayout) v.findViewById(R.id.detail_layout);
            author = (ImageView) v.findViewById(R.id.author_image);

            switch (type) {
                case SOCIAL:
                    View socialDetail = LayoutInflater.from(context).inflate(R.layout.detail_social, null);
                    socialPostText = (TextView) socialDetail.findViewById(R.id.postText);
                    socialAuthorText = (TextView) socialDetail.findViewById(R.id.authorText);
                    socialNetworkText = (TextView) socialDetail.findViewById(R.id.networkText);
                    detailLayout.addView(socialDetail);
                    break;
                case PHOTO:
                    photo = new ImageView(context);
                    detailLayout.addView(photo);
                    break;
            }

        }

        public Type getType() {
            return type;
        }

    }

}
