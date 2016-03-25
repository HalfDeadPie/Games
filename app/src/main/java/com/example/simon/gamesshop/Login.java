package com.example.simon.gamesshop;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the Title Bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        // Hide the Status Bar (signal, battery, time)
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //         WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void testLogin(View view){

        TextView name = (TextView) findViewById(R.id.login_name);
        TextView password = (TextView) findViewById(R.id.login_passwd);
        TextView error = (TextView) findViewById(R.id.login_error);

        if((name.getText().toString().equals("a")) && (password.getText().toString().equals("a"))){
            // uspesne zadanie mena a hesla

            error.setText("Prihlasenie je uspesne!");
            // farba nefunguje
            error.setLinkTextColor(Color.GREEN);

            Intent intent = new Intent(this, detail.class);
            startActivity(intent);
            //System.out.println("Som tu1!");
        }else{
            // nespravne zadane meno a heslo
            error.setText("Nespravne meno alebo heslo!");
        }


    }
}
