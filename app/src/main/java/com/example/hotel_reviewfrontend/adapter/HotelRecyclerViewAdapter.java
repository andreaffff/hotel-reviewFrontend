package com.example.hotel_reviewfrontend.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_reviewfrontend.R;
import com.example.hotel_reviewfrontend.model.ReviewModel;

import java.util.ArrayList;

public class HotelRecyclerViewAdapter extends RecyclerView.Adapter<HotelRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<ReviewModel> reviewModels;

    public HotelRecyclerViewAdapter(Context context, ArrayList<ReviewModel> reviewModels) {
        this.context = context;
        this.reviewModels = reviewModels;
    }

    @NonNull
    @Override
    public HotelRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.recycler_row_hotel, parent, false);
        return new HotelRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            holder.user.setText(" " + "User:" + " " + reviewModels.get(position).getUsername());
            holder.title.setText(" " + "Title:" + " " + reviewModels.get(position).getTitle());
            holder.text.setText(" " + "Text:" + " " + reviewModels.get(position).getText());
            holder.hotel.setText(" " + "Hotel:" + " " + reviewModels.get(position).getHotel());
            holder.zipCode.setText(" " + "Zip Code:" + " " + reviewModels.get(position).getZipCode());
            holder.ratingBar.setRating(reviewModels.get(position).getRating());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return reviewModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView user, title, text, hotel, zipCode;
        RatingBar ratingBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.hotel_username_review);
            title = itemView.findViewById(R.id.hotel_title_review);
            text = itemView.findViewById(R.id.hotel_text_review);
            hotel = itemView.findViewById(R.id.hotel_name_review);
            zipCode = itemView.findViewById(R.id.hotel_zipCode_review);
            ratingBar = itemView.findViewById(R.id.hotel_ratingBar_review);
        }
    }
}
