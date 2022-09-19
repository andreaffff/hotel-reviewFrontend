package com.example.hotel_reviewfrontend.signInAndLogin;

import android.content.Context;
import android.content.Intent;
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

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SigninActivity extends AppCompatActivity {

    private final int SLEEP = 500;
    Utils utils;
    Context context;
    private Toast toast;
    private TextInputLayout name;
    private TextInputLayout surname;
    private TextInputLayout email;
    private TextInputLayout address;
    private TextInputLayout phone;
    private TextInputLayout username;
    private TextInputLayout password;
    private TextInputLayout confirmPassword;
    private Button register;
    private String confirmPasswordString;
    private UserModel user;
    private LoadingDialog loadingDialog;
    private boolean requestDone = false;
    private boolean responseDone = false;
    private boolean responseSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_signin);
        Log.d("On create", "Entra qui");
        SharedPreferences preferences = this.getSharedPreferences("userData", Context.MODE_PRIVATE);
        if (preferences != null) {
            Log.d("sharedUsername", preferences.getString("username", ""));
            Log.d("sharedPassword", preferences.getString("password", ""));

        } else {
            Log.d("sharedPreferences", "Non ha nulla salvato");
        }
        this.initializeComponents();
        //this.setOnClickRegister();
    }


    private void initializeComponents() {

        Log.d("initializeComponent", "Entra qui");
        utils = new Utils();
        this.name = findViewById(R.id.name_txi);
        this.surname = findViewById(R.id.surname_txi);
        this.email = findViewById(R.id.email_txi);
        this.address = findViewById(R.id.address_txi);
        this.phone = findViewById(R.id.phone_txi);
        this.username = findViewById(R.id.username_txi);
        this.password = findViewById(R.id.password_txi);
        this.register = findViewById(R.id.enter_Btn);
        this.confirmPassword = findViewById(R.id.confirmPassword_txi);
        this.loadingDialog = new LoadingDialog(this);
        toast = new Toast(this);
        context = getApplicationContext();

        this.setOnClickRegister();
    }

    private void setOnClickRegister() {

        user = new UserModel();
        this.register.setOnClickListener(view -> {

            this.responseSuccess = false;
            this.responseDone = false;
            this.requestDone = false; //re-initialization

            user.setName(this.name.getEditText().getText().toString());
            user.setSurname(this.surname.getEditText().getText().toString());
            user.setEmail(this.email.getEditText().getText().toString());
            user.setAddress(this.address.getEditText().getText().toString());
            user.setPhone(this.phone.getEditText().getText().toString());
            user.setUsername(this.username.getEditText().getText().toString().toLowerCase(Locale.ROOT));
            user.setPassword(this.password.getEditText().getText().toString());
            confirmPasswordString = this.confirmPassword.getEditText()
                    .getText().toString();

            this.requestHandler();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        });
    }

    private void signIn() throws JSONException {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = getString(R.string.base_url) + "/user/signin";

        try {
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST, url, user.toJson(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    responseDone = true;
                    responseSuccess = true;
                    utils.showToast(context, getString(R.string.signin_ok));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                   /* int code = error.networkResponse.statusCode;
                    Log.v("status code", String.valueOf(code));*/
                    responseDone = true;
                    responseSuccess = false;

                    if (error.toString().equals("com.android.volley.ClientError")) {
                        utils.showToast(context, getString(R.string.Conflict));
                    } else if (error.toString().equals("com.android.volley.TimeoutError")) {
                        utils.showToast(context, getString(R.string.something_went_wrong));
                    }
                }
            });
            requestQueue.add(jsonReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestHandler() {
        if (this.checkForm()) {
            new Thread(() -> {
                utils.openLoadingDialog(loadingDialog, true);

                while (!this.requestDone) {
                    try {
                        Thread.sleep(SLEEP);
                    } catch (InterruptedException ignored) {
                    }
                    try {
                        this.signIn();
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
                utils.openLoadingDialog(loadingDialog, false);
                if (responseSuccess) {
                    SharedPreferences preferences = this.getSharedPreferences("userData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("username", user.getUsername());
                    editor.putString("password", user.getPassword());
                    editor.apply();
                }
                //TODO da collegare con la home o con la pagina di login
                     /* Codice per cancellare SharedPreferences
                    Context context =this;

                        */
            }).start();

        }
    }

    private boolean checkForm() {

        if (!user.getName().isEmpty() && !user.getSurname().isEmpty() && !user.getEmail().isEmpty()
                && !user.getAddress().isEmpty() && !user.getPhone().isEmpty()
                && !user.getUsername().isEmpty() && !user.getPassword().isEmpty()) {

            if (this.checkPassword(user)) {

                if (this.checkEmail(user)) {
                    return true;
                } else {
                    utils.showToast(context, getString(R.string.invalid_email));
                }
            }
        } else {
            utils.showToast(context, getString(R.string.empty_fields));
        }
        return false;

    }

    private boolean checkEmail(UserModel user) {

        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(user.getEmail());
        return matcher.matches();

    }

    private boolean checkPassword(UserModel user) {

        if (user.getPassword().length() >= 7) {
            if (confirmPasswordString.equals(user.getPassword())) {
                //TODO da aggiungere controllo su caratteri
                return true;
            } else {
                utils.showToast(context, getString(R.string.passwords_not_match));
            }
        } else {
            utils.showToast(context, getString(R.string.password_too_short));
        }
        return false;
    }


}
