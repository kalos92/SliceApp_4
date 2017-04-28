package it.polito.mad17.viral.sliceapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


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

        ArrayList<Gruppo> allGroups = SliceAppDB.getListaGruppi();
        ArrayList<Spesa> allExpenses = SliceAppDB.getListaSpese();
        ArrayList<Soldo> soldi = new ArrayList<Soldo>();

        Map<String, Double> amici = new HashMap<String, Double>();
        String uncname = new String(user.getName()+" "+user.getSurname());
        for(Gruppo g : allGroups){
            if(g.getPartecipante(user.getUserName()) != null){
                String groupID = g.getGroupID();

                for(Persona p: g.getPartecipanti().values() ) {
                    if (p.getTelephone() != user.getTelephone() && amici.get(p.getName() + " " + p.getSurname()) == null)
                        amici.put(p.getName() + " " + p.getSurname(), 0d);
                }

                for(Spesa s : allExpenses){
                    if(s.getGruppo().getGroupID().equals(groupID)){
                        Collection<Soldo> ss = s.getDivisioni().values();
                        for(Soldo so : ss){
                            String ncname = new String(so.getPersona().getName()+" "+so.getPersona().getSurname());

                                if(so.getPagante().getTelephone() == user.getTelephone() && !ncname.equals(uncname) ){
                                    Double importo = amici.get(ncname);
                                    importo += so.getImporto();
                                    amici.put(ncname,importo);
                                }
                                else if(so.getPagante().getTelephone() != user.getTelephone() && !ncname.equals(uncname)){
                                    Double importo = amici.get(ncname);
                                    importo -= so.getImporto();
                                    amici.put(ncname,importo);
                                }

                        }

                    }
                }
            }
        }

        ArrayList<Riga_Bilancio> amici_2 = new ArrayList<Riga_Bilancio>();
        for(String s: amici.keySet()){
            amici_2.add(new Riga_Bilancio(s,amici.get(s)));

        }
        final BalanceAdapter adapter = new BalanceAdapter(v.getContext(), R.layout.listview_balance_row, amici_2,user);
        mylist.setAdapter(adapter);

        return v;
    }
}