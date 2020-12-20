package com.example.cp;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    EditText textNum, textName;
    ListView List;
    ListAdapter ListAdapter;

    public static String server = "db.cp.by";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List = findViewById(R.id.listView);
    }

    public void onClickAdd(View view) {

    }

}
