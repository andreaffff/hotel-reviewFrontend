package com.example.hotel_reviewfrontend.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.io.IOException;

public class UpdatePasswordActivity extends AppCompatActivity {
    TextInputLayout oldPassword;
    TextInputLayout newPassword;
    TextInputLayout confirmPassword;
    Button save;
    Utils utils;
    String oldPasswordStr;
    String newPasswordStr;
    String confirmPasswordStr;
    Context context;
    LoadingDialog loadingDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_update_activity);

        this.initializeComponents();
        this.setOnClickSave();
    }
    private void initializeComponents(){
        oldPassword = findViewById(R.id.oldPsw_txi);
        newPassword = findViewById(R.id.newPsw_txi);
        confirmPassword = findViewById(R.id.confirmNewPsw_txi);
        save = findViewById(R.id.changePassword);
        context = getApplicationContext();
        utils = new Utils();
        //loadingDialog = new LoadingDialog(this);
    }
    private void setOnClickSave(){

        this.save.setOnClickListener(view->{
            //utils.openLoadingDialog(loadingDialog,true);
            oldPasswordStr = oldPassword.getEditText().getText().toString();
            newPasswordStr = newPassword.getEditText().getText().toString();
            confirmPasswordStr = confirmPassword.getEditText().getText().toString();

            if(confirmPasswordStr.equals(newPasswordStr) && confirmPasswordStr.length()>=7){
                updatePassword();
            } else if(confirmPasswordStr.length()<7){
                utils.showToast(this,getString(R.string.password_too_short));
            }else if(!confirmPasswordStr.equals(newPasswordStr)){
                utils.showToast(this,getString(R.string.passwords_not_match));
            }else
                utils.showToast(this,getString(R.string.something_went_wrong));


        });
    }
    private void updatePassword(){
        SharedPreferences preferences = this.getSharedPreferences("userData", Context.MODE_PRIVATE);
        String usernamePreference = preferences.getString("username", null);
        if (usernamePreference != null) {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = getString(R.string.base_url) + "/user/updatePassword?username=" + usernamePreference;
            JsonObjectRequest jsonReq = null;
            JSONObject jsonObject = new JSONObject();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("password", newPasswordStr);
            editor.apply();


            try {
                jsonObject.put("oldValue",oldPasswordStr);
                jsonObject.put("newValue",newPasswordStr);
                jsonReq = new JsonObjectRequest(Request.Method.PUT, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject res) {
                        //utils.openLoadingDialog(loadingDialog,false);
                        utils.showToast(context, getString(R.string.update_password_ok));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
