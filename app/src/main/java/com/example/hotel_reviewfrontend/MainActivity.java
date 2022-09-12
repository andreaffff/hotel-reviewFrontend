package com.example.hotel_reviewfrontend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hotel_reviewfrontend.signInAndLogin.LoginActivity;
import com.example.hotel_reviewfrontend.user.MyProfileActivity;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen_activity);

    }

    @Override
    protected void onStart(){
        super.onStart();
        //TODO non si vede lo splashscreen
        SharedPreferences preferences = getSharedPreferences("userData", Context.MODE_PRIVATE);
        String usernamePreference = preferences.getString("username", null);
        String passwordPreference = preferences.getString("password", null);
        Log.d("passwordPreference",passwordPreference);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = getString(R.string.base_url) + "/user/login";
        JSONObject jsonObject = new JSONObject();
        Context context = getApplicationContext();


        try {
            jsonObject.put("username", usernamePreference);
            jsonObject.put("password", passwordPreference);
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Intent intent = new Intent(context, MyProfileActivity.class); //TODO mettere home
                    startActivity(intent);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);

                }
            });
            requestQueue.add(jsonReq);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}