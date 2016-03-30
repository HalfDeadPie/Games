package com.example.simon.gamesshop;

/**
 * Created by Simon on 26.3.2016.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class CustomAdapter extends BaseAdapter{
    String [] Name;
    String[] ImageURL;
    String[] UID;
    int[] Count;
    Context context;
    private static LayoutInflater inflater=null;
    public CustomAdapter(MainActivity mainActivity, String[] Name, int[] Count, String[] ImageURL, String[] UID) {
        // TODO Auto-generated constructor stub
        context=mainActivity;
        this.Name=Name;
        this.ImageURL=ImageURL;
        this.UID = UID;
        this.Count = Count;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return Name.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView NTV;
        TextView CTV;
        ImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.row, null);
        holder.NTV=(TextView) rowView.findViewById(R.id.name);
        holder.img=(ImageView) rowView.findViewById(R.id.cover_image);
        holder.CTV=(TextView) rowView.findViewById(R.id.count);

        holder.NTV.setText(Name[position]);
        holder.CTV.setText(Integer.toString(Count[position]));
        holder.img.setImageBitmap(getImage(ImageURL[position]));
        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "You Clicked " + Name[position], Toast.LENGTH_LONG).show();
            }
        });
        return rowView;
    }


    protected Bitmap getImage(String link){
        Bitmap b = null;
        AsyncTask<String, String, Bitmap> img = new Image();
        img.execute(link);
        try {
            return b = (Bitmap) img.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}