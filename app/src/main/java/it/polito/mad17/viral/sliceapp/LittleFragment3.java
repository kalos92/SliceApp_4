package it.polito.mad17.viral.sliceapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kalos on 16/05/2017.
 */

public class LittleFragment3  extends Fragment {

    private View v;

    private Gruppo g;
    private ArrayList<Persona> persone = new ArrayList<>();
    private Policy p;

    GetPercentages_2 getPercentages2;

    public interface GetPercentages_2{
        public void getPercentages(Policy policy);
    }

    public LittleFragment3() {
        // Required empty public constructor
    }


    public static LittleFragment3 newInstance(ArrayList<Persona> persone) {
        LittleFragment3 fragment = new LittleFragment3();
        Bundle args = new Bundle();
        args.putSerializable("Gruppo", persone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!= null){
            this.persone= (ArrayList<Persona>) getArguments().getSerializable("Gruppo");
        }

    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.fragment_all_the_same, container, false);

        final ListView list = (ListView) v.findViewById(R.id.all_the_same1);

        final GetPercentages_2 getPercentages= (GetPercentages_2) getActivity();

        final NoBoxTextViewAdapter adapter = new NoBoxTextViewAdapter(v.getContext(), R.layout.all_the_same_row, persone);
        list.setAdapter(adapter);






        Button b = (Button) v.findViewById(R.id.save3);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                v.requestFocus();

                if(adapter.getPercentages()!=null)
                p = new Policy(adapter.getPercentages());
                else
                    p=null;

                if(getPercentages!=null)
                    getPercentages.getPercentages(p);
                v.setFocusable(false);
                v.setFocusableInTouchMode(false);
                v.clearFocus();
                Toast.makeText(getContext(),"Policy Setted",Toast.LENGTH_LONG).show();

            }
        });


        return v;
    }

}
