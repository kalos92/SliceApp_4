package it.polito.mad17.viral.sliceapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.util.ArrayList;

public class Group_Details extends AppCompatActivity {

    ArrayList<Persona> listP = new ArrayList<Persona>();

    private  boolean flag = true;
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
        Double[] perc = new Double[listP.size()+1];
        RadioGroup rg = (RadioGroup) findViewById(R.id.rg3);
        rg.setVisibility(View.GONE);
        rg.check(R.id.b5);
       // int i;
   /*     Double c = (double) 100/(listP.size()+1);
        long[] l = new long[listP.size()+1];
        for(i=0;i<listP.size();i++) {
            perc[i] = c;
            l[i]=listP.get(i).getTelephone();
        }
        policy = new Policy(perc,listP.size()+1,l);*/
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


        Log.d("NomeGruppo",groupName.getText().toString());
        if(id == R.id.action_continue){
           // Toast.makeText(getBaseContext(),"You have to select at least one contact", Toast.LENGTH_LONG).show();
            final FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
            final DatabaseReference users = database.getReference().child("users");
            final DatabaseReference groups= database.getReference().child("groups");
            final DatabaseReference groupLink = groups.push();
            final String groupID = groupLink.getKey();
            final int numMembers = listP.size()+1;
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                   for (Persona p : listP) {
                       String telephone = String.valueOf(p.getTelephone());
                       //  Log.d("TelefonoLista",telephone);
                       DataSnapshot member = dataSnapshot.child(telephone);
                       String nome = (String) member.child("name").getValue();
                       String cognome = (String) member.child("surname").getValue();
                       String username = (String) member.child("userName").getValue();
                       String dob = (String) member.child("dob").getValue();
                       String telefono = (String)member.child("telephone").getValue();

                       users.child(telephone).child("belongsToGroups").child(groupID).setValue("true");
                       Persona owner = SliceAppDB.getUser();
                       String phoneOwner = new String("" + owner.getTelephone());
                       users.child(phoneOwner).child("belongsToGroups").child(groupID).setValue("true");

                       Log.d("Member", nome + " " + cognome + " " + username + " " + dob + " " + new String("" + telefono));

                       Persona per = new Persona(nome, cognome, username, dob, telefono);
                       tmpList.add(per);
                   }
                   //CREO GRUPPO
                   tmpList.add(SliceAppDB.getUser());


                   Gruppo g = new Gruppo(groupName.getText().toString(), tmpList.size(), tmpList, policy);
                   g.setGroupID(groupID);
                   g.setUser(SliceAppDB.getUser());

                   Log.d("GroupID", groupID);
                   SliceAppDB.addGruppo(g);
                   groups.addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshot) {

                           groupLink.child("name").setValue(groupName.getText().toString());
                           groupLink.child("icon").setValue("ok");
                          groupLink.child("policy").setValue("");
                           //groupLink.child("numMembers").setValue(tmpList.size());
                        //   for(Persona p: tmpList){

                          // groupLink.child("policy").child(new String(""+p.getTelephone())).setValue(policy.getMyPolicy(p.getTelephone()));
                          // }
                           groupLink.child("expenses").setValue("");

                           Persona owner = SliceAppDB.getUser();
                           String phoneOwner = new String("" + owner.getTelephone());
                           groupLink.child("members").child(new String("" + phoneOwner)).setValue("true");
                            groupLink.child("policy").setValue("");
                           for (Persona p : tmpList) {
                               groupLink.child("members").child(new String("" + p.getTelephone())).setValue("true");
                           }
                           groupLink.child("numMembers").setValue(numMembers);
                           Intent i = new Intent(Group_Details.this, List_Pager_Act.class);
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
        return super.onOptionsItemSelected(item);
    }

}
