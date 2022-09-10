package com.example.hotel_reviewfrontend.signInAndLogin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
import com.example.hotel_reviewfrontend.model.UserModel;
import com.example.hotel_reviewfrontend.utils.Utils;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;
//TODO da fare una classe util in cui mettere funzioni in comune tra questa classe e quella delle registrazione
public class LoginActivity extends AppCompatActivity {

    Utils utils;
    private TextInputLayout username;
    private TextInputLayout password;
    private Button loginButton;
    private LoadingDialog loadingDialog;
    Toast toast ;
    private String usernameStr;
    private String passwordStr;
    private boolean requestDone = false;
    private boolean responseDone = false;
    private boolean responseSuccess = false;
    private int SLEEP = 500;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        SharedPreferences preference = this.getSharedPreferences("userData", Context.MODE_PRIVATE);
        Log.d("shared", preference.getString("username", null));
        Log.d("shared", preference.getString("password", null));
        this.initializeComponents();
    }

    private void initializeComponents() {

        Log.d("initializeComponent", "Entra qui");
        this.username = findViewById(R.id.username_txi);
        this.password = findViewById(R.id.password_txi);
        this.loadingDialog = new LoadingDialog(this);
        this.loginButton = findViewById(R.id.loginBtn);
        context = getApplicationContext();

        this.setOnClickLogin();
        utils = new Utils();
    }

    private void setOnClickLogin() {

        this.loginButton.setOnClickListener(view -> {
            responseDone = false;
            responseSuccess = false;
            requestDone = false;

            usernameStr = this.username.getEditText().getText().toString();
            passwordStr = this.password.getEditText().getText().toString();
            this.requestHandler();
        });

    }

    private void login(String usernameStr, String passwordStr) throws JSONException {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = getString(R.string.base_url) + "/user/login";
        JSONObject jsonObject = new JSONObject();


        try {
            jsonObject.put("username", usernameStr);
            jsonObject.put("password", passwordStr);
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    responseDone = true;
                    responseSuccess = true;
                    utils.showToast(context,getString(R.string.login_ok));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    responseDone = true;
                    responseSuccess = false;
                    Log.d("error:", error.toString());

                    if (error.toString().equals("com.android.volley.AuthFailureError")) {
                        utils.showToast(context,getString(R.string.username_or_password_wrong));
                    } else if (error.toString().equals("com.android.volley.TimeoutError")) {
                        utils.showToast(context,getString(R.string.something_went_wrong));
                    }
                }
            });
            requestQueue.add(jsonReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void requestHandler(){

        if (!usernameStr.isEmpty() || !passwordStr.isEmpty()) {
            new Thread(() -> {
                utils.openLoadingDialog(loadingDialog,true);

                while (!this.requestDone) {
                    try {
                        Thread.sleep(SLEEP);
                    } catch (InterruptedException ignored) {
                    }
                    try {
                        this.login(usernameStr, passwordStr);
                        requestDone = true;
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
                while (!responseDone) {
                    try {
                        Thread.sleep(SLEEP);
                    } catch (InterruptedException ignored) {
                    }
                }
                utils.openLoadingDialog(loadingDialog,false);
                if (responseSuccess) {
                    SharedPreferences preferences = this.getSharedPreferences("userData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("username", usernameStr);
                    editor.putString("password", passwordStr);
                    editor.apply();
                }

            }).start();
        } else {

            utils.showToast(context,getString(R.string.empty_fields));
        }
    }
}



