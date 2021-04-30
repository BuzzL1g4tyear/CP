package com.example.cp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Objects;

public class BasketActivity extends AppCompatActivity {

    private BaskedAdapter baskedAdapter;
    Button btnUPD;
    private static final String MY_SETTINGS = "my_settings";
    String name;
    java.util.Date utilDate;
    java.sql.Timestamp sqlDate;
    String catNum;
    RecyclerView basketRV;
    RecyclerView.LayoutManager layoutManager;

    public static final String SHOWBASKETQ = "SELECT Группа, Бренд, ShoppingCart.КатНомер, Наименование, [Ваша цена (BYN c НДС)], Количество " +
            "FROM ShoppingCart " +
            "JOIN Stock S ON ShoppingCart.КатНомер = S.КатНомер " +
            "WHERE Клиент = (SELECT LoginId FROM LoginData WHERE Login = ?)";
    public static final String DELETEFROMBASKET = "DELETE FROM ShoppingCart " +
            "WHERE КатНомер = ? AND Клиент = (SELECT LoginId FROM LoginData WHERE Login = ?)";
    public static final String ADDTOORDER = "INSERT INTO AndroidOrders (CustomerId, OrderStatus, OrderDate)" +
            " VALUES ((SELECT LoginId FROM LoginData WHERE Login = ?),?,?)";
    public static final String ADDTOORDERITEM = "INSERT INTO AndroidOrdersItems ( odCode, odName, odQuant, odStatus, odOrderId)" +
            " VALUES (?,(SELECT Наименование FROM Stock WHERE КатНомер = ?),?,?,?)";

    public static final String ID = "SELECT OrderId FROM AndroidOrders WHERE OrderDate = ?";

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
        new ItemTouchHelper(callback).attachToRecyclerView(basketRV);

        Thread thread = new Thread(showBasket);
        thread.start();
        baskedAdapter.notifyDataSetChanged();
        basketRV.setLayoutManager(layoutManager);
        basketRV.setAdapter(baskedAdapter);

        Toolbar toolbarBasket = findViewById(R.id.toolbarBasket);
        toolbarBasket.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(toolbarBasket);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        utilDate = new java.util.Date();
        sqlDate = new java.sql.Timestamp(utilDate.getTime());
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

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
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

    private void createOrder() {
        Thread thread0 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ps = connection.prepareStatement(ADDTOORDER);

                    ps.setString(1, name);//custumerID
                    ps.setInt(2, 1);//orderSt
                    ps.setTimestamp(3, sqlDate);//date
                    ps.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        thread0.start();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int idOrder = 0;
                try {

                    ps1 = connection.prepareStatement(ID);
                    ps1.setTimestamp(1, sqlDate);
                    rs = ps1.executeQuery();
                    while (rs.next()) {
                        idOrder = rs.getInt(1);
                    }

                    ps1 = connection.prepareStatement(ADDTOORDERITEM);
                    for (int i = 0; i < catNumList().size(); i++) {
                        ps1.setString(1, catNumList().get(i));//catNum
                        ps1.setString(2, catNumList().get(i));//name item
                        ps1.setFloat(3, quantityList().get(i));//qan item
                        ps1.setInt(4, 1);//status
                        ps1.setInt(5, idOrder);//order id
                        ps1.execute();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public List<String> catNumList() {
        List<String> list = new ArrayList<>();
        if (!arrayListItem.isEmpty()) {
            for (int i = 0; i < arrayListItem.size(); i++) {
                ListItem listItemMain = arrayListItem.get(i);
                list.add(listItemMain.getCatNum());
            }
        } else {
            Toast.makeText(this, "Заказ пустой", Toast.LENGTH_SHORT).show();
        }
        return list;
    }

    public List<Integer> quantityList() {
        List<Integer> list = new ArrayList<>();
        if (!arrayListItem.isEmpty()) {
            for (int i = 0; i < arrayListItem.size(); i++) {
                ListItem listItemMain = arrayListItem.get(i);
                list.add(listItemMain.getQuantity());
            }
        }
        return list;
    }

    private void replaceFragment(Fragment fragment){
        ConstraintLayout layout = findViewById(R.id.data_container);
        layout.removeAllViews();
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.data_container, fragment)
                .commit();
    }

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
                //createOrder();
                Toast.makeText(this, "Заказ отправлен", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.basketOrder:
                replaceFragment(new OrderFragment());
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}