package com.example.hotel_reviewfrontend.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hotel_reviewfrontend.LoadingDialog.LoadingDialog;
import com.example.hotel_reviewfrontend.R;
import com.example.hotel_reviewfrontend.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;


public class MyProfileActivity extends AppCompatActivity {
    private Utils utils;
    private TextView name;
    private TextView surname;
    private TextView email;
    private TextView username;
    private TextView phone;
    private TextView address;
    private Button update;
    private Button myReviews;
    private Context context;
    private LoadingDialog loadingDialog;
    private boolean requestDone;
    private boolean responseDone;
    private final int SLEEP = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile);
        this.initializeComponents();
    }
    private void setOnClickUpdateProfile() {
        this.update.setOnClickListener(view -> {
            Intent intent = new Intent(this, UpdateProfileActivity.class);
            intent.putExtra("username",username.getText());
            intent.putExtra("name",name.getText());
            intent.putExtra("surname",surname.getText());
            intent.putExtra("email",email.getText());
            intent.putExtra("address",address.getText());
            intent.putExtra("phone",phone.getText());
            startActivity(intent);

        });

        }

    private void initializeComponents() {

        this.name = findViewById(R.id.name_txo);
        this.surname = findViewById(R.id.surname_txo);
        this.email = findViewById(R.id.email_txo);
        this.address = findViewById(R.id.address_txo);
        this.phone = findViewById(R.id.phone_txo);
        this.username = findViewById(R.id.username_txo);
        this.update = findViewById(R.id.updateBtn);
        this.myReviews = findViewById(R.id.myReviewsBtn);
        this.loadingDialog = new LoadingDialog(this);
        this.requestDone = false;
        this.responseDone = false;
        utils = new Utils();
        context = getApplicationContext();
        this.requestHandler();
        this.setOnClickUpdateProfile();
    }

    protected void assignValues() { // Richiesta al server getOneUser
        Log.d("Assign values", "entra");
        SharedPreferences preferences = this.getSharedPreferences("userData", Context.MODE_PRIVATE);
        String usernameStr = preferences.getString("username", null);
        if (usernameStr != null) {
            Log.d("username!=", "entra");

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = getString(R.string.base_url) + "/user/?username=" + usernameStr;
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject res) {
                    Log.d("res", res.toString());
                    responseDone = true;
                    try {
                        username.setText(res.getString("username"));
                        name.setText(res.getString("name"));
                        surname.setText(res.getString("surname"));
                        phone.setText(res.getString("phone"));
                        address.setText(res.getString("address"));
                        email.setText(res.getString("email"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    utils.showToast(context,getString(R.string.something_went_wrong));
                    Log.d("errore", "errore");
                    responseDone = true;
                }
            });
            requestQueue.add(jsonReq);
        } else {
            // manda a pagina di login
        }


    }

    protected void requestHandler() { //creazione thread per richiesta e gestione caricamento
        responseDone = false;
        requestDone = false;

        new Thread(() -> {
            utils.openLoadingDialog(loadingDialog, true);

            while (!this.requestDone) {
                try {
                    Thread.sleep(SLEEP);
                } catch (InterruptedException ignored) {
                }
                    this.assignValues();
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
}
