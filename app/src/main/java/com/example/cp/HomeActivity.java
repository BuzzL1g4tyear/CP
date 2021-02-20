package com.example.cp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class HomeActivity extends AppCompatActivity {

    private static final String MY_SETTINGS = "my_settings";
    boolean hasLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SharedPreferences sp = getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        hasLogged = sp.getBoolean("hasLogged", false);
        Intent intent;

        if (hasConnection(this)) {
            if (hasLogged) {
                intent = new Intent(this, DbActivity.class);
            } else {
                intent = new Intent(this, LoginActivity.class);
            }
            startActivity(intent);
        } else {
            Dialog("Ошибка!", "Вы не подключились к Интернету!");
        }
    }
    public void onClickReg(View view) {
        Dialog("Activity",view.toString());
    }

    public void onClickShop(View view){
        Intent intent = new Intent(this,shopActivity.class);
        startActivity(intent);
    }

    public void onClickDB(View view) {
        Dialog("Activity",view.toString());
    }

    public void Snack(String mes) {
        View activity_home = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar
                .make(activity_home, mes, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public static boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    private void Dialog(String title, String mes){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(mes);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void Toast(String mess) {
        Toast.makeText(this, mess, Toast.LENGTH_LONG).show();
    }
}