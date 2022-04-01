package com.example.sdaassign4_2021;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.rey.material.widget.CheckBox;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Checkout class
 * @author Edited by Rafael Izarra 2022
 */

public class ReviewOrder extends AppCompatActivity {
    private final static String CART_KEY = "CART_KEY";
    private static final String USER_DATA_KEY = "USER_DATA_KEY";
    private static final String USER_NAME_KEY = "USER_NAME_KEY";
    private static final String USER_PHONE_KEY = "USER_PHONE_KEY";
    private static final String USER_ADDRESS_KEY = "USER_ADDRESS_KEY";

    String TAG1="Checkout";
    FirebaseFirestore dbRef = null;

    CheckBox checkBoxDelivery;
    TextView mDisplaySummary, mPaymentDetails, mBookAvailability, mTotalOrderAmount, mTotalOrderQty, mDeliveryFee, mTotalAmountCart, mDeliveryAddress;
    Button sendOrderButton, setDateButton, mAddCardButton;
    Calendar mDateAndTime = Calendar.getInstance();
    DocumentReference docRef;
    String bookID, totalAmount, totalQty;
    ImageView checkedIcon;
    SharedPreferences totalPrefs, cartPrefs;
    private int delivery_fee = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the content view  to activity check our xml
        setContentView(R.layout.activity_review_order);

        //instantiate the widgets variables
        checkedIcon = findViewById(R.id.cartCheckedIcon);
        mTotalOrderAmount = findViewById(R.id.totalAmountOrder);
        mTotalAmountCart = findViewById(R.id.totalAmountCart);
        mTotalOrderQty = findViewById(R.id.totalQtyOrder);
        mDeliveryFee = findViewById(R.id.deliveryFeeTxt);
        mDeliveryAddress = findViewById(R.id.deliveryDetailsTxt);
        sendOrderButton = findViewById(R.id.sendOrderBtn);
        mAddCardButton = findViewById(R.id.addCardBtn);
        setDateButton = findViewById(R.id.date);
        checkBoxDelivery = findViewById(R.id.deliveryChkbox);
        mPaymentDetails = findViewById(R.id.paymentDetailsStatus);

        bookID = getIntent().getStringExtra("bookID");
        totalPrefs = getSharedPreferences(CART_KEY, MODE_PRIVATE);
        int cartTotalQty = totalPrefs.getInt("cart_qty",0);
        int cartTotalAmount = totalPrefs.getInt("cart_amount",0);
        int totalOrderAmount = cartTotalAmount + delivery_fee;
        totalAmount = String.valueOf(cartTotalAmount);
        totalQty = String.valueOf(cartTotalQty);

        //boolean isCardAdded = true;
        mTotalAmountCart.setText(totalAmount + "$");
        mTotalOrderQty.setText(totalQty + " Items");
        mTotalOrderAmount.setText(String.valueOf(totalOrderAmount) + "$");
        //activity for result
        boolean b;
        ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == 78){
                            Intent intent = result.getData();
                            if(intent != null){
                                //get data
                                boolean isCardAdded = intent.getBooleanExtra("card_status",true);
                                if(isCardAdded == true){
                                    mPaymentDetails.setText("Card added.");
                                    checkedIcon.setVisibility(View.VISIBLE);
                                    sendOrderButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) { clickOnSendOrder(isCardAdded);}});
                                }
                            }
                        }
                    }
                });

        //add listener to addcard btn
        mAddCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getApplicationContext(), CheckoutActivityJava.class));
                Intent checkoutCardActivity = new Intent(getApplicationContext(),CheckoutActivityJava.class);
                resultLauncher.launch(checkoutCardActivity);
            }
        });
        sendOrderButton.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent motionEvent) {
                return false;
            }
        });


        //find the summary textview
        mDisplaySummary = findViewById(R.id.summaryTxt);

        loadUserData();

        //instantiate the firsetose database and get the data getData()
        dbRef = FirebaseFirestore.getInstance();
        getdata(bookID);
    }

    private void clickOnSendOrder(boolean isCardAdded) {
        if (isCardAdded == true){
            Toast.makeText(this, "Order placed!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Payment method is empty!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences(USER_DATA_KEY, Context.MODE_PRIVATE);
        String name = prefs.getString(USER_NAME_KEY,"user");
        String phone = prefs.getString(USER_PHONE_KEY,"phone");
        mDisplaySummary.setText("You are nearly there " + name + "! \n" +
                                "Please review or add delivery and payment method before placing your order. \n" +
                                "We will contact you though your: " + phone + " number to deliver your order \n");
        mDeliveryAddress.setText(prefs.getString(USER_ADDRESS_KEY,"address"));
        checkBoxDelivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(checkBoxDelivery.isChecked()){
                        delivery_fee = 3;
                    }else{
                        delivery_fee = 0;
                    }
                mDeliveryFee.setText(String.valueOf(delivery_fee) + "$  (delivery)");
            }
        });
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
                        Toast.makeText(ReviewOrder.this, "Congratulations, you have complete your purchase!.", Toast.LENGTH_SHORT).show();
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

        new DatePickerDialog(ReviewOrder.this, mDateListener,
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

    private void inserDateDB(String selectedDate){
        //this method simply update the books document with a new field orderDate
        docRef = dbRef.collection("books").document("1");
        Map<String, Object>data = new HashMap<>();
        data.put("orderDate", selectedDate);
        docRef.set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ReviewOrder.this, "Sucessfull date added!.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
