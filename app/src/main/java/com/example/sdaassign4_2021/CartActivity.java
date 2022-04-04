package com.example.sdaassign4_2021;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CartActivity extends Fragment {

    private static final String CART_PRODUCTID_LIST_KEY = "CART_PRODUCTID_LIST_KEY";
    private final static String CART_KEY = "CART_KEY";
    private static final String USER_DATA_KEY = "USER_DATA_KEY";
    private static final String USER_LOGGED_IN = "USER_LOGGED_IN";

    private ArrayList<Cart> mItem = new ArrayList<>();
    RecyclerView recyclerView;
    public TextView mProductNameTxt, mTotalAmountCart;
    CartViewAdapter recyclerViewAdapter;
    Button mCheckoutBtn, mEmptyBtn;
    TextView mTotalCartAmountTxt, mTotalCartQtyTxt, totalAmountTxt;
    private ArrayList<String> mProductList = new ArrayList<>();
    private ArrayList<String> mProductsID = new ArrayList<String>();
    SharedPreferences totalPrefs, prefs, userPrefs;

    public CartActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_cart, container, false);


        //share preferences
        totalPrefs = getActivity().getSharedPreferences(CART_KEY, Context.MODE_PRIVATE);
        prefs = getActivity().getSharedPreferences(CART_PRODUCTID_LIST_KEY, Context.MODE_PRIVATE);
        userPrefs = getActivity().getSharedPreferences(USER_DATA_KEY, Context.MODE_PRIVATE);

        //declare and initialise recylerview
        recyclerView = root.findViewById(R.id.cartListRecView);
        recyclerViewAdapter = new CartViewAdapter(getContext(), mProductList,mProductsID);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //initialise ui var
        mProductNameTxt = root.findViewById(R.id.productNameCart);
        mTotalAmountCart = root.findViewById(R.id.cart_totalAmount);
        totalAmountTxt = root.findViewById(R.id.cart_totalAmount);
        mCheckoutBtn = root.findViewById(R.id.cart_checkout_btn);
        mEmptyBtn = root.findViewById(R.id.cart_empty_btn);
        mTotalCartQtyTxt = root.findViewById(R.id.cart_totalQty);
        mTotalCartAmountTxt = root.findViewById(R.id.cart_totalAmount);
        //get data from sharePrefs and load it the recycleview (cart)
        getCartData();

        // handle the checkout btn
        mCheckoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean response = checkUserStatus();
                boolean cartResponse = checkCartStatus();
                if (response == true && cartResponse == true) {
                    Intent reviewOrder = new Intent(getActivity().getApplicationContext(), ReviewOrder.class);
                    //get the title and send it to the checkout activity
                    startActivity(reviewOrder);
                }
            }
        });
        mEmptyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalPrefs.edit().clear().apply();
                prefs.edit().clear().apply();
                Toast.makeText(getContext(), "Cart emptied!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });

        return root;
    }

    private boolean checkUserStatus() {
        boolean isUserLogged = userPrefs.getBoolean(USER_LOGGED_IN, false);
        if (isUserLogged == true){
            return  true;
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Notification!")
                    .setMessage("You need to sign in first.")
                    .setPositiveButton("OK", null)
                    .show();
            return false;
        }
    }

    public void getCartData() {
        int cartTotalQty = totalPrefs.getInt("cart_qty",0);
        int cartTotalAmount = totalPrefs.getInt("cart_amount",0);

        Map<String, String> prefMap = (Map<String, String>) prefs.getAll();

        for (Map.Entry<String,String> entry: prefMap.entrySet()){
            Log.i("SharePreferencesMap", "loaded Key: " + entry.getKey() + " Value: " + entry.getValue() );
            mProductsID.add(entry.getKey());
            mProductList.add(entry.getValue());

        }
        mTotalCartAmountTxt.setText("Total  = " + cartTotalAmount + "$");
        mTotalCartQtyTxt.setText("Products = " + cartTotalQty);
    }

    private boolean checkCartStatus() {
        int isCartEmpty = totalPrefs.getInt("cart_qty",0);
        if (isCartEmpty != 0){
            return  true;
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Notification!")
                    .setMessage("You need to add a product to the cart first.")
                    .setPositiveButton("OK", null)
                    .show();
            return false;
        }
    }

}