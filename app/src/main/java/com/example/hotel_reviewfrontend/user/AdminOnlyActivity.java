package com.example.hotel_reviewfrontend.user;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hotel_reviewfrontend.LoadingDialog.LoadingDialog;
import com.example.hotel_reviewfrontend.R;
import com.example.hotel_reviewfrontend.utils.Utils;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminOnlyActivity extends AppCompatActivity{
    private final int SLEEP = 500;
    private Utils utils;
    private TextInputLayout username;
    private Button enter;
    private CardView card;
    private TextView usernameOuput;
    private Switch role;
    private Button delete;
    private Button profile;
    private Button save;
    private Context context;
    private LoadingDialog loadingDialog;
    private boolean requestDone;
    private boolean responseDone;
    private boolean foundUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_only_activity);
        this.initializeComponents();
    }

    private void initializeComponents() {
        this.username = findViewById(R.id.username_txi);
        this.usernameOuput = findViewById(R.id.username_output);
        this.enter = findViewById(R.id.enterBtn);
        this.card = findViewById(R.id.cardView);
        this.role = findViewById(R.id.roleSwitch);
        this.delete = findViewById(R.id.deleteUser);
        this.profile = findViewById(R.id.goToProfile);
        this.save = findViewById(R.id.saveBtn);
        this.loadingDialog = new LoadingDialog(this);
        this.requestDone = false;
        this.responseDone = false;
        utils = new Utils();
        context = getApplicationContext();
        card.setVisibility(View.INVISIBLE);
        this.setOnClickEnter();
        this.setOnClickDelete();
    }

    private void requestHandler() {
        responseDone = false;
        requestDone = false;

        new Thread(() -> {
            utils.openLoadingDialog(loadingDialog, true);

            while (!this.requestDone) {
                try {
                    Thread.sleep(SLEEP);
                } catch (InterruptedException ignored) {
                }
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

    private void findAndAssignValues() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = getString(R.string.base_url) + "/user/?username=" + username.getEditText().getText().toString();
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject res) {
                Log.d("res", res.toString());
                responseDone = true;
                try {
                    usernameOuput.setText(res.getString("username"));
                    Log.i("usernameOutput", "usernameOutput");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                utils.showToast(context, getString(R.string.something_went_wrong));
                Log.d("errore", "errore");

                responseDone = true;
            }
        });
        requestQueue.add(jsonReq);

}
protected void findAndDeleteUser(){
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    String url = getString(R.string.base_url) + "/user/?username=" + usernameOuput.getText().toString();
    JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject res) {
            responseDone = true;
            try {

                utils.showToast(context, getString(R.string.delete));
                Log.i("usernameOutput", "usernameOutput");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            utils.showToast(context, getString(R.string.something_went_wrong));
            Log.d("errore", "errore");

            responseDone = true;
        }
    });
    requestQueue.add(jsonReq);

}

    protected void UpdateRole(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = getString(R.string.base_url) + "/user/?username=" + username.getEditText().getText().toString();
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject res) {
                Log.d("res", res.toString());
                responseDone = true;
                try {
                    utils.showToast(context, getString(R.string.delete));
                    Log.i("usernameOutput", "usernameOutput");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                utils.showToast(context, getString(R.string.something_went_wrong));
                Log.d("errore", "errore");

                responseDone = true;
            }
        });
        requestQueue.add(jsonReq);

    }

    private void setOnclickSave(){
        this.save.setOnClickListener(view -> {
            card.setVisibility(View.INVISIBLE);
        });
    }

    private void setOnClickEnter() {
        this.enter.setOnClickListener(view -> {
            findUser();
            if(foundUser){
                card.setVisibility(View.VISIBLE);
                requestHandler();
                findAndAssignValues();
            }
        });
    }
    //TODO ON ERROR CONTROLLARE L ERRORE SE è CLIENT/SERVER

    private void setOnClickDelete() {
        this.delete.setOnClickListener(view -> {
            card.setVisibility(View.INVISIBLE);
            card.setClickable(false);
            findAndDeleteUser();
        });
    }

    protected void findUser(){

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = getString(R.string.base_url) + "/user/?username=" + username.getEditText().getText().toString();
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject res) {
                Log.d("res", res.toString());
                responseDone = true;
                try {
                    Log.i("usernameOutput", "usernameOutput");

                } catch (Exception e) {
                    e.printStackTrace();
                }
                foundUser = true;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.toString().equals("com.android.volley.ServerError")) {
                    utils.showToast(context, getString(R.string.user_not_found));
                } else if (error.toString().equals("com.android.volley.TimeoutError")) {
                    utils.showToast(context, getString(R.string.something_went_wrong));
                }
                Log.d("errore", "errore");

                responseDone = true;
                foundUser = false;
            }
        });
        requestQueue.add(jsonReq);

    }

}