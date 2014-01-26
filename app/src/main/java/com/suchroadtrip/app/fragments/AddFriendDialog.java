package com.suchroadtrip.app.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.suchroadtrip.app.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 1/26/14.
 */
public class AddFriendDialog extends DialogFragment {

    EditText personName;

    /*For checking user logged-in status*/
    private static final String APP_SHARED_PREFS = "roadtrip_preferences";
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_add_friend, null);

        personName = (EditText) view.findViewById(R.id.friend_name);

        builder.setView(view).setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addFriend(personName.getText().toString());
            }
        }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

          /*Get shared prefs*/
        sharedPrefs = getActivity().getApplicationContext().getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);

        return builder.create();
    }

    private void addFriend(String name) {

        new AsyncRequest().execute(name);
    }

    private class AsyncRequest extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... params) {
            String id = getActivity().getApplicationContext().getSharedPreferences("roadtrip_preferences", Context.MODE_PRIVATE).getString("activeTrip", "not_found");
            HttpPost postRequest = new HttpPost(getString(R.string.roadtrip_add_friend));
            DefaultHttpClient httpClient = new DefaultHttpClient();
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair(" id", id));
            nameValuePairs.add(new BasicNameValuePair("friendName", params[0]));
            HttpResponse response = null;
            try {
                postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                response = httpClient.execute(postRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("Friend Response", String.valueOf(response.getStatusLine().getStatusCode()));
            return null;
        }
    }
}
