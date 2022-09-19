package com.example.hotel_reviewfrontend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hotel_reviewfrontend.review.HomeActivity;
import com.example.hotel_reviewfrontend.signInAndLogin.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    //TODO qui e nel login va inserito il ruolo nelle shared preference
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen_activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = this.getSharedPreferences("userData", Context.MODE_PRIVATE);
        String usernamePreference = preferences.getString("username", null);
        String passwordPreference = preferences.getString("password", null);
        //TODO controllo ruolo attraverso risposta login del backend
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
                    SharedPreferences preferences = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    try {
                        editor.putString("role", response.getString("role"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    editor.apply();
                    Intent intent = new Intent(context, HomeActivity.class);
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
    @Override
    protected void onRestart() {
        super.onRestart();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
    }

}