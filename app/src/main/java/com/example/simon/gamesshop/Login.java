package com.example.simon.gamesshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void testLogin(View view){
        String ok_name = "Jaro";
        String ok_password = "Simon";
        TextView name = (TextView) findViewById(R.id.login_name);
        TextView password = (TextView) findViewById(R.id.login_passwd);
        TextView error = (TextView) findViewById(R.id.login_error);

        if((name.getText().toString().equals("Jaro")) && (password.getText().toString().equals("Simon"))){
            error.setText("Prihlasenie je uspesne!");
            //System.out.println("Som tu1!");
        }else{
            assert error != null;
            System.out.println("Som tu2!");
            error.setText("Nespravne meno alebo heslo!");
        }


    }
}
