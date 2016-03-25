package com.example.simon.gamesshop;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by simon on 17.3.2016.
 */
public class Connector extends AsyncTask<String, String, String> {

    private static final String ourURL ="https://api.backendless.com/v1/data/Game";
    @Override
    protected String doInBackground(String... params) {

        URL url = null;
        try {
            url = new URL(ourURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        connection.addRequestProperty("application-id","94B456C3-9A44-D044-FF87-A1D0AA589D00");
            connection.addRequestProperty("secret-key","CDA1E692-BF29-7396-FF7F-0E699E669000");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuffer json = new StringBuffer(1024);
            String tmp="";
        try {
            while((tmp=reader.readLine())!=null) {
                json.append(tmp).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(json.toString());
        try {
            return bigParser(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    public String conect(){

        URL url = null;
        try {
            url = new URL(ourURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        connection.addRequestProperty("application-id","94B456C3-9A44-D044-FF87-A1D0AA589D00");
        connection.addRequestProperty("secret-key","CDA1E692-BF29-7396-FF7F-0E699E669000");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuffer json = new StringBuffer(1024);
        String tmp="";
        try {
            while((tmp=reader.readLine())!=null) {
                json.append(tmp).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(json.toString());
        try {
            return bigParser(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    public static String bigParser(String s) throws JSONException {
        JSONObject obj = null;

        obj = new JSONObject(s);


        JSONArray data = null;

        data = obj.getJSONArray("data");
        return data.toString();
        //System.out.println(data);
        //JSONArray arr = data.getJSONArray("name");
        //int pric = obj.getInt("price");
        //System.out.println("Price: " + pric);
        /*
        Hra h;
        ArrayList<Hra> hry = new ArrayList<>();
        for(int i = 0; i < data.length(); i++){
            JSONObject o = data.getJSONObject(i);
            System.out.println(o);
            h = new Hra();
            miniParser(o,h);
            hry.add(h);
            //TODO: tridu  hra poslat ako triedu s potrebnymi udajmy na vypis okna
        }
        //TODO: pridat pripojenie alebo posielnie jsonu pre big parser


        //setInfo(hry.get(1));
        */
    }
    public static void miniParser(JSONObject o,Hra h ) throws JSONException {
        h.count = o.getInt("count");
        h.description = o.get("description").toString();
        h.genre = Integer.parseInt(o.get("genre").toString());
        h.id = o.getString("objectId");
        h.image = o.get("image").toString();
        h.language = o.get("language").toString();
        h.name = o.get("name").toString();
        h.pegi = o.get("pegi").toString();
        h.platform = o.getInt("platform");
        h.price = o.getInt("price");
        h.producer = o.get("producer").toString();
        h.rating = o.getInt("rating");
        h.video = o.get("video").toString();
    }
}
