package com.example.simon.gamesshop;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by simon on 17.3.2016.
 */
public class Connector extends AsyncTask<String, String, String> {

    private static final String ourURL ="https://api.backendless.com/v1/data/cds";
    @Override
    protected String doInBackground(String... params) {
        try {

            URL url = new URL(ourURL);
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.addRequestProperty("application-id",
                    "F9615D38-AE50-A389-FF5E-8BD658331900");
            connection.addRequestProperty("secret-key",
                    "A4082182-4C7A-E9E8-FFF4-2D69B1025700");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null) {
                json.append(tmp).append("\n");
            }
            reader.close();
            return json.toString();

        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
    }
}
