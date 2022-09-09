package com.example.hotel_reviewfrontend.user;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotel_reviewfrontend.LoadingDialog.LoadingDialog;
import com.example.hotel_reviewfrontend.R;
import com.example.hotel_reviewfrontend.utils.Utils;
import com.google.android.material.textfield.TextInputLayout;

public class UpdateProfileActivity extends AppCompatActivity {
    TextInputLayout username;
    TextInputLayout name;
    TextInputLayout surname;
    TextInputLayout email;
    TextInputLayout phone;
    TextInputLayout address;
    Button save;
    LoadingDialog loadingDialog;
    boolean requestDone;
    boolean responseDone;
    String oldUsername;
    String newUsername;
    Utils utils;

    private void initializeComponents() {
        this.name = findViewById(R.id.name_txi);
        this.surname = findViewById(R.id.surname_txi);
        this.email = findViewById(R.id.email_txi);
        this.address = findViewById(R.id.address_txi);
        this.phone = findViewById(R.id.phone_txi);
        this.username = findViewById(R.id.username_txi);
        this.save = findViewById(R.id.saveBtn);

        this.loadingDialog = new LoadingDialog(this);
        this.requestDone = false;
        this.responseDone = false;
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

        });
    }
    private void getFromIntent(){
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            this.username.getEditText().setText(extras.getString("username"));
            this.name.getEditText().setText(extras.getString("name"));
            this.surname.getEditText().setText(extras.getString("surname"));
            this.email.getEditText().setText(extras.getString("email"));
            this.phone.getEditText().setText(extras.getString("phone"));
            this.address.getEditText().setText(extras.getString("address"));
        }
    }
    private void updateValues(){
        //volley request tranne username
    }
    private void updateUsername(){

    }
}
