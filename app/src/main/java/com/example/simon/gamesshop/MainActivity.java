package com.example.simon.gamesshop;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.SingleLaunchActivityTestCase;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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

        AllListBuilder(Json, GameList);
        for(int i=0;i<GameList.size();i++){
            Log.v("GameList","Name"+i+": "+ GameList.get(i).getName());
        }
        TextView outText = (TextView) findViewById(R.id.text);
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