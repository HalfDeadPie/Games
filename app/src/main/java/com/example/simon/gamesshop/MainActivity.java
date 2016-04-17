package com.example.simon.gamesshop;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

//import com.melnykov.fab.FloatingActionButton;

import com.shamanland.fab.FloatingActionButton;
import com.shamanland.fab.ShowHideOnScroll;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetAll();

        ListView listView = (ListView) findViewById(R.id.viewGL);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //listView.setOnTouchListener(new ShowHideOnScroll(fab, R.anim.floating_action_button_show, R.anim.floating_action_button_hide));
        fab.setColor(R.color.colorAccent);

        //fab.attachToListView(listView);
        // fab.setType(FloatingActionButton.TYPE_MINI);
        //fab.show(false);
        //fab.hide(false);
        //fab.setColorNormal(getResources().getColor(android.R.color.holo_red_light));
        //fab.setColorPressed(getResources().getColor(android.R.color.white));
        //fab.setShadow(true);
        //fab.setColorRipple(getResources().getColor(R.color.material_blue_500));
        // dorobit onclick listener

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        GetAll();
                                    }
                                }
        );
    }

    public boolean isConnectedToInternet() {

        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        //return true;// zakomentovat
        return false;
    }

    public void onRefresh() {
        GetAll();
    }

    public void GetAll() {
        if (isConnectedToInternet()) {
            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
            swipeRefreshLayout.setRefreshing(true);
            String Json = "";
            ArrayList<Game> GameList = new ArrayList<Game>();
            Connector con = new Connector(this);
            con.execute("GETALL");
            swipeRefreshLayout.setRefreshing(false);
        } else {
            makeAndShowDialogBox().show();
        }
    }

    private AlertDialog makeAndShowDialogBox() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Lost connection")
                .setMessage("You have lost your connection!")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        GetAll();
                    }
                })//setPositiveButton
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                })//setNegativeButton
                .create();
        return myQuittingDialogBox;
    }

    public void logOut(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    //Z json stringu zadaného ako argument vráti zoznam hier triedy Game zadaný ako argument
    public ArrayList<Game> AllListBuilder(String JsonString, ArrayList<Game> GameList) {
        try {
            JSONObject ParentObject = new JSONObject(JsonString);//mega json so všetkým, čo prišlo
            JSONArray ParentArray = ParentObject.getJSONArray("data");//pole jsonov - vybrané len data bez headeru
            if (ParentArray != null)//všetky hry v JSONe pridá do zoznamu
                for (int i = 0; i < ParentArray.length(); i++) {
                    GameList.add(ListParser(ParentArray.getJSONObject(i), new Game()));
                }
        } catch (Exception e) {
            Log.d("JSON", "Toto je chyba s JSONOM:" + e.getMessage());//debug výpis
        }
        return GameList;
    }

    //Funkcia, ktorá prevedie JSON jednej hry do objektu triedy Game
    public Game ListParser(JSONObject JG, Game SG) {
        try {
            SG.setName(JG.getString("name"));
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
        } catch (JSONException e) {
            Log.d("JSON", "Chyba pri parsovaní!");
        }
        return SG;
    }

    // po kliknuti n obrazok/ nazov hry sa pusti kod na zobrazenie detailu konkretnej hry
    public void toDetail(View view) {
        if (isConnectedToInternet()) {
            // ziska UID zaznamu a spusti novu aktivitu s tymto UID

            ViewGroup row = (ViewGroup) view.getParent();               // rodic
            //System.out.println(row);
            LinearLayout lay = (LinearLayout) row.findViewById(R.id.layout);
            //System.out.println(lay);
            TextView textView = (TextView) lay.findViewById(R.id.uid);  // dieta (skryty textview obsahujuci UID)
            //System.out.println(textView);
            String ID = textView.getText().toString();                  // z dietata nacitame UID

            //ProgressDialog Loading = ProgressDialog.show(MainActivity.this, "", "Loading. Please wait...", true);
            Intent intent = new Intent(this, detail.class);
            intent.putExtra("UID", ID);//Put your id to your next Intent
            startActivity(intent);
            finish();
        } else {
            makeAndShowDialogBox().show();
        }
    }

    public void sell(View view) {

    }

    public void buy(View view) {

    }

    public void toDetailFromName(View view) {
        // ziska UID zaznamu a spusti novu aktivitu s tymto UID

        LinearLayout lay = (LinearLayout) view.getParent();
        System.out.println(lay);
        ViewGroup table = (ViewGroup) lay.getParent();

        TextView textView = (TextView) table.findViewById(R.id.uid);  // dieta (skryty textview obsahujuci UID)
        System.out.println(textView);
        String ID = textView.getText().toString();                  // z dietata nacitame UID
        System.out.println("ID:     " + ID);
        // spustenie novej aktivity s UID


        ProgressDialog Loading = ProgressDialog.show(MainActivity.this, "", "Loading. Please wait...", true);
        Intent intent = new Intent(this, detail.class);
        intent.putExtra("UID", ID);//Put your id to your next Intent
        startActivity(intent);
        finish();

    }

    public void toEdit(View view) {
        LinearLayout lay = (LinearLayout) view.getParent();
        ViewGroup table = (ViewGroup) lay.getParent();

        TextView textView = (TextView) table.findViewById(R.id.uid);  // dieta (skryty textview obsahujuci UID)
        System.out.println(textView);
        String ID = textView.getText().toString();                  // z dietata nacitame UID
        System.out.println("ID:     " + ID);


        //ProgressDialog Loading = ProgressDialog.show(MainActivity.this, "", "Loading. Please wait...", true);
        //System.out.println("Som tu1");
        Intent intent = new Intent(this, edit_form.class);
        //System.out.println("Som tu2");
        intent.putExtra("ID", ID);//Put your id to your next Intent
        //System.out.println(intent);
        startActivity(intent);
        //System.out.println("Som tu4");
        finish();


    }

    public void BuyFromList(View view) {
        // ziska UID zaznamu a spusti novu aktivitu s tymto UID
        ViewGroup row = (ViewGroup) view.getParent();
        LinearLayout lay1 = (LinearLayout) row.getParent();
        LinearLayout lay2 = (LinearLayout) lay1.getParent();
        ViewGroup row2 = (ViewGroup) lay2.getParent();
        LinearLayout lay3 = (LinearLayout) row2.findViewById(R.id.layout);
        TextView textView1 = (TextView) lay3.findViewById(R.id.uid);  // dieta (skryty textview obsahujuci UID)
        String ID = textView1.getText().toString();
        TextView textView2 = (TextView) row2.findViewById(R.id.count);
        String count = textView2.getText().toString();

        Connector con = new Connector(this);
        con.execute("BUY", ID, count);

        int incremented = Integer.parseInt(count);
        incremented++;
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(incremented);
        String incString = sb.toString();
        textView2.setText(incString);
        textView2.setTextColor(Color.BLACK);
    }

    public void SellFromList(View view) {
        // ziska UID zaznamu a spusti novu aktivitu s tymto UID
        ViewGroup row = (ViewGroup) view.getParent();
        LinearLayout lay1 = (LinearLayout) row.getParent();
        LinearLayout lay2 = (LinearLayout) lay1.getParent();
        ViewGroup row2 = (ViewGroup) lay2.getParent();
        LinearLayout lay3 = (LinearLayout) row2.findViewById(R.id.layout);
        TextView textView1 = (TextView) lay3.findViewById(R.id.uid);  // dieta (skryty textview obsahujuci UID)
        String ID = textView1.getText().toString();
        TextView textView2 = (TextView) row2.findViewById(R.id.count);
        String count = textView2.getText().toString();
        int control = Integer.parseInt(count);
        if (control > 0) {

            Connector con = new Connector(this);
            con.execute("SELL", ID, count);

            int decremented = Integer.parseInt(count);
            decremented--;
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(decremented);
            String incString = sb.toString();
            textView2.setText(incString);
            if (decremented == 0) {
                textView2.setTextColor(Color.RED);
            } else {
                textView2.setTextColor(Color.BLACK);
            }
        } else {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            dlgAlert.setMessage("You are not able to sell this game!");
            dlgAlert.setTitle("Error!");
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        }
    }
}