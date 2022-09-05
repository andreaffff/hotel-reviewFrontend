package com.example.hotel_reviewfrontend.register;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SigninActivity extends AppCompatActivity  {
    private final int SLEEP = 500;
    private EditText name;
    private EditText surname;
    private EditText email;
    private EditText address;
    private EditText phone;
    private EditText username;
    private EditText password;
    private Button register;

    private String nameValue;
    private String surnameValue;
    private String emailValue;
    private String addressValue;
    private String phoneValue;
    private String usernameValue;
    private String passwordValue;

    private LoadingDialog loadingDialog;
    private boolean requestDone = false;
    private boolean requestError = false;
    private boolean alreadyExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_signin);

        this.initializeComponent();
        this.setOnClickRegister();
    }

    private void initializeComponent() {
        this.name = findViewById(R.id.name_txi);
        this.surname = findViewById(R.id.surname_txi);
        this.email = findViewById(R.id.email_txi);
        this.address =  findViewById(R.id.address_txi);
        this.phone =   findViewById(R.id.phone_txi);
        this.username =  findViewById(R.id.username_txi);
        this.password =  findViewById(R.id.password_txi);
        this.register = findViewById(R.id.registerBtn);

        //this.loadingDialog = new LoadingDialog(this);

        this.setOnClickRegister();

    }

    private void setOnClickRegister() {

    this.register.setOnClickListener(view -> {
        this.nameValue = this.name.getText().toString();
        this.surnameValue = this.surname.getText().toString();
        this.emailValue = this.email.getText().toString();
        this.addressValue = this.address.getText().toString();
        this.phoneValue = this.phone.getText().toString();
        this.usernameValue = this.username.getText().toString();
        this.passwordValue = this.password.getText().toString();

        if (this.checkForm()) {
        new Thread(() -> {
            this.openLoadingDialog(true);
            this.checkUsername();
            while(!this.requestDone){
                try {
                    Thread.sleep(SLEEP);
                }catch (InterruptedException ignored){
                }

                this.openLoadingDialog(false);
                if (!this.requestError) {
                    if (!this.alreadyExists) {
                        try {
                            this.signIn();
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

    private void signIn() throws JSONException {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = getString(R.string.base_url) + "/user/signin";
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject res) {
                try {
                   Log.d("output",res.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }



        },  new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("onErrorResponse", "Entra qui");


            }

        });
        requestQueue.add(jsonReq);
    }

    private void checkUsername() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = getString(R.string.base_url) + "/user/" + this.usernameValue;
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


    private boolean checkForm() {
        if(!this.nameValue.isEmpty() && !this.surnameValue.isEmpty() &&  !this.emailValue.isEmpty()
                && !this.addressValue.isEmpty() && !this.phoneValue.isEmpty()
                && !this.usernameValue.isEmpty() && !this.passwordValue.isEmpty() ){
            if(this.chekEmail()){
                 return true;
            }else{
                this.showToast(getString(R.string.invalid_email));
            }
        }else {
            this.showToast(getString(R.string.empty_fields));
        }
        return false;
    }

    private boolean chekEmail() {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(this.emailValue);
        return matcher.matches();
    }

    private void showToast(String message) {
        this.runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }


}
