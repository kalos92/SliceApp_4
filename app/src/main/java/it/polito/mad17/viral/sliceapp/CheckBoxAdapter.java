package it.polito.mad17.viral.sliceapp;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Kalos on 25/04/2017.
 */

public class CheckBoxAdapter  extends ArrayAdapter<Persona> {

    private int layoutResourceId;
    private List<Persona> memberNames;
    private boolean[] checkMarks;
    private HashMap<String,Double> percentages = new HashMap<String,Double>();
    private Context context;
    private Gruppo gruppo;

    private PayerHolder2 holder = null;
    private CheckBox cb;

    public CheckBoxAdapter(Context context, int layoutResourceId, List<Persona> memberNames, Gruppo gruppo) {
        super(context,layoutResourceId,memberNames);
        this.memberNames = memberNames;
        this.checkMarks = new boolean[memberNames.size()];
        int i=0;
        for(Persona p : memberNames) {
            checkMarks[i] = true;
            percentages.put(p.getTelephone(),-1d);
            i++;
        }
        this.context = context;
        this.layoutResourceId=layoutResourceId;
        this.gruppo=gruppo;
    }



    @Override
    public View getView(final int position, View view, ViewGroup parent){
        View row = view;

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new PayerHolder2();
            holder.propic = (ImageView) row.findViewById(R.id.imageView2);
            TextView tv = (TextView) row.findViewById(R.id.member_name);

            holder.name=tv;

            cb = (CheckBox) row.findViewById(R.id.check);
            holder.cb = cb;




            row.setTag(holder);

        }
        else holder = (PayerHolder2) row.getTag();

        Picasso.with(context).load(memberNames.get(position).getPropic()).placeholder(R.drawable.img_user).transform(new RoundedTransformation(500, 1)).into(holder.propic);
        holder.cb.setTag(position);

        final Persona p=memberNames.get(position);

        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    percentages.put(p.getTelephone(),-1d);
                } else {

                    checkMarks[position] = false;
                    percentages.put(p.getTelephone(),0d);
                }
            }
        });






        holder.cb.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    final int position = (Integer)v.getTag();
                    CheckBox Caption = (CheckBox) v;
                    if(!Caption.isChecked()) {
                        percentages.put(p.getTelephone(),0d);
                    }
                    if(Caption.isChecked())
                        percentages.put(p.getTelephone(),-1d);
                }
            }
        });

        holder.name.setText(memberNames.get(position).getName()+ " " +memberNames.get(position).getSurname());

        return row;
    }



    public HashMap<String, Double> getAllTheSame() {


        int n_zeros=0;
        for(Double d : percentages.values()){

            if(d.equals(0d)){
                n_zeros++;
            }

        }

        if(n_zeros == percentages.values().size())
        {
            Toast.makeText(context,"At least one person has to pay", Toast.LENGTH_SHORT).show();
            return null;
        }

        Double c = (double) 100/(percentages.values().size()-n_zeros);

        for(String s: percentages.keySet()){
            if(percentages.get(s).equals(-1d)){
                percentages.put(s,c);
            }
        }




    return percentages;
    }

    static class PayerHolder2{
         CheckBox cb;
        TextView name;
        ImageView propic;
    }

    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

}
