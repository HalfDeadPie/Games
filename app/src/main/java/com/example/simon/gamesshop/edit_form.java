package com.example.simon.gamesshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Jaroslav Lišiak on 13.04.2016.
 */
public class edit_form extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_form);

        Intent myIntent = getIntent();
        String gameId = myIntent.getStringExtra("ID");
        GetEdit(gameId);
    }

    private void GetEdit(String id) {
        Connector con = new Connector(this);
        //System.out.println(ID);
        con.execute("GETEDIT", id);

    }

    @Override
    public void onBackPressed()
    {
        //ProgressDialog Loading = ProgressDialog.show(detail.this, "", "Loading. Please wait...", true);
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
