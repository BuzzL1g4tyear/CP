package com.example.cp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {
    private static final String MY_SETTINGS = "my_settings";
    private static final String CHECKQ = "SELECT * FROM LoginData WHERE Login = ? AND Password = ?";

    TextView loginText, PasText;
    String login, Pas;


    ConnectionHelper connect = new ConnectionHelper();
    Connection connection = connect.getCon();
    PreparedStatement ps = null;
    ResultSet rs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar_login);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);
    }

    public void onClickAdd(View view) throws ExecutionException, InterruptedException {
        SharedPreferences sp = getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        loginText = findViewById(R.id.LoginTXT);
        PasText = findViewById(R.id.PasTXT);

        login = loginText.getText().toString();
        Pas = PasText.getText().toString();

        Intent intent;

        if (CheckFields(login, Pas)) {
            CheckUser checkUser = new CheckUser();
            boolean status = checkUser.execute().get();
            SharedPreferences.Editor e = sp.edit();
            if (status) {
                intent = new Intent(this, shopActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                e.putBoolean("hasLogged", true);
                e.putString("name", login);
                e.apply();
                Toast(login + " entered");
            } else {
                Snack("Неверный логин или пароль");
            }
        }
    }

    private void Toast(String mes) {
        Toast.makeText(this, mes,
                Toast.LENGTH_LONG).show();
    }

    private boolean CheckFields(String login, String pas) {
        boolean field = true;

        if (login.isEmpty() || pas.isEmpty()) {
            Dialog("Ошибка!", "Заполните все поля!");
            field = false;
        }
        return field;
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

    public void Snack(String mes) {
        View viewSnack = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar
                .make(viewSnack, mes, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(getResources().getColor(R.color.colorWhite));
        snackbar.show();
    }

    @SuppressLint("StaticFieldLeak")
    public final class CheckUser extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            login = loginText.getText().toString();
            Pas = PasText.getText().toString();
        }

        protected Boolean doInBackground(String... query) {

            boolean resultSet = false;

            try {
                ps = connection.prepareStatement(CHECKQ);
                ps.setString(1, login);
                ps.setString(2, Pas);
                rs = ps.executeQuery();

                if (rs.next()) {
                    resultSet = true;
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
            return resultSet;
        }
    }
}
