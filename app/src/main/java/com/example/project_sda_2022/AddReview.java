package com.example.project_sda_2022;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class AddReview extends AppCompatActivity {
    EditText mAddReviewTxt;
    Button mAddReviewBtn;
    private FirebaseFirestore fStore = null;
    private DocumentReference docRef;
    private String mUserName, userID, reviewID, productID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        mAddReviewTxt = findViewById(R.id.add_review_txt);
        mAddReviewBtn = findViewById(R.id.add_review_btn);

        productID = getIntent().getStringExtra("productID");
        userID = getIntent().getStringExtra("userID");
        mUserName = getIntent().getStringExtra("userName");
        fStore = FirebaseFirestore.getInstance();

        mAddReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReviewDB();
            }
        });

    }
    private void addReviewDB() {
        //updates the arrayList (simple adapter) when the database is updated.
        docRef = fStore.collection("reviews").document();
        Map<String , Object> reviews = new HashMap<>();
        reviews.put("comment", mAddReviewTxt.getText().toString());
        reviews.put("userID",userID);
        reviews.put("userName",mUserName);
        reviews.put("productID",productID);
        docRef.set(reviews).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Review added!",
                        Toast.LENGTH_LONG).show();
                reviewID = docRef.getId();
                Log.d("myReview", reviewID);
                Intent intent = new Intent();
                intent.putExtra("reviewID", reviewID);
                setResult(78, intent);
                AddReview.super.onBackPressed();
                Log.d("myReview", reviewID);
                insertReviewID(reviewID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "There was an error loading the reviews",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Isert review id field to product collection
     * @param reviewID the id of the review generated in the addReview method
     */
    private void insertReviewID(String reviewID) {
        docRef = fStore.collection("products").document(productID);
        Map<String, Object>data = new HashMap<>();
        data.put("reviewID", reviewID);
        docRef.set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AddReview.this, "Successful review added!.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}