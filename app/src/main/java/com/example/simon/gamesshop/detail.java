package com.example.simon.gamesshop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class detail extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent myIntent = getIntent();
        String gameId = myIntent.getStringExtra("UID");
        GetDetail(gameId);
    }
    @Override
    public void onBackPressed()
    {
        //ProgressDialog Loading = ProgressDialog.show(detail.this, "", "Loading. Please wait...", true);
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void GetDetail(String ID) {
        String Json = "";
        ArrayList<Game> GameList = new ArrayList<Game>();
        AsyncTask<String, String, String> con = new Connector();
        System.out.println(ID);
        con.execute("GETDETAIL", ID);
        try {
            Json = (String) con.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Game g = new Game();
        JSONObject jo = null;
        try {
            jo = new JSONObject(Json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ListParser(jo, g);
        setDetail(g);
    }

    protected void setDetail(Game g){

        TextView detail_description = (TextView) findViewById(R.id.detail_description);
        TextView detail_name = (TextView) findViewById(R.id.detail_name);
        ImageView detail_image = (ImageView) findViewById(R.id.detail_image);
        TextView detail_pegi = (TextView) findViewById(R.id.detail_pegi);
        TextView detail_rating = (TextView) findViewById(R.id.detail_rating);
        TextView detail_price = (TextView) findViewById(R.id.detail_price);
        TextView detail_date = (TextView) findViewById(R.id.detail_date);
        TextView detail_count = (TextView) findViewById(R.id.detail_count);
        TextView detail_producer = (TextView) findViewById(R.id.detail_producer);
        TextView detail_genre = (TextView) findViewById(R.id.detail_genre);
        TextView detail_language = (TextView) findViewById(R.id.detail_language);
        TextView detail_platform = (TextView) findViewById(R.id.detail_platform);



        detail_name.setText(g.getName());
        detail_image.setImageBitmap(getImage(g.getImage()));
        detail_pegi.setText(g.getPegi());
        detail_rating.setText(Integer.toString(g.getRating())+"%");
        detail_price.setText(Integer.toString(g.getPrice())+" €");
        detail_description.setText(g.getDescription());
        detail_count.setText(Integer.toString(g.getCount()));
        detail_date.setText(g.getReleaseDate());
        detail_producer.setText(g.getProducer());
        detail_genre.setText(Integer.toString(g.getGenre()));
        detail_language.setText(g.getLanguage());
        detail_platform.setText(Integer.toString(g.getPlatform()));
    }

    protected Bitmap getImage(String link){
        Bitmap b = null;
        AsyncTask<String, String, Bitmap> img = new Image();
        img.execute(link);
        try {
            return b = (Bitmap) img.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Game ListParser(JSONObject JG, Game SG){
        try {
            SG.setName(JG.getString("name"));
            SG.setDescription(JG.getString("description"));
            SG.setCount(JG.getInt("count"));
            SG.setGenre(JG.getInt("genre"));
            SG.setImage(JG.getString("image"));
            SG.setLanguage(JG.getString("language"));
            SG.setPegi(JG.getString("pegi"));
            SG.setPlatform(JG.getInt("platform"));
            SG.setPrice(JG.getInt("price"));
            SG.setProducer(JG.getString("producer"));
            SG.setRating(JG.getInt("rating"));
            SG.setVideo(JG.getString("video"));
            SG.setReleaseDate(JG.getString("release_date"));
        } catch (JSONException e) {
            Log.d("JSON", "Chyba pri parsovaní!");
        }
        return SG;
    }
}
