package com.example.sdaassign4_2021;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * @author Edited by Rafael Izarra 2022
 *
 *
 */
public class Settings extends Fragment {

    private static final String USER_NAME_KEY = "USER_NAME_KEY";
    private static final String USER_EMAIL_KEY = "USER_EMAIL_KEY";
    private static final String USER_ID_KEY = "USER_ID_KEY";
    public EditText mUserName, mUserEmail, mUserID;
    Button resetDetailsButton;


    public Settings() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final SharedPreferences prefs = getActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        // User name
        mUserName = (EditText) root.findViewById(R.id.userName);
        mUserName.setText(prefs.getString(USER_NAME_KEY,""));

        //User Email
        mUserEmail = (EditText) root.findViewById(R.id.email);
        mUserEmail.setText(String.valueOf(prefs.getString(USER_EMAIL_KEY, "")));

        //User ID
        mUserID = (EditText) root.findViewById(R.id.borrowerID);
        mUserID.setText(prefs.getString(USER_ID_KEY, ""));

        // Create a buttton and set an even listener to save detials locally in the share preferences
        Button saveDetailsButton = root.findViewById(R.id.saveDetailsButton);
        saveDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailText = mUserEmail.getText().toString();

                if (!emailText.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(USER_NAME_KEY, mUserName.getText().toString());
                    editor.putString(USER_EMAIL_KEY, mUserEmail.getText().toString());
                    editor.putString(USER_ID_KEY, mUserID.getText().toString());
                    editor.apply();
                    Toast.makeText(getActivity(), "Your details have been saved sucesfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Enter valid email address!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //Button to reset the users preferences

        resetDetailsButton = root.findViewById(R.id.resetButton);
        resetDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(USER_NAME_KEY, "");
                editor.putString(USER_EMAIL_KEY, "");
                editor.putString(USER_ID_KEY, "");
                editor.apply();
                mUserName.setText(String.valueOf(prefs.getString(USER_NAME_KEY, "")));
                mUserEmail.setText(String.valueOf(prefs.getString(USER_EMAIL_KEY, "")));
                mUserID.setText(prefs.getString(USER_ID_KEY, ""));
                Toast.makeText(getActivity(), "Your details have been reset!", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }


}
