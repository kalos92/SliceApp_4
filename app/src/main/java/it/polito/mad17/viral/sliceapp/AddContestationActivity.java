package it.polito.mad17.viral.sliceapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.internal.bind.DateTypeAdapter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AddContestationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contestation);
        SharedPreferences sharedPref = getSharedPreferences("data",MODE_PRIVATE);
        final String userTelephone = sharedPref.getString("telefono", null);
        final String username = sharedPref.getString("username",null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.contestToolbar);
        setSupportActionBar(toolbar);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        final String expenseID,groupID;

        expenseID = extras.getString("spesa");
        groupID = extras.getString("gruppo");

        final EditText title = (EditText)findViewById(R.id.titleid);
        final EditText comment = (EditText) findViewById(R.id.commentid);

        Button contestButton = (Button)findViewById(R.id.buttonContest);

        contestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference databaseRef = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/").getReference();
                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {



                        String groupName = (String)dataSnapshot.child("groups_prova").child(groupID).child("groupName").getValue();
                        String expenseName = (String)dataSnapshot.child("groups_prova").child(groupID).child("spese").child(expenseID).child("nome_spesa").getValue();


                        Contestazione contest = new Contestazione();
                        contest.setGroupID(groupID);
                        contest.setExpenseID(expenseID);
                        contest.setTitle(title.getText().toString());
                        contest.setPhoneNumber(userTelephone);
                        contest.setUserName(username);
                        contest.setGroupName(groupName);
                        contest.setNameExpense(expenseName);
                        contest.setTimestamp(System.currentTimeMillis());

                        Commento c = new Commento();
                        c.setCommento(comment.getText().toString());
                        c.setUserID(userTelephone);
                        c.setUserName(username);
                        c.setTimestamp(System.currentTimeMillis());

                        //per commento e contes IDs prima fare il listener su firebase

                        FirebaseDatabase database =FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
                        DatabaseReference contestRef = database.getReference().child("groups_prova").child(groupID).child("spese").child(expenseID).child("contestazioni");
                        DatabaseReference contestationIDref = contestRef.push();
                        contest.setContestID(contestationIDref.getKey());

                        String key_c = contestationIDref.getKey();
                        databaseRef.child("groups_prova").child(groupID).child("contested").child(key_c).setValue(true);
                        //metto il flag "contested" del gruppo a true


                        // metto il flag "contested" della spesa a true
                        databaseRef.child("groups_prova").child(groupID).child("spese").child(expenseID).child("contested").setValue(true);

                        DatabaseReference commentsIDref = contestationIDref.child("commenti").push();
                        c.setCommentoID(commentsIDref.getKey());

                        contest.getCommenti().put(c.getCommentoID(),c);

                        //setto le contestazioni su firebase
                        contestationIDref.setValue(contest);

                        //aggiungo la contestazione ai membri che ne fanno parte (compreso lo user che ha creato la contestazione)
                        Iterator<DataSnapshot> iterator = dataSnapshot.child("groups_prova").child(groupID).child("spese")
                                                            .child(expenseID).child("divisioni").getChildren().iterator();
                        while(iterator.hasNext()){
                            String telephone = iterator.next().getKey();
                            database.getReference().child("users_prova").child(telephone).child("contestazioni").child(contestationIDref.getKey()).setValue(contest);
                        }

                         Intent i = new Intent(AddContestationActivity.this,List_Pager_Act.class);
                         i.putExtra("page",2);
                         startActivity(i);
                         finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });


    }
}
