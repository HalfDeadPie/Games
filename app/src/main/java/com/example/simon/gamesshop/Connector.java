package com.example.simon.gamesshop;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by simon on 17.3.2016.
 */
public class Connector extends AsyncTask<String, String, ArrayList<Game>> {

    private AppCompatActivity activity;
    private ProgressDialog Loading;
    public Connector(AppCompatActivity activity) {
        this.activity = activity;
    }
    private static final String ourURL ="https://api.backendless.com/v1/data/Game";
    private int aktivita;   // 1 - getAll() 2-getDetail()   3-getEdit()     4-sendEdit();
    private String ID;
    @Override
    protected void onPreExecute() {//pred vykonaním doInBackground načíta a zobrazí loader
        super.onPreExecute();
        Loading = ProgressDialog.show(activity, "", "Loading. Please wait...", true);
    }

    @Override
    protected ArrayList<Game> doInBackground(String... params) {//vykonavanie v pozadí

        //1. FUNKCIA VYTVORÍ ZOZNAM ZO VŠETKÝCH HIER
        if(params[0].equals("GETALL")){
            aktivita = 1;   // v onPost spustame if ktory nastavi mainobrazovku
            return getAll();
            //2. FUNKCIA NA PRIDANIE ZÁZNAMU O HRE
        }else if(params[0].equals("POST")){

            //3. FUNKCIA NA ZMAZANIE ZÁZNAMU O HRE
        }else if(params[0].equals("DELETE")){
            //4. FUNKCIA, KTORA ZOBRAZÍ DETAILNÝ ZÁZNAM O HRE
        }else if(params[0].equals("GETDETAIL")){
            aktivita = 2;   // v onPost spustame if ktoy nastavi detail obrazovku
            return getDetail(params[1]);
        }else if(params[0].equals("GETEDIT")){
            aktivita = 3;
            ID = params[1];
            return getDetail(params[1]);
        }else if(params[0].equals("SENDEDIT")){
            aktivita = 4;
            return sendEdit();
        }

        return null;
    }

    private ArrayList<Game> sendEdit() {

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
        // pridat zostavenie jsonu a poslanie

        return null;
    }

