package com.suchroadtrip.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.suchroadtrip.app.R;

/**
* Created by david on 1/25/14.
*/
public class RegisterFragment extends Fragment {
    public static Fragment newInstance() {
        return new RegisterFragment();
    }

    // Values for email and password at the time of the login attempt.
    private String mUser;
    private String mEmail;
    private String mPassword;

    // UI references.
    private EditText mEmailView;
    private EditText mUserView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

}
