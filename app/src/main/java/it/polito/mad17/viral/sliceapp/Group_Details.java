package it.polito.mad17.viral.sliceapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.IdRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

public class Group_Details extends AppCompatActivity implements LittleFragment3.GetPercentages_2{

    ArrayList<Persona> listP = new ArrayList<Persona>();
    private HashMap<String,Double> percentuali = new HashMap<String,Double>();
    private Policy policy = null;
    private FragmentManager fm;
    private boolean RequestAllTheSame=true;
    private ArrayList<Persona> tmpList = new ArrayList<Persona>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
    private DatabaseReference groups_prova = database.getReference().child("groups_prova");
    private DatabaseReference users_prova = database.getReference().child("users_prova");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        Bundle extra =getIntent().getExtras();

        if(extra!=null){
            listP = (ArrayList<Persona>) extra.get("ListaPersone");

        }



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




                RadioGroup rg = (RadioGroup) findViewById(R.id.rg3);
                rg.check(R.id.b5);

                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                        if(group.getCheckedRadioButtonId()== R.id.b6){
                            RequestAllTheSame=false;
                            policy=null;
                            fm = getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.replace(R.id.f2, LittleFragment3.newInstance(tmpList));
                            ft.addToBackStack(null);
                            ft.commit();
                        }

                        if(group.getCheckedRadioButtonId()== R.id.b5){

                            RequestAllTheSame=true;
                            fm = getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            if(fm.findFragmentById(R.id.f2)!=null){
                                ft.remove(fm.findFragmentById(R.id.f2));
                                ft.addToBackStack(null);
                                ft.commit();


                            }}
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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


        final EditText groupName =  (EditText) findViewById(R.id.groupTitle);



        if(id == R.id.action_continue){

            final String groupID = groups_prova.push().getKey();

            if(RequestAllTheSame){
                for(Persona p: tmpList){
                    percentuali.put(p.getTelephone(), (double)100/tmpList.size());
                }
                policy=new Policy(percentuali);
            }

            if(policy==null){
                Toast.makeText(getBaseContext(),"You do not have insert a valid Policy", Toast.LENGTH_LONG).show();
                return false;
            }
            if(groupName.getText().equals("") || groupName.getText().length()==0 ){
                groupName.requestFocus();
                groupName.setError("Please inser a group Name");
                return false;
            }

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

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void getPercentages(Policy policy) {
        this.policy=policy;
    }
}
