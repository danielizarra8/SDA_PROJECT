package com.example.sdaassign4_2021;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.rey.material.widget.CheckBox;

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
    private static final String USER_ID_KEY = "USER_ID_KEY";
    private static final String USER_ADDRESS_KEY = "USER_ADDRESS_KEY";
    private static final String CART_PRODUCTID_LIST_KEY = "CART_PRODUCTID_LIST_KEY";


    String TAG1="Checkout";
    FirebaseFirestore dbRef = null;

    CheckBox checkBoxDelivery;
    TextView mDisplaySummary, mPaymentDetails, mTotalOrderAmount, mTotalOrderQty, mDeliveryFee, mTotalAmountCart, mDeliveryAddress;
    Button sendOrderButton, setDateButton, mAddCardButton, mChangeAddressBtn;
    Calendar mDateAndTime = Calendar.getInstance();
    DocumentReference docRef;
    String totalAmount, totalQty, userID, userName, userAddress, userPhone;
    ImageView checkedIcon;
    EditText mChangeAddress;
    SharedPreferences totalPrefs, userPrefs, productPrefs;
    int totalOrderAmount, cartTotalAmount, cartTotalQty;
    private int delivery_fee = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the content view  to activity check our xml
        setContentView(R.layout.activity_review_order);

        userPrefs = getSharedPreferences(USER_DATA_KEY, Context.MODE_PRIVATE);
        productPrefs = getSharedPreferences(CART_PRODUCTID_LIST_KEY, Context.MODE_PRIVATE);
        totalPrefs = getSharedPreferences(CART_KEY, MODE_PRIVATE);


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
        mChangeAddressBtn = findViewById(R.id.changeDeliveryDetails);
        mChangeAddress = findViewById(R.id.changeAddressEdit);

        userID = userPrefs.getString(USER_ID_KEY,"empty");
        userName = userPrefs.getString(USER_NAME_KEY,"user");
        userPhone = userPrefs.getString(USER_PHONE_KEY,"phone");
        userAddress = userPrefs.getString(USER_ADDRESS_KEY,"address");
        cartTotalQty = totalPrefs.getInt("cart_qty",0);
        cartTotalAmount = totalPrefs.getInt("cart_amount",0);
        totalOrderAmount = cartTotalAmount + delivery_fee;
        totalAmount = String.valueOf(cartTotalAmount);
        totalQty = String.valueOf(cartTotalQty);

        mDeliveryFee.setText(String.valueOf(delivery_fee) + "$  (delivery)");
        mTotalAmountCart.setText(totalAmount + "$");
        mTotalOrderQty.setText(totalQty + " Items");
        mTotalOrderAmount.setText(String.valueOf(totalOrderAmount) + "$");
        //activity for result
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
                                    sendOrderButton.setEnabled(true);
                                    sendOrderButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) { clickOnSendOrder(isCardAdded);}});
                                }
                            }
                        }
                    }
                });

        //add listener to add card btn
        mAddCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getApplicationContext(), CheckoutActivityJava.class));
                Intent checkoutCardActivity = new Intent(getApplicationContext(), CartPayment.class);
                resultLauncher.launch(checkoutCardActivity);
            }
        });
        //change delivery address
        mChangeAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChangeAddress.setVisibility(View.VISIBLE);
                mDeliveryAddress.setVisibility(View.GONE);
                userAddress = mChangeAddress.getText().toString();
            }
        });

        //find the summary textview
        mDisplaySummary = findViewById(R.id.summaryTxt);

        //load user data to the ui
        loadUserData();


        //instantiate the firsetose database and get the data getData()
        dbRef = FirebaseFirestore.getInstance();
    }

    private void clickOnSendOrder(boolean isCardAdded) {
        mDateAndTime.add(Calendar.DAY_OF_MONTH,5);
        Date currentDate  = mDateAndTime.getTime();
        Date expectedDelivery = mDateAndTime.getTime();
        if (isCardAdded == true){
            Intent orderConfirmed = new Intent(getApplicationContext(),OrderConfirmed.class);
            //String orderSummary = getFinalOrderSummary();
            orderConfirmed.putExtra("order_date", String.valueOf(currentDate));
            orderConfirmed.putExtra("order_expected",String.valueOf(expectedDelivery));
            orderConfirmed.putExtra("customerID",userID);
            orderConfirmed.putExtra("customer_name", userName);
            orderConfirmed.putExtra("order_amount", String.valueOf(totalAmount));
            orderConfirmed.putExtra("order_qty", String.valueOf(totalQty));
            orderConfirmed.putExtra("customer_phone", userPhone);
            orderConfirmed.putExtra("customer_address", userAddress);

            addOrderDB();
            totalPrefs.edit().clear().apply();
            productPrefs.edit().clear().apply();
            Toast.makeText(this, "Order placed!", Toast.LENGTH_SHORT).show();
            startActivity(orderConfirmed);
        }else{
            Toast.makeText(this, "Payment method is empty!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserData() {
        mDisplaySummary.setText("You are nearly there " + userName + "! \n" +
                                "Please review or add delivery and payment method before placing your order. \n" +
                                "We will contact you though your: " + userPhone + " number to deliver your order \n");
        mDeliveryAddress.setText(userAddress);
        checkBoxDelivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(checkBoxDelivery.isChecked()){
                        delivery_fee = 3;
                        totalOrderAmount -=3;
                    }else{
                        delivery_fee = 0;
                        totalOrderAmount +=3;
                    }
                    mTotalOrderAmount.setText(totalOrderAmount + " $");
                    mDeliveryFee.setText(String.valueOf(delivery_fee) + "$  (delivery)");
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

    private void addOrderDB() {
        //create collection with required fields; currentdate,duedate,bookid and userid
        Map<String , Object>orderDates = new HashMap<>();
        Date currentDate  = mDateAndTime.getTime();
        mDateAndTime.add(Calendar.DAY_OF_MONTH,5);
        Date dueDate = mDateAndTime.getTime();
        orderDates.put("currentDate", currentDate);
        orderDates.put("dueDate", dueDate);
        orderDates.put("customerID",userID);
        orderDates.put("customerName",userName);
        docRef = dbRef.collection("orders").document();
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
                    Toast.makeText(ReviewOrder.this, "Successful date added!.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
