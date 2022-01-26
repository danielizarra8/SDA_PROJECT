package com.example.sdaassign4_2021;


import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


/**
 * Images used are sourced from Public Domain Day 2019.
 * by Duke Law School's Center for the Study of the Public Domain
 * is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * A simple {@link Fragment} subclass.
 * @author Chris Coughlan
 */
public class BookList extends Fragment {
    private ArrayList<Book> mBook = new ArrayList<>();
    private ArrayList<String>mImagesUrls = new ArrayList<>();
    private static final String TAG1 = "retrieveImg";
    private static final String TAG2 = "retrievebooks";
    private static final String TAG3 = "retrieve";
    StorageReference imageRef;
    String u;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    LibraryViewAdapter recyclerViewAdapter;
    String author, title, id, url, url1;

    FirebaseFirestore dbRef;

    public BookList() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_book_list, container, false);
        dbRef = FirebaseFirestore.getInstance();
        getBookData();


        RecyclerView recyclerView = root.findViewById(R.id.bookView_view);
        recyclerViewAdapter = new LibraryViewAdapter(getContext(), mBook);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        return root;
    }

    public void getBookData() {
        storageReference = FirebaseStorage.getInstance().getReference();

        dbRef.collection("books").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        url = document.getString("url");
                        imageRef = storageReference.child("/images/"+url);
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                url1 = uri.toString();
                                title = (String) (document.getString("Title"));
                                author = (String) (document.getString("Author"));
                                id = document.getId();
                                mBook.add(new Book(author,title,id,url1));
                                recyclerViewAdapter.notifyDataSetChanged();
                            }
                        });

                    }

                }
            }
        });

    }
    public String getImageUri(String url){

        imageRef = storageReference.child("/images/"+url);
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                u = uri.toString();
                Log.i(TAG2,"URL SUCESSFULL" + uri.toString() );
                Toast.makeText(getContext(), "URL SUCESSFULL!" + uri.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return u;
    }


}