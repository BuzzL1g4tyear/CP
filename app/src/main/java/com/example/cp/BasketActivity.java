package com.example.cp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BasketActivity extends AppCompatActivity {

    private BaskedAdapter baskedAdapter;
    private static final String MY_SETTINGS = "my_settings";
    String name;

    public static final String SHOWBASKETQ = "SELECT Группа, Бренд, ShoppingCart.КатНомер, Наименование, [Ваша цена (BYN c НДС)], Количество " +
            "FROM ShoppingCart " +
            "JOIN Stock S ON ShoppingCart.КатНомер = S.КатНомер " +
            "WHERE Клиент = (SELECT LoginId FROM LoginData WHERE Login = ?)";
    ConnectionHelper connect = new ConnectionHelper();
    Connection connection = connect.getCon();
    PreparedStatement ps = null;
    ResultSet rs = null;
    ListView basketList;

    private List<ListItem> arrayListItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
        SharedPreferences sp = getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        name = sp.getString("name", "null");

        arrayListItem = new ArrayList<>();
        basketList = findViewById(R.id.basketListView);

        baskedAdapter = new BaskedAdapter(this, R.layout.basket_items, arrayListItem, getLayoutInflater());
        basketList.setAdapter(baskedAdapter);

        Thread thread = new Thread(showBasket);
        thread.start();
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
                            baskedAdapter.notifyDataSetChanged();
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

}