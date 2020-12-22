package com.example.cp;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    EditText textNum, textName;
    ListView List;
    ListAdapter ListAdapter;

    ConnectionHelper connect = new ConnectionHelper();
    Connection connection = connect.getCon();
    Statement st = null;
    ResultSet rs = null;

    public static final String SHOWALL = "SELECT * FROM CATALOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textNum = findViewById(R.id.txtNum);
        textName = findViewById(R.id.txtName);
        List = findViewById(R.id.listView);
    }

    @SuppressLint("StaticFieldLeak")
    public final class ShowAll extends AsyncTask<String, Void, JSONArray> {

        String num, name;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            name = textName.getText().toString();
            num = textNum.getText().toString();
        }

        protected JSONArray doInBackground(String... query) {

            JSONArray resultSet = new JSONArray();
            try {
                st = connection.createStatement();
                rs = st.executeQuery(SHOWALL);
                int columnCount = rs.getMetaData().getColumnCount();
                if (rs != null) {
                    JSONObject rowObject = new JSONObject();
                    for (int i = 1; i <= columnCount; i++) {
                        rowObject.put(rs.getMetaData().getColumnName(i), (rs.getString(i) != null) ? rs.getString(i) : "");
                    }
                    resultSet.put(rowObject);
                }
            } catch (SQLException | JSONException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (st != null) st.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }

            return resultSet;
        }
        public void AllItems(){

        }
    }
    public void onClickAdd(View view) {

    }

    public void onClickRead(View view) {
        ShowAll show = new ShowAll();
        show.execute("");
    }

    public void Toast(String mess) {
        Toast.makeText(this, mess, Toast.LENGTH_LONG).show();
    }
}
