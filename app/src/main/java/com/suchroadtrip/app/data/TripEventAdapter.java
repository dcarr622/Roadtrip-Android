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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.suchroadtrip.app.R;
import com.suchroadtrip.lib.CircleTransform;
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
        View v = null;

        if (cursor.getColumnIndex(RTOpenHelper.KEY_SOCIAL_SERVICE) != -1) {
            v = LayoutInflater.from(context).inflate(R.layout.card_social, null);
            v.setTag(new EventTag(EventTag.Type.SOCIAL, v));
            Log.d(TAG, "social event");
        } else if (cursor.getColumnIndex(RTOpenHelper.KEY_PHOTO_URI) != -1) {
            v = LayoutInflater.from(context).inflate(R.layout.card_media, null);
            v.setTag(new EventTag(EventTag.Type.PHOTO, v));
            Log.d(TAG, "photo event");
        }
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor c) {
        if (view.getTag() != null && view.getTag() instanceof EventTag) {
            EventTag tag = (EventTag) view.getTag();

            Picasso picasso = Picasso.with(context);

            if (tag.map != null) {
                LatLng latLng = new LatLng(c.getDouble(c.getColumnIndex(RTOpenHelper.KEY_LAT)), c.getDouble(c.getColumnIndex(RTOpenHelper.KEY_LNG)));
                tag.map.onCreate(null);
                tag.map.onResume();
                tag.map.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                tag.map.getMap().addMarker(new MarkerOptions().position(latLng).title(""));
                tag.map.getMap().getUiSettings().setZoomControlsEnabled(false);
                tag.map.getMap().getUiSettings().setAllGesturesEnabled(false);
            }

            picasso.load("http://vinnie.io/me.jpg").transform(new CircleTransform()).into(tag.author);

            switch (tag.getType()) {
                case SOCIAL:
                    tag.socialPostText.setText(c.getString(c.getColumnIndex(RTOpenHelper.KEY_TEXT)));
                    tag.socialAuthorText.setText(c.getString(c.getColumnIndex(RTOpenHelper.KEY_AUTHOR)));
                    tag.socialNetworkText.setText("via " + c.getString(c.getColumnIndex(RTOpenHelper.KEY_SOCIAL_SERVICE)));
                    break;
                case PHOTO:
                    picasso.load(Uri.parse(c.getString(c.getColumnIndex(RTOpenHelper.KEY_PHOTO_URI)))).resizeDimen(R.dimen.card_photo_size, R.dimen.card_photo_size).into(tag.photo);
                    break;
            }
        }
    }

    private static class EventTag {

        public static enum Type {
            SOCIAL, PHOTO, LOCATION
        }

        private Type type;

        public MapView map;

        public LinearLayout detailLayout;

        public ImageView author;

        public ImageView photo;

        public TextView socialPostText, socialAuthorText, socialNetworkText;

        public EventTag(Type type, View v) {
            this.type = type;

            map = (MapView) v.findViewById(R.id.map_view);
            author = (ImageView) v.findViewById(R.id.author_image);

            switch (type) {
                case SOCIAL:
                    socialPostText = (TextView) v.findViewById(R.id.postText);
                    socialAuthorText = (TextView) v.findViewById(R.id.authorText);
                    socialNetworkText = (TextView) v.findViewById(R.id.networkText);
                    break;
                case PHOTO:
                    photo = (ImageView) v.findViewById(R.id.media_image);
                    break;
            }

        }

        public Type getType() {
            return type;
        }

    }

}
