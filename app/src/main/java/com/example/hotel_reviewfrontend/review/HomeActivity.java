package com.example.hotel_reviewfrontend.review;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.hotel_reviewfrontend.adapter.HomeRecyclerViewAdapter;
import com.example.hotel_reviewfrontend.model.ReviewModel;
import com.example.hotel_reviewfrontend.utils.Utils;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private Utils utils;
    private Context context;
    private LoadingDialog loadingDialog;
    private boolean requestDone;
    private boolean responseDone;
    private HomeRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    ArrayList<ReviewModel> reviewModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        recyclerView = findViewById(R.id.myReviewRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.initializeComponents();

    }
    private void initializeComponents() {
        this.loadingDialog = new LoadingDialog(this);
        this.requestDone = false;
        this.responseDone = false;
        utils = new Utils();
        context = getApplicationContext();
        getMyReviews();
    }


    private void getMyReviews() {
        SharedPreferences preferences = this.getSharedPreferences("userData", Context.MODE_PRIVATE);
        String usernameStr = preferences.getString("username", null);
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getString(R.string.base_url) + "/reviews/byusername?username=" + usernameStr;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    ReviewModel reviewModel;
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            reviewModel = new ReviewModel();
                            reviewModel.setUsername(response.getJSONObject(i).getString("username"));
                            reviewModel.setText(response.getJSONObject(i).getString("text"));
                            reviewModel.setTitle(response.getJSONObject(i).getString("title"));
                            reviewModel.setHotel(response.getJSONObject(i).getString("hotel"));
                            reviewModel.setZipCode(response.getJSONObject(i).getString("zipCode"));
                            reviewModel.setUpVote(Integer.parseInt(response.getJSONObject(i).getString("upvote")));
                            reviewModel.setDownvote(Integer.parseInt(response.getJSONObject(i).getString("upvote")));
                            reviewModel.setRating((float) response.getJSONObject(i).getDouble("rating"));
                            reviewModels.add(reviewModel);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    this.responseDone = true;
                    createRVItem();
                },
                error -> {
                    Log.e("error", error.toString());
                    utils.showToast(context, getString(R.string.something_went_wrong));
                    this.requestDone = true;
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    public void createRVItem(){
        try {
            adapter = new HomeRecyclerViewAdapter(this,
                    reviewModels);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}