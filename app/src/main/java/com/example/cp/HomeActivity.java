package com.example.cp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class HomeActivity extends AppCompatActivity {

    private static final String MY_SETTINGS = "my_settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Snack("GG");
        SharedPreferences sp = getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);

        boolean hasVisited = sp.getBoolean("hasVisited", false);

        if (!hasVisited) {
            Toast("GG");
            Snack("GG");
            SharedPreferences.Editor e = sp.edit();
            e.putBoolean("hasVisited", true);
            e.apply(); // не забудьте подтвердить изменения
        }else {
            Toast("123");
        }
    }

    public void onClickReg(View view) {
        Snack("GG");
        Intent intent = new Intent(this, LoginActivity.class);

        startActivity(intent);
    }

    public void onClickDB(View view) {
        Intent intent = new Intent(this, DbActivity.class);
        startActivity(intent);
    }

    public void Snack(String mes) {
        View activity_home = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar
                .make(activity_home, mes, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void Toast(String mess) {
        Toast.makeText(this, mess, Toast.LENGTH_LONG).show();
    }
}