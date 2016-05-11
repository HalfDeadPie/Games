package com.example.simon.gamesshop;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
    private static int RECEIVETIME = 1000;
    String ID;
    private ProgressDialog Loading;
    public IOConnector(AppCompatActivity activity) {this.activity = activity;}
    private static final Object connectObj = new Object();
    private static final Object eventObj = new Object();

    private int error;

    @Override
    protected void onPreExecute() {//pred vykonaním doInBackground načíta a zobrazí loader
        super.onPreExecute();
        Loading = ProgressDialog.show(activity, "", "Loading. Please wait...", true);
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
            System.out.println("Connector SENDEDIT vypis:"+ID);
            aktivita = 4;
            return sendEdit(ID);
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
            setDetail(GameList.get(0)); // nastavi detail okno
            // Loading.dismiss();          // zastavi loading
        }else if(aktivita == 3){
            setEdit(GameList.get(0));
            //Loading.dismiss();
        }

        else if(aktivita == 4) {
            // Loading.dismiss();
        }
        Loading.dismiss();
    }

    private void addNew() {
        System.out.println("addNew");
        // z activity(reprezentuje okno z ktoreho bola funkcia zavolana) sa vytiahnu vsetky texty potrebne pre vytvorenie zaznamu
        RadioGroup groupGenre = (RadioGroup) activity.findViewById(R.id.add_form_groupGenre);
        RadioGroup groupPlatform = (RadioGroup) activity.findViewById(R.id.add_form_groupPlatform);

        int radioButtonID = groupGenre.getCheckedRadioButtonId();
        View radioButton = groupGenre.findViewById(radioButtonID);
        int GenreID = groupGenre.indexOfChild(radioButton);     // cislo zanru

        int radioButtonIDP = groupPlatform.getCheckedRadioButtonId();
        View radioButtonP = groupPlatform.findViewById(radioButtonIDP);
        int PlatformID = groupPlatform.indexOfChild(radioButtonP);  // cislo platformy

        TextView detail_description = (TextView) activity.findViewById(R.id.add_form_decription);
        TextView detail_name = (TextView) activity.findViewById(R.id.add_form_title);
        TextView detail_image = (TextView) activity.findViewById(R.id.add_form_image);
        TextView detail_pegi =(TextView) activity.findViewById(R.id.add_form_pegi);
        TextView detail_rating = (TextView) activity.findViewById(R.id.add_form_rating);
        TextView detail_price = (TextView) activity.findViewById(R.id.add_form_price);
        TextView detail_date = (TextView) activity.findViewById(R.id.add_form_release);
        TextView detail_count = (TextView) activity.findViewById(R.id.add_form_count);
        TextView detail_producer = (TextView) activity.findViewById(R.id.add_form_producer);
        TextView detail_language = (TextView) activity.findViewById(R.id.add_form_languages);

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

        send(g);
    }

    private void send(Game g){
        try {
            //odosielanie
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

                            if(json.getInt("statusCode")==200){
                                System.out.println("ANSWER OK : "+json.getInt("statusCode"));
                                activity.startActivity(new Intent(activity,MainActivity.class));
                                activity.finish();
                            }
                            else{
                                System.out.println("ANSWER WRONG: "+json.getInt("statusCode"));
                            }
                        } catch (JSONException ex) {
                            System.out.println(ex);
                        }
                    }
                    synchronized(eventObj) {

                        eventObj.notifyAll();

                    }
                }
            };
            data = new JSONObject();
            try {
                System.out.println("building json");
                //tu zostavím json
                String json = "";
                JSONObject jsonObject;



                data.put("image",g.getImage());
                data.put("pegi", g.getPegi());
                data.put("rating",g.getRating());
                data.put("count",g.getCount());
                data.put("description",g.getDescription());
                data.put("language",g.getLanguage());
                data.put("platform",g.getPlatform());
                data.put("release_date",g.getReleaseDate());
                data.put("price",g.getPrice());
                data.put("name", g.getName());
                data.put("genre",g.getGenre());
                data.put("producer",g.getProducer());

                String printJSON = data.toString();
                System.out.println(printJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //pridám do jsonu hlavicku
            try {
                jsObj = new JSONObject().put("data", data);
            } catch (JSONException ex) {
                System.out.println(ex);
            }
            System.out.println("Pridana hlavicka do JSONU");
            JSONObject jsPost = new JSONObject();
            try {
                jsPost.put("url", SERVERNAME);
                jsPost.put("data", jsObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println("Odosielanie . . .");
            mSocket.emit("post",jsPost,odpoved);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Buy(String UID, int i) {
        System.out.println("Som v Buy()");
        i++;
        ArrayList<Game> games = new ArrayList<Game>();
        games = getDetail(UID);
        Game g = new Game();
        System.out.println("V Buy() - ziskal som detail podla UID: " + UID);
        System.out.println("Velkost Listu je: " + games.size());
        if(games.size() > 0) {
            g = games.get(0);
            System.out.println("v Buy() idem poslat novy json");
            g.setCount(i);
            sendUpdate(g,UID);
        }else{
            System.out.println("Neuspesne stiahnutie detailu");
        }

    }

    private void Sell(String UID, int i) {
        i--;
        ArrayList<Game> games = new ArrayList<Game>();
        games = getDetail(UID);
        Game g = new Game();
        System.out.println("V Sell() - ziskal som detail podla UID: " + UID);
        System.out.println("Velkost Listu je: " + games.size());
        if(games.size() > 0) {
            g = games.get(0);
            System.out.println("v Sell() idem poslat novy json");
            g.setCount(i);
            sendUpdate(g,UID);
        }else{
            System.out.println("Neuspesne stiahnutie detailu");
        }

    }

    private ArrayList<Game> sendEdit(String ID) {
        System.out.println("Intro - sendEdit for:"+ID);
        RadioGroup groupGenre = (RadioGroup) activity.findViewById(R.id.groupGenre);
        RadioGroup groupPlatform = (RadioGroup) activity.findViewById(R.id.groupPlatform);

        int radioButtonID = groupGenre.getCheckedRadioButtonId();
        View radioButton = groupGenre.findViewById(radioButtonID);
        int GenreID = groupGenre.indexOfChild(radioButton);     // cislo zanru

        int radioButtonIDP = groupPlatform.getCheckedRadioButtonId();
        View radioButtonP = groupPlatform.findViewById(radioButtonIDP);
        int PlatformID = groupPlatform.indexOfChild(radioButtonP);  // cislo platformy

        TextView detail_description = (TextView) activity.findViewById(R.id.edit_form_decription);
        TextView detail_name = (TextView) activity.findViewById(R.id.edit_form_title);
        TextView detail_image = (TextView) activity.findViewById(R.id.edit_form_image);
        TextView detail_pegi =(TextView) activity.findViewById(R.id.edit_form_pegi);
        TextView detail_rating = (TextView) activity.findViewById(R.id.edit_form_rating);
        TextView detail_price = (TextView) activity.findViewById(R.id.edit_form_price);
        TextView detail_date = (TextView) activity.findViewById(R.id.edit_form_release);
        TextView detail_count = (TextView) activity.findViewById(R.id.edit_form_count);
        TextView detail_producer = (TextView) activity.findViewById(R.id.edit_form_producer);
        TextView detail_language = (TextView) activity.findViewById(R.id.edit_form_languages);
        TextView detail_uid = (TextView) activity.findViewById(R.id.edit_form_uid);

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

        System.out.println("Ready to call sendUpdate for:"+ID);

        sendUpdate(g,ID);
        return null;
    }



    public static void send(Socket sock,String event,JSONObject jObj,Ack ack){

        sock.emit(event, jObj, ack);
        synchronized(eventObj) {


            try {
                eventObj.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }

    private void Delete(String UID) {
        System.out.println("Idem vymazat zaznam s ID: "+ UID);
        if(mSocket == null){
            connect();
            System.out.println("Pripojil som sa na socket server!");
        }
        JSONObject getQuery = new JSONObject();

        try {
            System.out.println("Vytvaram Json s URL: " + SERVERNAME+"/"+UID);
            getQuery.put("url", SERVERNAME+"/"+UID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Ack ack = new Ack(){
            @Override
            public void call(Object... os) {
                System.out.println("Prijal som ACK");
                if (os[0] != null) {
                    JSONObject jsonAll = null;
                    try {
                        jsonAll = new JSONObject(os[0].toString());
                        int statusCode = jsonAll.getInt("statusCode");
                        if (statusCode != 200) {
                            System.out.println("Chyba, neprijal som 200 OK, ale chybu: " + statusCode);
                            error = statusCode;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                synchronized(eventObj) {

                    eventObj.notifyAll();

                }
            }

        };
        System.out.println("Posielam DELETE s JSONOM: " + getQuery);
        send(mSocket, "delete", getQuery, ack);
    }

    //nastavenie formularu na upravu hry
    private void setEdit(Game g) {
        TextView detail_description = (TextView) activity.findViewById(R.id.edit_form_decription);
        TextView detail_name = (TextView) activity.findViewById(R.id.edit_form_title);

        TextView detail_image = (TextView) activity.findViewById(R.id.edit_form_image);
        //ExpandableListView detail_pegi = (ExpandableListView) activity.findViewById(R.id.edit_form_pegi_list);
        TextView detail_pegi =(TextView) activity.findViewById(R.id.edit_form_pegi);

        //ImageView detail_image = (ImageView) activity.findViewById(R.id.edit_form_image);
        // TextView detail_pegi = (TextView) activity.findViewById(R.id.edit_form_pegi_list);

        TextView detail_rating = (TextView) activity.findViewById(R.id.edit_form_rating);
        TextView detail_price = (TextView) activity.findViewById(R.id.edit_form_price);
        TextView detail_date = (TextView) activity.findViewById(R.id.edit_form_release);
        TextView detail_count = (TextView) activity.findViewById(R.id.edit_form_count);
        TextView detail_producer = (TextView) activity.findViewById(R.id.edit_form_producer);
        TextView detail_language = (TextView) activity.findViewById(R.id.edit_form_languages);
        TextView uid = (TextView) activity.findViewById(R.id.edit_form_uid);

        RadioButton g0 = (RadioButton) activity.findViewById(R.id.radioButtonAction);
        RadioButton g1 = (RadioButton) activity.findViewById(R.id.radioButtonAdventure);
        RadioButton g2 = (RadioButton) activity.findViewById(R.id.radioButtonCasual);
        RadioButton g3 = (RadioButton) activity.findViewById(R.id.radioButtonIndie);
        RadioButton g4 = (RadioButton) activity.findViewById(R.id.radioButtonMM);
        RadioButton g5 = (RadioButton) activity.findViewById(R.id.radioButtonRacing);
        RadioButton g6 = (RadioButton) activity.findViewById(R.id.radioButtonRPG);
        RadioButton g7 =(RadioButton)  activity.findViewById(R.id.radioButtonSimulation);
        RadioButton g8 = (RadioButton) activity.findViewById(R.id.radioButtonSports);
        RadioButton g9 = (RadioButton) activity.findViewById(R.id.radioButtonStrategy);

        RadioButton p0 = (RadioButton) activity.findViewById(R.id.radioButtonPC);
        RadioButton p1 = (RadioButton) activity.findViewById(R.id.radiobutton4PS3);
        RadioButton p2 = (RadioButton) activity.findViewById(R.id.radioButton5PS4);
        RadioButton p3 = (RadioButton) activity.findViewById(R.id.radioButton3ONE);
        RadioButton p4 = (RadioButton) activity.findViewById(R.id.radioButton6360);
        RadioButton p5 = (RadioButton) activity.findViewById(R.id.radioButton2_Wii);


        detail_name.setText(g.getName());

        detail_image.setText(g.getImage());
        detail_pegi.setText(g.getPegi());

        //detail_image.setImageBitmap(g.getCoverImage());
        // detail_pegi.setText(g.getPegi());
        uid.setText(g.getUID());
        detail_rating.setText(Integer.toString(g.getRating()));
        detail_price.setText(Integer.toString(g.getPrice()));
        detail_description.setText(g.getDescription());
        detail_count.setText(Integer.toString(g.getCount()));
        detail_date.setText(g.getReleaseDate());
        detail_producer.setText(g.getProducer());
        detail_language.setText(g.getLanguage());

        System.out.println("Genre nastavujem na: "+g.getGenre());
        switch (g.getGenre()){
            case 0:
                System.out.println("Nastavujem 0 - action");
                g0.setChecked(true);
                break;
            case 1:
                g1.setChecked(true);
                break;
            case 2:
                g2.setChecked(true);
                break;
            case 3:
                g3.setChecked(true);
                break;
            case 4:
                g4.setChecked(true);
                break;
            case 5:
                g5.setChecked(true);
                break;
            case 6:
                g6.setChecked(true);
                break;
            case 7:
                g7.setChecked(true);
                break;
            case 8:
                g8.setChecked(true);
                break;
            case 9:
                g9.setChecked(true);
                break;
        }
        switch(g.getPlatform()){
            case 0:
                p0.setChecked(true);
                break;
            case 1:
                p1.setChecked(true);
                break;
            case 2:
                p2.setChecked(true);
                break;
            case 3:
                p3.setChecked(true);
                break;
            case 4:
                p4.setChecked(true);
                break;
            case 5:
                p5.setChecked(true);
                break;
        }
    }

    private ArrayList<Game> getDetail(String UID) {
        // vracia instanciu triedy "Game" v ktorej su ulozene vsetky data stiahnute so servera
        // UID je ID stahovaneho zaznamu
        System.out.println("GET DETAIL cez socket IO s id: " + UID);

        if(mSocket == null){
            connect();
            System.out.println("Pripojil som sa na socket server!");
        }
        ArrayList<Game> GameList = new ArrayList<Game>();
        JSONObject getQuery = new JSONObject();
        final JSONObject[] data = new JSONObject[1];

        try {
            System.out.println("Vytvaram Json s URL: " + SERVERNAME+"/"+UID);
            getQuery.put("url", SERVERNAME+"/"+UID);
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
                synchronized(eventObj) {

                    eventObj.notifyAll();

                }
            }
        };

        System.out.println("Posielam GET s JSONOM: " + getQuery);
        send(mSocket, "get", getQuery, ack);
        System.out.println("Poslal som GET!");
        if (error == 0) {
            System.out.println("Prijate data su: " +  data[0]);
            System.out.println("Posielam prijate data do parseru");
            AllListBuilder(data[0].toString(), GameList);
            System.out.println("Rozparsoval som data - velkost listu: "+ GameList.size());
            return GameList;
        }
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
                synchronized(eventObj) {

                    eventObj.notifyAll();

                }
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
                System.out.println("--->>> CONNECT <<<---");
                System.out.println("Pripojenie bolo uspesne!");
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

    private void sendUpdate(Game g,String UID){
        System.out.println("Sending update to:" +UID);
        try {
            //odosielanie
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

                            if(json.getInt("statusCode")==200){
                                System.out.println("ANSWER OK : "+json.getInt("statusCode"));
                                activity.startActivity(new Intent(activity,MainActivity.class));
                                activity.finish();
                            }
                            else{
                                System.out.println("ANSWER WRONG: "+json.getInt("statusCode"));
                            }
                        } catch (JSONException ex) {
                            System.out.println(ex);
                        }
                    }
                    synchronized(eventObj) {

                        eventObj.notifyAll();

                    }
                }
            };
            data = new JSONObject();
            try {
                System.out.println("building json");
                //tu zostavím json
                String json = "";
                JSONObject jsonObject;

                data.put("image",g.getImage());
                data.put("pegi", g.getPegi());
                data.put("rating",g.getRating());
                data.put("count",g.getCount());
                data.put("description",g.getDescription());
                data.put("language",g.getLanguage());
                data.put("platform",g.getPlatform());
                data.put("release_date",g.getReleaseDate());
                data.put("price",g.getPrice());
                data.put("name", g.getName());
                data.put("genre",g.getGenre());
                data.put("producer",g.getProducer());

                String printJSON = data.toString();
                System.out.println(printJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //pridám do jsonu hlavicku
            try {
                jsObj = new JSONObject().put("data", data);
            } catch (JSONException ex) {
                System.out.println(ex);
            }
            System.out.println("Pridana hlavicka do JSONU");
            JSONObject jsPost = new JSONObject();
            try {
                jsPost.put("url", SERVERNAME+"/"+UID);
                jsPost.put("data", jsObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println("Odosielanie . . . JSON: " + jsPost);
            mSocket.emit("put",jsPost,odpoved);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        }else if(aktivita == 2 || aktivita == 3 ||aktivita == 5||aktivita == 6){    // getDetail() - pride maly json alebo // getedit()
            System.out.println("Parsujem pre aktivitu 2");
            JSONObject data= null;
            try {

                JSONObject ParentObject = new JSONObject(JsonString);//mega json so všetkým, čo prišlo

                // JSONArray ParentArray = ParentObject.getJSONArray("data");//pole jsonov - vybrané len data bez headeru
                if (ParentObject != null)//všetky hry v JSONe pridá do zoznamu
                    data = ParentObject.getJSONObject("data");
                    String id = ParentObject.getString("id");
                    System.out.println(ParentObject.toString());
                    GameList.add(ListParser(data, id, new Game()));

            }
            catch(Exception e)
            {
                Log.d("JSON", "Toto je chyba s JSONOM:" + e.getMessage());//debug výpis
            }
            return GameList;

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
            //SG.vypis();
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
    protected void setDetail(Game g){
        // z premennej activity (aktualne detail okno) vyberame vsetky komponenty a
        // nastavime do nich data
        TextView detail_description = (TextView) activity.findViewById(R.id.detail_description);
        TextView detail_name = (TextView) activity.findViewById(R.id.detail_name);
        ImageView detail_image = (ImageView) activity.findViewById(R.id.detail_image);
        TextView detail_pegi = (TextView) activity.findViewById(R.id.detail_pegi);
        TextView detail_rating = (TextView) activity.findViewById(R.id.detail_rating);
        TextView detail_price = (TextView) activity.findViewById(R.id.detail_price);
        TextView detail_date = (TextView) activity.findViewById(R.id.detail_date);
        TextView detail_count = (TextView) activity.findViewById(R.id.detail_count);
        TextView detail_producer = (TextView) activity.findViewById(R.id.detail_producer);
        TextView detail_genre = (TextView) activity.findViewById(R.id.detail_genre);
        TextView detail_language = (TextView) activity.findViewById(R.id.detail_language);
        TextView detail_platform = (TextView) activity.findViewById(R.id.detail_platform);
        TextView detail_id = (TextView) activity.findViewById(R.id.detail_id);


        detail_name.setText(g.getName());
        detail_image.setImageBitmap(g.getCoverImage());
        detail_pegi.setText(g.getPegi());
        detail_rating.setText(Integer.toString(g.getRating())+"%");
        detail_price.setText(Integer.toString(g.getPrice())+"€");
        detail_description.setText(g.getDescription());
        detail_count.setText(Integer.toString(g.getCount()));
        if(g.getCount()==0){
            detail_count.setTextColor(Color.RED);
        }
        detail_date.setText(g.getReleaseDate());
        detail_producer.setText(g.getProducer());
        switch(g.getGenre()){
            case 0: detail_genre.setText("Action");break;
            case 1: detail_genre.setText("Adventure");break;
            case 2: detail_genre.setText("Casual");break;
            case 3: detail_genre.setText("Indie");break;
            case 4: detail_genre.setText("Massive Multiplayer");break;
            case 5: detail_genre.setText("Racing");break;
            case 6: detail_genre.setText("RPG");break;
            case 7: detail_genre.setText("Simulation");break;
            case 8: detail_genre.setText("Sports");break;
            case 9: detail_genre.setText("Strategy");break;
        }
        detail_language.setText(g.getLanguage());
        switch(g.getPlatform()){
            case 0: detail_platform.setText("PC");break;
            case 1: detail_platform.setText("PS3");break;
            case 2: detail_platform.setText("PS4");break;
            case 3: detail_platform.setText("XBOX ONE");break;
            case 4: detail_platform.setText("XBOX 360");break;
            case 5: detail_platform.setText("Nintendo Wii");break;
        }
        detail_id.setText(g.getUID());
    }

}
