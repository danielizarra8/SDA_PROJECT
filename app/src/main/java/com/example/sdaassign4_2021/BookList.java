package com.example.sdaassign4_2021;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Images used are sourced from Public Domain Day 2019.
 * by Duke Law School's Center for the Study of the Public Domain
 * is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * A simple {@link Fragment} subclass.
 * @author Chris Coughlan
 */
public class BookList extends Fragment {
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;
    private ArrayList<Book> mBook = new ArrayList<>();
    public ArrayList<Bitmap>mImagesUrls = new ArrayList<>();
    private StorageReference storageReference;
    private static final String TAG1 = "retreiveImg";

    public BookList() {
        // Required empty public constructor
    }

    private void initImageBitmaps(){
        mStorageReference = FirebaseStorage.getInstance().getReference().child("images/sku100010.jpg");

        try {
            final File localFile = File.createTempFile("sku100010","jpg");
            mStorageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getActivity(),"Image retrieved",Toast.LENGTH_SHORT).show();
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    ((ImageView)getActivity().findViewById(R.id.bookImage)).setImageBitmap(bitmap);
                    mImagesUrls.add(bitmap);
                    Log.i(TAG1,"Image was retrieved!" + mImagesUrls.size());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_book_list, container, false);

        initImageBitmaps();

        //add array for each item
        /*
        ArrayList<String> mAuthor = new ArrayList<>();
        ArrayList<String> mTitle = new ArrayList<>();
        ArrayList<Integer> mImageID = new ArrayList<>();
*/

        //simple loop here to add the images to the array without typing each one

/*
        for(int i=1;i<=14;i++) {
            int id = getResources().getIdentifier("sku1000" + i, "drawable",
                    root.getContext().getPackageName());
            mImageID.add(id);
        }

        //adding author and title.
        mAuthor.add("Edgar Rice Burroughs"); mTitle.add("Tarzan and the Golden Lion");
        mAuthor.add("Agatha Christie"); mTitle.add("The Murder on the Links");
        mAuthor.add("Winston S. Churchill"); mTitle.add("The World Crisis");
        mAuthor.add("E.e. cummings"); mTitle.add("Tulips and Chimneys");
        mAuthor.add("Robert Frost"); mTitle.add("New Hampshire");
        mAuthor.add("Kahlil Gibran"); mTitle.add("The Prophet");
        mAuthor.add("Aldous Huxley"); mTitle.add("Antic Hay");
        mAuthor.add("D.H. Lawrence"); mTitle.add("Kangaroo");
        mAuthor.add("Bertrand and Dora Russell"); mTitle.add("The Prospects of Industrial Civilization");
        mAuthor.add("Carl Sandberg"); mTitle.add("Rootabaga Pigeons");
        mAuthor.add("Edith Wharton"); mTitle.add("A Son at the Front");
        mAuthor.add("P.G. Wodehouse"); mTitle.add("The Inimitable Jeeves");
        mAuthor.add("P.G. Wodehouse"); mTitle.add("Leave it to Psmith");
        mAuthor.add("Viginia Woolf"); mTi
        */
        mBook.add(new Book("Edgar Rice Burroughs","Tarzan and the Golden Lion","1"));
        mBook.add(new Book("Agatha Christie","The Murder on the Links","2"));

        mBook.add(new Book("Winston S. Churchill","The World Crisis","3"));

        RecyclerView recyclerView = root.findViewById(R.id.bookView_view);
        LibraryViewAdapter recyclerViewAdapter = new LibraryViewAdapter(getContext(), mBook,mImagesUrls);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        return root;
    }

}
