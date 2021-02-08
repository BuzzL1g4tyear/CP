package com.example.cp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {
    TextView u_nameText, u_famText, phoneText, loginText, EmailText, PasText;
    String u_name, u_fam, phone, login, Email, Pas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    }
    public void onClickAdd(View view){
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


    }
}
