package com.example.sdaassign4_2021;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
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
        setContentView(R.layout.activity_check_out);

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

        dbRef = FirebaseFirestore.getInstance();
        getdata(bookID);
    }

    private void getdata(String bookID) {
        //Initialize db Firebase
        //dbRef = FirebaseFirestore.getInstance();
        //Point a reference to the db with a collection and document required.
        docRef = dbRef.collection("books").document(bookID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        Toast.makeText(CheckOut.this, "Sucessfull book!.", Toast.LENGTH_SHORT).show();
                        DocumentSnapshot dataSnapshot = task.getResult();
                        String author = dataSnapshot.getString("Author");
                        String availability = String.valueOf(dataSnapshot.getBoolean("Availability"));
                        setAvailabilityView(availability);
                        Log.i(TAG1, "data was retrieved!" + author);
                    } else {
                        Toast.makeText(CheckOut.this, "Book doesn't exist.", Toast.LENGTH_SHORT).show();

                    }

                } else {
                    Toast.makeText(CheckOut.this, "Failed to get data.", Toast.LENGTH_SHORT).show();
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

    //source SDA_2019 android course examples ViewGroup demo
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
        //date time year
        CharSequence currentTime = DateUtils.formatDateTime(this, mDateAndTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);
        CharSequence SelectedDate = DateUtils.formatDateTime(this, mDateAndTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
        String date = SelectedDate + ", " + currentTime;
        String finalSummary =  /* date1 + " " + date2 + "   " + "\n" +  */
                                "USER NAME: " + getIntent().getStringExtra("userName") + "\n" +
                                "USER ID: " + getIntent().getStringExtra("userID") + "\n" +
                                "BOOK TILE: "+ title + " - BOOK ID: " + bookID + "\n" +
                                "DATE: " + SelectedDate + " TIME: " + currentTime;
        mDisplaySummary.setText(finalSummary);
        sendOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inserDateDB(date);
                addOrderDB();
            }
        });

    }

    private void addOrderDB() {
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
            Toast.makeText(CheckOut.this, "SUCESSFULL ORDER CREATED!.", Toast.LENGTH_SHORT).show();
            }
        });
       // SimpleDateFormat ft = new SimpleDateFormat ("yyyyMMddHHmmss");

    }

    private void setAvailabilityView(String availability){
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
