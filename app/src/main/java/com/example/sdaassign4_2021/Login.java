package com.example.sdaassign4_2021;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.rey.material.widget.CheckBox;

public class Login extends AppCompatActivity {
    // Instance of variable required in the activity
    private static final String USER_EMAIL_KEY = "USER_EMAIL_KEY";
    private static final String USER_PASSWORD_KEY = "USER_PASSWORD_KEY";

    EditText mEmail, mPassword;
    Button mLoginBtn;
    CheckBox checkBoxRemember;
    TextView mCreateBtn, forgotPassword;
    ProgressBar progressBar;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initializing the variables.
        final SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);


        mEmail = findViewById(R.id.editTextEmail);
        mPassword = findViewById(R.id.password);
        forgotPassword = findViewById(R.id.forgetPassword);
        progressBar = findViewById(R.id.progressBar);
        mLoginBtn = findViewById(R.id.loginBtn);
        mCreateBtn = findViewById(R.id.createText);
        checkBoxRemember = findViewById(R.id.rememberChkbox);

        fAuth = FirebaseAuth.getInstance();

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validate the data user entered
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                //TextUtils class has a method that let us check for empty strings
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required!");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required!");
                    return;
                }
                if(password.length() < 6) {
                    mPassword.setError("Password is shorter than 6 Characters!");
                    return;
                }

                // display the progress bar to the user if conditions are mt
                progressBar.setVisibility(View.VISIBLE);

                //authenticate the user

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            // saving user's details in preferences if the user wants to keep logged in.
                            if (checkBoxRemember.isChecked()) {
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString(USER_EMAIL_KEY, email);
                                editor.putString(USER_PASSWORD_KEY, password);
                                editor.apply();
                                }
                            Toast.makeText(Login.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else{
                            Toast.makeText(Login.this, "Ups!, " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        // the user is given the option to reset the password 
        //a new textview is created with an alerDiaog object to display the instructions
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword(view);
            }
        });

        // Go to the register page when clicking the register(textview).
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });

    }

    private void forgotPassword(View view) {
        final EditText resetEmail = new EditText(view.getContext());
        final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
        passwordResetDialog.setTitle("Would like to rest your password?");
        passwordResetDialog.setMessage("Enter your email to reset your password.");
        passwordResetDialog.setView(resetEmail);

        //here depending on the users' choice, (yes or no) both options are handled using the same alerdialog obj
        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // get the email and then send the reset link to the user and then let the user know if it was successful.
                String email = resetEmail.getText().toString();
                fAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Login.this,"Reset link was sent to: " + email, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this,"Ups, there was a problem with: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

        passwordResetDialog.create().show();
    }
}