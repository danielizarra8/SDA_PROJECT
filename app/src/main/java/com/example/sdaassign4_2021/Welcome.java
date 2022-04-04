package com.example.sdaassign4_2021;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;


/**
 * A simple {@link Fragment} subclass.
 * @author Rafael Izarra, 2022
 * create an instance of this fragment.
 */
public class Welcome extends Fragment {

    private static final String USER_DATA_KEY = "USER_DATA_KEY";
    private static final String USER_NAME_KEY = "USER_NAME_KEY";
    private static final String USER_EMAIL_KEY = "USER_EMAIL_KEY";
    private static final String USER_LOGGED_IN = "USER_LOGGED_IN";

    private Button mSignInBtn, mSignUpBtn;
    TextView mWelcomeText;
    FirebaseAuth fAuth;

    private SharedPreferences prefs;
    private String userName,userEmail;
    private boolean isUserLoggedIn;

    public Welcome() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_wellcome, container, false);

        mWelcomeText = root.findViewById(R.id.welcomeTextUser);
        mSignInBtn = root.findViewById(R.id.signInBtn);
        mSignUpBtn = root.findViewById(R.id.signUpBtn);

        prefs = getActivity().getSharedPreferences(USER_DATA_KEY, Context.MODE_PRIVATE);
        userEmail = prefs.getString(USER_EMAIL_KEY,"");
        userName = prefs.getString(USER_NAME_KEY,"");
        isUserLoggedIn = prefs.getBoolean(USER_LOGGED_IN,false);
        //init the firestore auth module
        fAuth = FirebaseAuth.getInstance();
        //get user password (use token instead of saving password in an intent)
        String password = prefs.getString("user_pw","");
        //call LoginUser method if user has set the kept signed in option
        if(!TextUtils.isEmpty(userEmail) && isUserLoggedIn == false){
                LoginUser(userEmail,password);
        }
        //check user is signed in, otherwise ask to sign in or register
        if(fAuth.getCurrentUser() == null) {
            //open the signin activity
            mSignInBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getContext(), Login.class));
                }
            });
            //open the signup activity
            mSignUpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getContext(), Register.class));
                }
            });
        }
        else{
            mSignInBtn.setVisibility(getView().GONE);
            mSignUpBtn.setVisibility(getView().GONE);
            String nameTxtView = mWelcomeText.getText().toString() + ", " + userName;
            mWelcomeText.setText(nameTxtView);
            mWelcomeText.setVisibility(getView().VISIBLE);

        }
        return root;
        }

    private void LoginUser(String userEmail, String userPassword) {
        fAuth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getContext(), "Logged in successfully", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "Ups!, " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
