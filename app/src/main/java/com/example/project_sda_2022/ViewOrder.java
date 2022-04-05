package com.example.project_sda_2022;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.project_sda_2022.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * ViewOrder retrieve previous order made by the user
 *  * @author Rafael Izarra
 *  * @version 1.0
 *  * @since 02/04/2022
 */
public class ViewOrder extends AppCompatActivity {

    FirebaseFirestore fStore;
    Button mGoBackBtn;
    TextView mOrderAmount, mOrderQty, mCurrentOrderDae, mOrderOwner, mOrderExpected, mOrderID;
    String userID, orderID, totalAmount, totalQty, userName, userAddress, userPhone, currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);

        mOrderQty = findViewById(R.id.orderViewTotalQty);
        mOrderAmount = findViewById(R.id.orderViewTotalAmount);
        mOrderOwner = findViewById(R.id.orderViewUname);
        mCurrentOrderDae = findViewById(R.id.orderViewDate);
        mOrderExpected = findViewById(R.id.orderViewExpectDate);
        mOrderID = findViewById(R.id.orderViewOrderID);
        mGoBackBtn = findViewById(R.id.goBackBtn);

        userID = getIntent().getStringExtra("userID");
        fStore = FirebaseFirestore.getInstance();

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                orderID = documentSnapshot.getString("orderID");
                Log.d("orderID", orderID);
                DocumentReference documentReferenceOrder = fStore.collection("orders").document(orderID);
                documentReferenceOrder.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        mOrderOwner.setText("Name: " + documentSnapshot.getString("customerName"));
                        mCurrentOrderDae.setText("Order Placed: " + String.valueOf(
                                documentSnapshot.getTimestamp("currentDate")));
                        mOrderAmount.setText("Amount: " +String.valueOf(
                                documentSnapshot.getLong("orderAmount")));
                        mOrderQty.setText("Products Num: " + String.valueOf(
                                documentSnapshot.getString("orderQty")));
                        mOrderExpected.setText("Arriving by: " +String.valueOf(
                                documentSnapshot.getTimestamp("dueDate")));
                        mOrderID.setText("Order ID: " + orderID);
                    }
                });
            }
        });
        //return to main page
        mGoBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

    }
}