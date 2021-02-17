package com.example.cp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class shopActivity extends AppCompatActivity {
    private ListView listView;
    private CustomArrayAdapter customArrayAdapter;
    private List<ListItem> arrayListItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        arrayListItem = new ArrayList<>();
        listView = findViewById(R.id.ItemsList);
        customArrayAdapter = new CustomArrayAdapter(this,R.layout.item_list,arrayListItem,getLayoutInflater());
        listView.setAdapter(customArrayAdapter);

        ListItem items = new ListItem();
        items.setName("Mouseeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        items.setQuantity("10");
        items.setPrice("50");
        arrayListItem.add(items);
        items = new ListItem();
        items.setName("Keyboard");
        items.setQuantity("12");
        items.setPrice("55");
        arrayListItem.add(items);
        customArrayAdapter.notifyDataSetChanged();
    }

}