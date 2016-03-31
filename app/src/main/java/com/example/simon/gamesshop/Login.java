package com.example.simon.gamesshop;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

    public void Logged (){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void testLogin(View view) {

        TextView Login = (TextView) findViewById(R.id.login_name);
        TextView Password = (TextView) findViewById(R.id.login_passwd);

        if ((Login.getText().toString().equals("")) && (Password.getText().toString().equals(""))) {
            Log.v("Login", "Login sucessful");
            //ProgressDialog Loading = ProgressDialog.show(Login.this, "", "Loading. Please wait...", true);
            Logged();
        } else {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);

            dlgAlert.setMessage("Wrong password or username");
            dlgAlert.setTitle("Error!");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();

            dlgAlert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });


        }
    }
}
