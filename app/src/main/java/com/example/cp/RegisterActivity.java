package com.example.cp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    TextView u_nameText, u_famText, phoneText, loginText, EmailText, PasText;
    String u_name, u_fam, phone, login, Email, Pas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    }

    public void onClickAdd(View view) {
        u_nameText = (TextView) findViewById(R.id.U_nameTXT);
        u_famText = (TextView) findViewById(R.id.U_famTXT);
        phoneText = (TextView) findViewById(R.id.phoneTXT);
        loginText = (TextView) findViewById(R.id.LoginTXT);
        PasText = (TextView) findViewById(R.id.PasTXT);
        EmailText = (TextView) findViewById(R.id.EmailTXT);

        u_name = u_nameText.getText().toString();
        u_fam = u_famText.getText().toString();
        phone = phoneText.getText().toString();
        login = loginText.getText().toString();
        Pas = PasText.getText().toString();
        Email = EmailText.getText().toString();

        if(CheckFields(u_name,u_fam,phone,login,Pas,Email)) {
            Toast(u_fam + " " + u_name + " добавлен");
        }
    }

    private void Toast(String mes) {
        Toast.makeText(this, mes,
                Toast.LENGTH_LONG).show();
    }

    private boolean CheckFields(String name, String fam, String phone, String login, String pas, String email) {
        boolean field = true;

        if (name.equals("") || fam.trim().equals("") || phone.isEmpty() ||
                login.isEmpty() || pas.isEmpty() || email.isEmpty()) {
            Dialog("Ошибка!","Заполните все поля!");
            field = false;
        }
        return field;
    }
    private void Dialog(String title, String mes){
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
}
