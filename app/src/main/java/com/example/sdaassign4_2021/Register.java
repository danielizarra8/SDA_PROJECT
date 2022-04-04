package com.example.sdaassign4_2021;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private static final String TAG = "UserCreationDB";
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

        mFullName = findViewById(R.id.editTextPersonName);
        mEmail = findViewById(R.id.editTextEmail);
        mPhone = findViewById(R.id.editTextPhone);
        mPassword = findViewById(R.id.password);
        mPassword2 = findViewById(R.id.password2);
        mAddress = findViewById(R.id.editTextTextAddress);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mLoginBtn = findViewById(R.id.loginActivity);
        progressBar = findViewById(R.id.progressBar);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        //Check if user is already log in, if so, send it to the main activity.
       // if(fAuth.getCurrentUser() !=null) {

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
                    //TextUtils class has a method that let us check for empty strings
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
            /*
        }else{
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

             */

        //calling login activity
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });
    }


}