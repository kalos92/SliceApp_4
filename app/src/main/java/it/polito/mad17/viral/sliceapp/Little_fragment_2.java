package it.polito.mad17.viral.sliceapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class Little_fragment_2 extends Fragment {
    private View v;
    private Float[] percentuali;
    private Float percentuale;
    private Gruppo g;
    private List<Persona> persone = new ArrayList<>();
    private Policy p;

    private AlltheSame alltheSame;

    public interface AlltheSame{
        public void AlltheSame(Policy policy);
    }

    public Little_fragment_2() {
        // Required empty public constructor
    }


    public static Little_fragment_2 newInstance(Gruppo g) {
        Little_fragment_2 fragment = new Little_fragment_2();
        Bundle args = new Bundle();
        args.putSerializable("Gruppo", g);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!= null){
            this.g=(Gruppo) getArguments().getSerializable("Gruppo");
        }

    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.fragment_little_fragment_2, container, false);

        final ListView list = (ListView) v.findViewById(R.id.littleFragment2);

        persone.addAll(g.obtainPartecipanti().values());

        alltheSame = (AlltheSame) getActivity();

        final CheckBoxAdapter adapter = new CheckBoxAdapter(v.getContext(), R.layout.percentages_row, persone,g);
        list.setAdapter(adapter);




        Button b = (Button) v.findViewById(R.id.save2);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                v.requestFocus();


                if(adapter.getAllTheSame()!=null) {
                    p = new Policy(adapter.getAllTheSame());
                }

                if(alltheSame!=null)
                    alltheSame.AlltheSame(p);
                v.setFocusable(false);
                v.setFocusableInTouchMode(false);
                v.clearFocus();
                Toast.makeText(getContext(),"Policy Setted",Toast.LENGTH_LONG).show();

            }
        });


        return v;
    }


}
