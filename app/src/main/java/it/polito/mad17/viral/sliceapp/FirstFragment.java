package it.polito.mad17.viral.sliceapp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;


/**
 * Created by Kalos on 27/03/2017.
 */
//1a prova video
    //2a prova
public class FirstFragment extends Fragment{

    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
    private DatabaseReference rootRef = database.getReference();
    private DatabaseReference groups_ref = rootRef.child("groups_prova");
    private ArrayList<String> numbers = new ArrayList<String>();
    private HashMap<String, Persona> partecipanti = new HashMap<String,Persona>();
    private Gruppo g;


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
            }
        });
        ArrayList<Gruppo> gruppi = new ArrayList<Gruppo>();
        //gruppi.addAll(SliceAppDB.getListaGruppi());
        gruppi.addAll(SliceAppDB.getGruppi().values()); // se uso la hashmap per recuperare i gruppi, sballa l'ordine di visualizzazione
                                                        // devo ordinarli
        GroupAdapter adapter2 = new GroupAdapter(v.getContext(), R.layout.listview_group_row, gruppi);





       // DatabaseReference user_ref= rootRef.child("users_prova").child(SliceAppDB.getUser().getTelephone()).child("gruppi_partecipo");
        Query ref = rootRef.child("users_prova").child(SliceAppDB.getUser().getTelephone()).child("gruppi_partecipo");

        FirebaseListAdapter<String> adapter= new FirebaseListAdapter<String>(getActivity(), String.class, R.layout.listview_group_row, ref) {
            @Override
            protected void populateView(View v, String model, int position) {

                TextView title = (TextView)v.findViewById(R.id.groupName);
                StringTokenizer st = new StringTokenizer(model, ";");
                title.setText(st.nextToken());

                TextView key = (TextView)v.findViewById(R.id.groupKey);
                key.setText(st.nextToken());

            }
        };
        mylist.setAdapter(adapter);


        mylist.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               TextView key = (TextView) view.findViewById(R.id.groupKey);
                final String chiave = (String) key.getText();
                // caricare i partecipanti nel gruppo;
                final DatabaseReference users_prova = rootRef.child("users_prova");

                groups_ref.child((String) key.getText()).child("partecipanti_numero_cnome").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it =dataSnapshot.getChildren().iterator();
                        while(it.hasNext()){
                            numbers.add(it.next().getKey()); // qui ho tutti i numeri delle persone

                        }

                        groups_ref.child(chiave).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                               g = dataSnapshot.getValue(Gruppo.class);

                                users_prova.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Iterator<DataSnapshot> it =dataSnapshot.getChildren().iterator();

                                        while(it.hasNext()){
                                            DataSnapshot ds = it.next();
                                            if(numbers.contains(ds.getKey())) {
                                                Persona p = ds.getValue(Persona.class);
                                                partecipanti.put(p.getTelephone(),p);


                                            }

                                        }
                                        g.setPartecipanti_3(partecipanti);
                                        g.setUser(SliceAppDB.getUser());


                                        Intent i = new Intent(getActivity(),ExpensesActivity.class);


                                        i.putExtra("Gruppo",g);
                                        i.putExtra("User", SliceAppDB.getUser());
                                        startActivity(i);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });




                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });









            }
        });
        return v;


    }
}