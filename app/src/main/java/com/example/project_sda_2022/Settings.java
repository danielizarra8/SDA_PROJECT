package com.example.project_sda_2022;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_sda_2022.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * Setting class user can review their information
 * User can reser password
 * User can review previous order
 * User can sign out
 * Rafael Izarra 2022
 *  * @version 1.0
 *  * @since 30/03/2022
 *
 */
public class Settings extends AppCompatActivity {

    private static final String TAG = "verifyUserMessage";
    private static final String USER_DATA_KEY = "USER_DATA_KEY";
    private static final String USER_NAME_KEY = "USER_NAME_KEY";
    private static final String USER_PHONE_KEY = "USER_PHONE_KEY";
    private static final String USER_EMAIL_KEY = "USER_EMAIL_KEY";
    private static final String USER_ADDRESS_KEY = "USER_ADDRESS_KEY";
    SharedPreferences userPrefs;

    TextView name, email, address, phone, verifyMessage, mLoginMessage;
    Button mLogoutBtn, mVerifyBtn, mChangePwdBtn, mLoginBtn, mViewOrder;
    String userID;

    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FirebaseFirestore fStore;
        @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.fragment_setting);
            userPrefs = getSharedPreferences(USER_DATA_KEY,Context.MODE_PRIVATE);
            fAuth = FirebaseAuth.getInstance();

            mChangePwdBtn = findViewById(R.id.changePwdBtn);
            mLogoutBtn = findViewById(R.id.logoutBtn);
            mViewOrder = findViewById(R.id.viewOrderBtm);

            // display data if user is logged in otherwise display empty fields
            if(fAuth.getCurrentUser() !=null) {

                //check if user has verified the email
                verifyMessage = findViewById(R.id.verifyMessage);
                mVerifyBtn = findViewById(R.id.verifyBtn);

                name = findViewById(R.id.nameProfile);
                email = findViewById(R.id.emailProfile);
                address = findViewById(R.id.addressProfile);
                phone = findViewById(R.id.phoneProfile);

                name.setText(userPrefs.getString(USER_NAME_KEY,""));
                email.setText(userPrefs.getString(USER_EMAIL_KEY,""));
                address.setText(userPrefs.getString(USER_ADDRESS_KEY,""));
                phone.setText(userPrefs.getString(USER_PHONE_KEY,""));

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
                mLogoutBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userPrefs.edit().clear().apply();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), Login.class));
                    }
                });
                //view previous orders
                mViewOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent viewOrderIntent = new Intent(getApplicationContext(), ViewOrder.class);
                        viewOrderIntent.putExtra("userID", userID);
                        startActivity(viewOrderIntent);
                    }
                });
            }else{
                mLoginMessage = findViewById(R.id.loginMessage);
                mLoginBtn = findViewById(R.id.loginBtn);
                mLoginMessage.setVisibility(View.VISIBLE);
                mLoginBtn.setVisibility(View.VISIBLE);
                mChangePwdBtn.setVisibility(View.GONE);
                mLogoutBtn.setVisibility(View.GONE);

                mLoginBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }
                });

            }
        }

}
