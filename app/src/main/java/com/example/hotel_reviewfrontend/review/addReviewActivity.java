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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;
//TODO aggiungere controllo su inserimento di recensioni multiple sullo stesso hotel o qui o backend
public class addReviewActivity extends AppCompatActivity {
    TextInputLayout title;
    TextInputLayout description;
    TextInputLayout hotel;
    TextInputLayout zipCode;
    RatingBar rating;
    Button enter;
    LoadingDialog loadingDialog;

    Boolean responseDone;
    Boolean requestDone;
    final int SLEEP = 500;
    Utils utils;
    ReviewModel reviewModel;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        this.initializeComponents();
    }

    private void initializeComponents(){
        title = findViewById(R.id.title_txi);
        description = findViewById(R.id.description);
        hotel = findViewById(R.id.hotel_txi);
        zipCode = findViewById(R.id.zip_code_txi);
        rating = findViewById(R.id.ratingReview);
        enter = findViewById(R.id.enter_Btn);
        rating.setStepSize(1);
        responseDone = false;
        requestDone = false;
        loadingDialog = new LoadingDialog(this);
        utils = new Utils();
        reviewModel = new ReviewModel();
        context = getApplicationContext();
        this.setOnClickEnter();
    }
    private void setOnClickEnter() {
        this.enter.setOnClickListener(view -> {
            reviewModel.setTitle(title.getEditText().getText().toString());
            reviewModel.setDescription(description.getEditText().getText().toString());
            reviewModel.setHotel(hotel.getEditText().getText().toString());
            reviewModel.setZipCode(zipCode.getEditText().getText().toString());
            reviewModel.setRating((rating.getRating()));
            requestHandler();
        });
    }
    private void requestHandler(){
        responseDone = false;
        requestDone = false;

        new Thread(() -> {
            utils.openLoadingDialog(loadingDialog, true);

            while (!this.requestDone) {
                try {
                    Thread.sleep(SLEEP);
                } catch (InterruptedException ignored) {
                }
                this.addReview();
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
    private void addReview(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        SharedPreferences preferences = this.getSharedPreferences("userData", Context.MODE_PRIVATE);
        String usernameStr = preferences.getString("username", null);
        if (usernameStr != null) {
            String url = getString(R.string.base_url) + "/reviews?username="+usernameStr;

            try {
                JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST, url, reviewModel.toJson(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        responseDone = true;
                        utils.showToast(context, getString(R.string.signin_ok));
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

