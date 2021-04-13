package com.example.cp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TableRow;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class shopActivity extends AppCompatActivity {
    private static final String MY_SETTINGS = "my_settings";
    private ListView listView;
    private CustomArrayAdapter customArrayAdapter;
    private List<ListItem> arrayListItem;

    public Button btnMin, btnPlu, btnOK;
    public EditText text;

    private int count = 0;
    public String quantity;
    public String nameItem;
    public String catNum;
    public String name;

    public static final String SHOWQ = "SELECT * FROM STOCK";
    public static final String ADDTOBASKET = "INSERT INTO ShoppingCart (КатНомер, Количество, Дата, Клиент)" +
            " VALUES (?,?,?,(SELECT LoginId FROM LoginData WHERE Login = ?))";
    public static final String UPDBASKET = "UPDATE ShoppingCart SET Количество = ?" +
            "WHERE КатНомер = ? AND Клиент = ?";

    ConnectionHelper connect = new ConnectionHelper();
    Connection connection = connect.getCon();
    PreparedStatement ps1 = null;
    PreparedStatement ps2 = null;
    Statement st = null;
    ResultSet rs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sp = getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        name = sp.getString("name", "null");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        arrayListItem = new ArrayList<>();
        listView = findViewById(R.id.itemsList);

        customArrayAdapter = new CustomArrayAdapter(this, R.layout.item_list, arrayListItem, getLayoutInflater());
        listView.setAdapter(customArrayAdapter);

        runOnUiThread(showItems);

        setListViewHeightBasedOnChildren(listView);
        Toolbar toolbarShop = findViewById(R.id.toolbar);
        setSupportActionBar(toolbarShop);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nameItem = arrayListItem.get(position).getName();
                catNum = arrayListItem.get(position).getCatNum();

                openDialog(nameItem);
            }
        });
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, TableRow.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public void openDialog(String title) {
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
                count = count + 1;
                String translate = String.valueOf(count);
                text.setText(translate);
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = text.getText().toString();
                if (CheckFields(quantity)) {
                    count = Integer.parseInt(text.getText().toString());
                    if(count != 0) {
                        Thread thread1 = new Thread(addToBasket);
                        thread1.start();
//                    Thread thread2 = new Thread(addToBDAOI);
//                    thread2.start();
                        Toast(nameItem + " добавленно в корзину " + quantity);
                        dialog.dismiss();
                    } else {
                        Toast("Выбранно количество 0");
                    }
                } else {
                    Toast("Пустое поле");
                }
            }
        });

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

    Runnable showItems = new Runnable() {
        ListItem listItem;

        @Override
        public void run() {
            try {
                st = connection.createStatement();
                rs = st.executeQuery(SHOWQ);

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
                            items.setAvailable(rs.getString(6).trim());
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
        }
    };

    Runnable addToBasket = new Runnable() {
        @Override
        public void run() {
            try {
                ps1 = connection.prepareStatement(ADDTOBASKET);

                java.util.Date utilDate = new java.util.Date();
                java.sql.Timestamp sqlDate = new java.sql.Timestamp(utilDate.getTime());

                ps1.setString(1, catNum);// catNum
                ps1.setString(2, quantity);// quantity
                ps1.setTimestamp(3, sqlDate);// date
                ps1.setString(4, name);// client

                ps1.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps1 != null) ps1.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
    };

//    Runnable addToBDAOI = new Runnable() {
//        @Override
//        public void run() {
//            try {
//                ps2 = connection.prepareStatement(ADDQAOI);
//
//                ps2.setInt(1, 1);// odCode
//                ps2.setString(2, catNum);// odCode
//                ps2.setString(3, nameItem);// odName
//                ps2.setInt(4, Integer.parseInt(quantity));// odQuant
//                ps2.setInt(5, 1);// odStatus
//                ps2.setInt(6, 1);// odOrderId
//
//                ps2.execute();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (ps2 != null) ps2.close();
//                } catch (SQLException e) {
//                    throw new RuntimeException(e.getMessage());
//                }
//            }
//        }
//    };

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