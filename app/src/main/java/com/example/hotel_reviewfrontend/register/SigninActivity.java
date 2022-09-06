package com.example.hotel_reviewfrontend.register;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
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
    private boolean requestError = false;
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
                    //this.checkUsername();
                    while (!this.requestDone) {
                        try {
                            Thread.sleep(SLEEP);
                        } catch (InterruptedException ignored) {
                        }

                        this.openLoadingDialog(false);
                        if (!this.requestError) {
                            if (!this.alreadyExists) {
                                try {
                                    this.signIn(user);
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                this.alreadyExists = false;
                                this.showToast(getString(R.string.username_already_used));
                            }
                        } else {
                            this.showToast(getString(R.string.something_went_wrong));
                        }

                        this.requestDone = false;
                        this.requestError = false;
                    }
                }).start();
            }

        });
    }

    private void signIn(UserModel user) throws JSONException {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = getString(R.string.base_url) + "/user/signin";

        try {
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST, url,user.toJson(), res -> {
                Log.d("output", res.toString());
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("onErrorResponse", "Entra qui");
            }

        });
        requestQueue.add(jsonReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkUsername() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getString(R.string.base_url) + "/user/" ;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    if (!response.equals("0"))
                        this.alreadyExists = true;
                    this.requestDone = true;
                },
                error -> {
                    this.requestError = true;
                    this.requestDone = true;
                }
        );
        requestQueue.add(stringRequest);
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
                && !user.getUsername().isEmpty() && !user.getPassword().isEmpty()
                ) {
            if (this.checkEmail(user)) {
                return true;
            } else {
                this.showToast(getString(R.string.invalid_email));
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

    private void showToast(String message) {
        this.runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }


}
