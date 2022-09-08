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
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;
//TODO da fare una classe util in cui mettere funzioni in comune tra questa classe e quella delle registrazione
public class LoginActivity extends AppCompatActivity {
    private TextInputLayout username;
    private TextInputLayout password;
    private Button loginButton;
    private LoadingDialog loadingDialog;
    private String usernameStr;
    private String passwordStr;
    private boolean requestDone = false;
    private boolean responseDone = false;
    private boolean responseSuccess = false;
    private int SLEEP = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        this.initializeComponent();
    }

    private void initializeComponent() {

        Log.d("initializeComponent", "Entra qui");
        this.username = findViewById(R.id.username_txi);
        this.password = findViewById(R.id.password_txi);
        this.loadingDialog = new LoadingDialog(this);
        this.loginButton = findViewById(R.id.loginButton);
        this.setOnClickLogin();
    }

    private void setOnClickLogin() {

        this.loginButton.setOnClickListener(view -> {
            responseDone = false;
            responseSuccess = false;
            requestDone = false;

            usernameStr = this.username.getEditText().getText().toString();
            passwordStr = this.password.getEditText().getText().toString();
            if (!usernameStr.isEmpty() || !passwordStr.isEmpty()) {
                new Thread(() -> {
                    this.openLoadingDialog(true);

                    while (!this.requestDone) {
                        try {
                            Thread.sleep(SLEEP);
                        } catch (InterruptedException ignored) {
                        }
                        try {
                            this.login(usernameStr,passwordStr);
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
                    this.openLoadingDialog(false);
                    if (responseSuccess) {
                        SharedPreferences preferences = this.getSharedPreferences("userData", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("username", usernameStr);
                        editor.putString("password", passwordStr);
                        editor.apply();
                    }

                }).start();
            } else {
                showToast(getString(R.string.empty_fields));
            }
        });

    }

    private void login(String usernameStr,String passwordStr) throws JSONException {

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
                    showToast(getString(R.string.login_ok));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    responseDone = true;
                    responseSuccess = false;
                    Log.d("error:",error.toString());

                    if (error.toString().equals("com.android.volley.AuthFailureError")) {
                        showToast(getString(R.string.username_or_password_wrong));
                    } else if (error.toString().equals("com.android.volley.TimeoutError")) {
                        showToast(getString(R.string.something_went_wrong));
                    }
                }
            });
            requestQueue.add(jsonReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void openLoadingDialog(boolean flag) {
        this.runOnUiThread(() -> {
            if (flag)
                this.loadingDialog.show();
            else
                this.loadingDialog.dismiss();
        });
    }
    private void showToast(String message) {
        this.runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }
}
