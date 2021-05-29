package com.example.cp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BasketActivity extends AppCompatActivity {

    private BaskedAdapter baskedAdapter;
    private static final String MY_SETTINGS = "my_settings";
    String name, catNum, mQuantity;
    java.util.Date utilDate;
    java.sql.Timestamp sqlDate;
    RecyclerView basketRV;
    RecyclerView.LayoutManager layoutManager;
    public Button btnMin, btnPlu, btnOK;
    public EditText text;
    private TextInputLayout inputLayout;

    private int count = 0;
    public String quantity;
    public String nameItem;

    public static final String SHOWBASKETQ = "SELECT Группа, Бренд, ShoppingCart.КатНомер, " +
            "Наименование, [Ваша цена (BYN c НДС)], Количество " +
            "FROM ShoppingCart " +
            "JOIN Stock S ON ShoppingCart.КатНомер = S.КатНомер " +
            "WHERE Клиент = (SELECT LoginId FROM LoginData WHERE Login = ?)";
    public static final String DELETEFROMBASKET = "DELETE FROM ShoppingCart " +
            "WHERE КатНомер = ? AND Клиент = (SELECT LoginId FROM LoginData WHERE Login = ?)";
    public static final String ADDTOORDER = "INSERT INTO AndroidOrders " +
            "(CustomerId, OrderStatus, OrderDate) " +
            "VALUES ((SELECT LoginId FROM LoginData WHERE Login = ?),?,?)";
    public static final String ADDTOORDERITEM = "INSERT INTO AndroidOrdersItems " +
            "( odCode, odName, odQuant, odStatus, odOrderId) " +
            "VALUES (?,(SELECT Наименование FROM Stock WHERE КатНомер = ?),?,?,?)";

    public static final String ID = "SELECT OrderId FROM AndroidOrders WHERE OrderDate = ?";
    private static final String UPDSHOPCART = "UPDATE ShoppingCart SET Количество = ? " +
            "WHERE КатНомер = ?";

    ConnectionHelper connect = new ConnectionHelper();
    Connection connection = connect.getCon();
    PreparedStatement ps = null;
    PreparedStatement ps1 = null;
    PreparedStatement ps2 = null;
    ResultSet rs = null;
    ResultSet rs1 = null;

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

        runOnUiThread(showBasket);

        basketRV.setLayoutManager(layoutManager);
        basketRV.setAdapter(baskedAdapter);

        Toolbar toolbarBasket = findViewById(R.id.toolbarBasket);
        toolbarBasket.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(toolbarBasket);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        baskedAdapter.setOnClickListener(position -> {
            nameItem = arrayListItem.get(position).getName();
            catNum = arrayListItem.get(position).getCatNum();
            mQuantity = String.valueOf(arrayListItem.get(position).getQuantity());

            openDialog(nameItem);
        });
    }

    Runnable showBasket = new Runnable() {
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
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_blank, null);

        text = view.findViewById(R.id.valueNumb);
        btnMin = view.findViewById(R.id.btnMinus);
        btnPlu = view.findViewById(R.id.btnPlus);
        btnOK = view.findViewById(R.id.btnOK);

        inputLayout = view.findViewById(R.id.textInputLayout);

        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!text.getText().toString().isEmpty()) {
                    if (text.length() > 9) {
                        inputLayout.setError("big data");
                        btnOK.setClickable(false);
                        btnPlu.setClickable(false);
                    } else {
                        inputLayout.setError(null);
                        btnOK.setClickable(true);
                        btnPlu.setClickable(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        text.setText(mQuantity);
        btnMin.setOnClickListener(v -> {
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
        });
        btnPlu.setOnClickListener(v -> {
            count = Integer.parseInt(text.getText().toString());
            count = count + 1;
            String translate = String.valueOf(count);
            text.setText(translate);
        });
        btnOK.setOnClickListener(v -> {
            if (hasConnection(this)) {
                quantity = text.getText().toString();
                if (CheckFields(quantity)) {
                    count = Integer.parseInt(text.getText().toString());
                    if (count != 0) {
                        Thread thread1 = new Thread(updBasket);
                        thread1.start();
                        Toast(nameItem + " отредактирован " + quantity);
                        dialog.dismiss();
                        restartAct();
                    } else {
                        Toast("Выбранно количество 0");
                    }
                } else {
                    Toast("Пустое поле");
                }
            } else {
                Snack(getString(R.string.no_ethernet));
            }
        });

        dialog.setContentView(view);
        dialog.setTitle(title);
        dialog.show();
    }

    private void restartAct() {
        Intent intent = new Intent(BasketActivity.this,BasketActivity.class);
        startActivity(intent);
    }

    Runnable updBasket = new Runnable() {
        @Override
        public void run() {
            try {
                ps1 = connection.prepareStatement(UPDSHOPCART);

                ps1.setString(1, quantity);
                ps1.setString(2, catNum);

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

    public void Snack(String mes) {
        View viewSnack = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar
                .make(viewSnack, mes, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(getResources().getColor(R.color.colorWhite));
        snackbar.show();
    }

    public static boolean hasConnection(@NotNull final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        return wifiInfo != null && wifiInfo.isConnected();
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
        Thread thread0 = new Thread(() -> {
            try {
                int idOrder = 0;
                utilDate = new java.util.Date();
                sqlDate = new java.sql.Timestamp(utilDate.getTime());

                ps = connection.prepareStatement(ADDTOORDER);
                ps.setString(1, name);//custumerID
                ps.setInt(2, 1);//orderSt
                ps.setTimestamp(3, sqlDate);//date
                ps.execute();

                ps2 = connection.prepareStatement(ID);
                ps2.setTimestamp(1, sqlDate);
                rs1 = ps2.executeQuery();
                while (rs1.next()) {
                    idOrder = rs1.getInt(1);
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
        });
        thread0.start();
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
                createOrder();
                Toast.makeText(this, "Заказ отправлен", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.basketOrder:
                Intent intent = new Intent(BasketActivity.this, OrderFragment.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}