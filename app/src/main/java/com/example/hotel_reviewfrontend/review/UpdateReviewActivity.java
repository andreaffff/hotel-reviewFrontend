package com.example.hotel_reviewfrontend.review;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hotel_reviewfrontend.LoadingDialog.LoadingDialog;
import com.example.hotel_reviewfrontend.R;
import com.example.hotel_reviewfrontend.model.ReviewModel;
import com.example.hotel_reviewfrontend.signInAndLogin.LoginActivity;
import com.example.hotel_reviewfrontend.utils.Utils;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

public class UpdateReviewActivity extends AppCompatActivity {
    final int SLEEP = 500;
    TextInputLayout title;
    TextInputLayout description;
    TextInputLayout hotel;
    TextInputLayout zipCode;
    RatingBar rating;
    Button enter;
    LoadingDialog loadingDialog;
    Boolean responseDone;
    Boolean requestDone;
    Utils utils;
    ReviewModel reviewModel;
    Context context;
    int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        this.initializeComponents();
    }

    private void initializeComponents() {

        title = findViewById(R.id.title_txi);
        description = findViewById(R.id.description);
        hotel = findViewById(R.id.hotel_txi);
        zipCode = findViewById(R.id.zip_code_txi);
        rating = findViewById(R.id.ratingReview);
        enter = findViewById(R.id.enter_Btn);
        responseDone = false;
        requestDone = false;
        loadingDialog = new LoadingDialog(this);
        utils = new Utils();
        reviewModel = new ReviewModel();
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        title.getEditText().setText(intent.getStringExtra("title"));
        description.getEditText().setText(intent.getStringExtra("description"));
        hotel.getEditText().setText(intent.getStringExtra("hotel"));
        zipCode.getEditText().setText(intent.getStringExtra("zipCode"));
        rating.setRating(intent.getFloatExtra("rating", 0));
        context = getApplicationContext();
        this.setOnClickEnter();
    }

    private void setOnClickEnter() {
        this.enter.setOnClickListener(view -> {
            requestHandler();
            Intent intent = new Intent(context, HomeActivity.class);
            startActivity(intent);
        });
    }

    private void requestHandler() {
        responseDone = false;
        requestDone = false;
        reviewModel.setTitle(title.getEditText().getText().toString());
        reviewModel.setText(description.getEditText().getText().toString());
        reviewModel.setHotel(hotel.getEditText().getText().toString());
        reviewModel.setZipCode(zipCode.getEditText().getText().toString());
        reviewModel.setRating((rating.getRating()));
        if (reviewModel.getZipCode().length() == 5
                && reviewModel.getZipCode().matches("[0-9]+")) {
            new Thread(() -> {
                utils.openLoadingDialog(loadingDialog, true);

                while (!this.requestDone) {
                    try {
                        Thread.sleep(SLEEP);
                    } catch (InterruptedException ignored) {
                    }
                    this.updateReview();
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
        } else {
            utils.showToast(context, getString(R.string.zipCode_5_numeric));
        }
    }

    private void updateReview() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        SharedPreferences preferences = this.getSharedPreferences("userData", Context.MODE_PRIVATE);
        String usernameStr = preferences.getString("username", null);
        if (usernameStr != null) {
            String url = getString(R.string.base_url) + "/reviews?id=" + id;
            try {
                JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT, url, reviewModel.toJson(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        responseDone = true;
                        utils.showToast(context, getString(R.string.review_ok));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        responseDone = true;
                        utils.showToast(context, getString(R.string.something_went_wrong));
                    }
                });
                requestQueue.add(jsonReq);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.deleteSharedPreferences("userData");
            } else
                context.getSharedPreferences("userData", Context.MODE_PRIVATE).edit().clear().apply();
        }
    }
}



