package com.example.hotel_reviewfrontend.user;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.hotel_reviewfrontend.model.UserModel;
import com.example.hotel_reviewfrontend.utils.Utils;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class UpdateProfileActivity extends AppCompatActivity {
    private final static int SLEEP = 500;
    private TextInputLayout username;
    private TextInputLayout name;
    private TextInputLayout surname;
    private TextInputLayout email;
    private TextInputLayout phone;
    private TextInputLayout address;
    private String usernameStr;
    private String nameStr;
    private String surnameStr;
    private String emailStr;
    private String phoneStr;
    private String addressStr;
    private Button save;
    private  LoadingDialog loadingDialog;
    private boolean requestDone;
    private boolean responseDone;
    private boolean requestUsernameDone;
    private boolean responseUsernameDone;
    private String newUsername;
    private Utils utils;
    private Context context;
    private UserModel user;

    private void initializeComponents() {
        this.name = findViewById(R.id.name_txi);
        this.surname = findViewById(R.id.surname_txi);
        this.email = findViewById(R.id.email_txi);
        this.address = findViewById(R.id.address_txi);
        this.phone = findViewById(R.id.phone_txi);
        this.username = findViewById(R.id.username_txi);
        this.save = findViewById(R.id.saveBtn);

        context = getApplicationContext();

        this.loadingDialog = new LoadingDialog(this);
        this.requestDone = false;
        this.responseDone = false;
        this.requestUsernameDone = false;
        this.responseUsernameDone = false;
        user = new UserModel();
        utils = new Utils();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        this.initializeComponents();
        this.getFromIntent();
        this.setOnClickSave();
    }

    private void setOnClickSave() {
        this.save.setOnClickListener(view -> {
            requestHandler();

        });
    }
    private void getFromIntent(){
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            usernameStr = extras.getString("username");
            nameStr = extras.getString("name");
            surnameStr = extras.getString("surname");
            emailStr = extras.getString("email");
            phoneStr = extras.getString("phone");
            addressStr = extras.getString("address");

            //popolo i campi con i valori attuali dello user
            this.username.getEditText().setText(usernameStr);
            this.name.getEditText().setText(nameStr);
            this.surname.getEditText().setText(surnameStr);
            this.email.getEditText().setText(emailStr);
            this.phone.getEditText().setText(phoneStr);
            this.address.getEditText().setText(addressStr);

        }
    }
    private void updateValues(){
        SharedPreferences preferences = this.getSharedPreferences("userData", Context.MODE_PRIVATE);
        String usernamePreference = preferences.getString("username", null);
        if (usernamePreference != null) {
            Log.d("username!=", "entra");

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = getString(R.string.base_url) + "/user/updateUser?username=" + usernamePreference;
            JsonObjectRequest jsonReq = null;

            user.setName(name.getEditText().getText().toString());
            user.setSurname(surname.getEditText().getText().toString());
            user.setEmail(email.getEditText().getText().toString());
            user.setPhone(phone.getEditText().getText().toString());
            user.setAddress(address.getEditText().getText().toString());
            try {
                jsonReq = new JsonObjectRequest(Request.Method.PUT, url, user.toJson(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject res) {
                        utils.showToast(context,getString(R.string.update_user_ok));
                        responseDone = true;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        utils.showToast(context,getString(R.string.something_went_wrong));
                        Log.d("errore", "errore");
                        responseDone = true;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            requestQueue.add(jsonReq);
        } else {
            // manda a pagina di login
        }


    }
        //volley request tranne username

    private void updateUsername() {
        //TODO quando fai la modifica dello username cambia anche il valore nella sharedPreference
        //TODO mettere shared preferences nelle variabili di classe e inizializzarla insieme alle altre variabili
        SharedPreferences preferences = this.getSharedPreferences("userData", Context.MODE_PRIVATE);
        String usernamePreference = preferences.getString("username", null);
        if (usernamePreference != null) {
            Log.d("username!=", "entra");

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = getString(R.string.base_url) + "/user/updateUsername?username=" + usernamePreference;
            JsonObjectRequest jsonReq = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("oldValue",usernameStr);
                jsonObject.put("newValue",newUsername);

                jsonReq = new JsonObjectRequest(Request.Method.PUT, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject res) {
                        Log.d("res",res.toString());
                        utils.showToast(context,getString(R.string.update_username_ok));
                        responseDone = true;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        utils.showToast(context,getString(R.string.something_went_wrong));
                        Log.d("errore", "errore");
                        responseDone = true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            requestQueue.add(jsonReq);
        } else {
            // manda a pagina di login
        }
    }
    protected void requestHandler() { //creazione thread per richiesta e gestione caricamento
        responseDone = false;
        requestDone = false;
        requestUsernameDone = false;
        responseUsernameDone = false;

        new Thread(() -> {
            utils.openLoadingDialog(loadingDialog, true);

            while (!this.requestDone && !requestUsernameDone) {
                try {
                    Thread.sleep(SLEEP);
                } catch (InterruptedException ignored) {
                }
                this.updateValues();
                newUsername = username.getEditText().getText().toString();
                if(usernameStr != newUsername) {
                    this.updateUsername();
                    requestUsernameDone = true;
                } else {
                    requestUsernameDone = true;
                }

                requestDone = true;
            }
            while (!responseDone && !responseUsernameDone) {
                try {
                    Thread.sleep(SLEEP);
                } catch (InterruptedException ignored) {
                }
            }
            utils.openLoadingDialog(loadingDialog, false);

        }).start();
    }
}
