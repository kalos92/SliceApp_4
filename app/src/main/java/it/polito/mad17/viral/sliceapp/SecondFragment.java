package it.polito.mad17.viral.sliceapp;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SecondFragment extends Fragment {

    private Persona user;

    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
    private DatabaseReference rootRef = database.getReference();
    private DatabaseReference user_ref = rootRef.child("users_prova");

    public SecondFragment() {
        // Required empty public constructor
    }

    public static SecondFragment newInstance(Persona user) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putSerializable("User", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (Persona) getArguments().getSerializable("User");

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.slide_balance, container, false);
        final RecyclerView mylist = (RecyclerView) v.findViewById(R.id.listView1);


        Query ref = rootRef.child("users_prova").child(SliceAppDB.getUser().getTelephone()).child("amici");

        FirebaseRecyclerAdapter<Riga_Bilancio,BalanceHolder> adapter= new  FirebaseRecyclerAdapter<Riga_Bilancio,BalanceHolder>(Riga_Bilancio.class, R.layout.listview_balance_row, BalanceHolder.class,ref) {
            @Override
            protected void populateViewHolder(BalanceHolder viewHolder, Riga_Bilancio model, int position) {



                if(model.calculate().compareTo(0d)<0) {
                    String str = String.format("%."+model.getDigits()+"f",model.calculate());
                    viewHolder.money.setText(str);
                    viewHolder.name_p.setText("You owe to "+ model.getNcname()+":");
                    viewHolder.money.setTextColor(getContext().getResources().getColor(R.color.debiti));
                    viewHolder.name_p.setTextColor(getContext().getResources().getColor(R.color.debiti));
                    viewHolder.currency.setText(model.getSymbol());
                    viewHolder.currency.setTextColor(getContext().getResources().getColor(R.color.debiti));
                }
                else if (model.calculate().compareTo(0d)>0){
                    String str = String.format("%."+model.getDigits()+"f",model.calculate());
                    viewHolder.money.setText("+"+ str);
                    viewHolder.name_p.setText(model.getNcname()+ " owe to you:");

                    viewHolder.money.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                    viewHolder.name_p.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                    viewHolder.currency.setText(model.getSymbol());
                    viewHolder.currency.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                }
                else if (model.calculate().compareTo(0d)==0){
                    viewHolder.currency.setText("");
                    viewHolder.money.setText("");
                    viewHolder.name_p.setText(model.getNcname()+ " has no problem with you");

                    viewHolder.money.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                    viewHolder.name_p.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));

                }
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





        return v;
    }


    public static class BalanceHolder extends RecyclerView.ViewHolder{
        TextView name_p;
        TextView money;
        TextView currency;

        public BalanceHolder(View itemView) {
            super(itemView);

            name_p = (TextView) itemView.findViewById(R.id.person_name);
            money = (TextView) itemView.findViewById(R.id.money);
            currency = (TextView) itemView.findViewById(R.id.expCurrency);



        }
    }


}