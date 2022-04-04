package com.example.sdaassign4_2021;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;


public class CartViewAdapter extends RecyclerView.Adapter<CartViewAdapter.ViewHolder> {
    private static final String TAG = "ViewHolder";
    public Context mNewContext;
    private ArrayList<Cart> mItem;
    private ArrayList<String>mProductName, mProductID;

    CartViewAdapter(Context mNewContext, ArrayList<String> mProductName, ArrayList<String>mProductsID) {
        this.mNewContext = mNewContext;
        this.mProductName = mProductName;
        this.mProductID = mProductsID;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_list_layout, viewGroup, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        Log.d(TAG, "onBindViewHolder CART: was called" );
        viewHolder.mProductNameTxt.setText(mProductName.get(position));
        viewHolder.mProductIDTxt.setText("ID: " +mProductID.get(position));

    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mProductName.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mProductNameTxt, mProductIDTxt;
        RelativeLayout itemCartParentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mProductNameTxt = itemView.findViewById(R.id.productNameCart);
            mProductIDTxt = itemView.findViewById(R.id.productIdCart);
            itemCartParentLayout = itemView.findViewById(R.id.cartListItem);
        }
    }
}
