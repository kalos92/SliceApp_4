package it.polito.mad17.viral.sliceapp;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class Fragment_of_money extends Fragment {



    private String ID;
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
    private DatabaseReference rootRef = database.getReference();
    private DatabaseReference groups_ref = rootRef.child("groups_prova");
    private TextView credits;
    private TextView debts;
    private Gruppo gruppo;

    public Fragment_of_money() {
        // Required empty public constructor
    }



    public static Fragment_of_money newInstance(Gruppo gruppo) {
        Fragment_of_money fragment = new Fragment_of_money();
        Bundle args = new Bundle();
        args.putSerializable("Gruppo", gruppo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           gruppo = (Gruppo) getArguments().getSerializable("Gruppo");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(SliceAppDB.getGroup(gruppo.getGroupID())!=null)
               gruppo = SliceAppDB.getGroup(gruppo.getGroupID());

        gruppo.setUser(SliceAppDB.getUser());

        groups_ref.child(gruppo.getGroupID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gruppo = dataSnapshot.getValue(Gruppo.class);
                gruppo.setUser(SliceAppDB.getUser());
                Double credit=gruppo.getAllCredits();
                Double debt=gruppo.getAllDebts();
                if(debt.compareTo(credit)>0){
                    debt-=credit;
                    debt=debt*(-1);
                    credit=0d;}
                else if(debt.compareTo(credit)==0){
                    debt=0d;
                    credit=0d;
                }
                else if(debt.compareTo(credit)<0){
                    credit-=debt;
                    debt=0d;

                }
                if(gruppo!=null) {
                    String s = String.format("%."+gruppo.getCurr().getDigits()+"f", debt);
                    debts.setText(s+" "+gruppo.getCurr().getSymbol());
                }
                if(gruppo!=null) {
                    String s = String.format("%."+gruppo.getCurr().getDigits()+"f", credit);
                    credits.setText("+"+s+" "+gruppo.getCurr().getSymbol());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_fragment_of_money, container, false);

        debts =(TextView) v.findViewById(R.id.debts_number);
        credits = (TextView) v.findViewById(R.id.credits_number);
        Double credit=gruppo.getAllCredits();
                Double debt=gruppo.getAllDebts();
   if(debt.compareTo(credit)>0){
        debt-=credit;
        debt=debt*(-1);
        credit=0d;}
        else if(debt.compareTo(credit)==0){
       debt=0d;
       credit=0d;
   }
   else if(debt.compareTo(credit)<0){
       credit-=debt;
       debt=0d;

   }

        if(gruppo!=null) {
            String s = String.format("%."+gruppo.getCurr().getDigits()+"f", debt);
            debts.setText(s+" "+gruppo.getCurr().getSymbol());
        }
        if(gruppo!=null) {
            String s = String.format("%."+gruppo.getCurr().getDigits()+"f", credit);
            credits.setText("+"+s+" "+gruppo.getCurr().getSymbol());
        }


        Button openbal = (Button) v.findViewById(R.id.openbal);


      v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =  new Intent (getActivity(),Group_balance.class);
                i.putExtra("Gruppo",gruppo);
                startActivity(i);
                getActivity().finish();

            }
        });

        return v;

    }







    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

}
