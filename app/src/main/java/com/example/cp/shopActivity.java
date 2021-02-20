package com.example.cp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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

    public Button btnMin, btnPlu;
    public EditText text;
    private int count = 0;

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

        openDialog("Проверка");
    }

    public void openDialog(String title) {
        Dialog dialog = new Dialog(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_blank,null);

        dialog.setContentView(view);
        dialog.setTitle(title);

        text = view.findViewById(R.id.valueNumb);
        btnMin = view.findViewById(R.id.btnMinus);
        btnPlu = view.findViewById(R.id.btnPlus);

        text.setText("0");
        btnMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = Integer.parseInt(text.getText().toString());
                count = count - 1;
                String translate = String.valueOf(count);
                text.setText(translate);
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
        dialog.show();
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
                            items.setName(rs.getString(1));
                            items.setQuantity(rs.getString(2));
                            items.setPrice(rs.getString(3));
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
}