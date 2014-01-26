package com.suchroadtrip.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.suchroadtrip.lib.RTApi;

/**
 * Created by vmagro on 1/25/14.
 */
public class NewPhotoReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("newphoto", intent.getData().toString());
        LocationManager mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = mgr.getBestProvider(criteria, true);
        Location loc = mgr.getLastKnownLocation(provider);
        RTApi.addPicture(context, null, intent.getData(), loc);
    }

}
