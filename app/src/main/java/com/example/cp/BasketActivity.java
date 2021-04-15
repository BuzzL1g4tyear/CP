package com.example.cp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BasketActivity extends AppCompatActivity {

    private BaskedAdapter baskedAdapter;
    Button btnUPD;
    private static final String MY_SETTINGS = "my_settings";
    String name;
    String catNum;
    RecyclerView basketRV;
    RecyclerView.LayoutManager layoutManager;

    public static final String SHOWBASKETQ = "SELECT Группа, Бренд, ShoppingCart.КатНомер, Наименование, [Ваша цена (BYN c НДС)], Количество " +
            "FROM ShoppingCart " +
            "JOIN Stock S ON ShoppingCart.КатНомер = S.КатНомер " +
            "WHERE Клиент = (SELECT LoginId FROM LoginData WHERE Login = ?)";
    public static final String DELETEFROMBASKET = "DELETE FROM ShoppingCart " +
            "WHERE КатНомер = ? AND Клиент = (SELECT LoginId FROM LoginData WHERE Login = ?)";

    ConnectionHelper connect = new ConnectionHelper();
    Connection connection = connect.getCon();
    PreparedStatement ps = null;
    PreparedStatement ps1 = null;
    ResultSet rs = null;


    private List<ListItem> arrayListItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        setTitle(R.string.labelBasket);

        SharedPreferences sp = getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        name = sp.getString("name", "null");

        arrayListItem = new ArrayList<>();
        basketRV = findViewById(R.id.basketListView);

        baskedAdapter = new BaskedAdapter(arrayListItem);
        basketRV.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        basketRV.setLayoutManager(layoutManager);
        new ItemTouchHelper(callback).attachToRecyclerView(basketRV);
        basketRV.setAdapter(baskedAdapter);

        Thread thread = new Thread(showBasket);
        thread.start();
        baskedAdapter.notifyDataSetChanged();

        Toolbar toolbarBasket = findViewById(R.id.toolbarBasket);
        toolbarBasket.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(toolbarBasket);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    Runnable showBasket = new Runnable() {
        ListItem listItem;

        @Override
        public void run() {
            try {
                ps = connection.prepareStatement(SHOWBASKETQ);
                ps.setString(1, name);
                rs = ps.executeQuery();

                if (rs != null) {
                    int columnCount = rs.getMetaData().getColumnCount();
                    while (rs.next()) {
                        ListItem items = new ListItem();

                        for (int i = columnCount; i <= columnCount; i++) {
                            items.setGroup(rs.getString(1).trim());
                            items.setBrand(rs.getString(2).trim());
                            items.setCatNum(rs.getString(3).trim());
                            items.setName(rs.getString(4).trim());
                            items.setPrice(rs.getFloat(5));
                            items.setQuantity(rs.getInt(6));
                            arrayListItem.add(items);

                        }
                        listItem = items;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (ps != null) ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
    };

    public void openDialog(String title) {
        Dialog dialog = new Dialog(BasketActivity.this, R.style.CustomStyleDialog);
        LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_basket, null);

        btnUPD = view.findViewById(R.id.btnUpd);

        dialog.setContentView(view);
        dialog.setTitle(title);
        dialog.show();
    }

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            ListItem item = arrayListItem.get(viewHolder.getAdapterPosition());
            catNum = item.getCatNum();
            Thread thread = new Thread(deleteItem);
            thread.start();
            arrayListItem.remove(viewHolder.getAdapterPosition());
            baskedAdapter.notifyDataSetChanged();
        }
    };

    Runnable deleteItem = new Runnable() {
        @Override
        public void run() {
            try {
                ps1 = connection.prepareStatement(DELETEFROMBASKET);

                ps1.setString(1, catNum);
                ps1.setString(2, name);

                ps1.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (ps1 != null) ps1.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
    };

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.basket_toolbar, menu);

        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Поиск");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            BaskedAdapter adapter = new BaskedAdapter(arrayListItem);

            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                basketRV.setAdapter(adapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                basketRV.setAdapter(adapter);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addCart:
                Toast.makeText(this, "Отправить заказ", Toast.LENGTH_SHORT).show();
                return true;
            default:
            return super.onOptionsItemSelected(item);
        }
    }
}