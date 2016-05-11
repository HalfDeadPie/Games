package com.example.simon.gamesshop;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        ProgressDialog Loading = ProgressDialog.show(detail.this, "", "Loading. Please wait...", true);
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void GetDetail(String ID) {
        IOConnector con = new IOConnector(this);
        //System.out.println(ID);
        con.execute("GETDETAIL", ID);
    }

    protected Bitmap getImage(String link) {
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

    public Game ListParser(JSONObject JG, Game SG) {
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

    public void BuyFromList(View view) {
        // ziska UID zaznamu a spusti novu aktivitu s tymto UID
        TextView countView = (TextView) findViewById(R.id.detail_count);
        String count = countView.getText().toString();
        TextView idView = (TextView) findViewById(R.id.detail_id);
        String ID = idView.getText().toString();

        System.out.println("DETAILBUY:ID:" + ID + "  Count:" + count);

        IOConnector con = new IOConnector(this);
        con.execute("BUY", ID, count);

        int incremented = Integer.parseInt(count);
        incremented++;
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(incremented);
        String incString = sb.toString();
        countView.setText(incString);
        countView.setTextColor(Color.BLACK);
    }

    public void showImage(View view){
        ImageView i = (ImageView) findViewById(R.id.detail_image);
        TextView id = (TextView) findViewById(R.id.detail_id);
        Bitmap bitmap = ((BitmapDrawable)i.getDrawable()).getBitmap();

        Intent intent = new Intent(this, image_detail.class);
        intent.putExtra("IMAGE", bitmap );//Put your id to your next Intent
        intent.putExtra("UID",id.getText().toString());
        startActivity(intent);
        finish();
    }

    public void SellFromList(View view) {
        // ziska UID zaznamu a spusti novu aktivitu s tymto UID
        TextView countView = (TextView) findViewById(R.id.detail_count);
        String count = countView.getText().toString();
        TextView idView = (TextView) findViewById(R.id.detail_id);
        String ID = idView.getText().toString();

        System.out.println("DETAILSELL:ID:" + ID + "  Count:" + count);

        int control = Integer.parseInt(count);
        if (control > 0) {

            IOConnector con = new IOConnector(this);
            con.execute("SELL", ID, count);

            int decremented = Integer.parseInt(count);
            decremented--;
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(decremented);
            String incString = sb.toString();
            countView.setText(incString);
            System.out.println("Detail derecremented:" + decremented);
            if (decremented == 0) {
                countView.setTextColor(Color.RED);
            } else {
                countView.setTextColor(Color.BLACK);
            }
        } else {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            dlgAlert.setMessage("You are not able to sell this game!");
            dlgAlert.setTitle("Error!");
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        }
    }

    public void Delete(View view) {
        // ziska UID zaznamu a spusti novu aktivitu s tymto UID
        TextView idView = (TextView) findViewById(R.id.detail_id);
        String ID = idView.getText().toString();
        System.out.println("DETAILDEL:ID:" + ID);
        IOConnector con = new IOConnector(this);
        con.execute("DEL", ID);
        onBackPressed();
    }

}
