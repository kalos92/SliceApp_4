package it.polito.mad17.viral.sliceapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
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

    public static SecondFragment newInstance(Persona user, Processor proc) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putSerializable("User", user);
        args.putSerializable("Proc", proc);
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
        final ListView mylist = (ListView) v.findViewById(R.id.listView1);


        Query ref = rootRef.child("users_prova").child(SliceAppDB.getUser().getTelephone()).child("amici");

        FirebaseListAdapter<Riga_Bilancio> adapter= new FirebaseListAdapter<Riga_Bilancio>(getActivity(), Riga_Bilancio.class, R.layout.listview_balance_row, ref) {

            @Override
            protected void populateView(View v, Riga_Bilancio model, int position) {

                TextView name = (TextView) v.findViewById(R.id.person_name);
                TextView money = (TextView) v.findViewById(R.id.money);

                if(model.getImporto()<0) {
                    String str = String.format("%.2f",model.getImporto()*-1);
                    money.setText("-" + str);
                    name.setText("You owe to "+ model.getNcname()+":");
                    money.setTextColor(getContext().getResources().getColor(R.color.row_non_pagate_bck));
                    name.setTextColor(getContext().getResources().getColor(R.color.row_non_pagate_bck));
                }
                else if (model.getImporto()>0){
                    String str = String.format("%.2f",model.getImporto());
                    money.setText("+"+ str);
                    name.setText(model.getNcname()+ " owe to you:");

                    money.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                    name.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                }
                else if (model.getImporto()==0){

                    money.setText("");
                    name.setText(model.getNcname()+ " has no problem with you");

                    money.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                    name.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                }










            }
        };
        mylist.setAdapter(adapter);

        //BalanceAdapter adapter = new BalanceAdapter(v.getContext(),R.layout.listview_balance_row,null,user);



        return v;
    }



}