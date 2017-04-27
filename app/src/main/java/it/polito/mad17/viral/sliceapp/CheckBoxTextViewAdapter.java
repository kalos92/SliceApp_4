package it.polito.mad17.viral.sliceapp;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abdel on 03/04/2017.
 */

public class CheckBoxTextViewAdapter extends ArrayAdapter<Persona> {
    private int layoutResourceId;
    private List<Persona> memberNames;
    private boolean[] checkMarks;
    private Double[] percentages;
    private Context context;

    private String value;
    private Gruppo gruppo;




    public CheckBoxTextViewAdapter(Context context, int layoutResourceId, List<Persona> memberNames, Gruppo gruppo) {
        super(context,layoutResourceId,memberNames);
        this.memberNames = memberNames;
        this.checkMarks = new boolean[memberNames.size()];
        this.percentages = new Double[memberNames.size()];
        for(int i=0;i<memberNames.size();i++) {
            checkMarks[i] = true;
            percentages[i] = 0.0d;
        }
        this.context = context;
        this.layoutResourceId=layoutResourceId;
        this.gruppo=gruppo;
    }



    @Override
    public View getView(final int position, View view, ViewGroup parent){
        View row = view;

        PayerHolder holder =null;
        CheckBox cb =null;
        EditText et=null;


        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new PayerHolder();

            TextView tv = (TextView) row.findViewById(R.id.member_name);

            holder.name=tv;

            cb = (CheckBox) row.findViewById(R.id.check);
            holder.cb = cb;


            et = (EditText)row.findViewById(R.id.member_percentage);
            holder.percentage=et;

            row.setTag(holder);

        }
        else holder = (PayerHolder) row.getTag();

        holder.cb.setTag(position);

        final PayerHolder h = holder;

        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    h.percentage.setEnabled(true);
                    checkMarks[position] = true;

                } else {
                    h.percentage.setEnabled(false);
                    checkMarks[position] = false;
                    h.percentage.setText("");
                    percentages[position]=new Double(0);
                }
            }
        });

        holder.percentage.addTextChangedListener(new TextWatcher() {

            @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {



                }


                @Override
                public void afterTextChanged(Editable s){
                    String st = h.percentage.getText().toString();
                    if(!st.equals(""))
                        percentages[position] = Double.parseDouble(st);
                    else
                        percentages[position] = 0.0d;

        }
            });



        holder.cb.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    final int position = (Integer)v.getTag();
                    CheckBox Caption = (CheckBox) v;
                    if(!Caption.isChecked()) {
                        h.percentage.setEnabled(false);
                        h.percentage.setText("");
                        percentages[position]=new Double(0);
                    }else
                        h.percentage.setEnabled(true);
                }
            }
        });

        holder.name.setText(memberNames.get(position).getName()+ " " +memberNames.get(position).getSurname());
        holder.percentage.setTag(position);
        holder.percentage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    final int position = (Integer)v.getTag();
                    EditText Caption = (EditText) v;
                    if(!Caption.getText().toString().equals(""))
                    percentages[position] =  Double.parseDouble(Caption.getText().toString());
                    else
                        percentages[position] = new Double(0);
                }
            }
        });
        return row;
    }

    public boolean[] getChecks(){ return checkMarks; }

    public Double[] getPercentages() {


        Double[] percentages_ordinato = new Double[gruppo.getN_partecipanti()];
        int k=0;
        Integer i= 0;
        for(Persona p: memberNames){
            i=p.getPosizione(gruppo);

            percentages_ordinato[i.intValue()]=percentages[k];

            k++;

        }



        return percentages_ordinato;}

    static class PayerHolder{
        public CheckBox cb;
        TextView name;
        EditText percentage;

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
