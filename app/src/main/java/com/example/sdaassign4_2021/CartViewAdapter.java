package com.example.sdaassign4_2021;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;


public class CartViewAdapter extends RecyclerView.Adapter<CartViewAdapter.ViewHolder> {
    private static final String TAG = "ViewHolder Cart";
    private Context mNewContext;
    private ArrayList<Cart> mItem;

    CartViewAdapter(Context mNewContext, ArrayList<Cart> mCart) {
        this.mNewContext = mNewContext;
        this.mItem = mCart;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_list_layout, viewGroup, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Log.d(TAG, "onBindViewHolder car: was called" );

        viewHolder.mProductNameTxt.setText(mItem.get(position).getProductName());
        viewHolder.mProductPriceTxt.setText("Amount: " + String.valueOf(mItem.get(position).getPrice() + " $"));
        viewHolder.mProductQtyTxt.setText("Qty: " + String.valueOf(mItem.get(position).getQuantity()));
        //viewHolder.mTotalAmountCart.setText(" ");
    }


    @Override
    public int getItemCount() {
        return mItem.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mProductNameTxt, mProductPriceTxt, mProductQtyTxt, mTotalAmountCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mProductNameTxt = itemView.findViewById(R.id.productNameCart);
            mProductPriceTxt = itemView.findViewById(R.id.productAmountCart);
            mProductQtyTxt = itemView.findViewById(R.id.productQtyCart);
            mTotalAmountCart = itemView.findViewById(R.id.cart_totalAmount);
        }
    }
}
