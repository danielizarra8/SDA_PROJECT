package com.example.sdaassign4_2021;

        /*
         * Copyright (C) 2016 The Android Open Source Project
         *
         * Licensed under the Apache License, Version 2.0 (the "License");
         * you may not use this file except in compliance with the License.
         * You may obtain a copy of the License at
         *
         *      http://www.apache.org/licenses/LICENSE-2.0
         *
         * Unless required by applicable law or agreed to in writing, software
         * distributed under the License is distributed on an "AS IS" BASIS,
         * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
         * See the License for the specific language governing permissions and
         * limitations under the License.
         */

        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.graphics.Color;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.RelativeLayout;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

        import com.bumptech.glide.Glide;

        import java.util.ArrayList;

/**
 * @author Chris Coughlan 2019
 * @author Edited by Rafael Izarra 2022
 */
public class ProductViewAdapter extends RecyclerView.Adapter<ProductViewAdapter.ViewHolder> {
    private static final String USER_NAME_KEY = "USER_NAME_KEY";
    private static final String USER_DATA_KEY = "USER_DATA_KEY";
    private static final String TAG = "RecyclerViewAdapter";
    private Context mNewContext;
    private ArrayList<Product> mProduct;


    ProductViewAdapter(Context mNewContext, ArrayList<Product> mProduct) {
        this.mNewContext = mNewContext;
        this.mProduct = mProduct;
    }

    //declare methods
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_list_item, viewGroup, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        Log.d(TAG, "onBindViewHolder: was called" + mProduct.get(position).getProductURL());

        viewHolder.mProductNameText.setText(mProduct.get(position).getProductName());
        viewHolder.mPriceText.setText(String.valueOf(mProduct.get(position).getProductPrice() + " $"));

        /**  the following if statement checks the availability of the book and if the book is available(true)
         * the text will be set "Available" and color "green"
         *Otherwise, show "Out of stock" and "reed"
         */

        if (mProduct.get(position).getProductQuantity() > 0) {
            viewHolder.availabilityText.setText("Available");
            viewHolder.availabilityText.setTextColor(Color.parseColor("#2c8300"));
        }else{
            viewHolder.availabilityText.setText("Out of stock");

            viewHolder.availabilityText.setTextColor(Color.parseColor("#e31300"));
        }

        Glide.with(viewHolder.imageItem.getContext()).load(mProduct.get(position).getProductURL()).into(viewHolder.imageItem);

        //should check here to see if the book is available.

            viewHolder.checkOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickOnProductView(v,position);
                }
            });
    }

    @Override
    public int getItemCount() {
        return mProduct.size();
    }

    //view holder class for recycler_list_item.xml
    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageItem;
        TextView mProductNameText;
        TextView mPriceText;
        TextView availabilityText;
        Button checkOut;
        RelativeLayout itemParentLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            //grab the image, the text and the layout id's
            imageItem = itemView.findViewById(R.id.productImage);
            mProductNameText = itemView.findViewById(R.id.productNameTxt);
            mPriceText = itemView.findViewById(R.id.productPriceTxt);
            availabilityText = itemView.findViewById(R.id.availabilityTextView);
            checkOut = itemView.findViewById(R.id.out_button);
            itemParentLayout = itemView.findViewById(R.id.listItemLayout);

        }
    }
    private void clickOnProductView(View v, int position) {
        final SharedPreferences prefs = mNewContext.getSharedPreferences(USER_DATA_KEY, Context.MODE_PRIVATE);

        String texUserName = prefs.getString(USER_NAME_KEY, "");
        //String userID = prefs.getString(USER_ID_KEY, "");
        if (texUserName.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mNewContext);
            builder.setTitle("Notification!").setMessage("Customer Name not set.").setPositiveButton("OK", null).show();
        } else {
            //...
            Intent displaySingleProduct = new Intent(mNewContext, ProductDisplay.class);
            //get the title and send it to the checkout activity
            displaySingleProduct.putExtra("imageURL", mProduct.get(position).getProductURL());
            displaySingleProduct.putExtra("description", mProduct.get(position).getProductDescription());
            displaySingleProduct.putExtra("productName", mProduct.get(position).getProductName());
            displaySingleProduct.putExtra("productID", mProduct.get(position).getProductID());
            displaySingleProduct.putExtra("productPrice", String.valueOf(mProduct.get(position).getProductPrice()));
            displaySingleProduct.putExtra("productQty", String.valueOf(mProduct.get(position).getProductQuantity()));
            displaySingleProduct.putExtra("userName",texUserName);
            //displaySingleProduct.putExtra("userID",userID);
            mNewContext.startActivity(displaySingleProduct);
        }
    }
}
