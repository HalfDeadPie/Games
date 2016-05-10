package com.example.simon.gamesshop;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Jaroslav Lišiak on 09.05.2016.
 */
public class IOConnector extends AsyncTask<String, String, ArrayList<Game>> {
    int aktivita = 0;
    private String SERVERNAME = "/data/SS-JL-MTAA";
    private Socket mSocket = null;
    private AppCompatActivity activity;
    String ID;
    public IOConnector(AppCompatActivity activity) {this.activity = activity;}
    private static final Object connectObj = new Object();
    private static final Object eventObj = new Object();
    private int error;

    @Override
    protected void onPreExecute() {

    }


    @Override
    protected ArrayList<Game> doInBackground(String... params) {
        System.out.println("Background parameter 1:" + params[0]);

        if(params[0].equals("GETALL")){
            System.out.println("GETALL");
            aktivita = 1;   // v onPost spustame if ktory nastavi mainobrazovku
            return getAll();
        }else if(params[0].equals("GETDETAIL")){
            aktivita = 2;   // v onPost spustame if ktoy nastavi detail obrazovku
            return getDetail(params[1]);
        }else if(params[0].equals("GETEDIT")){
            aktivita = 3;
            ID = params[1];
            return getDetail(params[1]);
        }else if(params[0].equals("SENDEDIT")){
            ID = params[1];
            System.out.println("Connector vypis:"+ID);
            aktivita = 4;
            return sendEdit();
        }
        else if(params[0].equals("BUY")){
            aktivita = 5;
            System.out.println("(EXECUTE)UID:"+params[1]+" COUNT:"+params[2]);
            Buy(params[1],Integer.parseInt(params[2]));
        }
        else if(params[0].equals("SELL")){
            aktivita = 6;
            System.out.println("(EXECUTE)UID:"+params[1]+" COUNT:"+params[2]);
            Sell(params[1], Integer.parseInt(params[2]));
        }
        else if(params[0].equals("DEL")){
            aktivita = 7;
            Delete(params[1]);
        }else if(params[0].equals("ADD")){
            System.out.println("ADD");
            aktivita = 8;
            addNew();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ArrayList<Game> GameList) {
        super.onPostExecute(GameList);
        if(aktivita == 1){   // getAll();
            setMain(GameList);
            //Loading.dismiss();
        }else if(aktivita == 2){    // getDetail()
            // setDetail(GameList.get(0)); // nastavi detail okno
            // Loading.dismiss();          // zastavi loading
        }else if(aktivita == 3){
            //setEdit(GameList.get(0));
            //Loading.dismiss();
        }

        else if(aktivita == 4) {
            // Loading.dismiss();
        }
        else if(aktivita == 5){//Buy() - inkrementovanie hodnoty počtu kusov
            //  Loading.dismiss();
        }
        else if(aktivita == 6){//Sell() - dekrementovanie hodnoty počtu kusov
            // Loading.dismiss();
        }
        else if(aktivita == 7){
            // Loading.dismiss();
        }

    }

    private void addNew() {
        // z activity(reprezentuje okno z ktoreho bola funkcia zavolana) sa vytiahnu vsetky texty potrebne pre vytvorenie zaznamu
    }

    private void Buy(String UID, int i) {

    }

    private void Sell(String UID, int i) {

    }

    private ArrayList<Game> sendEdit() {
        return  null;
    }



    public static void send(Socket sock,String event,JSONObject jObj,Ack ack){

            sock.emit(event, jObj, ack);


            synchronized(eventObj) {
                try {
                    eventObj.wait(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


    }

    private void Delete(String UID) {

    }



    private ArrayList<Game> getDetail(String UID) {
        // vracia instanciu triedy "Game" v ktorej su ulozene vsetky data stiahnute so servera
        // UID je ID stahovaneho zaznamu
        return null;
    }


    private  ArrayList<Game> getAll() {
        System.out.println("Spustam getAll");
        if(mSocket == null){
            connect();
            System.out.println("Pripojil som sa na socket server!");
        }

        ArrayList<Game> GameList = new ArrayList<Game>();
        JSONObject getQuery = new JSONObject();
        final JSONObject[] data = new JSONObject[1];

        try {
            System.out.println("Vytvaram Json s URL: " + SERVERNAME);
            getQuery.put("url", SERVERNAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Ack ack = new Ack(){
            @Override
            public void call(Object... os) {
                System.out.println("Prijal som ACK");
                if (os[0] != null) {
                    //System.out.println(os[0]);
                    JSONObject jsonAll = null;
                    try {
                        jsonAll = new JSONObject(os[0].toString());
                        int statusCode = jsonAll.getInt("statusCode");
                        if (statusCode != 200) {
                            System.out.println("Chyba, neprijal som 200 OK, ale chybu: " + statusCode);
                            error = statusCode;
                        }
                        else {
                            System.out.println("Prijal som data v poriadku");
                            data[0] = jsonAll.getJSONObject("body");
                            System.out.println("Ulozil  som data do premennej data[0] vypis: \n" + data[0]);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //openEvent();
            }
        };

        System.out.println("Posielam GET s JSONOM: " + getQuery);
        send(mSocket, "get", getQuery, ack);
        System.out.println("Poslal som GET!");
        if (error == 0) {
            System.out.println("Prijate data su: " +  data[0]);
            System.out.println("Posielam prijate data do parseru");
            AllListBuilder(data[0].toString(), GameList);
            System.out.println("Rozparsoval som data");
            return GameList;
        }
        return null;
    }

    private String post(){
        if(mSocket == null){
            System.out.println("Pripajam sa na socket server!");
            connect();
            System.out.println("Som pripojeny na socket server!");
        }
        JSONObject data = null;
        JSONObject jsObj = null;
        Ack odpoved = new Ack(){
            @Override
            public void call(Object... os) { //funkcia volana po prijati potvrdenia
                if (os[0] != null) {
                    System.out.println(os[0]);
                    JSONObject json = null;
                    try {
                        json = new JSONObject(os[0].toString());
                        System.out.println(json.getJSONObject("body"));
                    } catch (JSONException ex) {
                       System.out.println(ex);
                    }
                }
            }
        };
        data = new JSONObject();
        try {
            data.put("Nazov","Skusobna hra");
            data.put("price",200);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            jsObj = new JSONObject().put("data", data);
        } catch (JSONException ex) {
            System.out.println(ex);
        }
        JSONObject jsPost = new JSONObject();


        try {
            jsPost.put("url", SERVERNAME);
            jsPost.put("data", jsObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("post", jsPost, odpoved);
        return "koniec";
    }

    private void connect(){
        System.out.println("Spustam pripajanie na server");
        IO.Options opts = new IO.Options();
        opts.secure = false;
        opts.port = 1341;
        opts.reconnection = true;
        opts.forceNew = true;
        opts.timeout = 5000;
        try {
            mSocket = IO.socket("http://sandbox.touch4it.com:1341/?__sails_io_sdk_version=0.12.1",opts);
            mSocket.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //System.out.println(args[0]);
                System.out.println("--->>> CONNECT <<<---");
                System.out.println("Pripojenie bolo uspesne!");
                //socket.emit("foo", "hi");
                //socket.disconnect();
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println(args[0]);
                System.out.println("--->>> DISCONNECT <<<---");
                System.out.println("ODPOJENIE OD SERVERA!!!");
            }
        });
        System.out.println("Koniec pripajania na server");
    }

    private void disconnect(){
        mSocket.disconnect();
        mSocket.off();
    }































    public ArrayList<Game> AllListBuilder(String JsonString,  ArrayList<Game> GameList){
        if(aktivita ==1){   // getAll() - pride velky json
            try {
                JSONObject ParentObject = new JSONObject(JsonString);//mega json so všetkým, čo prišlo
                JSONArray ParentArray = ParentObject.getJSONArray("data");//pole jsonov - vybrané len data bez headeru
                System.out.println("Ciastocny parser vyparsoval array");
                if (ParentArray != null)//všetky hry v JSONe pridá do zoznamu
                    for (int i=0;i<ParentArray.length();i++){
                        System.out.println("parentArray[i]"+ParentArray.getJSONObject(i));

                        String id = ParentArray.getJSONObject(i).getString("id"); // id parsovaneho JSONU
                        JSONObject data = ParentArray.getJSONObject(i).getJSONObject("data");
                        System.out.println("ID: " + id);
                        System.out.println("Data ktore idem este parsovat: " + data);
                        GameList.add(ListParser(data, id, new Game()));
                    }
            }
            catch(Exception e)
            {
                Log.d("JSON", "Toto je chyba s JSONOM:" + e.getMessage());//debug výpis
            }
            return GameList;
        }else if(aktivita == 2 || aktivita == 3){    // getDetail() - pride maly json alebo // getedit()
            /*
            try {

                JSONObject ParentObject = new JSONObject(JsonString);//mega json so všetkým, čo prišlo
                // JSONArray ParentArray = ParentObject.getJSONArray("data");//pole jsonov - vybrané len data bez headeru
                if (ParentObject != null)//všetky hry v JSONe pridá do zoznamu
                    for (int i=0;i<ParentObject.length();i++){
                        GameList.add(ListParser(ParentObject, new Game()));
                    }
            }
            catch(Exception e)
            {
                Log.d("JSON", "Toto je chyba s JSONOM:" + e.getMessage());//debug výpis
            }
            return GameList;
            */
        }

        return GameList;
    }

    public Game ListParser(JSONObject JG,String id, Game SG){
        System.out.println("Som v parsery");
        System.out.println("Idem parsovat nasledujuce data:" + JG.toString());
        try {
            System.out.println("Som tu: NAME = " + JG.getString("name"));
            SG.setName(JG.getString("name"));
            SG.setReleaseDate(JG.getString("release_date"));
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
            //SG.setVideo(JG.getString("video"));
            SG.setUID(id);
            SG.setCoverImage(image(SG.getImage()));
            System.out.println("Idem vypisat SG");
            SG.vypis();
        } catch (JSONException e) {
            Log.d("JSON","Chyba pri parsovaní!");
        }
        return SG;
    }


    protected void setMain(ArrayList<Game> GameList){
        ListView viewGL = (ListView) activity.findViewById(R.id.viewGL);

        String[] Names = new String[GameList.size()];
        int[] Counts = new int[GameList.size()];
        Bitmap[] Images = new Bitmap[GameList.size()];
        String[] UIDs = new String[GameList.size()];

        for(int i=0; i<GameList.size() ;i++){
            Names[i] = GameList.get(i).getName();
            Counts[i] = GameList.get(i).getCount();
            Images[i]= GameList.get(i).getCoverImage();
            String ID = GameList.get(i).getUID();
            UIDs[i] = ID;
        }
        viewGL.setAdapter(new CustomAdapter(activity, Names, Counts, Images, UIDs));
    }
    private Bitmap image(String link) {
        System.out.println("Stary link: " + link);
        String newLink = link.replace("'\'","");
        System.out.println("Novy link: "+ newLink);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(newLink).getContent());
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("ERROR : Vraciam null - stahovanie obrazku sa nepodarilo");
        return null;
    }
}
