package it.polito.mad17.viral.sliceapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Kalos on 02/06/2017.
 */

public class Partecipanti_adapter extends RecyclerView.Adapter<Partecipant_Group.PartecipantsHolder> {

    private Policy pol;
    private ArrayList<Persona> p;
    private Context c;

    public Partecipanti_adapter (ArrayList<Persona> p, Context c,Policy pol){
        this.p=p;
        this.c=c;
        this.pol=pol;
    }




    @Override
    public Partecipant_Group.PartecipantsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v =LayoutInflater.from(parent.getContext()).inflate(R.layout.partecipanti_row,parent,false);
        Partecipant_Group.PartecipantsHolder ph = new Partecipant_Group.PartecipantsHolder(v);
        return ph;
    }

    @Override
    public void onBindViewHolder(Partecipant_Group.PartecipantsHolder holder, int position) {

        Picasso.with(c).load(p.get(position).getPropic()).placeholder(R.drawable.img_user).transform(new RoundedTransformation(500, 1)).into(holder.propic);
        holder.namep.setText(p.get(position).getName()+" "+p.get(position).getSurname());
        String str = String.format("%.1f", pol.getPercentuali().get(p.get(position).getTelephone()));
        holder.policy.setText(str+"%");
    }

    @Override
    public int getItemCount() {
        return p.size();
    }
}
