package com.example.cp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class shopActivity extends AppCompatActivity {
    private static final String MY_SETTINGS = "my_settings";
    private RecyclerView recyclerView;
    private static CustomArrayAdapter customArrayAdapter;
    private List<ListItem> arrayListItem;
    RecyclerView.LayoutManager layoutManager;

    public Button btnMin, btnPlu, btnOK;
    public EditText text;
    private TextInputLayout inputLayout;

    private int count = 0;
    private int countSQL = 0;
    public String quantity;
    public String nameItem;
    public String catNum;
    public String name;

    boolean hasLogged;

    public static final String SHOWQ = "SELECT * FROM STOCK";
    public static final String COUNTSQL = "SELECT COUNT(Группа) FROM Stock";
    public static final String ADDTOBASKET = "INSERT INTO ShoppingCart (КатНомер, Количество, Дата, Клиент)" +
            " VALUES (?,?,?,(SELECT LoginId FROM LoginData WHERE Login = ?))";

    ConnectionHelper connect = new ConnectionHelper();
    Connection connection = connect.getCon();
    PreparedStatement ps = null;
    PreparedStatement ps1 = null;
    Statement st = null;
    ResultSet rs = null;
    ResultSet rs1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sp = getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        name = sp.getString("name", "null");
        hasLogged = sp.getBoolean("hasLogged", false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        recyclerView = findViewById(R.id.itemsList);
        arrayListItem = new ArrayList<>();

        if (!hasConnection(shopActivity.this)) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.layoutShop, new NoEthernetFragment());
            ft.commit();
        } else {
            if (!hasLogged) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                runOnUiThread(countRows);

                Thread thread = new Thread(showItems);
                thread.start();
                Toolbar toolbarShop = findViewById(R.id.toolbar_shop);
                toolbarShop.setTitleTextColor(getResources().getColor(R.color.colorWhite));
                setSupportActionBar(toolbarShop);

                setTitle(R.string.labelShop);
                recyclerView.setHasFixedSize(true);
                layoutManager = (new LinearLayoutManager(this));
                recyclerView.setLayoutManager(layoutManager);
                customArrayAdapter = new CustomArrayAdapter(arrayListItem, recyclerView);
                recyclerView.setAdapter(customArrayAdapter);

                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (layoutManager.getItemCount() < countSQL) {
                            customArrayAdapter.notifyDataSetChanged();
                            Toast("More");
                        }
                    }
                });

                customArrayAdapter.setLoad_more(new loadMore() {
                    @Override
                    public void onLoadMore() {
                        if (layoutManager.getItemCount() < countSQL) {
                            Toast("End");
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (hasConnection(shopActivity.this)) {
            customArrayAdapter.notifyDataSetChanged();
            customArrayAdapter.setOnClickListener(position -> {
                nameItem = arrayListItem.get(position).getName();
                catNum = arrayListItem.get(position).getCatNum();

                openDialog(nameItem);
            });
        }
    }

    public void openDialog(String title) {
        Dialog dialog = new Dialog(shopActivity.this, R.style.CustomStyleDialog);
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
        text.setText("0");
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
                        Thread thread1 = new Thread(addToBasket);
                        thread1.start();
                        Toast(nameItem + " добавленно в корзину " + quantity);
                        dialog.dismiss();
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

    Runnable countRows = () -> {
        try {
            ps = connection.prepareStatement(COUNTSQL);
            rs1 = ps.executeQuery();

            if (rs1 != null) {
                while (rs1.next()) {
                    countSQL = rs1.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs1 != null) rs1.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    };

    Runnable showItems = new Runnable() {

        @Override
        public void run() {
            try {
                st = connection.createStatement();
                rs = st.executeQuery(SHOWQ);

                if (rs != null) {

                    while (rs.next()) {
                        ListItem items = new ListItem();

                        items.setGroup(rs.getString(1).trim());
                        items.setBrand(rs.getString(2).trim());
                        items.setCatNum(rs.getString(3).trim());
                        items.setName(rs.getString(4).trim());
                        items.setPrice(rs.getFloat(5));
                        items.setAvailable(rs.getString(6).trim());
                        arrayListItem.add(items);

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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.items_toolbar, menu);

        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Поиск");
        if (hasConnection(this)) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    customArrayAdapter.filter(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    customArrayAdapter.filter(newText);
                    return true;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.basketToolbar:
                if (hasConnection(this)) {
                    Intent intent = new Intent(shopActivity.this, BasketActivity.class);
                    startActivity(intent);
                    return true;
                } else {
                    Snack(getString(R.string.no_ethernet));
                }
            case R.id.basketLogout:
                if (hasConnection(this)) {
                    SharedPreferences sp = getSharedPreferences(MY_SETTINGS,
                            Context.MODE_PRIVATE);
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor e = sp.edit();
                    e.putBoolean("hasLogged", false);
                    e.putString("name", "null");
                    e.apply();
                    Intent intent1 = new Intent(shopActivity.this, LoginActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);
                } else {
                    Snack(getString(R.string.no_ethernet));
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}