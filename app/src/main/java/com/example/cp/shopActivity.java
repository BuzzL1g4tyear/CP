package com.example.cp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class shopActivity extends AppCompatActivity {
    private ListView listView;
    private CustomArrayAdapter customArrayAdapter;
    private List<ListItem> arrayListItem;

    public Button btnMin, btnPlu, btnOK;
    public EditText text;

    private int count = 0;
    public String quantity;
    public String nameItem;
    public int valueItem;

    public static final String SHOWQ = "SELECT * FROM STOCK";

    ConnectionHelper connect = new ConnectionHelper();
    Connection connection = connect.getCon();
    Statement st = null;
    ResultSet rs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        arrayListItem = new ArrayList<>();
        listView = findViewById(R.id.ItemsList);

        customArrayAdapter = new CustomArrayAdapter(this, R.layout.item_list, arrayListItem, getLayoutInflater());
        listView.setAdapter(customArrayAdapter);

        ShowCat cat = new ShowCat();
        cat.execute();

        Toolbar toolbarShop = findViewById(R.id.toolbar);
        setSupportActionBar(toolbarShop);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nameItem = arrayListItem.get(position).getName();
                valueItem = Integer.parseInt(arrayListItem.get(position).getQuantity());

                Log.d("MyLog", nameItem);

                openDialog(nameItem, valueItem);
            }
        });
    }

    public void openDialog(String title, int value) {
        Dialog dialog = new Dialog(shopActivity.this, R.style.CustomStyleDialog);
        LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_blank, null);

        text = view.findViewById(R.id.valueNumb);
        btnMin = view.findViewById(R.id.btnMinus);
        btnPlu = view.findViewById(R.id.btnPlus);
        btnOK = view.findViewById(R.id.btnOK);

        text.setText("0");
        btnMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(text.getText().toString()) > 0) {

                    if (text.getText().toString().trim().isEmpty()) {
                        text.setText("0");
                    }

                    count = Integer.parseInt(text.getText().toString());
                    count = count - 1;
                    String translate = String.valueOf(count);
                    text.setText(translate);
                } else {
                    Toast("Нельзя выбрать число меньше!");
                }
            }
        });
        btnPlu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = Integer.parseInt(text.getText().toString());
                if (value > count) {
                    count = count + 1;
                    String translate = String.valueOf(count);
                    text.setText(translate);
                } else {
                    Toast("Такого количества товара нет!");
                }
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = text.getText().toString();
                if (CheckFields(quantity)) {
                    count = Integer.parseInt(text.getText().toString());
                    if (value > count) {
                        Toast(nameItem + " добавленно в корзину " + quantity);
                        dialog.dismiss();
                    } else {
                        Toast("Такого количества товара нет!");
                    }
                }
            }
        });
//фильтр, корзина
        dialog.setContentView(view);
        dialog.setTitle(title);
        dialog.show();
    }

    public boolean CheckFields(String quantity) {
        boolean field = true;

        if (quantity == null || quantity.isEmpty()) {
            Toast("Пустое поле");
            field = false;
        }
        return field;
    }

    private void Toast(String mes) {
        Toast.makeText(this, mes,
                Toast.LENGTH_LONG).show();
    }

    @SuppressLint("StaticFieldLeak")
    public final class ShowCat extends AsyncTask<String, Void, ListItem> {

        ListItem listItem;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected ListItem doInBackground(String... query) {
            try {
                st = connection.createStatement();
                rs = st.executeQuery(SHOWQ);

                if (rs != null) {
                    int columnCount = rs.getMetaData().getColumnCount();
                    while (rs.next()) {
                        ListItem items = new ListItem();

                        for (int i = columnCount; i <= columnCount; i++) {
                            items.setName(rs.getString(2).trim());
                            items.setQuantity(rs.getString(3));
                            items.setPrice(rs.getString(4));
                            arrayListItem.add(items);
                            customArrayAdapter.notifyDataSetChanged();
                        }
                        listItem = items;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (st != null) st.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
            return listItem;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.items_toolbar, menu);

        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Поиск");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            CustomArrayAdapter adapter = new CustomArrayAdapter(shopActivity.this,
                    R.layout.item_list, arrayListItem, getLayoutInflater());

            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                listView.setAdapter(adapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                listView.setAdapter(adapter);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}