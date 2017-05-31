package it.polito.mad17.viral.sliceapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class CommentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        final DatabaseReference databaseRef = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/").getReference();
        Bundle extras = getIntent().getExtras();
        final String contestationID = extras.getString("contestationID");
        final String expenseID = extras.getString("expenseID");
        final String groupID = extras.getString("groupID");
        final String contestatorID = extras.getString("contestator");

        RecyclerView mylist = (RecyclerView) findViewById(R.id.listViewComments);

        Query commentsRef = databaseRef.child("users_prova").child(SliceAppDB.getUser().getTelephone()).child("contestazioni").child(contestationID).child("commenti");
        FirebaseRecyclerAdapter<Commento, RecyclerView.ViewHolder> adapter = new FirebaseRecyclerAdapter<Commento, RecyclerView.ViewHolder>(Commento.class, R.layout.comment_row, RecyclerView.ViewHolder.class, commentsRef) {


            @Override
            protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Commento model, int position) {

                if(model.getUserID().equals(SliceAppDB.getUser().getTelephone()))
                    populateMe((CommentHolder) viewHolder, model, position);
                else
                    populateOther((CommentHolder2) viewHolder, model, position);



            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                switch (viewType) {
                    case 0:
                        View userType1 = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.comment_row, parent, false);
                        return new CommentHolder2(userType1);
                    case 1:
                        View userType2 = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.comment_row_out, parent, false);
                        return new CommentHolder(userType2);

                }
                return super.onCreateViewHolder(parent, viewType);
            }


            @Override
            public int getItemViewType(int position) {
                Commento model = getItem(position);
                if(model.getUserID().equals(SliceAppDB.getUser().getTelephone()))
                    return 1;
                else
                    return 0;

            }

           void  populateMe(CommentHolder viewHolder, Commento model, int position){

               viewHolder.userName.setText("You");
               viewHolder.comment.setText(model.getCommento());
            }

            void  populateOther(CommentHolder2 viewHolder, Commento model, int position){

                viewHolder.userName.setText(model.getUserName());
                viewHolder.comment.setText(model.getCommento());
            }

        };

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mylist.setLayoutManager(llm);

        mylist.setAdapter(adapter);

        final EditText comment = (EditText)findViewById(R.id.addComment);

        FloatingActionButton commentButton = ( FloatingActionButton) findViewById(R.id.commentButton);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = comment.getText().toString();
                if(commentText.isEmpty()){
                    comment.requestFocus();
                    comment.setError("Insert comment");
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


        FloatingActionButton resolveButton = ( FloatingActionButton) findViewById(R.id.resolveButton);
        if(!contestatorID.equals(SliceAppDB.getUser().getTelephone())){
            resolveButton.setVisibility(View.GONE);
            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) comment.getLayoutParams();
            p.addRule(RelativeLayout.LEFT_OF,R.id.commentButton);
           comment.setLayoutParams(p);
            resolveButton.setClickable(false);}

        resolveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Risolvi la contestazione solo il contestatore può farlo


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

    public static class CommentHolder2 extends RecyclerView.ViewHolder{
        TextView userName;// colui che ha commentato
        TextView comment;// commento

        public CommentHolder2(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.userNameComment);
            comment = (TextView) itemView.findViewById(R.id.comment);
        }
    }
}


