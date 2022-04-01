package com.example.sdaassign4_2021;

import androidx.appcompat.app.AppCompatActivity;

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

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ProductDisplay extends AppCompatActivity {
    private final static String CART_KEY = "CART_KEY";
    private static final String CART_PRODUCTID_LIST_KEY = "CART_PRODUCTID_LIST_KEY";
    private static final String PRODUCTID_LIST_KEY = "PRODUCTID_LIST_KEY";
    private ArrayList<Cart> cartArrayList;

    Button mCheckOutBtn, mAddCartBtn;
    ElegantNumberButton numberButton;
    ImageView mImageItem, mEmptyCart;
    TextView mDescriptionTxt, mNameTxt, mPriceTxt, mQtyText, mAvailability, mCartQtyTxt, mTotalAmountTxt;
    SharedPreferences totalPrefs, cartPrefs;
    ArrayList<String> productListID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_display);
        cartArrayList = new ArrayList<>();
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
        mEmptyCart = findViewById(R.id.deleteIcon);

        //a list to put all the ids of products selected
        productListID = new ArrayList<>();

        String productName = getIntent().getStringExtra("productName");
        String productID = getIntent().getStringExtra("productID");
        String imageURL = getIntent().getStringExtra("imageURL");

        int productPrice = Integer.valueOf(getIntent().getStringExtra("productPrice"));
        cartPrefs = this.getSharedPreferences(productID, MODE_PRIVATE);
        int cartSinQty = cartPrefs.getInt("product_qty", 0);
        int cartSinAmount = cartPrefs.getInt("product_amount",0);

        totalPrefs = getSharedPreferences(CART_KEY, MODE_PRIVATE);
        int cartTotalQty = totalPrefs.getInt("cart_qty",0);
        int cartTotalAmount = totalPrefs.getInt("cart_amount",0);

        mCartQtyTxt.setText(String.valueOf(cartTotalQty));
        mTotalAmountTxt.setText(String.valueOf(cartTotalAmount));

        //populate field values
        populateFields(productName, productPrice);

        Glide.with(mImageItem.getContext()).load(imageURL).into(mImageItem);

        //start checkout activity
        mCheckOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent checkOutIntent = new Intent(getApplicationContext(), ReviewOrder.class);
                //get the title and send it to the checkout activity
                checkOutIntent.putExtra("userName","dummy text for user name");
                checkOutIntent.putExtra("userID", "dummy text for user id");
                checkOutIntent.putExtra("bookID", "dummy text for bookID");
                startActivity(checkOutIntent);
            }
        });
        //add products to the cart
        mAddCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cartArrayList.add(new Cart(productID,productName,196,169));
                saveData(productID, productName);
                addTotalCart(cartTotalQty, productPrice, cartTotalAmount);
                //saveProductID(productName, productID, cartSinQty, price, cartSinAmount);
            }
        });

        //Empty cart
        mEmptyCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartPrefs.edit().clear().apply();
                totalPrefs.edit().clear().apply();
                productListID.clear();
                Toast.makeText(getApplicationContext(), "Cart emptied!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    private void populateFields(String productName, int productPrice) {
        String qty = getIntent().getStringExtra("productQty");
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

    //Save each product individually in sharepreferences

    // add total amount and total quantity of items
    private void addTotalCart(int qty, int price, int totalCartAmount) {
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
    private void saveData(String productID, String productName) {
        SharedPreferences prefs = getSharedPreferences(CART_PRODUCTID_LIST_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        /*
        Gson gson   = new Gson();
        String json = gson.toJson(cartArrayList);
         */
        edit.putString(productID,productName);
        edit.apply();
        Toast.makeText(this,"products saved in cart! ", Toast.LENGTH_SHORT).show();
    }
}