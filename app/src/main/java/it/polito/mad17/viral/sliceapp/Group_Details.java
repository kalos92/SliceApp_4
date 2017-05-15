package it.polito.mad17.viral.sliceapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Group_Details extends AppCompatActivity {

    ArrayList<Persona> listP = new ArrayList<Persona>();

    private Policy policy = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extra =getIntent().getExtras();

        if(extra!=null){
            listP = (ArrayList<Persona>) extra.get("ListaPersone");
            int count = 0;
            for(Persona p: listP){
                Log.d(new String("Cycle"+count),p.getName());
                count++;
            }
        }
        RadioGroup rg = (RadioGroup) findViewById(R.id.rg3);
        rg.check(R.id.b5);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contacts_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        final ArrayList<Persona> tmpList = new ArrayList<Persona>();
        final EditText groupName =  (EditText) findViewById(R.id.groupTitle);



        if(id == R.id.action_continue){
            final FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
            final DatabaseReference groups_prova = database.getReference().child("groups_prova");
            final DatabaseReference users_prova = database.getReference().child("users_prova");
            final String groupID = groups_prova.push().getKey();
            final ArrayList<String> numeri = new ArrayList<>();

            for(int i=0; i<listP.size();i++){
               numeri.add(listP.get(i).getTelephone());
           }


            users_prova.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                       Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                       while(it.hasNext()) {
                            DataSnapshot ds = it.next();
                            if(numeri.contains(ds.getKey())){
                                Persona per = ds.getValue(Persona.class);
                                tmpList.add(per);}
                       }

                   //CREO GRUPPO
                   tmpList.add(SliceAppDB.getUser());


                   Gruppo g = new Gruppo(groupID,groupName.getText().toString(), tmpList.size(), tmpList, policy);
                   g.setGroupID(groupID);
                   g.setUser(SliceAppDB.getUser());


                    Gson gson = new Gson();
                    Gruppo g1 = gson.fromJson(gson.toJson(g),Gruppo.class);


                    groups_prova.child(g1.getGroupID()).setValue(g1);

                    for(Persona p: tmpList){
                        Persona p1 = gson.fromJson(gson.toJson(p),Persona.class);
                         users_prova.child(p1.getTelephone()).setValue(p1);

                    }
                    Intent i = new Intent(Group_Details.this, List_Pager_Act.class);
                    startActivity(i);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
        return super.onOptionsItemSelected(item);
    }




}
