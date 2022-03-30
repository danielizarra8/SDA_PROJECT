package com.example.sdaassign4_2021;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

public class ProductDisplay extends AppCompatActivity {
    private final static String CART_KEY = "CART_KEY";

    Button mCheckOutBtn, mAddCartBtn;
    ElegantNumberButton numberButton;
    ImageView mImageItem;
    TextView mDescriptionTxt, mNameTxt, mPriceTxt, mQtyText, mAvailability, mCartQtyTxt, mTotalAmountTxt;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_display);
        prefs = getSharedPreferences(CART_KEY, MODE_PRIVATE);

        int cartQty = prefs.getInt("cart_qty", 0);
        int cartTotalPrice = prefs.getInt("cart_amount",0);

        mCheckOutBtn = findViewById(R.id.checkoutBtn);
        mImageItem = findViewById(R.id.productImage);
        mAddCartBtn = findViewById(R.id.addProductBtn);
        mDescriptionTxt = findViewById(R.id.descriptionTxt);
        mQtyText = findViewById(R.id.qtyTxt);
        mNameTxt = findViewById(R.id.productNameTxt);
        mPriceTxt = findViewById(R.id.priceTxt);
        mAvailability = findViewById(R.id.availabilityTxt);
        numberButton = findViewById(R.id.addQtyBtn);
        mTotalAmountTxt = findViewById(R.id.totalMoneyTxt);
        mCartQtyTxt = findViewById(R.id.cartQty);

        mCartQtyTxt.setText(String.valueOf(cartQty));
        mTotalAmountTxt.setText(String.valueOf(cartTotalPrice));

        String imageURL = getIntent().getStringExtra("imageURL");

        //pass values from intent
        String productName = getIntent().getStringExtra("productName");
        String price = getIntent().getStringExtra("productPrice");
        String qty = getIntent().getStringExtra("productQty");
        String description = getIntent().getStringExtra("description");

        //populate field values
        mNameTxt.setText(productName);
        mPriceTxt.setText(price + " $");
        mQtyText.setText(qty);
        mDescriptionTxt.setText(description);
        if (Integer.valueOf(price) > 0) {
            mAvailability.setText("Available");
            mAvailability.setTextColor(Color.parseColor("#2c8300"));
        }else{
            mAvailability.setText("Out of stock");
            mAvailability
                    .setTextColor(Color.parseColor("#e31300"));
        }
        Glide.with(mImageItem.getContext()).load(imageURL).into(mImageItem);

        //set chekout btn
        mCheckOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent checkOutIntent = new Intent(getApplicationContext(), CheckOut.class);
                //get the title and send it to the checkout activity
                checkOutIntent.putExtra("title", "dummy text for title");
                checkOutIntent.putExtra("bookID", "dummy text for bookID");
                checkOutIntent.putExtra("userName","dummy text for user name");
                checkOutIntent.putExtra("userID", "dummy text for user id");
                startActivity(checkOutIntent);
            }
        });
        //set addToCart btn
        mAddCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart(cartQty, Integer.valueOf(price), cartTotalPrice);
            }
        });
    }

    private void addToCart(int qty, int price, int totalCartAmount) {
        int cartQty = qty + Integer.valueOf(numberButton.getNumber());
        int cartTotal = (price * Integer.valueOf(numberButton.getNumber())) + totalCartAmount;
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("cart_products"," product");
        edit.putInt("cart_amount",cartTotal);
        edit.putInt("cart_qty",cartQty);
        edit.apply();
        //prefs.edit().clear().apply();
        Toast.makeText(this, "Produc added! " + cartQty + ", " + Integer.valueOf(numberButton.getNumber()), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}