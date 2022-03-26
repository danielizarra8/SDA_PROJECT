package com.example.sdaassign4_2021;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ProductDisplay extends AppCompatActivity {
    Button mCheckOutBtn;
    ImageView mImageItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_display);

        mCheckOutBtn = findViewById(R.id.checkoutBtn);
        mImageItem = findViewById(R.id.productImage);
        String imageURL = getIntent().getStringExtra("imageURL");

        Glide.with(mImageItem.getContext()).load(imageURL).into(mImageItem);


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
    }
}