package it.polito.mad17.viral.sliceapp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.firebase.database.DataSnapshot;
import java.util.ArrayList;


/**
 * Created by Kalos on 27/03/2017.
 */
//1a prova video
    //2a prova
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

        View v = inflater.inflate(R.layout.slide_groups, container, false);
        ListView mylist = (ListView) v.findViewById(R.id.listView1);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.add_group);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AddNewGroup.class);
                startActivity(i);
                int n;
            }
        });
        ArrayList<Gruppo> gruppi = new ArrayList<Gruppo>();
        //gruppi.addAll(SliceAppDB.getListaGruppi());
        gruppi.addAll(SliceAppDB.getGruppi().values()); // se uso la hashmap per recuperare i gruppi, sballa l'ordine di visualizzazione
                                                        // devo ordinarli
        GroupAdapter adapter = new GroupAdapter(v.getContext(), R.layout.listview_group_row, gruppi);
        mylist.setAdapter(adapter);


        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent appInfo= new Intent(getActivity(), ExpensesActivity.class);
                appInfo.putExtra("Gruppo", SliceAppDB.getGruppoArray(position));
                appInfo.putExtra("User", SliceAppDB.getUser());
                startActivity(appInfo);
            }
        });
        return v;


    }
}