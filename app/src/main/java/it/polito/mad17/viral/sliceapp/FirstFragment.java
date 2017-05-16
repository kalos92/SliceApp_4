package it.polito.mad17.viral.sliceapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;



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
    private String key;
    private FloatingActionButton fab;


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
        RecyclerView mylist = (RecyclerView) v.findViewById(R.id.listView1);

        fab = (FloatingActionButton) v.findViewById(R.id.add_group);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AddNewGroup.class);
                startActivity(i);
            }
        });





       // DatabaseReference user_ref= rootRef.child("users_prova").child(SliceAppDB.getUser().getTelephone()).child("gruppi_partecipo");
        Query ref = rootRef.child("users_prova").child(SliceAppDB.getUser().getTelephone()).child("gruppi_partecipo");

        GroupsRecyclerAdapter adapter= new GroupsRecyclerAdapter(Dettagli_Gruppo.class,R.layout.listview_group_row, FirstFragment.GroupHolder.class,ref,getContext(),numbers,g,partecipanti);


        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mylist.setLayoutManager(llm);
        DividerItemDecoration verticalDecoration = new DividerItemDecoration(mylist.getContext(), DividerItemDecoration.VERTICAL);
        Drawable verticalDivider = getContext().getDrawable(R.drawable.horizontal_divider);
        verticalDecoration.setDrawable(verticalDivider);
        mylist.addItemDecoration(verticalDecoration);
        mylist.setAdapter(adapter);
        mylist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0)
                    fab.hide();
                else if (dy < 0)
                    fab.show();

            }
        });




        return v;


    }

    public static class GroupHolder extends RecyclerView.ViewHolder implements View.OnClickListener

    {
        TextView groupName;
        ImageView img;
        TextView unread;
        TextView last;

        public GroupHolder(View itemView) {
            super(itemView);
            groupName = (TextView)itemView.findViewById(R.id.groupName);
           // img = (ImageView) itemView.findViewById(R.id.imgIcon);
            unread = (TextView)itemView.findViewById(R.id.unread);
            last = (TextView)itemView.findViewById(R.id.last);

        }

        @Override
        public void onClick(final View view) {




}}}