package com.example.simon.gamesshop;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


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
        IOConnector con = new IOConnector(this);
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
        RadioGroup groupGenre = (RadioGroup) findViewById(R.id.groupGenre);
        RadioGroup groupPlatform = (RadioGroup) findViewById(R.id.groupPlatform);

        int radioButtonID = groupGenre.getCheckedRadioButtonId();
        View radioButton = groupGenre.findViewById(radioButtonID);
        int GenreID = groupGenre.indexOfChild(radioButton);     // cislo zanru

        int radioButtonIDP = groupPlatform.getCheckedRadioButtonId();
        View radioButtonP = groupPlatform.findViewById(radioButtonIDP);
        int PlatformID = groupPlatform.indexOfChild(radioButtonP);  // cislo platformy

        TextView detail_description = (TextView) findViewById(R.id.edit_form_decription);
        TextView detail_name = (TextView) findViewById(R.id.edit_form_title);
        TextView detail_image = (TextView) findViewById(R.id.edit_form_image);
        TextView detail_pegi =(TextView) findViewById(R.id.edit_form_pegi);
        TextView detail_rating = (TextView) findViewById(R.id.edit_form_rating);
        TextView detail_price = (TextView) findViewById(R.id.edit_form_price);
        TextView detail_date = (TextView) findViewById(R.id.edit_form_release);
        TextView detail_count = (TextView) findViewById(R.id.edit_form_count);
        TextView detail_producer = (TextView) findViewById(R.id.edit_form_producer);
        TextView detail_language = (TextView) findViewById(R.id.edit_form_languages);
        TextView detail_uid = (TextView) findViewById(R.id.edit_form_uid);

        Game g = new Game();
        g.setName(detail_name.getText().toString());
        g.setDescription(detail_description.getText().toString());
        g.setPegi(detail_pegi.getText().toString());
        g.setRating(Integer.parseInt(detail_rating.getText().toString()));
        g.setImage(detail_image.getText().toString());
        g.setPrice(Integer.parseInt(detail_price.getText().toString()));
        g.setReleaseDate(detail_date.getText().toString());
        g.setCount(Integer.parseInt(detail_count.getText().toString()));
        g.setProducer(detail_producer.getText().toString());
        g.setLanguage(detail_language.getText().toString());
        g.setGenre(GenreID);
        g.setPlatform(PlatformID);
        g.setUID(detail_uid.getText().toString());

        if(isEmpty(g)){
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            dlgAlert.setMessage("Please fill all required fields!");
            dlgAlert.setTitle("Error!");
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        }else {

            IOConnector con = new IOConnector(this);
            TextView idView = (TextView) findViewById(R.id.edit_form_uid);

            String id = idView.getText().toString();
            con.execute("SENDEDIT", id);
        }

    }
    private boolean isEmpty(Game g) {
        if(     g.getGenre() == -1 ||
                g.getPlatform() == -1 ||
                g.getCount() <0 ||
                g.getDescription().equals("") ||
                g.getImage().equals("") ||
                g.getLanguage().equals("") ||
                g.getName().equals("") ||
                g.getPegi().equals("") ||
                g.getPrice() < 0 ||
                g.getReleaseDate().equals("") ||
                g.getRating() < 0 ||
                g.getProducer().equals("")){
            return  true;
        }else{
            return false;
        }
    }
}
