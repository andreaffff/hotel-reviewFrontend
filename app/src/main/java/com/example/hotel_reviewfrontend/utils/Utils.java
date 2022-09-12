package com.example.hotel_reviewfrontend.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotel_reviewfrontend.LoadingDialog.LoadingDialog;

public class Utils extends AppCompatActivity {

    public void showToast(Context context, String message) {
        this.runOnUiThread(() -> Toast.makeText(context, message, Toast.LENGTH_LONG).show());
    }

    public void openLoadingDialog(LoadingDialog loadingDialog, boolean flag) {
        this.runOnUiThread(() -> {
            if (flag)
                loadingDialog.show();
            else
                loadingDialog.dismiss();
        });
    }
}
