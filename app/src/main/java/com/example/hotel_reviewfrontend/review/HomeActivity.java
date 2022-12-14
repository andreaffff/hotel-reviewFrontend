package com.example.hotel_reviewfrontend.review;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hotel_reviewfrontend.LoadingDialog.LoadingDialog;
import com.example.hotel_reviewfrontend.R;
import com.example.hotel_reviewfrontend.adapter.HomeRecyclerViewAdapter;
import com.example.hotel_reviewfrontend.model.ReviewModel;
import com.example.hotel_reviewfrontend.user.AdminOnlyActivity;
import com.example.hotel_reviewfrontend.user.MyProfileActivity;
import com.example.hotel_reviewfrontend.utils.OnClickAction;
import com.example.hotel_reviewfrontend.utils.Utils;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements OnClickAction {
    private final int SLEEP = 500;
    ArrayList<ReviewModel> myReviewModels = new ArrayList<>();
    private Utils utils;
    private Context context;
    private LoadingDialog loadingDialog;
    private boolean requestDone;
    private boolean responseDone;
    private TextInputLayout searchBar;
    private Button searchBtn;
    private Button profile;
    private ImageButton addReviews;
    private Button adminOnlyBtn;
    private HomeRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        searchBar = findViewById(R.id.hotel_txi);
        searchBtn = findViewById(R.id.findHotel);
        addReviews = findViewById(R.id.addReviewBtn);
        adminOnlyBtn = findViewById(R.id.adminOnly);
        recyclerView = findViewById(R.id.myReviewRecyclerView);
        profile = findViewById(R.id.profile);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        this.initializeComponents();

    }

    private void initializeComponents() {
        this.loadingDialog = new LoadingDialog(this);
        this.requestDone = false;
        this.responseDone = false;
        utils = new Utils();
        context = getApplicationContext();
        SharedPreferences preferences = getSharedPreferences("userData", Context.MODE_PRIVATE);
        String role = preferences.getString("role", "");
        if (!role.equals("admin")) {
            adminOnlyBtn.setVisibility(View.INVISIBLE);
        }
        getMyReviewsHandler();
        setOnClickSearch();
        setOnClickAdminOnly();
        setOnClickProfile();
        setOnClickAddReview();
    }

    private void setOnClickSearch() {
        this.searchBtn.setOnClickListener(view -> {
            getReviewsByHotel();
        });
    }

    private void setOnClickProfile() {
        this.profile.setOnClickListener(view -> {
            Intent intent = new Intent(this, MyProfileActivity.class);
            startActivity(intent);
        });
    }

    private void setOnClickAddReview() {
        this.addReviews.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddReviewActivity.class);
            startActivity(intent);
        });
    }

    private void setOnClickAdminOnly() {
        this.adminOnlyBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, AdminOnlyActivity.class);
            startActivity(intent);

        });
    }


    private void getReviewsByHotel() {
        String hotelToSearch = searchBar.getEditText().getText().toString().toLowerCase(Locale.ROOT);
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
                            reviewModel.setRating((float) response.getJSONObject(i).getDouble("rating"));
                            reviewModel.setId(response.getJSONObject(i).getInt("id"));
                            reviewModel.setHotel(response.getJSONObject(i).getString("hotel"));
                            reviewModel.setZipCode(response.getJSONObject(i).getString("zipCode"));
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
                    myReviewModels, this);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDelete(int id) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = getString(R.string.base_url) + "/reviews?id=" + id;
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject res) {
                responseDone = true;
                utils.showToast(context, getString(R.string.delete_reviews));
                Intent intent = new Intent(context, HomeActivity.class);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                utils.showToast(context, getString(R.string.something_went_wrong));
                responseDone = true;
            }
        });
        requestQueue.add(jsonReq);
    }

    @Override
    public void onUpdate(int id, String hotel, String zipCode) {
        Intent intent = new Intent(this, UpdateReviewActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("hotel", hotel);
        intent.putExtra("zipCode", zipCode);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
    }

}