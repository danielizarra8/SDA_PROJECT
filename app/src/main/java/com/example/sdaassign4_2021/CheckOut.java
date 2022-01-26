package com.example.sdaassign4_2021;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Checkout class
 * @author Edited by Rafael Izarra 2022
 */

public class CheckOut extends AppCompatActivity {
    String TAG1="Checkout";
    FirebaseFirestore dbRef = null;

    TextView mDisplaySummary, mBookAvailability;
    Button sendOrderButton, setDateButton;
    Calendar mDateAndTime = Calendar.getInstance();
    DocumentReference docRef;
    String title, bookID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the content view  to activity check our xml
        setContentView(R.layout.activity_check_out);

        //instantiate the widgets variables
        TextView mBookTitle = findViewById(R.id.confirm);
        mBookAvailability = findViewById(R.id.availability);
        sendOrderButton = findViewById(R.id.orderButton);
        setDateButton = findViewById(R.id.date);

        //set the toolbar we have overridden
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        //find the summary textview
        mDisplaySummary = findViewById(R.id.orderSummary);

        //set the text title Textview field of the book clicked and extract ID of the book
        title = getIntent().getStringExtra("title");
        bookID = getIntent().getStringExtra("bookID");
        mBookTitle.setText(title);

        //instantiate the firsetose database and get the data getData()
        dbRef = FirebaseFirestore.getInstance();
        getdata(bookID);
    }

    private void getdata(String bookID) {
        //Point a reference to the db with a collection and document required.
        docRef = dbRef.collection("books").document(bookID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        //pass the data from task to dataSnapshop to get access to individual fields
                        DocumentSnapshot dataSnapshot = task.getResult();
                        String author = dataSnapshot.getString("Author");
                        String availability = String.valueOf(dataSnapshot.getBoolean("Availability"));
                        setAvailabilityView(availability);
                        Toast.makeText(CheckOut.this, "Sucessfull book!.", Toast.LENGTH_SHORT).show();
                        Log.i(TAG1, "data was retrieved succesfully!");
                    } else {
                        Log.i(TAG1, "Book was not retrieved!");

                    }

                } else {
                    Log.i(TAG1, "Failed to get data!");
                }
            }
        });

    }

    // this method handle the functionality of the back arrow in the toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Taken from source SDA_2019 android course examples ViewGroup demo
    public void onDateClicked(View v) {

        DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                mDateAndTime.set(Calendar.YEAR, year);
                mDateAndTime.set(Calendar.MONTH, monthOfYear);
                mDateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateAndTimeDisplay();
            }
        };

        new DatePickerDialog(CheckOut.this, mDateListener,
                mDateAndTime.get(Calendar.YEAR),
                mDateAndTime.get(Calendar.MONTH),
                mDateAndTime.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void updateDateAndTimeDisplay() {
        //get the data and time from datepicker and save them as a string values
        CharSequence currentTime = DateUtils.formatDateTime(this, mDateAndTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);
        CharSequence SelectedDate = DateUtils.formatDateTime(this, mDateAndTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
        String date = SelectedDate + ", " + currentTime;
        //update the field summary with all details
        String finalSummary =   "USER NAME: " + getIntent().getStringExtra("userName") + "\n" +
                                "USER ID: " + getIntent().getStringExtra("userID") + "\n" +
                                "BOOK TILE: "+ title + " - BOOK ID: " + bookID + "\n" +
                                "DATE: " + SelectedDate + " TIME: " + currentTime;
        //set the summary text to the textview field
        mDisplaySummary.setText(finalSummary);
        //we call the insertDB() method to insert the order date field to the database (books document)
        //the addOrder creates a new collection "orders"
        sendOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inserDateDB(date);
                addOrderDB();
            }
        });

    }

    private void addOrderDB() {
        //create collection with required fields; currentdate,duedate,bookid and userid
        Map<String , Object>orderDates = new HashMap<>();
        Date currentDate  = mDateAndTime.getTime();
        mDateAndTime.add(Calendar.DAY_OF_MONTH,14);
        Date dueDate = mDateAndTime.getTime();
        orderDates.put("currentDate", currentDate);
        orderDates.put("dueDate", dueDate);
        orderDates.put("bookID",bookID);
        orderDates.put("userID",getIntent().getStringExtra("userID"));
        docRef = dbRef.collection("orders").document("1");
        docRef.set(orderDates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            sendOrderButton.setEnabled(false);
            }
        });


    }

    private void setAvailabilityView(String availability){
        // this method enables the button (send order and select date) if details in the setting tab were sucessfully entered.
        sendOrderButton.setEnabled(false);
        setDateButton.setEnabled(false);
        if (availability.equals("true")){
        mBookAvailability.setText("Book is available, press send order to continue!");
        sendOrderButton.setEnabled(true);
        setDateButton.setEnabled(true);
        }else{
            mBookAvailability.setText("Book is unavailable at the moment!");
        }
    }

    private void inserDateDB(String selectedDate){
        //this method simply update the books document with a new field orderDate
        docRef = dbRef.collection("books").document("1");
        Map<String, Object>data = new HashMap<>();
        data.put("orderDate", selectedDate);
        docRef.set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CheckOut.this, "Sucessfull date added!.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
