package it.polito.mad17.viral.sliceapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.Inflater;


/**
 * Created by Kalos on 27/03/2017.
 */

public class FirstFragment extends Fragment{


    public static FirstFragment newInstance() {
        FirstFragment fragmentFirst = new FirstFragment();
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View v= inflater.inflate(R.layout.slide_groups, container, false);


        final ListView mylist = (ListView) v.findViewById(R.id.listView1);

        final GroupAdapter adapter = new GroupAdapter(v.getContext(), R.layout.listview_group_row, SliceAppDB.getListaGruppi());
        adapter.notifyDataSetChanged();
        mylist.setAdapter(adapter);


        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent appInfo= new Intent(getActivity(), ExpensesActivity.class);
                appInfo.putExtra("Gruppo",SliceAppDB.getGruppo(new Integer(position)));
                appInfo.putExtra("User",SliceAppDB.getUser());
                startActivity(appInfo);
            }
        });

        return v;
    }








}
