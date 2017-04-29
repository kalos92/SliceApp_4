package it.polito.mad17.viral.sliceapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;


/**
 * Created by Kalos on 27/03/2017.
 */

public class FirstFragment extends Fragment{

    private GroupAdapter adapter;
    private ListView mylist;

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
        View v = inflater.inflate(R.layout.slide_groups, container, false);
        mylist = (ListView) v.findViewById(R.id.listView1);
        adapter = new GroupAdapter(v.getContext(), R.layout.listview_group_row, SliceAppDB.getListaGruppi());
        mylist.setAdapter(adapter);

        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent appInfo= new Intent(getActivity(), ExpensesActivity.class);
                appInfo.putExtra("Gruppo", SliceAppDB.getGruppo(position));
                appInfo.putExtra("User", SliceAppDB.getUser());
                //getActivity().finish();
                startActivity(appInfo);
            }
        });
        return v;
    }
}