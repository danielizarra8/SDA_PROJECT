package com.example.sdaassign4_2021;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CartActivity extends Fragment {

    private static final String CART_PRODUCTID_LIST_KEY = "CART_PRODUCTID_LIST_KEY";
    private static final String PRODUCTID_LIST_KET = "PRODUCTID_LIST_KEY";
    private ArrayList<Cart> mItem = new ArrayList<>();
    private final static String CART_KEY = "CART_KEY";

    RecyclerView recyclerView;
    CartViewAdapter recyclerViewAdapter;
    Button mCheckoutBtn, mEmptyBtn;
    TextView totalAmountTxt;
    private StorageReference storageReference;
    private FirebaseFirestore dbRef;
    private String productID;
    //private ArrayList<Cart> cartArrayList;

    private SharedPreferences cartPrefs, totalPrefs;

    public CartActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerView = root.findViewById(R.id.cartListRecView);
        recyclerView.setHasFixedSize(true);
        recyclerViewAdapter = new CartViewAdapter(getContext(), mItem);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        totalAmountTxt = root.findViewById(R.id.cart_totalAmount);
        mCheckoutBtn = root.findViewById(R.id.cart_checkout_btn);
        mEmptyBtn = root.findViewById(R.id.cart_empty_btn);

        dbRef = FirebaseFirestore.getInstance();
        //loadData();
        getCartData();

        mCheckoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reviewOrder = new Intent(getActivity().getApplicationContext(), ReviewOrder.class);
                //get the title and send it to the checkout activity
                reviewOrder.putExtra("title", "dummy text for title");
                reviewOrder.putExtra("bookID", "dummy text for bookID");
                reviewOrder.putExtra("userName","dummy text for user name");
                reviewOrder.putExtra("userID", "dummy text for user id");
                startActivity(reviewOrder);
            }
        });

        return root;
    }

    public void getCartData() {
        SharedPreferences prefs = getActivity().getSharedPreferences(CART_PRODUCTID_LIST_KEY,Context.MODE_PRIVATE);
        Map<String, String> prefMap = (Map<String, String>) prefs.getAll();

        for (Map.Entry<String,String> entry: prefMap.entrySet()){
            Log.i("SharePreferencesMap", "loaded Key: " + entry.getKey() + " Value: " + entry.getValue() );
            //mItem = entry.getValue();
            //loadData(entry.getValue());
            mItem.add(new Cart(entry.getKey(),entry.getValue(),69,96));
        }
        /*
        // get the storage reference from firebase where images are stored
        storageReference = FirebaseStorage.getInstance().getReference();
        //point to the right collection in the database
        dbRef.collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    //loop through each ducument retrieved
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String productName = (String) (document.getString("name"));
                        productID = document.getId();
                        loadData(productID);
                        //cartArrayList.add(new Cart(productID,productName,96,69));
                        searchProductCart(productID);

                        //saveData(productID);
                    } // end of loop

                } // end of if statement
            }


        });

         */
    }
    private void saveData(String productID) {
        SharedPreferences prefs = getActivity().getSharedPreferences(CART_PRODUCTID_LIST_KEY,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        Gson gson   = new Gson();
        String json = gson.toJson(mItem);
        edit.putString(PRODUCTID_LIST_KET,json);
        edit.apply();
        Toast.makeText(getContext(),"products saved in cart! ", Toast.LENGTH_SHORT).show();
    }

    //load data (product id) from all products in the db and store those ids in shareprefs (gson).
    private void loadData(String productID) {
        //SharedPreferences prefs = getActivity().getSharedPreferences(CART_PRODUCTID_LIST_KEY,Context.MODE_PRIVATE);
        // creating a variable for gson.
        Gson gson = new Gson();
        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        // *** String json = prefs.getString(productID,null);
        String json = productID;
        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<Cart>>() {}.getType();
        mItem = gson.fromJson(json,type);
        if(mItem == null){
            mItem = new ArrayList<>();
        }
        Toast.makeText(getContext(),"List: " + mItem.size(),Toast.LENGTH_SHORT).show();
    }

    private void searchProductCart(String productID) {
        cartPrefs = getActivity().getSharedPreferences(productID, Context.MODE_PRIVATE);
        totalPrefs = getActivity().getSharedPreferences(CART_KEY,Context.MODE_PRIVATE);
        int qty = cartPrefs.getInt("product_qty",0);
        if (productID.equals(cartPrefs.getString("product_id",""))
                && (qty > 0)) {
            String productName = cartPrefs.getString("product_name","");
            //int price = totalPrefs.getInt("cart_amount",0);
            // int cartQty = totalPrefs.getInt("cart_qty",0);
            int price = cartPrefs.getInt("product_amount",0);
            int cartQty = cartPrefs.getInt("product_qty",0);
            mItem.add(new Cart(productID,productName,price,cartQty));
            // update the recyclerview adapter to reflect the changes, otherwise it won't display the data
            recyclerViewAdapter.notifyDataSetChanged();
        }
        //Empty cart
        mEmptyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartPrefs.edit().clear().apply();
                totalPrefs.edit().clear().apply();
                startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}