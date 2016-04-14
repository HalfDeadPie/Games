package com.example.simon.gamesshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

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

    public void sendEdit(View view){
        Connector con = new Connector(this);
        TextView idView = (TextView) findViewById(R.id.edit_form_uid);
        System.out.println("očakavana chyba:"+idView);
        String id = idView.getText().toString();
        con.execute("SENDEDIT",id);

        TextView detail_description = (TextView) findViewById(R.id.edit_form_decription);
        TextView detail_name = (TextView) findViewById(R.id.edit_form_title);
        TextView detail_image = (TextView) findViewById(R.id.edit_form_image);
        //ExpandableListView detail_pegi = (ExpandableListView) findViewById(R.id.edit_form_pegi_list);
        TextView detail_pegi =(TextView) findViewById(R.id.edit_form_pegi);
        TextView detail_rating = (TextView) findViewById(R.id.edit_form_rating);
        TextView detail_price = (TextView) findViewById(R.id.edit_form_price);
        TextView detail_date = (TextView) findViewById(R.id.edit_form_release);
        TextView detail_count = (TextView) findViewById(R.id.edit_form_count);
        TextView detail_producer = (TextView) findViewById(R.id.edit_form_producer);
        TextView detail_language = (TextView) findViewById(R.id.edit_form_languages);

        RadioButton g0 = (RadioButton) findViewById(R.id.radioButtonAction);
        RadioButton g1 = (RadioButton) findViewById(R.id.radioButtonAdventure);
        RadioButton g2 = (RadioButton) findViewById(R.id.radioButtonCasual);
        RadioButton g3 = (RadioButton) findViewById(R.id.radioButtonIndie);
        RadioButton g4 = (RadioButton) findViewById(R.id.radioButtonMM);
        RadioButton g5 = (RadioButton) findViewById(R.id.radioButtonRacing);
        RadioButton g6 = (RadioButton) findViewById(R.id.radioButtonRPG);
        RadioButton g7 =(RadioButton)  findViewById(R.id.radioButtonSimulation);
        RadioButton g8 = (RadioButton) findViewById(R.id.radioButtonSports);
        RadioButton g9 = (RadioButton) findViewById(R.id.radioButtonStrategy);

        RadioButton p0 = (RadioButton) findViewById(R.id.radioButtonPC);
        RadioButton p1 = (RadioButton) findViewById(R.id.radiobutton4PS3);
        RadioButton p2 = (RadioButton) findViewById(R.id.radioButton3ONE);
        RadioButton p3 = (RadioButton) findViewById(R.id.radioButton2_Wii);
        RadioButton p4 = (RadioButton) findViewById(R.id.radioButton5PS4);
        RadioButton p5 = (RadioButton) findViewById(R.id.radioButton6360);

    }
}
