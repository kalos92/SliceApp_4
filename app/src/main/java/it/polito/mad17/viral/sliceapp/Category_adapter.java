package it.polito.mad17.viral.sliceapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Kalos on 12/04/2017.
 */

public class Category_adapter extends BaseAdapter {
    Integer[] icons = new Integer[]{R.drawable.dollar,R.drawable.birthday, R.drawable.hotel, R.drawable.amusement,R.drawable.trip_aboard, R.drawable.clubbing, R.drawable.medic,R.drawable.urgent,R.drawable.default_spese,  R.drawable.tech, R.drawable.travelling };
    String [] names = new String[]{"General expenditure", "BirthDay", "Hotels", "Amusement", "Abroad Journey", "Clubbing", "Medical", "Priority", "Food", "Tecnology", "Travel"};
    private Context c;
    public Category_adapter(Context c){
        this.c=c;
    }
    @Override
    public int getCount() {
        return icons.length;
    }

    @Override
    public Object getItem(int position) {
        return names[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) c.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View row = convertView;
        Holder holder;
        if(row== null) {
            row = inflater.inflate(R.layout.spinner_category_row, parent, false);
            holder = new Holder();
            holder.category = (ImageView) row.findViewById(R.id.category);
            holder.category.setImageResource(icons[position]);
            holder.cat_descr = (TextView) row.findViewById(R.id.description);
            holder.cat_descr.setText(names[position]);
            row.setTag(holder);
        }
        else{
            holder = (Holder) row.getTag();
        }

        holder.category.setImageResource(icons[position]);
        holder.cat_descr.setText(names[position]);
        return row;
    }

    private static class Holder{
        ImageView category;
        TextView cat_descr;
    }
}
