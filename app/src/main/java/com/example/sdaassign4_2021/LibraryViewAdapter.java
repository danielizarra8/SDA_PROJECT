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

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.graphics.Bitmap;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

        import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.StorageReference;

        import java.util.ArrayList;

/*
 * @author Chris Coughlan 2019
 */
public class LibraryViewAdapter extends RecyclerView.Adapter<LibraryViewAdapter.ViewHolder> {
    private static final String USER_NAME_KEY = "USER_NAME_KEY";
    private static final String USER_ID_KEY = "USER_ID_KEY";
    private static final String TAG = "RecyclerViewAdapter";
    private Context mNewContext;
    private ArrayList<Book>mBook;
    private ArrayList<Bitmap>mImages;
    public FirebaseStorage mStorage;
    public StorageReference storageReference;
    private StorageReference gsReference;
    /*
    //add array for each item\
    private ArrayList<String> mAuthor;
    private ArrayList<String> mTitle;
    private ArrayList<Integer> mImageID;

    LibraryViewAdapter(Context mNewContext, ArrayList<String> author, ArrayList<String> title, ArrayList<Integer> imageId) {
        this.mNewContext = mNewContext;
        this.mAuthor = author;
        this.mTitle = title;
        this.mImageID = imageId;
    }
*/
    LibraryViewAdapter(Context mNewContext, ArrayList<Book> mbook, ArrayList<Bitmap>images) {
        this.mNewContext = mNewContext;
        this.mBook = mbook;
        this.mImages = images;
    }

    //declare methods
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_list_item, viewGroup, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        Log.d(TAG, "onBindViewHolder: was called");

        // Glide.with(mNewContext).asBitmap().load(mImages.get(position)).into(viewHolder.imageItem);
/*
        viewHolder.authorText.setText(mAuthor.get(position));
        viewHolder.titleText.setText(mTitle.get(position));
        viewHolder.imageItem.setImageResource(mImageID.get(position));
*/
        viewHolder.authorText.setText(mBook.get(position).getBookAuthor());
        viewHolder.titleText.setText(mBook.get(position).getBookTitle());
        //viewHolder.imageItem.setImageBitmap(mImages.get(position));

        //should check here to see if the book is available.

            viewHolder.checkOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkOutBook(v,position);
                }
            });
    }

    @Override
    public int getItemCount() {
        return mBook.size();
    }

    //view holder class for recycler_list_item.xml
    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageItem;
        TextView authorText;
        TextView titleText;
        Button checkOut;
        RelativeLayout itemParentLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            //grab the image, the text and the layout id's
            imageItem = itemView.findViewById(R.id.bookImage);
            authorText = itemView.findViewById(R.id.authorText);
            titleText = itemView.findViewById(R.id.bookTitle);
            checkOut = itemView.findViewById(R.id.out_button);
            itemParentLayout = itemView.findViewById(R.id.listItemLayout);

        }
    }
    private void checkOutBook(View v, int position) {
        final SharedPreferences prefs = mNewContext.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);

        String texUserName = prefs.getString(USER_NAME_KEY, "");
        String userID = prefs.getString(USER_ID_KEY, "");
        if (texUserName.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mNewContext);
            builder.setTitle("Notification!").setMessage("Customer Name not set.").setPositiveButton("OK", null).show();
        } else {
            //Toast.makeText(mNewContext, mTitle.get(position), Toast.LENGTH_SHORT).show();
            //Toast.makeText(mNewContext, mBook.get(position).getBookTitle(), Toast.LENGTH_SHORT).show();
            //...
            Intent myOrder = new Intent(mNewContext, CheckOut.class);
            //get the title and send it to the checkout activity
            myOrder.putExtra("title",mBook.get(position).getBookTitle());
            myOrder.putExtra("bookID",mBook.get(position).getBookID());
            myOrder.putExtra("userName",texUserName);
            myOrder.putExtra("userID",userID);
            mNewContext.startActivity(myOrder);
        }
    }
}
