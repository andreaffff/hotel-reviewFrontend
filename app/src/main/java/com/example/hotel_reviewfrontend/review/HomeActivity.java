package com.example.hotel_reviewfrontend.review;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

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
import com.example.hotel_reviewfrontend.user.MyProfileActivity;
import com.example.hotel_reviewfrontend.utils.Utils;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private Utils utils;
    private Context context;
    private final int SLEEP = 500;
    private LoadingDialog loadingDialog;
    private boolean requestDone;
    private boolean responseDone;
    private TextInputLayout searchBar;
    private Button searchBtn;
    private HomeRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
   // ArrayList<ReviewModel> reviewModels = new ArrayList<>();
    ArrayList<ReviewModel> myReviewModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        searchBar = findViewById(R.id.hotel_txi);
        searchBtn = findViewById(R.id.findHotel);
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
        getMyReviewsHandler();
        setOnClickSearch();
    }

    private void setOnClickSearch() {
        this.searchBtn.setOnClickListener(view -> {
            getReviewsByHotel();
        });
    }



    private void getReviewsByHotel() {
        String hotelToSearch = searchBar.getEditText().getText().toString();
        Intent intent = new Intent(context, HotelReviewActivity.class);
        intent.putExtra("hotelToSearch", hotelToSearch);
        startActivity(intent);
    }

    private void getMyReviewsHandler() {
        responseDone = false;
        requestDone = false;

        new Thread(() -> {
            utils.openLoadingDialog(loadingDialog, true);

            while (!this.requestDone) {
                try {
                    Thread.sleep(SLEEP);
                } catch (InterruptedException ignored) {
                }
                this.getMyReviews();
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
                            myReviewModels.add(reviewModel);
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
                    this.responseDone = true;
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    public void createRVItem() {
        try {
            adapter = new HomeRecyclerViewAdapter(this,
                    myReviewModels);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}