    private void send(Game g){
        try {
            URL url = new URL(ourURL+"/"+ID);
            System.out.println(url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.addRequestProperty("application-id", "94B456C3-9A44-D044-FF87-A1D0AA589D00");
            connection.addRequestProperty("secret-key", "CDA1E692-BF29-7396-FF7F-0E699E669000");
            connection.addRequestProperty("application-type", "REST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            //tu zostavím json
            String json = "";
            JSONObject jsonObject;
            jsonObject = new JSONObject();
            jsonObject.put("iamge",g.getImage());
            jsonObject.put("pegi",g.getPegi());
            jsonObject.put("rating",g.getRating());
            jsonObject.put("count",g.getCount());
            jsonObject.put("description",g.getDescription());
            jsonObject.put("language",g.getLanguage());
            jsonObject.put("platform",g.getPlatform());
            jsonObject.put("release_date",g.getReleaseDate());
            jsonObject.put("price",g.getPrice());
            jsonObject.put("name",g.getName());
            jsonObject.put("genre",g.getGenre());
            jsonObject.put("producer",g.getProducer());
            json = jsonObject.toString();
            System.out.println(json);

            //tu by sa to malo odoslať
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            OutputStreamWriter out = new OutputStreamWriter(
                    connection.getOutputStream());
            out.write(json);
            out.flush();
            out.close();
            System.out.println(connection.getResponseCode());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private ArrayList<Game> getDetail(String ID) {
        ArrayList<Game> GameList = new ArrayList<Game>();
        System.out.println(ID);
        try {
            URL url = new URL(ourURL+"/"+ID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            System.out.println(url);
            connection.addRequestProperty("application-id", "94B456C3-9A44-D044-FF87-A1D0AA589D00");
            connection.addRequestProperty("secret-key", "CDA1E692-BF29-7396-FF7F-0E699E669000");
            connection.addRequestProperty("application-type", "REST");
            //connection.addRequestProperty("objectId", ID);
            BufferedReader reader;
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder json = new StringBuilder(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null) {
                json.append(tmp).append("\n");  // vytvorime jeden velky json
            }

            reader.close();

            AllListBuilder(json.toString(), GameList);
            AllListBuilder(json.toString(), GameList);//vytvorenie zoznamu hier z JSONu
            System.out.println("ID je: " + ID);
            System.out.println("Velkost GameList-u je: " + GameList.size());
            //json vlozime do GameListu, kedze detail zobrazuje iba 1 zaznam gamelist obsahuje len 1 zaznam
            return GameList;//vrátenie kompletného zoznamu hier s potrebnými atribútmi
            // returnujeme gamelist a zaroven spustame onpost

            //System.out.println("Server vratil odpoved : " + json.toString());
            //return json.toString();

        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
    }

    private  ArrayList<Game> getAll() {
        ArrayList<Game> GameList = new ArrayList<Game>();
        try {
            URL url = new URL(ourURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("application-id", "94B456C3-9A44-D044-FF87-A1D0AA589D00");
            connection.addRequestProperty("secret-key", "CDA1E692-BF29-7396-FF7F-0E699E669000");
            BufferedReader reader = new BufferedReader( new InputStreamReader(connection.getInputStream()));
            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null) {
                json.append(tmp).append("\n");
            }
            reader.close();
            AllListBuilder(json.toString(), GameList);//vytvorenie zoznamu hier z JSONu
            return GameList;//vrátenie kompletného zoznamu hier s potrebnými atribútmi
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Game> GameList) {
        super.onPostExecute(GameList);
        if(aktivita == 1){   // getAll();
            setMain(GameList);
            Loading.dismiss();
        }else if(aktivita == 2){    // getDetail()
            setDetail(GameList.get(0)); // nastavi detail okno
            Loading.dismiss();          // zastavi loading
        }else if(aktivita == 3){
            setEdit(GameList.get(0));
            Loading.dismiss();
        }
        else if(aktivita == 4){

            Loading.dismiss();
        }


    }

    private void setEdit(Game g) {
        TextView detail_description = (TextView) activity.findViewById(R.id.edit_form_decription);
        TextView detail_name = (TextView) activity.findViewById(R.id.edit_form_title);
        TextView detail_image = (TextView) activity.findViewById(R.id.edit_form_image);
        //ExpandableListView detail_pegi = (ExpandableListView) activity.findViewById(R.id.edit_form_pegi_list);
        TextView detail_pegi =(TextView) activity.findViewById(R.id.edit_form_pegi);
        TextView detail_rating = (TextView) activity.findViewById(R.id.edit_form_rating);
        TextView detail_price = (TextView) activity.findViewById(R.id.edit_form_price);
        TextView detail_date = (TextView) activity.findViewById(R.id.edit_form_release);
        TextView detail_count = (TextView) activity.findViewById(R.id.edit_form_count);
        TextView detail_producer = (TextView) activity.findViewById(R.id.edit_form_producer);
        TextView detail_language = (TextView) activity.findViewById(R.id.edit_form_languages);
       // TextView uid = (TextView) activity.findViewById(R.id.uid);

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
        RadioButton p2 = (RadioButton) activity.findViewById(R.id.radioButton3ONE);
        RadioButton p3 = (RadioButton) activity.findViewById(R.id.radioButton2_Wii);
        RadioButton p4 = (RadioButton) activity.findViewById(R.id.radioButton5PS4);
        RadioButton p5 = (RadioButton) activity.findViewById(R.id.radioButton6360);


        detail_name.setText(g.getName());
        detail_image.setText(g.getImage());
        detail_pegi.setText(g.getPegi());
        detail_rating.setText(Integer.toString(g.getRating()) + "%");
        detail_price.setText(Integer.toString(g.getPrice()) + " €");
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
            // System.out.println("Nastavuje hre[" + i + "] ID: " +ID);
            UIDs[i] = ID;
        }
        viewGL.setAdapter(new CustomAdapter(activity, Names, Counts, Images, UIDs));
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



        detail_name.setText(g.getName());
        detail_image.setImageBitmap(g.getCoverImage());
        detail_pegi.setText(g.getPegi());
        detail_rating.setText(Integer.toString(g.getRating()));
        detail_price.setText(Integer.toString(g.getPrice()));
        detail_description.setText(g.getDescription());
        detail_count.setText(Integer.toString(g.getCount()));
        detail_date.setText(g.getReleaseDate());
        detail_producer.setText(g.getProducer());
        detail_genre.setText(Integer.toString(g.getGenre()));
        detail_language.setText(g.getLanguage());
        detail_platform.setText(Integer.toString(g.getPlatform()));
    }




    public static String httpGet(String ID) throws IOException {
        ID = "267FBCE3-25CF-DC4E-FF67-B9311AE18E00";
        URL url = new URL(ourURL+"/"+ID);
        HttpURLConnection conn =(HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.addRequestProperty("application-id", "94B456C3-9A44-D044-FF87-A1D0AA589D00");
        conn.addRequestProperty("secret-key", "CDA1E692-BF29-7396-FF7F-0E699E669000");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }

        // Buffer the result into a string
        BufferedReader rd = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        conn.disconnect();
        rd.close();

        conn.disconnect();
        return sb.toString();
    }

    public static String httpPost(String urlStr, String[] paramName,String[] paramVal) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn =(HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setAllowUserInteraction(false);
        conn.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");

        // Create the form content
        OutputStream out = conn.getOutputStream();
        Writer writer = new OutputStreamWriter(out, "UTF-8");
        for (int i = 0; i < paramName.length; i++) {
            writer.write(paramName[i]);
            writer.write("=");
            writer.write(URLEncoder.encode(paramVal[i], "UTF-8"));
            writer.write("&");
        }
        writer.close();
        out.close();

        if (conn.getResponseCode() != 200) {
            throw new IOException(conn.getResponseMessage());
        }

        // Buffer the result into a string
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();

        conn.disconnect();
        return sb.toString();
    }

    private Bitmap image(String link) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(link).getContent());
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("ERROR : Vraciam null - stahovanie obrazku sa nepodarilo");
        return null;
    }

    public ArrayList<Game> AllListBuilder(String JsonString,  ArrayList<Game> GameList){
        if(aktivita ==1){   // getAll() - pride velky json
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
        }else if(aktivita == 2 || aktivita == 3){    // getDetail() - pride maly json alebo // getedit()
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
        }

        return GameList;
    }

    public Game ListParser(JSONObject JG, Game SG){
        try {
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
            SG.setVideo(JG.getString("video"));
            SG.setUID(JG.getString("objectId"));
            SG.setCoverImage(image(SG.getImage()));
        } catch (JSONException e) {
            Log.d("JSON","Chyba pri parsovaní!");
        }
        return SG;
    }

}
