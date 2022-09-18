package com.example.hotel_reviewfrontend.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_reviewfrontend.R;
import com.example.hotel_reviewfrontend.model.ReviewModel;
import com.example.hotel_reviewfrontend.review.HomeActivity;
import com.example.hotel_reviewfrontend.utils.OnClickAction;


import java.util.ArrayList;


public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.MyViewHolder>  {
    Context context;
    ArrayList<ReviewModel> reviewModels;
    OnClickAction onClickAction;

    public HomeRecyclerViewAdapter(Context context, ArrayList<ReviewModel> reviewModels, OnClickAction onClickAction) {
        this.context = context;
        this.reviewModels = reviewModels;
        this.onClickAction = onClickAction;
    }

    @NonNull
    @Override
    public HomeRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_row, parent, false);
        return new HomeRecyclerViewAdapter.MyViewHolder(view, context, onClickAction);
    }
    //TODO RISOLVERE HARDCODING
    @Override
    public void onBindViewHolder(@NonNull HomeRecyclerViewAdapter.MyViewHolder holder, int position) {
        try {
            holder.user.setText(" "+"User:" +" "+ reviewModels.get(position).getUsername());
            holder.title.setText(" "+"Title:" + " " + reviewModels.get(position).getTitle());
            holder.text.setText(" "+"Text:" + " " + reviewModels.get(position).getText());
            holder.hotel.setText(" "+"Hotel:" + " " + reviewModels.get(position).getHotel());
            holder.zipCode.setText(" "+"Zip Code:" + " " + reviewModels.get(position).getZipCode());
            holder.ratingBar.setRating( reviewModels.get(position).getRating());
            holder.id = reviewModels.get(position).getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return reviewModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public int id;
        TextView user, title, text, hotel, zipCode;
        RatingBar ratingBar;
        ImageButton delete, update;
        Context context;
        OnClickAction onClickAction;



        public MyViewHolder(@NonNull View itemView, Context context, OnClickAction onClickAction) {
            super(itemView);
            user = itemView.findViewById(R.id.username_review);
            title = itemView.findViewById(R.id.title_review);
            text = itemView.findViewById(R.id.text_review);
            hotel = itemView.findViewById(R.id.hotel_review);
            zipCode = itemView.findViewById(R.id.zipCode_review);
            ratingBar = itemView.findViewById(R.id.ratingBar_review);
            delete = itemView.findViewById(R.id.deleteReviewBtn);
            update = itemView.findViewById(R.id.updateReviewBtn);
            id = itemView.getId();
            

            this.context = context;
            this.onClickAction = onClickAction;
            delete.setOnClickListener(this);
            update.setOnClickListener(this);
        }
        public int getId() {
            return id;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.deleteReviewBtn:
                    Toast.makeText(context,"delete", Toast.LENGTH_SHORT ).show();
                    onClickAction.onDelete(id);
                    break;
                case R.id.updateReviewBtn:
                    Toast.makeText(context,"update", Toast.LENGTH_SHORT ).show();
                    onClickAction.onUpdate(id, new ReviewModel());
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + view.getId());
            }
        }
    }
}
