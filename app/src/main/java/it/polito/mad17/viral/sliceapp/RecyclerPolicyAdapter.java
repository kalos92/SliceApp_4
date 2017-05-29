package it.polito.mad17.viral.sliceapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kalos on 23/05/2017.
 */

public class RecyclerPolicyAdapter extends RecyclerView.Adapter<RecyclerPolicyAdapter.PolicyHolder>  {

    public class PolicyHolder extends RecyclerView.ViewHolder {
        TextView name_p;
        TextView perc_p;
        TextView price_p;

        public PolicyHolder(View itemView) {
            super(itemView);
            name_p = (TextView) itemView.findViewById(R.id.name_p);
            perc_p = (TextView) itemView.findViewById(R.id.perc_p);
            price_p = (TextView) itemView.findViewById(R.id.num_p);
        }
    }
    private Policy p;
    private HashMap<String,Soldo>divisioni;
    private ArrayList<Soldo> soldi = new ArrayList<Soldo>();
    private int digits;
    private String valuta;


    public RecyclerPolicyAdapter(Policy p, HashMap<String,Soldo> divisioni,int digits,String valuta){

        this.p=p;
        this.divisioni=divisioni;
        soldi.addAll(divisioni.values());
        this.digits=digits;
        this.valuta=valuta;

    }


    @Override
    public RecyclerPolicyAdapter.PolicyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_item,parent,false);
        RecyclerPolicyAdapter.PolicyHolder ph = new RecyclerPolicyAdapter.PolicyHolder(v);
        return ph;
    }

    @Override
    public void onBindViewHolder(RecyclerPolicyAdapter.PolicyHolder holder, int position) {
        holder.name_p.setText(soldi.get(position).getPersona().getName()+" "+soldi.get(position).getPersona().getSurname());
        String str = String.format("%.1f",p.getPercentuali().get(soldi.get(position).getPersona().getTelephone()));
        holder.perc_p.setText(str+"%");
        String str2 = String.format("%."+digits+"f",soldi.get(position).getImporto());
        holder.price_p.setText("Total: "+ str2+" "+valuta);
    }

    @Override
    public int getItemCount() {
        return soldi.size();
    }
}
