package it.polito.mad17.viral.sliceapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SecondFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SecondFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecondFragment extends Fragment {

    private Persona user;

    public SecondFragment() {
        // Required empty public constructor
    }

    public static SecondFragment newInstance(Persona user) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putSerializable("User",user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
           user =(Persona) getArguments().getSerializable("User");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.slide_balance, container, false);
        final ListView mylist = (ListView) v.findViewById(R.id.listView1);

        ArrayList<Soldo> soldi = new ArrayList<Soldo>();
        for(Gruppo g : user.getGruppi()){
            for(Spesa s: g.getSpese()){
                for(Soldo so: s.getDivisioni().values())
                    soldi.add(so);
            }
        }

        final BalanceAdapter adapter = new BalanceAdapter(v.getContext(), R.layout.listview_balance_row, soldi,user);
        mylist.setAdapter(adapter);

        return v;
    }
}