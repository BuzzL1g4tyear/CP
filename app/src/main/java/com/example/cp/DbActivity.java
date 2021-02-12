package com.example.cp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DbActivity extends AppCompatActivity {

    public EditText textNum, textName, textID;
    public ListView listView;
    public ArrayAdapter<String> adapter;

    ConnectionHelper connect = new ConnectionHelper();
    Connection connection = connect.getCon();
    Statement st = null;
    ResultSet rs = null;

    private static final String MY_SETTINGS = "my_settings";
    public static final String SHOWQ = "SELECT * FROM CATALOG";
    public static final String ADDQ = "INSERT INTO CATALOG (id, Number, Name) VALUES (?,?,?)";

    ArrayList<String> ItemsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);
        textNum = findViewById(R.id.txtNum);
        textName = findViewById(R.id.txtName);
        textID = findViewById(R.id.ID_TXT);
        listView = findViewById(R.id.listView);
    }

    @SuppressLint("StaticFieldLeak")
    public final class ShowCat extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected JSONArray doInBackground(String... query) {

            JSONArray resultSet = new JSONArray();

            try {
                st = connection.createStatement();
                rs = st.executeQuery(SHOWQ);

                if (rs != null) {
                    int columnCount = rs.getMetaData().getColumnCount();
                    while (rs.next()) {
                        JSONObject rowObject = new JSONObject();
                        for (int i = 1; i <= columnCount; i++) {
                            rowObject.put(rs.getMetaData().getColumnName(i), (rs.getString(i) != null) ? rs.getString(i) : "");
                        }
                        resultSet.put(rowObject);
                    }
                    for (int i = 0; i < resultSet.length(); i++) {
                        ItemsList.add(resultSet.getString(i));
                    }

                }
            } catch (SQLException | JSONException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (st != null) st.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
            return resultSet;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public final class InsertCat extends AsyncTask<String, Void, JSONArray> {

        String num, name;
        int id;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            id = Integer.parseInt(textID.getText().toString());
            name = textName.getText().toString();
            num = textNum.getText().toString();
        }

        @Override
        protected JSONArray doInBackground(String... proc_params) {
            JSONArray resultSet = new JSONArray();

            try {
                st = connection.createStatement();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            PreparedStatement ps = null;
            try {

                if (connection != null) {
                    ps = connection.prepareStatement(ADDQ);

                    ps.setInt(1, id);
                    ps.setString(2, num);
                    ps.setString(3, name);
                    ps.addBatch();
                    ps.execute();

                    return resultSet;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
            return resultSet;
        }
    }

    public void onClickAdd(View view) {
        InsertCat insertCat = new InsertCat();
        insertCat.execute();
        ShowCat show = new ShowCat();
        show.execute();
        adapter = new ArrayAdapter<>(DbActivity.this, android.R.layout.simple_list_item_1, ItemsList);
        adapter.clear();
        adapter.notifyDataSetChanged();
        Toast("Добавлено!");
    }

    public void onClickRead(View view) {
        ShowCat show = new ShowCat();
        show.execute();
        adapter = new ArrayAdapter<>(DbActivity.this, android.R.layout.simple_list_item_1, ItemsList);
        adapter.clear();
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }
    public void onClickUnlog(View view) {
        SharedPreferences sp = getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putBoolean("hasLogged", false);
        e.apply();
        Dialog("ОК!","Успешно вышли!");
    }

    private void Dialog(String title, String mes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(mes);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void Toast(String mess) {
        Toast.makeText(this, mess, Toast.LENGTH_LONG).show();
    }
}
