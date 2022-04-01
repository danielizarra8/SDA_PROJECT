package com.example.sdaassign4_2021;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * A simple {@link Fragment} subclass.
 * @author Edited by Rafael Izarra 2022
 *
 *
 */
public class Settings extends Fragment {

    private static final String TAG = "verifyUserMessage";
    private static final String USER_DATA_KEY = "USER_DATA_KEY";
    private static final String USER_NAME_KEY = "USER_NAME_KEY";
    private static final String USER_PHONE_KEY = "USER_PHONE_KEY";
    private static final String USER_ADDRESS_KEY = "USER_ADDRESS_KEY";
    SharedPreferences userPrefs;

    TextView name, email, address, phone, verifyMessage;
    Button mLogoutBtn, mVerifyBtn, mChangePwdBtn;
    String userID;

    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FirebaseFirestore fStore;

    public Settings() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //save name share preferences
        userPrefs = getActivity().getSharedPreferences(USER_DATA_KEY,Context.MODE_PRIVATE);

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_setting, container, false);

        fAuth = FirebaseAuth.getInstance();

        // display data if user is logged in otherwise display empty fields
        if(fAuth.getCurrentUser() !=null) {


            verifyMessage = root.findViewById(R.id.verifyMessage);
            mVerifyBtn = root.findViewById(R.id.verifyBtn);
            mChangePwdBtn = root.findViewById(R.id.changePwdBtn);

            name = root.findViewById(R.id.nameProfile);
            email = root.findViewById(R.id.emailProfile);
            address = root.findViewById(R.id.addressProfile);
            phone = root.findViewById(R.id.phoneProfile);

            //Instancitate the firebase authenticate and database module
            //fAuth = FirebaseAuth.getInstance();
            fStore = FirebaseFirestore.getInstance();
            //get current user to verify email.
            fUser = fAuth.getCurrentUser();
            //retrieve the current user id.
            userID = fAuth.getCurrentUser().getUid();

            //To display message depending user is has been verified through email.
            if (!fUser.isEmailVerified()) {
                verifyMessage.setVisibility(View.VISIBLE);
                mVerifyBtn.setVisibility(View.VISIBLE);

                mVerifyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        // if the user verify the email
                        fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(view.getContext(), "Verification email has been resent!", Toast.LENGTH_SHORT).show();
                            }
                            //if the user does not verify the email
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(TAG, "Failure to verify user's email address. Reason: " + e.getMessage());
                            }
                        });
                    }
                });
            }

            //retrieve data (phone,email,name) using DocumentReference from the firestore db associated with the user is.
            DocumentReference documentReference = fStore.collection("users").document(userID);
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    name.setText(documentSnapshot.getString("name"));
                    email.setText(documentSnapshot.getString("email"));
                    address.setText(documentSnapshot.getString("address"));
                    phone.setText(documentSnapshot.getString("phone"));
                    saveDetailsSharePreferences(documentSnapshot.getString("name"),
                                                documentSnapshot.getString("address"),
                                                documentSnapshot.getString("phone"),
                                                userPrefs);
                }
            });
            //Reset the password button logic
            mChangePwdBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final EditText resetPassword = new EditText(view.getContext());
                    final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                    passwordResetDialog.setTitle("Would like to rest your password?");
                    passwordResetDialog.setMessage("Enter your new password");
                    passwordResetDialog.setView(resetPassword);

                    //here depending on the users' choice, (yes or no) both options are handled using the same alerdialog obj
                    passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // get the email and then send the reset link to the user and then let the user know if it was successful.
                            String newPassword = resetPassword.getText().toString();
                            fUser.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(view.getContext(), "Password has been reset successfully",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(view.getContext(), "Sorry, password has not been reset!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // if clicked no, close the dialog and redirect user to the same page.
                        }
                    });
                    //display the dialog
                    passwordResetDialog.create().show();
                }
            });

            //logout the user and redirect it to the login page
            mLogoutBtn = root.findViewById(R.id.logoutBtn);
            mLogoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userPrefs.edit().clear().apply();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getActivity(), Login.class));
                }
            });
        }else{
            Log.d(TAG,"Log in first");
        }

        return root;
    }

    private void saveDetailsSharePreferences(String name, String address, String phone, SharedPreferences prefs) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USER_NAME_KEY,name);
        editor.putString(USER_ADDRESS_KEY,address);
        editor.putString(USER_PHONE_KEY,phone);
        editor.apply();
    }

}
