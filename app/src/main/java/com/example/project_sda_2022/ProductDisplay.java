package com.example.project_sda_2022;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

/**
 * ProductDisplay class takes the user selection (product) and display it individually
 *  * @author Rafael Izarra
 *  * @version 1.0
 *  * @since 30/03/2022
 */

public class ProductDisplay extends AppCompatActivity {
    private final static String CART_KEY = "CART_KEY";
    private static final String CART_PRODUCTID_LIST_KEY = "CART_PRODUCTID_LIST_KEY";
    private static final String USER_LOGGED_IN = "USER_LOGGED_IN";
    private static final String USER_DATA_KEY = "USER_DATA_KEY";
    private static final String USER_ID_KEY = "USER_ID_KEY";

    Button mCheckOutBtn, mAddCartBtn;
    ElegantNumberButton numberButton;
    ImageView mImageItem, mEmptyCart;
    TextView mDescriptionTxt, mNameTxt, mPriceTxt, mQtyText, mAvailability, mCartQtyTxt, mTotalAmountTxt, mReviews;
    String productID,productName, imageURL, productQty, userID;
    int productPrice;
    SharedPreferences totalPrefs, userPrefs, prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_display);

        mCheckOutBtn = findViewById(R.id.checkoutBtn); mImageItem = findViewById(R.id.productImage);
        mAddCartBtn = findViewById(R.id.addProductBtn); mDescriptionTxt = findViewById(R.id.descriptionTxt);
        mQtyText = findViewById(R.id.qtyTxt); mNameTxt = findViewById(R.id.productNameTxt);
        mPriceTxt = findViewById(R.id.priceTxt); mAvailability = findViewById(R.id.availabilityTxt);
        numberButton = findViewById(R.id.addQtyBtn); mTotalAmountTxt = findViewById(R.id.totalMoneyTxt);
        mCartQtyTxt = findViewById(R.id.cartQty); mEmptyCart = findViewById(R.id.deleteIcon);
        mReviews = findViewById(R.id.openChat);

        //sharepreferences
        totalPrefs = getSharedPreferences(CART_KEY, MODE_PRIVATE);
        userPrefs = getSharedPreferences(USER_DATA_KEY, Context.MODE_PRIVATE);
        prefs = getSharedPreferences(CART_PRODUCTID_LIST_KEY, Context.MODE_PRIVATE); //a list to put all the ids of products selected
        //get intent data
        productName = getIntent().getStringExtra("productName");
        productID = getIntent().getStringExtra("productID");
        imageURL = getIntent().getStringExtra("imageURL");
        productPrice = Integer.valueOf(getIntent().getStringExtra("productPrice"));
        productQty = getIntent().getStringExtra("productQty");
        userID = userPrefs.getString(USER_ID_KEY,"");

        int cartTotalQty = totalPrefs.getInt("cart_qty",0);
        int cartTotalAmount = totalPrefs.getInt("cart_amount",0);

        mCartQtyTxt.setText(String.valueOf(cartTotalQty));
        mTotalAmountTxt.setText(String.valueOf(cartTotalAmount));

        //populate field values
        populateFields(productName, productPrice, productQty);

        Glide.with(mImageItem.getContext()).load(imageURL).into(mImageItem);

        //start checkout activity
        mCheckOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean response = checkUserStatus();
                boolean cartResponse = checkCartStatus();
                if (response == true && cartResponse == true){
                    Intent checkOutIntent = new Intent(getApplicationContext(), ReviewOrder.class);
                    //get the title and send it to the checkout activity
                    checkOutIntent.putExtra("userName","dummy text for user name");
                    checkOutIntent.putExtra("userID", "dummy text for user id");
                    checkOutIntent.putExtra("bookID", "dummy text for bookID");
                    startActivity(checkOutIntent);
                }
            }
        });
        //add products to the cart
        mAddCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean response = checkUserStatus();
                if (response == true) {
                    saveData(productID, productName, prefs);
                    addTotalCart(cartTotalQty, productPrice, cartTotalAmount, userID);
                }
            }
        });
        //open review activity
        mReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reviewActivity = new Intent(getApplicationContext(), TopicActivity.class);
                reviewActivity.putExtra("productID",productID);
                reviewActivity.putExtra("productName", productName);
                reviewActivity.putExtra("imageURL",imageURL);
                startActivity(reviewActivity);
            }
        });

        //Empty cart
        mEmptyCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalPrefs.edit().clear().apply();
                prefs.edit().clear().apply();
                Toast.makeText(getApplicationContext(), "Cart emptied!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
    //check cart is empty
    private boolean checkCartStatus() {
        int isCartEmpty = totalPrefs.getInt("cart_qty",0);
        if (isCartEmpty != 0){
            return  true;
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Notification!")
                    .setMessage("You need to add a product to the cart first.")
                    .setPositiveButton("OK", null)
                    .show();
            return false;
        }
    }
    //check if user is signed in
    private boolean checkUserStatus() {
        boolean isUserLogged = userPrefs.getBoolean(USER_LOGGED_IN, false);
        if (isUserLogged == true){
            return  true;
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Notification!")
                    .setMessage("You need to sign in first.")
                    .setPositiveButton("OK", null)
                    .show();
            return false;
        }
    }
    // populate rest of fields in the ui
    private void populateFields(String productName, int productPrice, String qty) {
        String description = getIntent().getStringExtra("description");
        mNameTxt.setText(productName);
        mPriceTxt.setText(productPrice + " $");
        mQtyText.setText(qty + " qty");
        mDescriptionTxt.setText(description);

        int quantity = Integer.valueOf(qty);
        //display availability
        if ( quantity > 0) {
            mAvailability.setText("Available");
            mAvailability.setTextColor(Color.parseColor("#2c8300"));
            numberButton.setRange(1,quantity);

        }else{
            mAvailability.setText("Out of stock");
            mAvailability.setTextSize(20);
            mAvailability.setTextColor(Color.parseColor("#e31300"));
            numberButton.setRange(0,0);
            mAddCartBtn.setEnabled(false);
            mAddCartBtn.setVisibility(View.GONE);
        }
    }

    /**
     * Gets the previous amount and add it to the new product added by the user
     * @param qty quantity of the product
     * @param price price of the procut
     * @param totalCartAmount the sumup of all products amount
     * @param userID id of the user
     * add total amount and total quantity of items
     * */
    private void addTotalCart(int qty, int price, int totalCartAmount, String userID) {
        int cartQty = qty + Integer.valueOf(numberButton.getNumber());
        int cartTotal = (price * Integer.valueOf(numberButton.getNumber())) + totalCartAmount;
        SharedPreferences.Editor edit = totalPrefs.edit();
        edit.putString("cart_products"," product");
        edit.putInt("cart_amount",cartTotal);
        edit.putInt("cart_qty",cartQty);
        edit.commit();

        Toast.makeText(this, "Product added!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

    }
    //save product details in preferences
    private void saveData(String productID, String productName, SharedPreferences prefs) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(productID,productName);
        edit.apply();
        Toast.makeText(this,"products saved in cart! ", Toast.LENGTH_SHORT).show();
    }
}