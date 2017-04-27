package it.polito.mad17.viral.sliceapp;

import android.support.v4.app.FragmentActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



public class Fragment_of_money extends Fragment {



    private Gruppo gruppo;



    public Fragment_of_money() {
        // Required empty public constructor
    }



    public static Fragment_of_money newInstance(Gruppo g) {
        Fragment_of_money fragment = new Fragment_of_money();
        Bundle args = new Bundle();
        args.putSerializable("Gruppo", g);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_fragment_of_money, container, false);
        TextView debts =(TextView) v.findViewById(R.id.debts_number);
        if(gruppo!=null) {
            String s = String.format("%.2f", gruppo.getAllDebts()*-1);
            debts.setText(s+"€");
        }
        TextView credits =(TextView) v.findViewById(R.id.credits_number);
        if(gruppo!=null) {
            String s = String.format("%.2f", gruppo.getAllCredits());
            credits.setText("+"+s+"€");
        }
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
