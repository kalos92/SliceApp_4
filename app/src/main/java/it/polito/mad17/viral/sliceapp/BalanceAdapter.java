package it.polito.mad17.viral.sliceapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;



class BalanceAdapter extends ArrayAdapter<Riga_Bilancio> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<Riga_Bilancio> data = null;
    private Persona user;
    public BalanceAdapter(Context context, int layoutResourceId, ArrayList<Riga_Bilancio> objects, Persona user ) {
        super(context, layoutResourceId, objects);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = objects;
        this.user = user;
    }
//crediti degli altri + //debiti miei -


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        BalanceAdapter.BalanceHolder holder= null;
        Riga_Bilancio soldo=data.get(position);
       // BitmapManager bm = new BitmapManager(context,data.get(position).getPersona().getImg(),50,70);

       // Bitmap b=  bm.scaleDown(data.get(position).getImg(),100,true);


        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new BalanceAdapter.BalanceHolder();

            TextView tv1 = (TextView) row.findViewById(R.id.person_name);
            TextView tv2 = (TextView) row.findViewById(R.id.money);


            holder.name= tv1;
            holder.money=tv2;

            row.setTag(holder);
        }
        else
        {
            holder = (BalanceAdapter.BalanceHolder)row.getTag();
        }

        if(soldo.getImporto()<0) {
            String str = String.format("%.2f",soldo.getImporto()*-1);
            holder.money.setText("-" + str);
            holder.name.setText("You owe to "+ soldo.getNcname()+":");
            holder.money.setTextColor(context.getResources().getColor(R.color.row_non_pagate_bck));
            holder.name.setTextColor(context.getResources().getColor(R.color.row_non_pagate_bck));
        }
        else{
            String str = String.format("%.2f",soldo.getImporto());
            holder.money.setText("+"+ str);
            holder.name.setText(soldo.getNcname()+ " owe to you:");

            holder.money.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.name.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }



        return row;
    }


    static class BalanceHolder
    {
       // ImageView imgIcon;
        TextView name;
        TextView money;


    }


    @Override
    public int getViewTypeCount() {

        return 1;
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }



}
