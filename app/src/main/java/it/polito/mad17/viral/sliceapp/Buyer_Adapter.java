package it.polito.mad17.viral.sliceapp;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.ArrayRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Kalos on 12/04/2017.
 */

class Buyer_Adapter extends BaseAdapter {

    private Context c;
    private List<String> data;
    private Gruppo g;
    public Buyer_Adapter(@NonNull Context context, @NonNull List<String> objects, Gruppo g) {
        data=objects;
        this.g=g;
        c=context;

    }
    public int getIdofMe(String userName){
        for(String s: data){
            if(s.equals(userName))
                return data.indexOf(s);
        }
        return 0;
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) c.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View row = convertView;
        Buyer_Adapter.Holder holder;
        Persona p=g.getPartecipante(data.get(position));

        if(row== null) {
            row = inflater.inflate(R.layout.buyer_row, parent, false);
            holder = new Buyer_Adapter.Holder();
            holder.profile_pic = (ImageView) row.findViewById(R.id.propic);
            holder.profile_pic.setImageDrawable(p.getProPic(c));
            holder.name = (TextView) row.findViewById(R.id.person);
            holder.name.setText(p.getName()+" "+ p.getSurname()+" has paid");
            row.setTag(holder);
        }
        else{
            holder = (Buyer_Adapter.Holder) row.getTag();
        }

        holder.profile_pic.setImageDrawable(p.getProPic(c));
        holder.name.setText(p.getName()+" "+ p.getSurname()+" has paid");
        return row;
    }


private static class Holder{
    ImageView profile_pic;
    TextView  name;

}



}