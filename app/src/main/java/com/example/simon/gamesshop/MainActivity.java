package com.example.simon.gamesshop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        con.execute("GETALL");
        try {
            Json = (String) con.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        List<HashMap<String, Object>> fillMaps = new ArrayList<HashMap<String, Object>>();
        AllListBuilder(Json, GameList);
        String[] from = new String[] { "image","name", "count", "uid"};
        int[] to = new int[] { R.id.cover_image, R.id.name, R.id.count,R.id.uid};
        ListView viewGL = (ListView)findViewById(R.id.viewGL);


        for(int i=0;i<GameList.size();i++){

            Bitmap b = getImage(GameList.get(i).getImage());
            // b = objekt typu bitmap s nacitanym suborom

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("name", GameList.get(i).getName()); // This will be shown in R.id.title
            // do cover_image poslem bitmap ale obrazok sa nenastavi..
            map.put("image", b); // And this in R.id.description
            map.put("count", GameList.get(i).getCount()); // And this in R.id.description
            map.put("uid", GameList.get(i).getUID()); // And this in R.id.description
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
                for (int i=0;i<ParentArray.length();i++){
                    GameList.add(ListParser(ParentArray.getJSONObject(i), new Game()));
                }
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
            SG.setUID(JG.getString("objectId"));
        } catch (JSONException e) {
            Log.d("JSON","Chyba pri parsovaní!");
        }
        return SG;
    }
    // po kliknuti n obrazok/ nazov hry sa pusti kod na zobrazenie detailu konkretnej hry
    public void toDetail(View view){
        // ziska UID zaznamu a spusti novu aktivitu s tymto UID
        ViewGroup row = (ViewGroup) view.getParent();               // rodic
        TextView textView = (TextView) row.findViewById(R.id.uid);  // dieta (skryty textview obsahujuci UID)
        String ID = textView.getText().toString();                  // z dietata nacitame UID
        // spustenie novej aktivity s UID


        Intent intent = new Intent(this, detail.class);
        intent.putExtra("UID", ID);//Put your id to your next Intent
        startActivity(intent);
        finish();
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
}