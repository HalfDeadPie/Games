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

    private static final String ourURL ="https://api.backendless.com/v1/data/Game";
    @Override
    protected String doInBackground(String... params) {
        try {

            URL url = new URL(ourURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.addRequestProperty("application-id", "94B456C3-9A44-D044-FF87-A1D0AA589D00");
            connection.addRequestProperty("secret-key", "CDA1E692-BF29-7396-FF7F-0E699E669000");
            String randomstringForTesting = null;
            String testingStringForPush = null;
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
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
}
