package com.example.project_sda_2022;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

public class ChatReyclerViewAdapter extends RecyclerView.Adapter<ChatReyclerViewAdapter.ViewHolder> {
    private ArrayList<SetChatItem> mData;
    private LayoutInflater mInflater;
    private static final String TAG = "MyRecyclerViewAdapter";

    // data is passed into the constructor
    public ChatReyclerViewAdapter(ArrayList<SetChatItem> itemList, Context context) {
        this.mData = itemList;
        this.mInflater = LayoutInflater.from(context);
    }
    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, final int position) {

        final SetChatItem chatContent = mData.get(position);

        // Set the data to the views here
        holder.userDetails.setText(chatContent.getName());
        holder.chatDetails.setText(chatContent.getReviewContent());

        if(!chatContent.getReviewContent().equals("")) {
            holder.messageImageView.setVisibility(View.GONE);
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData == null? 0: mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userDetails;
        ImageView messageImageView;
        TextView chatDetails;
        ImageView messengerImageView;

        ViewHolder(View itemView) {
            super(itemView);
            userDetails = itemView.findViewById(R.id.userDetails);
            messageImageView = itemView.findViewById(R.id.messageImageView);
            chatDetails = itemView.findViewById(R.id.chatdetails);
            messengerImageView = itemView.findViewById(R.id.messengerImageView);
        }

    }
}

