package it.polito.mad17.viral.sliceapp;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by abdel on 03/04/2017.
 */

public class NoBoxTextViewAdapter extends ArrayAdapter<Persona> {

    private int layoutResourceId;
    private List<Persona> memberNames;
    private Context context;
    private HashMap<String,Double> percentages_map = new HashMap<String,Double>();




    public NoBoxTextViewAdapter(Context context, int layoutResourceId, List<Persona> memberNames) {
        super(context,layoutResourceId,memberNames);
        this.memberNames = memberNames;
        for(Persona p: memberNames){
            percentages_map.put(p.getTelephone(),0d);
        }


        this.context = context;
        this.layoutResourceId=layoutResourceId;

    }



    @Override
    public View getView(final int position, View view, ViewGroup parent){
        View row = view;
        Persona p = memberNames.get(position);
        PayerHolder holder =null;
        EditText et=null;


        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new PayerHolder();

            TextView tv = (TextView) row.findViewById(R.id.member_name);

            holder.name=tv;
            et = (EditText)row.findViewById(R.id.member_percentage);
            holder.percentage=et;

            row.setTag(holder);

        }
        else holder = (PayerHolder) row.getTag();



        final PayerHolder h = holder;
        final Persona p2 = p;



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
                    percentages_map.put(p2.getTelephone(),Double.parseDouble(st));
                else
                    percentages_map.put(p2.getTelephone(),0d);

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
                        percentages_map.put(p2.getTelephone(),Double.parseDouble(Caption.getText().toString()));
                    else
                        percentages_map.put(p2.getTelephone(),0d);
                }
            }
        });
        return row;
    }



    public HashMap<String,Double> getPercentages() {
        Double sum=0d;
        Double max = 100d;

       for(Double d: percentages_map.values() )
         sum+=d;

        if(sum.compareTo(max)!=0){
            return null;}
       else{
           return percentages_map;}
    }


    static class PayerHolder{

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
