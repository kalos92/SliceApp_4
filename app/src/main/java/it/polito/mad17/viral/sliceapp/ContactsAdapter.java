package it.polito.mad17.viral.sliceapp;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ContactsAdapter extends  ArrayAdapter<Persona> {

    private int layoutResourceIdCheckBox,layoutResourceButton;
    private List<Persona> members = new ArrayList<Persona>();
    private Context context;
    private ContactHolder holder = null;
    private Map<Integer,Persona> membersMap = new HashMap<Integer,Persona>();

    public ContactsAdapter(Context context, int layoutResourceIdCheckBox,int layoutResourceButton, List<Persona> members) {
        super(context,layoutResourceIdCheckBox,members);

        this.layoutResourceIdCheckBox = layoutResourceIdCheckBox;
        this.layoutResourceButton = layoutResourceButton;
        this.members.addAll(members);
        this.context = context;

    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        View row = view;
            if(row == null){

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(layoutResourceButton, parent, false);
                holder = new ContactHolder();

                TextView tv = (TextView) row.findViewById(R.id.ContactNameCheckBox);
                tv.setText(members.get(position).getName());
                holder.name = tv;
                CheckBox cb = (CheckBox) row.findViewById(R.id.checkInviteCheckBox);
                Button b = (Button) row.findViewById(R.id.SelectButton);

                if (members.get(position).getIsInDB() == 1 && !members.get(position).getTelephone().equals("null_$")){
                        holder.cb = cb;
                        holder.b=b;
                        holder.b.setVisibility(View.GONE);
                    }
                else  if (members.get(position).getIsInDB() == 0 && !members.get(position).getTelephone().equals("null_$")){
                        holder.cb=cb;
                        holder.b = b;
                        holder.cb.setVisibility(View.GONE);
                    }

                else if (members.get(position).getTelephone().equals("null_$") && members.get(position).getSurname().equals("null_$") && members.get(position).getUsername().equals("null_$") ){
                    holder.cb=cb;
                    holder.cb.setVisibility(View.GONE);
                    holder.b=b;
                    holder.b.setVisibility(View.GONE);
                    holder.name.setText("Person who are not using SliceApp");
                    holder.name.setTextSize(20);
                    holder.name.setTypeface(null, Typeface.BOLD);
                }


                row.setTag(holder);
            }
            else holder = (ContactHolder) row.getTag();


        if (holder.b.getVisibility() == View.GONE && holder.cb.getVisibility() != View.GONE) {
            holder.cb.setTag(position);


            holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        membersMap.put(new Integer(position),members.get(position));
                    }else{
                        membersMap.remove(new Integer(position));
                    }
                }
            });
        }

        if(holder.b.getVisibility() != View.GONE && holder.cb.getVisibility() == View.GONE){
            holder.b.setTag(position);

            holder.b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = new String(""+members.get(position).getTelephone());
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.setType("vnd.android-dir/mms-mms");
                    smsIntent.setData(Uri.parse("sms: " + phone));
                    smsIntent.putExtra("sms_body","Hey I want to use SliceApp with you, Go to the PlayStore and download it!.");
                    smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(smsIntent);
                }
            });
        }

        if(holder.b.getVisibility() == View.GONE && holder.cb.getVisibility() == View.GONE){
            holder.name.setText("Person who are not using SliceApp");
        }

        return row;
    }
        @Override
        public int getViewTypeCount () {

            return getCount();
        }


        @Override
        public int getItemViewType ( int position){

            return position;
        }

        //Elements which caracterised my row
        static class ContactHolder {
            public CheckBox cb;
            public Button b;
            TextView name;
        }

        public Map<Integer,Persona> getGroupMembers(){
            return this.membersMap;
        }
    }



