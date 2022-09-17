package com.example.hotel_reviewfrontend.review;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.hotel_reviewfrontend.LoadingDialog.LoadingDialog;
import com.example.hotel_reviewfrontend.R;
import com.example.hotel_reviewfrontend.adapter.HotelRecyclerViewAdapter;
import com.example.hotel_reviewfrontend.model.ReviewModel;
import com.example.hotel_reviewfrontend.utils.Utils;

import java.util.ArrayList;

public class HotelReviewActivity extends AppCompatActivity {
    final int SLEEP = 500;
    ArrayList<ReviewModel> reviewModels = new ArrayList<>();
    private Utils utils;
    private Context context;
    private LoadingDialog loadingDialog;
    private boolean requestDone;
    private boolean responseDone;
    private HotelRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hotel_review_page);
        recyclerView = findViewById(R.id.hotelRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.initializeComponents();

    }

    private void initializeComponents() {
        this.loadingDialog = new LoadingDialog(this);
        this.requestDone = false;
        this.responseDone = false;
        utils = new Utils();
        context = getApplicationContext();
        getReviewsByHotelHandler();
    }

    private void getReviewsByHotelHandler() {
        responseDone = false;
        requestDone = false;

        new Thread(() -> {
            utils.openLoadingDialog(loadingDialog, true);

            while (!this.requestDone) {
                try {
                    Thread.sleep(SLEEP);
                } catch (InterruptedException ignored) {
                }
                this.getHotelReviews();
                requestDone = true;
            }
            while (!responseDone) {
                try {
                    Thread.sleep(SLEEP);
                } catch (InterruptedException ignored) {
                }
            }
            utils.openLoadingDialog(loadingDialog, false);

        }).start();
    }


    private void getHotelReviews() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Intent intent = getIntent();
        String hotelToSearch = intent.getStringExtra("hotelToSearch");
        String url = getString(R.string.base_url) + "/reviews/byhotel?hotel=" + hotelToSearch + "&cap=03043";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    ReviewModel reviewModel;
                    if (response.length() > 0) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                reviewModel = new ReviewModel();
                                reviewModel.setUsername(response.getJSONObject(i).getString("username"));
                                reviewModel.setText(response.getJSONObject(i).getString("text"));
                                reviewModel.setTitle(response.getJSONObject(i).getString("title"));
                                reviewModel.setHotel(response.getJSONObject(i).getString("hotel"));
                                reviewModel.setZipCode(response.getJSONObject(i).getString("zipCode"));
                                reviewModel.setRating((float) response.getJSONObject(i).getDouble("rating"));
                                reviewModels.add(reviewModel);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        createRVItem();


                    } else {
                        utils.showToast(context, getString(R.string.no_reviews_hotel));
                    }
                    this.responseDone = true;

                },
                error -> {
                    Log.e("error", error.toString());
                    utils.showToast(context, getString(R.string.something_went_wrong));
                    this.responseDone = true;
                }
        );
        requestQueue.add(jsonArrayRequest);
    }


    public void createRVItem() {
        try {
            adapter = new HotelRecyclerViewAdapter(this,
                    reviewModels);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

