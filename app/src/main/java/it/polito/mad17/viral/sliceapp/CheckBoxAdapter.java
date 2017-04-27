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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Kalos on 25/04/2017.
 */

public class CheckBoxAdapter  extends ArrayAdapter<Persona> {

    private int layoutResourceId;
    private List<Persona> memberNames;
    private boolean[] checkMarks;
    private Double[] percentages;
    private Context context;
    private Gruppo gruppo;

    private PayerHolder2 holder = null;
    private CheckBox cb;

    public CheckBoxAdapter(Context context, int layoutResourceId, List<Persona> memberNames, Gruppo gruppo) {
        super(context,layoutResourceId,memberNames);
        this.memberNames = memberNames;
        this.checkMarks = new boolean[memberNames.size()];
        this.percentages = new Double[memberNames.size()];
        for(int i=0;i<memberNames.size();i++) {
            checkMarks[i] = true;
            percentages[i] = -1d;
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

            TextView tv = (TextView) row.findViewById(R.id.member_name);

            holder.name=tv;

            cb = (CheckBox) row.findViewById(R.id.check);
            holder.cb = cb;




            row.setTag(holder);

        }
        else holder = (PayerHolder2) row.getTag();

        holder.cb.setTag(position);


        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                        percentages[position]= new Double(-1);
                } else {

                    checkMarks[position] = false;
                    percentages[position]=new Double(0);
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
                        percentages[position]=new Double (0);
                    }
                    if(Caption.isChecked())
                        percentages[position]= new Double(-1);
                }
            }
        });

        holder.name.setText(memberNames.get(position).getName()+ " " +memberNames.get(position).getSurname());

        return row;
    }



    public Double[] getAllTheSame() {


        Double[] percentages_ordinato = new Double[gruppo.getN_partecipanti()];
        int k=0, n_zeros=0;
        Integer i= 0;
        for(Persona p: memberNames){
            i=p.getPosizione(gruppo);

            percentages_ordinato[i.intValue()]=percentages[k];
        if(percentages_ordinato[i.intValue()].equals(0d))
            n_zeros++;

            k++;

        }
        if(n_zeros == percentages_ordinato.length)
        {
            Toast.makeText(context,"At least one person has to pay", Toast.LENGTH_SHORT).show();
            return null;
        }

        Double c = (double) 100/(percentages_ordinato.length-n_zeros);

        for(int m=0; m<percentages_ordinato.length; m++)
            if(percentages_ordinato[m]==-1)
                percentages_ordinato[m]=c;



        return percentages_ordinato;}

    static class PayerHolder2{
        public CheckBox cb;
        TextView name;
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
