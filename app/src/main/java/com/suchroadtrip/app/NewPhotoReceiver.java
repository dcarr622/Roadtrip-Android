package com.suchroadtrip.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by vmagro on 1/25/14.
 */
public class NewPhotoReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("newphoto", intent.getData().toString());
    }

}
