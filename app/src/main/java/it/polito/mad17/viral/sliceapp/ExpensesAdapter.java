package it.polito.mad17.viral.sliceapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by abdel on 28/03/2017.
 */

public class ExpensesAdapter extends ArrayAdapter<Spesa>  {

    private Context context;
    private int layoutResourceId;
    private ArrayList<Spesa> data = null;
    private Persona user;


    public ExpensesAdapter(Context context, int layoutResourceId, ArrayList<Spesa> objects,Persona user) {
        super(context, layoutResourceId, objects);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = objects;
        this.user=user;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        GroupHolder holder= null;

        BitmapManager bm = new BitmapManager(context,data.get(position).getCat().getImg(),50,70);

        Bitmap b=  bm.scaleDown(data.get(position).getCat().getImg(),100,true);


        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new GroupHolder();
            holder.imgIcon = (ImageView) row.findViewById(R.id.expIcon);
            TextView title = (TextView)row.findViewById(R.id.expName);
            title.setText(data.get(position).getNome());
            holder.txtTitle = title;
            holder.imgIcon.setImageBitmap(b);
            TextView price = (TextView)row.findViewById(R.id.expPrice);
            price.setText(Double.toString(data.get(position).getImporto()));
            holder.price= price;
            TextView buyer = (TextView)row.findViewById(R.id.buyer);
            buyer.setText("Buyer: "+data.get(position).getPagante().getUserName());
            holder.buyer=buyer;

            TextView currency = (TextView)row.findViewById(R.id.expCurrency);
            currency.setText("€");
            holder.currency= currency;


            if(!data.get(position).getDivisioni().get(user.getUserName()).getHaPagato())
               row.setBackgroundColor(context.getResources().getColor(R.color.row_non_pagate_bck));


            row.setTag(holder);
        }
        else
        {
            holder = (GroupHolder)row.getTag();
        }

        Spesa s1 = data.get(position);
        holder.txtTitle.setText(s1.getNome());
        holder.imgIcon.setImageBitmap(b);
        holder.currency.setText("€");
        holder.price.setText(Double.toString(s1.getImporto()));



        return row;
    }



    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    static class GroupHolder {
        ImageView imgIcon;
        TextView txtTitle;
        TextView price;
        TextView currency;
        TextView buyer;


    }
}

