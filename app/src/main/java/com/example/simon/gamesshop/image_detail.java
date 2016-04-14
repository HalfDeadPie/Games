package com.example.simon.gamesshop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class image_detail extends AppCompatActivity {

    private String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        Intent myIntent = getIntent();
        Bitmap b = (Bitmap) myIntent.getParcelableExtra("IMAGE");
        String id = myIntent.getStringExtra("UID");
        this.ID = id;

        ImageView image = (ImageView) findViewById(R.id.image);
        image.setImageBitmap(b);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        //ProgressDialog Loading = ProgressDialog.show(this, "", "Loading. Please wait...", true);

        Intent intent = new Intent(this, detail.class);
        intent.putExtra("UID", ID);//Put your id to your next Intent
        startActivity(intent);
        finish();
    }
}
