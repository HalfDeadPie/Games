package com.example.simon.gamesshop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        Connector con = new Connector(this);
        con.execute("GETALL");
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
        //System.out.println(row);
        LinearLayout lay = (LinearLayout) row.findViewById(R.id.layout);
        //System.out.println(lay);
        TextView textView = (TextView) lay.findViewById(R.id.uid);  // dieta (skryty textview obsahujuci UID)
        //System.out.println(textView);
        String ID = textView.getText().toString();                  // z dietata nacitame UID

        //ProgressDialog Loading = ProgressDialog.show(MainActivity.this, "", "Loading. Please wait...", true);
        Intent intent = new Intent(this, detail.class);
        intent.putExtra("UID", ID);//Put your id to your next Intent
        startActivity(intent);
        finish();
    }
    public void sell(View view){

    }
    public void buy(View view){

    }
    public void toDetailFromName(View view){
        // ziska UID zaznamu a spusti novu aktivitu s tymto UID

        LinearLayout lay = (LinearLayout) view.getParent();
        System.out.println(lay);
        ViewGroup table = (ViewGroup) lay.getParent();

        TextView textView = (TextView) table.findViewById(R.id.uid);  // dieta (skryty textview obsahujuci UID)
        System.out.println(textView);
        String ID = textView.getText().toString();                  // z dietata nacitame UID
        System.out.println("ID:     "+ID);
        // spustenie novej aktivity s UID


        ProgressDialog Loading = ProgressDialog.show(MainActivity.this, "", "Loading. Please wait...", true);
        Intent intent = new Intent(this, detail.class);
        intent.putExtra("UID", ID);//Put your id to your next Intent
        startActivity(intent);
        finish();

    }

    public void toEdit(View view){
        LinearLayout lay = (LinearLayout) view.getParent();
        ViewGroup table = (ViewGroup) lay.getParent();

        TextView textView = (TextView) table.findViewById(R.id.uid);  // dieta (skryty textview obsahujuci UID)
        System.out.println(textView);
        String ID = textView.getText().toString();                  // z dietata nacitame UID
        System.out.println("ID:     "+ID);


        //ProgressDialog Loading = ProgressDialog.show(MainActivity.this, "", "Loading. Please wait...", true);
        //System.out.println("Som tu1");
        Intent intent = new Intent(this,edit_form.class);
        //System.out.println("Som tu2");
        intent.putExtra("ID", ID);//Put your id to your next Intent
        //System.out.println(intent);
        startActivity(intent);
        //System.out.println("Som tu4");
        finish();


    }

    public void BuyFromList(View view){
        // ziska UID zaznamu a spusti novu aktivitu s tymto UID

        ViewGroup row = (ViewGroup) view.getParent();
        LinearLayout lay1 = (LinearLayout) row.getParent();
        LinearLayout lay2 = (LinearLayout) lay1.getParent();
        ViewGroup row2 = (ViewGroup) lay2.getParent();
        LinearLayout lay3 = (LinearLayout) row2.findViewById(R.id.layout);
        TextView textView = (TextView) lay3.findViewById(R.id.uid);  // dieta (skryty textview obsahujuci UID)
        String ID = textView.getText().toString();
        System.out.println(ID);
    }
}