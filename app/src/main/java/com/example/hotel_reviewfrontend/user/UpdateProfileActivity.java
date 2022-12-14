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
import com.example.hotel_reviewfrontend.model.UserModel;
import com.example.hotel_reviewfrontend.signInAndLogin.LoginActivity;
import com.example.hotel_reviewfrontend.utils.Utils;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {
    private final static int SLEEP = 500;
    private TextInputLayout name;
    private TextInputLayout surname;
    private TextInputLayout email;
    private TextInputLayout phone;
    private TextInputLayout address;
    private String nameStr;
    private String surnameStr;
    private String emailStr;
    private String phoneStr;
    private String addressStr;
    private Button save;
    private Button passwordBtn;
    private Button usernameBtn;
    private LoadingDialog loadingDialog;
    private boolean requestDone;
    private boolean responseDone;
    private Utils utils;
    private Context context;
    private UserModel user;

    private void initializeComponents() {
        this.name = findViewById(R.id.name_txi);
        this.surname = findViewById(R.id.surname_txi);
        this.email = findViewById(R.id.email_txi);
        this.address = findViewById(R.id.address_txi);
        this.phone = findViewById(R.id.phone_txi);
        this.save = findViewById(R.id.saveBtn);
        this.passwordBtn = findViewById(R.id.changePassword);
        this.usernameBtn = findViewById(R.id.changeUsername);

        context = getApplicationContext();

        this.loadingDialog = new LoadingDialog(this);
        this.requestDone = false;
        this.responseDone = false;

        user = new UserModel();
        utils = new Utils();

        this.getFromIntent();
        this.setOnClickSave();
        this.setOnClickPassword();
        this.setOnClickUsername();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile_activity);

        this.initializeComponents();
    }

    private void setOnClickSave() {
        this.save.setOnClickListener(view -> {

            requestHandler();
        });
    }

    private void getFromIntent() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            nameStr = extras.getString("name");
            surnameStr = extras.getString("surname");
            emailStr = extras.getString("email");
            phoneStr = extras.getString("phone");
            addressStr = extras.getString("address");

            //popolo i campi con i valori attuali dello user
            this.name.getEditText().setText(nameStr);
            this.surname.getEditText().setText(surnameStr);
            this.email.getEditText().setText(emailStr);
            this.phone.getEditText().setText(phoneStr);
            this.address.getEditText().setText(addressStr);

        }
    }

    private void updateValues() { //volley request tranne username
        SharedPreferences preferences = this.getSharedPreferences("userData", Context.MODE_PRIVATE);
        String usernamePreference = preferences.getString("username", null);
        if (usernamePreference != null) {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = getString(R.string.base_url) + "/user/updateUser?username=" + usernamePreference;
            JsonObjectRequest jsonReq = null;

            try {
                jsonReq = new JsonObjectRequest(Request.Method.PUT, url, user.toJson(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject res) {
                        utils.showToast(context, getString(R.string.update_user_ok));
                        responseDone = true;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        utils.showToast(context, getString(R.string.something_went_wrong));
                        Log.d("errore", "errore");
                        responseDone = true;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            requestQueue.add(jsonReq);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            responseDone = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.deleteSharedPreferences("userData");
            } else
                context.getSharedPreferences("userData", Context.MODE_PRIVATE).edit().clear().apply();
        }
    }

    protected void requestHandler() { //creazione thread per richiesta e gestione caricamento
        responseDone = false;
        requestDone = false;
        user.setName(name.getEditText().getText().toString());
        user.setSurname(surname.getEditText().getText().toString());
        user.setEmail(email.getEditText().getText().toString());
        user.setPhone(phone.getEditText().getText().toString());
        user.setAddress(address.getEditText().getText().toString());

        if (checkForm()) {
            new Thread(() -> {
                utils.openLoadingDialog(loadingDialog, true);

                while (!this.requestDone ) {
                    try {
                        Thread.sleep(SLEEP);
                    } catch (InterruptedException ignored) {
                    }
                    this.updateValues();
                    requestDone = true;
                }
                while (!responseDone) {
                    try {
                        Thread.sleep(SLEEP);
                    } catch (InterruptedException ignored) {
                    }
                }
                utils.openLoadingDialog(loadingDialog, false);
                Intent intent = new Intent(this, MyProfileActivity.class);
                startActivity(intent);
            }).start();
        }
    }

    private boolean checkForm() {

        if (!user.getName().isEmpty() && !user.getSurname().isEmpty() && !user.getEmail().isEmpty()
                && !user.getAddress().isEmpty() && !user.getPhone().isEmpty() ) {

            if (this.checkEmail(user)) {
                return true;
            } else {
                utils.showToast(context, getString(R.string.invalid_email));
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

    private void setOnClickPassword() {
        this.passwordBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, UpdatePasswordActivity.class);
            intent.putExtra("username", user.getUsername());
            startActivity(intent);
        });
    }

    private void setOnClickUsername() {
        this.usernameBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, UpdateUsernameActivity.class);
            intent.putExtra("username", user.getUsername());
            startActivity(intent);
        });
    }



}


