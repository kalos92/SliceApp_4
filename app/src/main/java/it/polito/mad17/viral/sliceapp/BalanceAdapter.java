package it.polito.mad17.viral.sliceapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;



class BalanceAdapter extends ArrayAdapter<Soldo> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<Soldo> data = null;
    private Persona user;
    public BalanceAdapter(Context context, int layoutResourceId, ArrayList<Soldo> objects, Persona user ) {
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
        Soldo soldo=data.get(position);
       // BitmapManager bm = new BitmapManager(context,data.get(position).getPersona().getImg(),50,70);

       // Bitmap b=  bm.scaleDown(data.get(position).getImg(),100,true);


        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new BalanceAdapter.BalanceHolder();

            //Se è una spesa che ho fatto io User e il soldo che ho in questo momento è di un altro e non mi è stato ancora ridato -> io ho un credito con lui
            if(soldo.getPagante().getUserName().equals(user.getUserName()) && !soldo.getPersona().getUserName().equals(user.getUserName()) && !soldo.getHaPagato()){
                TextView title = (TextView)row.findViewById(R.id.person_name);
                title.setText(soldo.getPersona().getName()+ " " +soldo.getPersona().getSurname()+ " Credits: ");
                holder.name = title;
                TextView money = (TextView)row.findViewById(R.id.money);
                money.setText("+ " + Double.toString(soldo.getImporto()));
                money.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                holder.money=money;
                holder.name = title;
            }
            //se è un mio debito è una spesa io non ho pagato il pagante non sono io
            else if(!soldo.getPagante().getUserName().equals(user.getUserName()) && soldo.getPersona().getUserName().equals(user.getUserName()) && !soldo.getHaPagato()){
                TextView title = (TextView)row.findViewById(R.id.person_name);
                title.setText(soldo.getPagante().getName()+ " " +soldo.getPagante().getSurname()+ " Debts: ");
                holder.name = title;
                TextView money = (TextView)row.findViewById(R.id.money);
                String str = String.format("%.2f", soldo.getImporto()*-1);
                money.setText(str);
                money.setTextColor(context.getResources().getColor(R.color.row_non_pagate));
                holder.money=money;
                holder.name = title;
            }
            else
            {
                row.setVisibility(View.INVISIBLE);
                row.getLayoutParams().height = 1;
            }


            row.setTag(holder);
        }
        else
        {
            holder = (BalanceAdapter.BalanceHolder)row.getTag();
        }

        //Se è una spesa che ho fatto io User e il soldo che ho in questo momento è di un altro e non mi è stato ancora ridato -> io ho un credito con lui
        if(soldo.getPagante().getUserName().equals(user.getUserName()) && !soldo.getPersona().getUserName().equals(user.getUserName()) && !soldo.getHaPagato()){
            TextView title = (TextView)row.findViewById(R.id.person_name);
            title.setText(soldo.getPersona().getName()+ " " +soldo.getPersona().getSurname()+ " Credits: ");
            holder.name = title;
            TextView money = (TextView)row.findViewById(R.id.money);
            String str = String.format("%.2f", soldo.getImporto());
            money.setText("+" +str);
            money.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.money=money;
            holder.name = title;
        }
        //se è un mio debito è una spesa io non ho pagato il pagante non sono io
        if(!soldo.getPagante().getUserName().equals(user.getUserName()) && soldo.getPersona().getUserName().equals(user.getUserName()) && !soldo.getHaPagato()){
            TextView title = (TextView)row.findViewById(R.id.person_name);
            title.setText(soldo.getPagante().getName()+ " " +soldo.getPagante().getSurname()+ " Debts: ");
            holder.name = title;
            TextView money = (TextView)row.findViewById(R.id.money);
            String str = String.format("%.2f", soldo.getImporto()*-1);
            money.setTextColor(context.getResources().getColor(R.color.row_non_pagate));
            money.setText(str);
            holder.money=money;
            holder.name = title;
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
