package it.polito.mad17.viral.sliceapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
                        contest.setPhoneNumber(SliceAppDB.getUser().getTelephone());
                        contest.setUserName(SliceAppDB.getUser().getUsername());
                        contest.setGroupName(groupName);
                        contest.setNameExpense(expenseName);

                        Commento c = new Commento();
                        c.setCommento(comment.getText().toString());
                        c.setUserID(SliceAppDB.getUser().getTelephone());
                        c.setUserName(SliceAppDB.getUser().getUsername());

                        //per commento e contes IDs prima fare il listener su firebase

                        FirebaseDatabase database =FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
                        DatabaseReference contestRef = database.getReference().child("groups_prova").child(groupID).child("spese").child(expenseID).child("contestazioni");
                        DatabaseReference contestationIDref = contestRef.push();
                        contest.setContestID(contestationIDref.getKey());



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
                         i.putExtra("three",2);
                         startActivity(i);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });


    }
}
