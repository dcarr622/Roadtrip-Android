package com.suchroadtrip.app.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.suchroadtrip.app.LocationMonitorService;
import com.suchroadtrip.app.R;
import com.suchroadtrip.lib.RTApi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 1/25/14.
 */
public class NewTripFragment extends DialogFragment {

    private static final String TAG = "NewTripFragment";

    List<EditText> personNames;
    EditText tripName;
    EditText destination;
    EditText box1;
    LinearLayout nameBoxes;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_create_trip, null);

        EditText tripName = (EditText) view.findViewById(R.id.trip_name);
        EditText destination = (EditText) view.findViewById(R.id.destination);

        personNames = new ArrayList<EditText>();

        box1 = ((EditText) view.findViewById(R.id.friend_box));
        personNames.add(box1);
        nameBoxes = (LinearLayout) view.findViewById(R.id.name_boxes_list);

        builder.setView(view).setPositiveButton("Start", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startTrip();
            }
        }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        ((EditText) view.findViewById(R.id.friend_box)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText newName = new EditText(getActivity());
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                nameBoxes.setLayoutParams(p);
                newName.setHint(R.string.friends_username);
                nameBoxes.addView(newName);
                personNames.add(newName);
            }
        });

        return builder.create();
    }

    private void startTrip() {

        LocationManager mgr = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = mgr.getBestProvider(criteria, true);
        final Location loc = mgr.getLastKnownLocation(provider);

         RTApi.startTrip(getActivity(), tripName.getEditableText().toString(), loc, destination.getEditableText().toString(), new RTApi.StartTripCallback() {
             @Override
             public void tripStarted(String id) {
                 Log.d(TAG, "started trip with id " + id);
                 if (id == null) {
                     Log.e(TAG, "null trip id");
                     return;
                 }
                 Intent intent = new Intent(getActivity(), LocationMonitorService.class);
                 intent.putExtra("tripId", id);
                 getActivity().startService(intent);
             }
         });

        for (EditText nameBox: personNames) {
            if (nameBox.getEditableText().toString() != null && nameBox.getEditableText().toString().length() > 1) {

            }
        }
    }

    public static Fragment newInstance() {
        return new NewTripFragment();
    }
}
