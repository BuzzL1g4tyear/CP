package com.example.cp;

import android.os.StrictMode;
import android.util.Log;

import java.sql.*;

public class ConnectionHelper {

    public static final String SERVER = "db.cp.by";
    public static final String DBNAME = "Andriod";
    public static final String LOGIN = "androidtest";
    public static final String PAS = "LB@m$V#pJCj6fA";
    public static final int PORT = 7878;

    private int id;
    private String num;
    private String name;

    public Connection getCon() {

        Connection con = null;
        String ConnURL = "jdbc:jtds:sqlserver://" + SERVER + ":" + PORT + ":/" + DBNAME;

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            con = DriverManager.getConnection(ConnURL, LOGIN, PAS);
            Log.d("MyTag", "Подключилось");
        } catch (SQLException | ClassNotFoundException e) {
            Log.d("MyTag", "Увы");
            e.printStackTrace();
        }

        return con;
    }

}