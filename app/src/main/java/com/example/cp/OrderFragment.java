package com.example.cp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderFragment extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    ConnectionHelper connect = new ConnectionHelper();
    Connection connection = connect.getCon();
    private List<ListItem> arrayListItem;
    String name;

    private static final String MY_SETTINGS = "my_settings";
    public static final String SHOWQ = "SELECT OrderDate, " +
            "AndroidOrdersItems.odCode, " +
            "AndroidOrdersItems.odName, " +
            "AndroidOrdersItems.odQuant, " +
            "AndroidOrdersItemsStatus.Status, " +
            "AndroidOrdersStatus.Status, " +
            "AndroidOrders.OrderId " +
            "FROM AndroidOrders " +
            "JOIN AndroidOrdersItems ON odOrderId = AndroidOrders.OrderId " +
            "JOIN AndroidOrdersStatus ON AndroidOrders.OrderStatus = AndroidOrdersStatus.Id " +
            "JOIN AndroidOrdersItemsStatus ON AndroidOrdersItems.odStatus = AndroidOrdersItemsStatus.Id " +
            "WHERE CustomerId = (SELECT LoginId FROM LoginData WHERE Login = ?)";

    PreparedStatement ps = null;
    ResultSet rs = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_order);
        setTitle(getString(R.string.orders));
        SharedPreferences sp = getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        name = sp.getString("name", "null");
        recyclerView = findViewById(R.id.rv_order);

        Toolbar toolbarBasket = findViewById(R.id.toolbar_order);
        toolbarBasket.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(toolbarBasket);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        arrayListItem = new ArrayList<>();
        adapter = new OrderAdapter(arrayListItem);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        runOnUiThread(runnable);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                ps = connection.prepareStatement(SHOWQ);
                ps.setString(1, name);
                rs = ps.executeQuery();

                if (rs != null) {
                    int columnCount = rs.getMetaData().getColumnCount();
                    while (rs.next()) {
                        ListItem items = new ListItem();

                        for (int i = columnCount; i <= columnCount; i++) {
                            items.setDate(rs.getTimestamp(1));
                            items.setCatNum(rs.getString(2).trim());
                            items.setName(rs.getString(3).trim());
                            items.setQuantity(rs.getInt(4));
                            items.setStatus(rs.getString(5).trim());
                            items.setOrStatus(rs.getString(6).trim());
                            items.setNum(rs.getString(7).trim());
                            arrayListItem.add(items);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    };
}