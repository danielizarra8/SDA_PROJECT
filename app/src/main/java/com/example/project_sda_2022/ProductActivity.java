package com.example.project_sda_2022;


import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


/**
 * ProductActivity retrieve the product data and display it to the ui
 * A simple {@link Fragment} subclass.
 * @author Rafael Izarra 2022
 */
public class ProductActivity extends Fragment {
    //declare variables, database and widgets
    private ArrayList<Product> mProduct = new ArrayList<>();

    StorageReference imageRef;
    StorageReference storageReference;
    ProductViewAdapter recyclerViewAdapter;

    int productPrice, productQty;
    String productDescription, productName;
    String id, url, url1;

    FirebaseFirestore fStore;

    public ProductActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_product_list, container, false);

        //instantiate the database and call the getBookData() method to retireve book's data
        fStore = FirebaseFirestore.getInstance();
        getProductData();

        RecyclerView recyclerView = root.findViewById(R.id.productView_view);
        recyclerViewAdapter = new ProductViewAdapter(getContext(), mProduct);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return root;
    }

    /**
     * Access the firabeseStorage
     * gets a referencer for the product collection
     * and pass the data to the recyclerview
     */

    public void getProductData() {
        // get the storage reference from firebase where images are stored
        storageReference = FirebaseStorage.getInstance().getReference();

        //point to product collection in the database
        fStore.collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    //loop through each ducument(product) retrieved
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        url = document.getString("url");
                        // set the right path
                        imageRef = storageReference.child("/images/"+url);
                        // download the image url
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // pass the values and create a new Product object
                                url1 = uri.toString();
                                productPrice =  document.getLong("price").intValue();
                                productName = (String) (document.getString("name"));
                                productDescription = (String) (document.getString("description"));
                                productQty = document.getLong("quantity").intValue();
                                id = document.getId();
                                mProduct.add(new Product(productName, productPrice, productDescription, id, url1, productQty));

                                // update the recyclerview adapter to reflect the changes, otherwise it won't display the data
                                recyclerViewAdapter.notifyDataSetChanged();
                            }
                        });

                    } // end of loop

                } // end of if statement
            }
        });

    }

}