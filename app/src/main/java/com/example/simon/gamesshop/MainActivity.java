package com.example.simon.gamesshop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.SingleLaunchActivityTestCase;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GetAll();
    }

    public void GetAll() {
        String Json = "";
        ArrayList<Game> GameList = new ArrayList<Game>();
        AsyncTask<String, String, String> con = new Connector();
        con.execute("");
        try {
            Json = (String) con.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        List<HashMap<String, Object>> fillMaps = new ArrayList<HashMap<String, Object>>();
        AllListBuilder(Json, GameList);
        String[] from = new String[] { "image","name", "count"};
        int[] to = new int[] { R.id.cover_image, R.id.name, R.id.count};
        ListView viewGL = (ListView)findViewById(R.id.viewGL);


        for(int i=0;i<GameList.size();i++){
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("name", GameList.get(i).getName()); // This will be shown in R.id.title
            map.put("image", "blabla"); // And this in R.id.description
            map.put("count", GameList.get(i).getCount()); // And this in R.id.description
            fillMaps.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.row, from, to);
        viewGL.setAdapter(adapter);
    }

    //Z json stringu zadaného ako argument vráti zoznam hier triedy Game zadaný ako argument
    public ArrayList<Game> AllListBuilder(String JsonString,  ArrayList<Game> GameList){
        try {
            JSONObject ParentObject = new JSONObject(JsonString);//mega json so všetkým, čo prišlo
            JSONArray ParentArray = ParentObject.getJSONArray("data");//pole jsonov - vybrané len data bez headeru
            if (ParentArray != null)//všetky hry v JSONe pridá do zoznamu
                for (int i=0;i<ParentArray.length();i++){GameList.add(ListParser(ParentArray.getJSONObject(i), new Game()));}
        }
        catch(Exception e)
        {
            Log.d("JSON", "Toto je chyba s JSONOM:" + e.getMessage());//debug výpis
        }
        return GameList;
    }

    //Funkcia, ktorá prevedie JSON jednej hry do objektu triedy Game
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
        } catch (JSONException e) {
            Log.d("JSON","Chyba pri parsovaní!");
        }
        return SG;
    }
}