package it.polito.mad17.viral.sliceapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import java.util.Iterator;
import java.util.Map;

import static java.security.AccessController.getContext;

public class CommentsExpenseActivity extends AppCompatActivity {

    private String groupID,expenseID,contestationID,contestatorID;
    private SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

       // Toolbar toolbar = (Toolbar) findViewById(R.id.contestationToolbar);
       // setSupportActionBar(toolbar);

        final DatabaseReference databaseRef = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/").getReference();
        Bundle extras = getIntent().getExtras();
        contestationID = extras.getString("contestationID");
        expenseID = extras.getString("expenseID");
        groupID = extras.getString("groupID");
        contestatorID = extras.getString("contestator");

        RecyclerView mylist = (RecyclerView) findViewById(R.id.listViewComments);

       // sharedPref= getSharedPreferences("data",MODE_PRIVATE);
        //final String userTelephone = sharedPref.getString("telefono", null);

        Query commentsRef = databaseRef.child("users_prova").child(contestatorID).child("contestazioni").child(contestationID).child("commenti");
        FirebaseRecyclerAdapter<Commento, CommentHolder> adapter = new FirebaseRecyclerAdapter<Commento, CommentHolder>(Commento.class, R.layout.comment_row, CommentHolder.class, commentsRef) {
            @Override
            protected void populateViewHolder(CommentHolder viewHolder, Commento model, int position) {
                viewHolder.userName.setText("User: " + model.getUserName());
                viewHolder.comment.setText("Comment: " + model.getCommento());
            }
        };

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mylist.setLayoutManager(llm);
        DividerItemDecoration verticalDecoration = new DividerItemDecoration(mylist.getContext(), DividerItemDecoration.VERTICAL);
        Drawable verticalDivider = getApplicationContext().getDrawable(R.drawable.horizontal_divider);
        verticalDecoration.setDrawable(verticalDivider);
        mylist.addItemDecoration(verticalDecoration);

        mylist.setAdapter(adapter);

        final EditText comment = (EditText)findViewById(R.id.addComment);

        FloatingActionButton commentButton = (FloatingActionButton) findViewById(R.id.commentButton);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = comment.getText().toString();
                if(commentText.isEmpty()){
                    comment.requestFocus();
                    comment.setError("Inserisci un commento");
                    return;
                }
                DatabaseReference comments = databaseRef.child("users_prova").child(SliceAppDB.getUser().getTelephone()).child("contestazioni").child(contestationID).child("commenti");
                final DatabaseReference commentRef = comments.push();
                final Commento commento = new Commento();
                commento.setCommento(commentText);
                commento.setUserName(SliceAppDB.getUser().getUsername());
                commento.setCommentoID(commentRef.getKey());
                commento.setUserID(SliceAppDB.getUser().getTelephone()); // è giusto? chi mette il commento è l'utente dell'app?
                commento.setTimestamp(System.currentTimeMillis());
                commentRef.setValue(commento);
                comment.getText().clear();

                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> members = dataSnapshot.child("groups_prova").child(groupID).child("spese").child(expenseID).child("divisioni").getChildren().iterator();
                        while(members.hasNext()){
                            String memberPhone = members.next().getKey();
                            databaseRef.child("users_prova").child(memberPhone).child("contestazioni").child(contestationID).child("commenti").child(commentRef.getKey()).setValue(commento);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
        });

        FloatingActionButton resolveContestationButton = (FloatingActionButton) findViewById(R.id.resolveButton);
        resolveContestationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Eliminiamo la contestazione
                FirebaseDatabase dbContest = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
                DatabaseReference dbContestRef = dbContest.getReference().child("groups_prova").child(groupID).child("spese")
                        .child(expenseID).child("contestazioni")
                        .child(contestationID);
                if(SliceAppDB.getUser().getTelephone().equals(contestatorID)){
                    dbContestRef.removeValue();
                    final DatabaseReference dbUserRef = dbContest.getReference().child("users_prova");

                    dbUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                            while(iterator.hasNext()){
                                DataSnapshot user = iterator.next();
                                if(user.child("contestazioni").hasChild(contestationID)){
                                    String keyUser = (String)user.getKey();
                                    dbUserRef.child(keyUser).child("contestazioni").child(contestationID).removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Log.d("ContestSuccess","I'm here");
                    Toast.makeText(getApplicationContext(),"You succesfully delete your contestation",Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(CommentsExpenseActivity.this,List_Pager_Act.class);
                    i.putExtra("three",2);
                    startActivity(i);

                }else{
                    Log.d("ContestFailure","I'm here");
                    Toast.makeText(getApplicationContext(),"You can't delete the contestation!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public static class CommentHolder extends RecyclerView.ViewHolder{
        TextView userName;// colui che ha commentato
        TextView comment;// commento

        public CommentHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.userNameComment);
            comment = (TextView) itemView.findViewById(R.id.comment);
        }
    }


}


