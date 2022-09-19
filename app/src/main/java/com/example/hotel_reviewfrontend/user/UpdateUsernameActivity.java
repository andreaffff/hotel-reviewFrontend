package com.example.hotel_reviewfrontend.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hotel_reviewfrontend.LoadingDialog.LoadingDialog;
import com.example.hotel_reviewfrontend.R;
import com.example.hotel_reviewfrontend.signInAndLogin.LoginActivity;
import com.example.hotel_reviewfrontend.utils.Utils;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.Locale;

public class UpdateUsernameActivity extends AppCompatActivity {
    final int SLEEP = 500;
    TextInputLayout oldusername;
    TextInputLayout newUsername;
    TextInputLayout confirmUsername;
    Button save;
    Utils utils;
    String oldusernameStr;
    String newUsernameStr;
    String confirmUsernameStr;
    Context context;
    LoadingDialog loadingDialog;
    Boolean responseDone = false;
    Boolean requestDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_username_activity);
        this.initializeComponents();

    }

    private void initializeComponents() {
        oldusername = findViewById(R.id.oldUsername_txi);
        newUsername = findViewById(R.id.newUsername_txi);
        confirmUsername = findViewById(R.id.confirmNewUsername_txi);
        save = findViewById(R.id.enter_Username_Btn);
        context = getApplicationContext();
        utils = new Utils();
        responseDone = false;
        requestDone = false;
        loadingDialog = new LoadingDialog(this);
        this.setOnClickSave();
    }

    private void setOnClickSave() {

        this.save.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            requestHandler();
        });
    }

    protected void requestHandler() { //creazione thread per richiesta e gestione caricamento
        responseDone = false;
        requestDone = false;

        oldusernameStr = oldusername.getEditText().getText().toString().toLowerCase(Locale.ROOT);
        newUsernameStr = newUsername.getEditText().getText().toString().toLowerCase(Locale.ROOT);
        confirmUsernameStr = confirmUsername.getEditText().getText().toString().toLowerCase(Locale.ROOT);


        new Thread(() -> {

            if (confirmUsernameStr.equals(newUsernameStr) && !oldusernameStr.equals(newUsernameStr)) {
                utils.openLoadingDialog(loadingDialog, true);
                while (!this.requestDone) {
                    try {
                        Thread.sleep(SLEEP);
                    } catch (InterruptedException ignored) {
                    }
                    this.updateUsername();
                    requestDone = true;
                }
                while (!responseDone) {
                    try {
                        Thread.sleep(SLEEP);
                    } catch (InterruptedException ignored) {
                    }
                }
                utils.openLoadingDialog(loadingDialog, false);

            } else if (!confirmUsernameStr.equals(newUsernameStr)) {
                utils.showToast(this, getString(R.string.username_dont_match));
            } else if (oldusernameStr.equals(newUsernameStr)) {
                utils.showToast(this, getString(R.string.new_username_equal_old_username));
            } else
                utils.showToast(this, getString(R.string.something_went_wrong));


        }).start();
    }

    private void updateUsername() {
        SharedPreferences preferences = this.getSharedPreferences("userData", Context.MODE_PRIVATE);
        String usernamePreference = preferences.getString("username", null);
        if (usernamePreference != null) {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = getString(R.string.base_url) + "/user/updateUsername?username=" + usernamePreference;
            JsonObjectRequest jsonReq = null;
            JSONObject jsonObject = new JSONObject();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("username", newUsernameStr);
            editor.apply();


            try {
                jsonObject.put("oldValue", oldusernameStr);
                jsonObject.put("newValue", newUsernameStr);
                jsonReq = new JsonObjectRequest(Request.Method.PUT, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject res) {
                        responseDone = true;
                        utils.showToast(context, getString(R.string.update_username_ok));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        responseDone = true;

                        if (error.toString().equals("com.android.volley.AuthFailureError")) {
                            utils.showToast(context, getString(R.string.username_wrong));
                        } else
                            utils.showToast(context, getString(R.string.something_went_wrong));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            requestQueue.add(jsonReq);
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
