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
                picasso.load(url).resizeDimen(R.dimen.map_width, R.dimen.map_height).centerCrop().into(tag.map);
            }

            String authorPicUrl = c.getString(c.getColumnIndex(RTOpenHelper.KEY_AUTHOR_PIC));
            if(authorPicUrl == null)
                authorPicUrl = "http://ptzlabs.com/me.jpg";
            picasso.load(authorPicUrl).transform(new CircleTransform()).into(tag.author);

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

        public ImageView map;

        public LinearLayout detailLayout;

        public ImageView author;

        public ImageView photo;

        public TextView socialPostText, socialAuthorText, socialNetworkText;

        public EventTag(Context context, Type type, View v) {
            this.type = type;

            map = (ImageView) v.findViewById(R.id.map_image);
            author = (ImageView) v.findViewById(R.id.author_image);
            detailLayout = (LinearLayout) v.findViewById(R.id.card_detail_content);

            Log.d(TAG, "detailLayout is null: " + (detailLayout == null));

            switch (type) {
                case SOCIAL:
                    View socialDetail = LayoutInflater.from(context).inflate(R.layout.detail_social, null);
                    socialPostText = (TextView) socialDetail.findViewById(R.id.postText);
                    socialAuthorText = (TextView) socialDetail.findViewById(R.id.authorText);
                    socialNetworkText = (TextView) socialDetail.findViewById(R.id.networkText);
                    detailLayout.addView(socialDetail);
                    break;
                case PHOTO:
                    View mediaDetail = LayoutInflater.from(context).inflate(R.layout.detail_media, null);
                    photo = (ImageView) mediaDetail.findViewById(R.id.media_image);
                    detailLayout.addView(mediaDetail);
                    break;
            }

        }

        public Type getType() {
            return type;
        }

    }

}
