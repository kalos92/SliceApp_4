package it.polito.mad17.viral.sliceapp;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Kalos on 13/05/2017.
 */

public class Processor implements Serializable {
    private Persona user = SliceAppDB.getUser();
    final FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
    final DatabaseReference rootRef = database.getReference();
    final DatabaseReference groups_ref = rootRef.child("groups_prova");

    private ArrayList<Gruppo> gruppi_bilancio = new ArrayList<Gruppo>();
    private ArrayList<String> numbers = new ArrayList<String>();
    private HashMap<String, Persona> partecipanti = new HashMap<String,Persona>();
    Map<String, Double> amici = new HashMap<String,Double>();
    ArrayList<Riga_Bilancio> amici_2 = new ArrayList<Riga_Bilancio>();


    public void run(final SplashScreen sp) {

            amici = new HashMap<String, Double>();
            final String uncname = new String(user.getName() + " " + user.getSurname());
        final Processor proc=this;
            final ArrayList<String> gruppi_da_prendere = new ArrayList<>(user.getGruppi_partecipo().keySet());
            Log.d("ArrayESISTE?","WE WAJO BELL STU OROLOG");
            groups_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();

                    while (it.hasNext()) {
                        DataSnapshot ds = it.next();

                        if (gruppi_da_prendere.contains(ds.getKey())) {
                            gruppi_bilancio.add(ds.getValue(Gruppo.class));
                        }

                    }

                    for (final Gruppo g :gruppi_bilancio) {


                        final DatabaseReference users_prova = rootRef.child("users_prova");

                        groups_ref.child(g.getGroupID()).child("partecipanti_numero_cnome").
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();

                                        while (it.hasNext()) {
                                            numbers.add(it.next().getKey()); // qui ho tutti i numeri delle persone
                                        }

                                        users_prova.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();

                                                while (it.hasNext()) {
                                                    DataSnapshot ds = it.next();
                                                    if (numbers.contains(ds.getKey())) {
                                                        Persona p = ds.getValue(Persona.class);
                                                        partecipanti.put(p.getTelephone(), p);
                                                    }

                                                }
                                                g.setPartecipanti(partecipanti);

                                                if (g.getPartecipante(user.getTelephone()) != null) {
                                                    String groupID = g.getGroupID();

                                                    for (Persona p : g.obtainPartecipanti().values()) {
                                                        if (!p.getTelephone().equals(user.getTelephone()) && amici.get(p.getName() + " " + p.getSurname()) == null)
                                                            amici.put(p.getName() + " " + p.getSurname(), 0d);
                                                    }

                                                    for (Spesa s : g.getSpese().values()) {
                                                        if (s.getGruppo().equals(groupID)) {
                                                            Collection<Soldo> ss = s.getDivisioni().values();
                                                            for (Soldo so : ss) {
                                                                String ncname = so.getPersona().getName() + " " + so.getPersona().getSurname();

                                                                if (so.getPagante().getTelephone().equals(user.getTelephone()) && !ncname.equals(uncname) && !so.getHaPagato()) {
                                                                    Double importo = amici.get(ncname);
                                                                    importo += so.getImporto();
                                                                    amici.put(ncname, importo);
                                                                } else if (!so.getPagante().getTelephone().equals(user.getTelephone()) && ncname.equals(uncname) && !so.getHaPagato()) {
                                                                    Double importo = amici.get(so.getPagante().getName() + " " + so.getPagante().getSurname());
                                                                    importo -= so.getImporto();
                                                                    amici.put(so.getPagante().getName() + " " + so.getPagante().getSurname(), importo);
                                                                }


                                                            }

                                                        }
                                                    }
                                                }



                                                for (String s : amici.keySet()) {
                                                    amici_2.add(new Riga_Bilancio(s, amici.get(s)));

                                                }
                                                Collections.sort(amici_2, new Comparator<Riga_Bilancio>() {
                                                    @Override
                                                    public int compare(Riga_Bilancio o1, Riga_Bilancio o2) {
                                                        return o1.getNcname().compareTo(o2.getNcname());
                                                    }

                                                    @Override
                                                    public boolean equals(Object obj) {
                                                        return false;
                                                    }

                                                });




                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }


                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }}
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

    }
    public ArrayList<Riga_Bilancio> getAmici_2(){
        return amici_2;
    }
}
