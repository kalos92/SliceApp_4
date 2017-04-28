package it.polito.mad17.viral.sliceapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Collection;


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
        args.putSerializable("User", user);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.slide_balance, container, false);
        final ListView mylist = (ListView) v.findViewById(R.id.listView1);

        // modifica di abdel
        System.out.println("User bilancio " + user.getUserName());
        ArrayList<Gruppo> allGroups = SliceAppDB.getListaGruppi();
        ArrayList<Spesa> allExpenses = SliceAppDB.getListaSpese();
        for(Gruppo g : allGroups)
            System.out.println("Gruppo " + g.getGroupID());

        ArrayList<Soldo> soldi = new ArrayList<Soldo>();
        for(Gruppo g : allGroups){
            if(g.getPartecipante(user.getUserName()) != null){
                String groupName = g.getGroupID();
                System.out.println("gruppo a cui partecipo " + groupName);
                for(Spesa s : allExpenses){
                    if(s.getGruppo().getGroupID().equals(groupName)){
                        System.out.println("Spese del gruppo corrente " + s.getNome());
                        Collection<Soldo> ss = s.getDivisioni().values();
                        for(Soldo so : ss)
                            soldi.add(so);
                    }
                }
            }
        }
        System.out.println("numero soldi " + soldi.size());
        // fine modifica di abdel

        /*ArrayList<Soldo> soldi = new ArrayList<Soldo>();
        for(Gruppo g : user.getGruppi()){
            for(Spesa s: g.getSpese()){
                for(Soldo so: s.getDivisioni().values())
                    soldi.add(so);
            }
        }*/

        final BalanceAdapter adapter = new BalanceAdapter(v.getContext(), R.layout.listview_balance_row, soldi,user);
        mylist.setAdapter(adapter);

        return v;
    }
}