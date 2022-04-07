package com.example.project_sda_2022;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TopicActivity extends AppCompatActivity {

    private static final String TAG = "DB_LISTENER";
    private FirebaseFirestore fStore = null;
    ImageView mImageItem;
    Button mAddReviewBtn;
    ArrayList<SetChatItem> mListReviews = new ArrayList<>();
    String mUserName, mUserEmail, mUserUri, mUser_login, productID, productName, imageURL;

    ChatReyclerViewAdapter recyclerViewAdapter;

    //initialise sharedpreferenecs in this activity
    private static final String USER_DATA_KEY = "USER_DATA_KEY";
    private static final String USER_ID_KEY = "USER_ID_KEY";
    private static final String USER_NAME_KEY = "USER_NAME_KEY";
    private static final String USER_EMAIL_KEY = "USER_EMAIL_KEY";
    public  static final String USER_URI_KEY = "user_pic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        //set the toolbar and the home button
        Toolbar toolbar = findViewById(R.id.topic_toolbar);
        setSupportActionBar(toolbar);
        mImageItem = findViewById(R.id.imageProductRev);
        mAddReviewBtn = findViewById(R.id.sendButton);

        //instantiate the firestore database and get the data getData()
        fStore = FirebaseFirestore.getInstance();

        //call sharedpreferences if we are here we should be logged in.
        SharedPreferences userPrefs = getSharedPreferences(USER_DATA_KEY, MODE_PRIVATE);

        //get product id, name and url from intent
        productID = getIntent().getStringExtra("productID");
        productName = getIntent().getStringExtra("productName");
        imageURL = getIntent().getStringExtra("imageURL");

        //this adds the users avatar to the Topic selection page.
        TextView myAvatarText = findViewById(R.id.user_welcome);

        //use sharedPreferences if they exist.
        mUserName = userPrefs.getString(USER_NAME_KEY, "");
        mUserEmail = userPrefs.getString(USER_EMAIL_KEY,"");
        mUserUri =  userPrefs.getString(USER_URI_KEY,"");

        //set data from the product are reviewing the products
        mUser_login = productName+'\n'+productID;
        myAvatarText.setText(mUser_login);
        Glide.with(mImageItem.getContext()).load(imageURL).into(mImageItem);

        //get the reviews displayed into the ui
        getReviewsDB();

        // initialising the recycler view and the layout manager
        RecyclerView recyclerView = findViewById(R.id.messageRecyclerView);
        recyclerViewAdapter = new ChatReyclerViewAdapter(mListReviews,this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Results from calling the AddReview activity
        ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == 78){
                            Intent resultData = result.getData();
                            if(resultData != null){
                                //get data from the intent (the review id) which was created in the AddReview.class
                                String reviewID = resultData.getStringExtra("reviewID");
                                Log.d("myReview ID: ", reviewID);
                                //getReviewsDB();
                                fStore.collection("reviews").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                                        @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                            Log.w(TAG, "listen:error", e);
                                            return;
                                        }
                                        //set an even listener to changes in the document (new reviews are added) an then update
                                        // the recyclerview accordingly
                                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                                String user = dc.getDocument().getString("comment");
                                                String msg = dc.getDocument().getString("userName");
                                                mListReviews.add(new SetChatItem(user,msg));
                                                // update the recyclerview adapter to reflect the changes, otherwise it won't display the data
                                                recyclerViewAdapter.notifyDataSetChanged();
                                                Log.d(TAG, "Review: " + dc.getDocument().getData());
                                            }
                                        }

                                    }
                                });
                            }
                        }
                    }
                });
        /**
         *         initiate the activity onResult
         *         it start a new activity to add the review into the databse
         */
        mAddReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addReviewActivity = new Intent(getApplicationContext(), AddReview.class);
                addReviewActivity.putExtra("userName", mUserName);
                addReviewActivity.putExtra("productID", productID);
                addReviewActivity.putExtra("userID", userPrefs.getString(USER_ID_KEY,""));
                resultLauncher.launch(addReviewActivity);
            }
        });
    }

    /**
     * Get the revies from the datbase and display in the recycler view
     */
    private void getReviewsDB() {
        fStore.collection("reviews").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    //loop through each ducument(reviews) retrieved
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String user = document.getString("userName");
                        String msg = document.getString("comment");
                        mListReviews.add(new SetChatItem(user,msg));
                        // update the recyclerview adapter to reflect the changes, otherwise it won't display the data
                        recyclerViewAdapter.notifyDataSetChanged();

                    } // end of loop

                } // end of if statement

            }
        });
    }

    /**
     * To return to previous page
     * @param item arrow back
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}