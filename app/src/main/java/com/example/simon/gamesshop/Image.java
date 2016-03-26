package com.example.simon.gamesshop;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Jaroslav Lišiak on 26.3.2016.
 */
public class Image extends AsyncTask<String, String, Bitmap> {

    @Override
    protected Bitmap doInBackground(String... params) {
        return download(params[0]);
    }



    public Bitmap download(String imageUrl){
        try {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent());
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
