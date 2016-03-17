package com.example.simon.gamesshop;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getAll(View view) {
        String out = "";
        AsyncTask<String, String, String> con = new Connector();
        Log.i("test", "testovanie");
        con.execute("");
        Log.i("test", "testovanie");
        try {
            Log.i("test", "testovanie");
            out = (String) con.get();
            Log.i("test", "testovanie");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        TextView outText = (TextView) findViewById(R.id.text);
        outText.setText(out);
    }


}