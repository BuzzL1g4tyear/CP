package com.example.cp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class BasketActivity extends AppCompatActivity {

    public static final String SHOWBASKETQ = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
    }
}