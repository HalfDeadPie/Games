package com.example.simon.gamesshop;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by simon on 17.3.2016.
 */
public class Connector extends AsyncTask<String, String, String> {

    private static final String ourURL ="https://api.backendless.com/v1/data/Game";
    @Override
    protected String doInBackground(String... params) {
        System.out.println("Vykonavam background "+params[0]);
        if(params[0].equals("GETALL")){
            return all();
        }else if(params[0].equals("POST")){

        }else if(params[0].equals("DELETE")){

        }else if(params[0].equals("GETDETAIL")){
            System.out.println(params[1].toString());
            return GetDetail(params[1].toString());
        }

        return null;
    }
    // get 1 konkretny zaznam s ID = Stringu ID
    // zaznam ulozi do g


    protected String all(){
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
            System.out.println(json.toString());
            return json.toString();

        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
    }
    protected String GetDetail(String ID){
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
                json.append(tmp).append("\n");
            }
            reader.close();
            System.out.println("Server vratil odpoved : " + json.toString());
            return json.toString();

        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
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

}
