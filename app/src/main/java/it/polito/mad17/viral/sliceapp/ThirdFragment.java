package it.polito.mad17.viral.sliceapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ThirdFragment extends Fragment {

    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
    private DatabaseReference rootRef = database.getReference();
    private DatabaseReference user_ref = rootRef.child("users_prova");


    public ThirdFragment() {
        // Required empty public constructor
    }


    public static ThirdFragment newInstance(String param1, String param2) {
        ThirdFragment fragment = new ThirdFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.slide_balance, container, false);
        final RecyclerView mylist = (RecyclerView) v.findViewById(R.id.listView1);

        Query ref = rootRef.child("users_prova").child(SliceAppDB.getUser().getTelephone()).child("contestazioni");
        final FirebaseRecyclerAdapter<Contestazione,ContestationHolder> adapter = new FirebaseRecyclerAdapter<Contestazione, ContestationHolder>(Contestazione.class,R.layout.listview_contestation_row,ContestationHolder.class,ref) {
            @Override
            protected void populateViewHolder(ContestationHolder viewHolder, final Contestazione model, int position) {

                viewHolder.nameContestation.setText("Reason: " + model.getTitle());
                viewHolder.nameExpense.setText("Contested expense: " + model.getNameExpense());
                viewHolder.nameGroup.setText("Expense group: " + model.getGroupName());
                viewHolder.namePerson.setText("Contestator: " + model.getUserName());

                // attacco un listener alla contestazione
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String contestationID = model.getContestID();
                        Intent i = new Intent(getActivity(), CommentsActivity.class);
                        i.putExtra("contestationID", contestationID);
                        i.putExtra("expenseID", model.getExpenseID());
                        i.putExtra("groupID", model.getGroupID());
                        i.putExtra("contestator",model.getPhoneNumber());
                        startActivity(i);
                    }
                });


            }
        };

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mylist.setLayoutManager(llm);
        DividerItemDecoration verticalDecoration = new DividerItemDecoration(mylist.getContext(), DividerItemDecoration.VERTICAL);
        Drawable verticalDivider = getContext().getDrawable(R.drawable.horizontal_divider);
        verticalDecoration.setDrawable(verticalDivider);
        mylist.addItemDecoration(verticalDecoration);
        mylist.setAdapter(adapter);

        // Inflate the layout for this fragment
        return v;
    }

    public static class ContestationHolder extends RecyclerView.ViewHolder{
        TextView namePerson;//colui che contesta
        TextView nameContestation;//titolo
        TextView nameExpense;
        TextView nameGroup;

        public ContestationHolder(View itemView) {
            super(itemView);

            namePerson = (TextView) itemView.findViewById(R.id.contestator);
            nameContestation = (TextView) itemView.findViewById(R.id.titleContestation);
            nameExpense = (TextView) itemView.findViewById(R.id.expenseContested);
            nameGroup = (TextView) itemView.findViewById(R.id.groupContestation);
        }
    }
}
