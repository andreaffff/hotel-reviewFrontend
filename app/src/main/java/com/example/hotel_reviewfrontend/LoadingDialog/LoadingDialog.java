package com.example.hotel_reviewfrontend.LoadingDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.hotel_reviewfrontend.R;

public class LoadingDialog {
    private final AlertDialog alertDialog;

    public LoadingDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.loading_dialog, null));
        builder.setCancelable(false);
        this.alertDialog = builder.create();
    }

    public void show() {
        this.alertDialog.show();
    }

    public void dismiss() {
        this.alertDialog.dismiss();
    }
}
