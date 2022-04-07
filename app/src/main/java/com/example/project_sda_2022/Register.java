package com.example.project_sda_2022;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_sda_2022.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Register class implements a class that display a form to the user
 * the user must fill the the form in order to register
 * User is register when clicked on register if the form was filled properly
 *  * @author Rafael Izarra
 *  * @version 1.0
 *  * @since 30/03/2022
 */
public class Register extends AppCompatActivity {

    private static final String TAG = "UserCreationDB";
    private static final String USER_EMAIL_KEY = "USER_EMAIL_KEY";
    private static final String USER_LOGGED_IN = "USER_LOGGED_IN";
    private static final String USER_DATA_KEY = "USER_DATA_KEY";
    private static final String USER_NAME_KEY = "USER_NAME_KEY";
    private static final String USER_ID_KEY = "USER_ID_KEY";
    private static final String USER_PHONE_KEY = "USER_PHONE_KEY";
    private static final String USER_ADDRESS_KEY = "USER_ADDRESS_KEY";
    SharedPreferences userPrefs;
    //Declaration of variable for components needed to implement the registration page
    String userID;
    EditText mFullName, mEmail, mPassword, mPassword2, mAddress, mPhone;
    Button mRegisterBtn;
    TextView mLoginBtn;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // init ui var
        mFullName = findViewById(R.id.editTextPersonName);
        mEmail = findViewById(R.id.editTextEmail);
        mPhone = findViewById(R.id.editTextPhone);
        mPassword = findViewById(R.id.password);
        mPassword2 = findViewById(R.id.password2);
        mAddress = findViewById(R.id.editTextTextAddress);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mLoginBtn = findViewById(R.id.loginActivity);
        progressBar = findViewById(R.id.progressBar);

        userPrefs = this.getSharedPreferences(USER_DATA_KEY, Context.MODE_PRIVATE);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        //Check if user is already log in, if so, send it to the main activity.

            mRegisterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Validate the data user entered
                    String fullname = mFullName.getText().toString();
                    String phone = mPhone.getText().toString();
                    String email = mEmail.getText().toString().trim();
                    String address = mAddress.getText().toString();
                    String password = mPassword.getText().toString().trim();
                    String password2 = mPassword2.getText().toString().trim();
                    //validating user input before creating the user.
                    if (TextUtils.isEmpty(email)) {
                        mEmail.setError("Email is Required!");
                        return;
                    }
                    if (TextUtils.isEmpty(password)) {
                        mPassword.setError("Password is Required!");
                        return;
                    }
                    if (password.length() < 5) {
                        mPassword.setError("Password is shorter than 5 Characters!");
                        return;
                    }
                    if (!password.equals(password2)) {
                        mPassword2.setError("Passwords must be the same!");
                        return;
                    }
                    // display the progress bar to the user if conditions are mt
                    progressBar.setVisibility(View.VISIBLE);

                    //register the user using firebase and check if it was sucessfull
                    fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                //verify email address for the user before creating the account. Get the user details through Firebase and pass it to fUser.
                                FirebaseUser fUser = fAuth.getCurrentUser();
                                // if the user verify the email
                                fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(Register.this, "Verification email has been sent to: " + email,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    //if the user does not verify the email
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Register.this,"Failure to verify user's email address. Reason: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });

                                Toast.makeText(Register.this, "User created successfully", Toast.LENGTH_SHORT).show();
                                //retrieve the user id from the firestore auth module which will be used to create a new doc for each user.
                                userID = fAuth.getCurrentUser().getUid();
                                //get the reference of the firestore db and create a user collection to save users' details in the given document (user id).
                                DocumentReference documentReference = fStore.collection("users").document(userID);
                                //data stored in a Map key (user) pair object to store the key and the actual data.
                                Map<String, Object> user = new HashMap<>();
                                user.put("name", fullname);
                                user.put("email", email);
                                user.put("phone", phone);
                                user.put("address", address);
                                saveDetailsSharePreferences(fullname,address,phone,email,userID, password);
                                //send the data to the db
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.i(TAG, "User was create successfully with ID: " + userID);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.i(TAG, "User was NO created due to: " + e.toString());
                                    }
                                });
                                //redirect the app to the main activity if no errors.
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                Toast.makeText(Register.this, "There was an error creating the user! - " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                mEmail.setError(task.getException().getMessage());
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });

                }
            });

        //calling login activity
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });
    }
    /**
     * Save user detials to sharepreferences
     * @param name user name
     * @param address user address
     * @param phone user phone
     * @param email user email
     * @param ID user id
     * @param password user password
     */
    private void saveDetailsSharePreferences(String name, String address, String phone, String email, String ID, String password) {
        if (userPrefs == null) {
            SharedPreferences.Editor editor = userPrefs.edit();
            editor.putString(USER_ID_KEY, ID);
            editor.putString(USER_NAME_KEY, name);
            editor.putString(USER_ADDRESS_KEY, address);
            editor.putString(USER_PHONE_KEY, phone);
            editor.putString(USER_EMAIL_KEY, email);
            editor.putBoolean(USER_LOGGED_IN, true);
            editor.putString("user_pw", password);
            editor.apply();
        }
    }

}