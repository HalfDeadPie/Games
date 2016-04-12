package com.example.simon.gamesshop;

/**
 * Created by Simon on 26.3.2016.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
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
    Bitmap[] ImageURL;
    String[] UID;
    int[] Count;
    Context context;
    private static LayoutInflater inflater=null;
    public CustomAdapter(AppCompatActivity mainActivity, String[] Name, int[] Count, Bitmap[] ImageURL, String[] UID) {
        // TODO Auto-generated constructor stub
        context=mainActivity;
        this.Name=Name;
        this.ImageURL=ImageURL;
        this.UID = UID;
        this.Count = Count;

        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        TextView ID;
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
        holder.ID=(TextView) rowView.findViewById(R.id.uid);

        holder.NTV.setText(Name[position]);
        holder.CTV.setText(Integer.toString(Count[position]));
        holder.img.setImageBitmap(ImageURL[position]);
        holder.ID.setText(UID[position]);
        return rowView;
    }
}