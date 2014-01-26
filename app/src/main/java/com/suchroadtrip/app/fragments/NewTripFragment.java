package com.suchroadtrip.app.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.suchroadtrip.app.R;
import com.suchroadtrip.app.activities.MainActivity;
import com.suchroadtrip.lib.RTApi;

/**
 * Created by david on 1/25/14.
 */
public class NewTripFragment extends DialogFragment implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String TAG = "NewTripFragment";

//    List<EditText> personNames;
    EditText tripName;
    EditText destination;
    EditText box1;
//    LinearLayout nameBoxes;

    /*For checking user logged-in status*/
    private static final String APP_SHARED_PREFS = "roadtrip_preferences";
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;


    private LocationClient locationClient;
    private Location location = null;
    private boolean attemptedBeforeLocation;
    private String sTripName, sDestination;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        locationClient = new LocationClient(getActivity(), this, this);
        locationClient.connect();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_create_trip, null);

        tripName = (EditText) view.findViewById(R.id.trip_name);
        destination = (EditText) view.findViewById(R.id.destination);

//        personNames = new ArrayList<EditText>();

//        box1 = ((EditText) view.findViewById(R.id.friend_box));
//        personNames.add(box1);
//        nameBoxes = (LinearLayout) view.findViewById(R.id.name_boxes_list);

        builder.setView(view).setPositiveButton("Start", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startTrip(tripName.getText().toString(), destination.getText().toString());
            }
        }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

//        ((EditText) view.findViewById(R.id.friend_box)).addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                EditText newName = new EditText(getActivity());
//                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                nameBoxes.setLayoutParams(p);
//                newName.setHint(R.string.friends_username);
//                nameBoxes.addView(newName);
//                personNames.add(newName);
//            }
////        });
//
          /*Get shared prefs*/
        sharedPrefs = getActivity().getApplicationContext().getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);

        return builder.create();
    }

    private void startTrip(String tripname, String destination) {
         /* Show/hide proper menu bar icons */
        getActivity().invalidateOptionsMenu();
        MainActivity.getInstance().startTimer();

        if (location == null) {
            attemptedBeforeLocation = true;
            sTripName = tripname;
            sDestination = destination;
            return;
        }

        RTApi.startTrip(getActivity(), tripname, location, destination, (MainActivity) getActivity());

//        HttpPost postRequest = new HttpPost(getString(R.string.roadtrip_add_friend));
//        DefaultHttpClient httpClient = new DefaultHttpClient();

//        for (EditText nameBox: personNames) {
//            if (nameBox.getText().toString() != null) {
//                String friend = nameBox.getText().toString();
//                String id = getActivity().getApplicationContext().getSharedPreferences("roadtrip_preferences", Context.MODE_PRIVATE).getString("activeTrip", "not_found");
//                Log.v("Friend to Add", friend);
//                Log.v("ID of Friend to add", id);
//                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//                nameValuePairs.add(new BasicNameValuePair(" id", id));
//                nameValuePairs.add(new BasicNameValuePair("friendName", friend));
//                HttpResponse response = null;
//                try {
//                    postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//                    response = httpClient.execute(postRequest);
//                    Log.d("Friend Response", String.valueOf(response.getStatusLine().getStatusCode()));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        location = locationClient.getLastLocation();

        if (attemptedBeforeLocation) {
            attemptedBeforeLocation = false;
            startTrip(sTripName, sDestination);
        }
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
