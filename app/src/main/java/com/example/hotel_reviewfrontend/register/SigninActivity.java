package com.example.hotel_reviewfrontend.register;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hotel_reviewfrontend.LoadingDialog.LoadingDialog;
import com.example.hotel_reviewfrontend.R;
import com.example.hotel_reviewfrontend.model.UserModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SigninActivity extends AppCompatActivity {
    private final int SLEEP = 500;
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

    private LoadingDialog loadingDialog;
    private boolean requestDone = false;
    private boolean responseDone = false;
    private boolean alreadyExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_signin);
        Log.d("On create", "Entra qui");

        initializeComponent();
        //this.setOnClickRegister();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("On Start", "Entra qui");


    }

    private void initializeComponent() {
        Log.d("initializeComponent", "Entra qui");
        this.name = findViewById(R.id.name_txi);
        this.surname = findViewById(R.id.surname_txi);
        this.email = findViewById(R.id.email_txi);
        this.address = findViewById(R.id.address_txi);
        this.phone = findViewById(R.id.phone_txi);
        this.username = findViewById(R.id.username_txi);
        this.password = findViewById(R.id.password_txi);
        this.register = findViewById(R.id.registerBtn);
        this.confirmPassword = findViewById(R.id.confirmPassword_txi);


        this.loadingDialog = new LoadingDialog(this);

        this.setOnClickRegister();

    }

    private void setOnClickRegister() {
        this.responseDone = false;
        this.requestDone = false; //re-initialization

        UserModel user = new UserModel();
        this.register.setOnClickListener(view -> {

            user.setName(this.name.getEditText().getText().toString());
            user.setSurname(this.surname.getEditText().getText().toString());
            user.setEmail(this.email.getEditText().getText().toString());
            user.setAddress(this.address.getEditText().getText().toString());
            user.setPhone(this.phone.getEditText().getText().toString());
            user.setUsername(this.username.getEditText().getText().toString());
            user.setPassword(this.password.getEditText().getText().toString());
            confirmPasswordString = this.confirmPassword.getEditText()
                    .getText().toString();


            if (this.checkForm(user)) {
                new Thread(() -> {
                    this.openLoadingDialog(true);

                    while (!this.requestDone) {
                        try {
                            Thread.sleep(SLEEP);
                        } catch (InterruptedException ignored) {
                        }
                        try {
                            this.signIn(user);
                            requestDone = true;
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                    while(!responseDone){
                        Log.v("while response","entra qui");
                    }
                    this.openLoadingDialog(false);

                }).start();

            }
        });
    }

    private void signIn(UserModel user) throws JSONException {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = getString(R.string.base_url) + "/user/signin";
        Log.v("url:", url);
        try {
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST, url, user.toJson(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.v("output", "entra nella response");
                    Log.v("response", response.toString());
                    responseDone = true;
                    showToast(getString(R.string.signin_ok));


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                   /* int code = error.networkResponse.statusCode;
                    Log.v("status code", String.valueOf(code));*/
                    responseDone = true;

                    if (error.toString().equals("com.android.volley.ClientError")) {
                        showToast(getString(R.string.Conflict));
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


    private boolean checkForm(UserModel user) {

        if (!user.getName().isEmpty() && !user.getSurname().isEmpty() && !user.getEmail().isEmpty()
                && !user.getAddress().isEmpty() && !user.getPhone().isEmpty()
                && !user.getUsername().isEmpty() && !user.getPassword().isEmpty()) {
            if (this.checkPassword(user)) {
                if (this.checkEmail(user)) {
                    return true;
                } else {
                    this.showToast(getString(R.string.invalid_email));
                }
            }
        } else {
            this.showToast(getString(R.string.empty_fields));
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
                this.showToast(getString(R.string.passwords_not_match));
            }
        } else {
            this.showToast(getString(R.string.password_too_short));
        }
        return false;
    }

    private void showToast(String message) {
        this.runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }


}
