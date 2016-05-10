package com.example.simon.gamesshop;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Jaroslav Lišiak on 09.05.2016.
 */
public class IOConnector extends AsyncTask<String, String, String> {
    int aktivita = 0;
    private String SERVERNAME = "/data/SSJL";
    private Socket mSocket = null;
    private AppCompatActivity activity;
    public IOConnector(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {

    }


    @Override
    protected String doInBackground(String... params) {
        System.out.println("Background parameter 1:" + params[0]);

        if(params[0].equals("GETALL")){
            System.out.println("GETALL");
            aktivita = 1;   // v onPost spustame if ktory nastavi mainobrazovku
            //return getAll();
        }else if(params[0].equals("POST")){
            System.out.println("POST");
            aktivita = 6;   // v onPost spustame if ktory nastavi mainobrazovku
            return post();
        }

        return null;
    }


    private  void getAll() {
        if(mSocket == null){
            IOConnector();
            System.out.println("Pripajam sa na socket server!");
        }
    }

    private String post(){
        if(mSocket == null){
            System.out.println("Pripajam sa na socket server!");
            IOConnector();
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
        mSocket.emit("post",jsPost,odpoved);
        return "koniec";
    }

    private void IOConnector(){
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
                System.out.println("CONNECT");
                //socket.emit("foo", "hi");
                //socket.disconnect();
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println(args[0]);
                System.out.println("DISCONNECT");
            }
        });

    }
    private void connect(){

    }

    private void disconnect(){
        mSocket.disconnect();
        mSocket.off();
    }
}
