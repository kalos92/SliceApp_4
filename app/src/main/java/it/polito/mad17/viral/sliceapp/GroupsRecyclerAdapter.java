package it.polito.mad17.viral.sliceapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import dmax.dialog.SpotsDialog;

/**
 * Created by Kalos on 14/05/2017.
 */

public class GroupsRecyclerAdapter extends FirebaseRecyclerAdapter<Dettagli_Gruppo, FirstFragment.GroupHolder> {

    private Context context;
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
    private DatabaseReference rootRef = database.getReference();
    private DatabaseReference groups_ref = rootRef.child("groups_prova");
    private ArrayList<String> numbers = new ArrayList<String>();
    private HashMap<String, Persona> partecipanti = new HashMap<String, Persona>();
    private Gruppo g;


    /**
     * @param modelClass      Firebase will marshall the data at a location into
     *                        an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list.
     *                        You will be responsible for populating an instance of the corresponding
     *                        view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location,
     *                        using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public GroupsRecyclerAdapter(Class<Dettagli_Gruppo> modelClass, int modelLayout, Class<FirstFragment.GroupHolder> viewHolderClass, Query ref, Context context, ArrayList<String> n, Gruppo g, HashMap<String, Persona> p) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
        this.numbers = n;
        this.g = g;
        this.partecipanti = p;

    }

    @Override
    protected void populateViewHolder(FirstFragment.GroupHolder viewHolder, Dettagli_Gruppo model, int position) {
        viewHolder.groupName.setText(model.getNome_gruppo());

        viewHolder.valuta.setText(model.getValuta());
        if (model.getUri() != null)
            Picasso.with(context).load(model.getUri()).placeholder(R.drawable.img_gruppi).transform(new RoundedTransformation(400, 10)).into(viewHolder.img);
        else
            Picasso.with(context).load(R.drawable.img_gruppi).transform(new RoundedTransformation(100, 1)).into(viewHolder.img);

        if (model.getUnread() <= 99 && model.getUnread() > 0) {
            viewHolder.unread.setVisibility(View.VISIBLE);
            viewHolder.unread.setText(String.valueOf(model.getUnread()));
        } else if (model.getUnread() > 99) {
            viewHolder.unread.setVisibility(View.VISIBLE);
            viewHolder.unread.setText("+99");
        } else if (model.getUnread() == 0) {
            viewHolder.unread.setVisibility(View.GONE);
        }

        if (!model.getLast().equals("null_$")) {
            viewHolder.last.setVisibility(View.VISIBLE);
            viewHolder.last.setText(model.getLast());
        } else
            viewHolder.last.setVisibility(View.GONE);

        String key = model.getChiave();

        final String key2 = key;

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View v2 = v;
                final String chiave = key2;
                if (SliceAppDB.getGroup(chiave) != null) {
                    final DatabaseReference users_prova = rootRef.child("users_prova");
                    final SpotsDialog progressDialog = new SpotsDialog(v.getContext(), R.style.Custom);

                    g= SliceAppDB.getGroup(chiave);

                    numbers.addAll(g.getPartecipanti_numero_cnome().values());

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
                            g.setPartecipanti_3(partecipanti);
                            g.setUser(SliceAppDB.getUser());

                            Intent i = new Intent(v2.getContext(), ExpensesActivity.class);

                            i.putExtra("Gruppo", g);
                            i.putExtra("User", SliceAppDB.getUser());
                            SliceAppDB.createListenerOnGroupID(g.getGroupID(), g);
                            v2.getContext().startActivity(i);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                        });

                } else {
                    // caricare i partecipanti nel gruppo;
                    final DatabaseReference users_prova = rootRef.child("users_prova");


                    final SpotsDialog progressDialog = new SpotsDialog(v.getContext(), R.style.Custom);
                    progressDialog.show();
                    //  final ProgressDialog waiting = ProgressDialog.show(v.getContext(),"Downloading","I'm getting your information",true);

                    groups_ref.child(chiave).child("partecipanti_numero_cnome").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                             Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                             while (it.hasNext()) {
                                numbers.add(it.next().getKey()); // qui ho tutti i numeri delle persone
                            }

                            groups_ref.child(chiave).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    g = dataSnapshot.getValue(Gruppo.class);


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
                                            g.setPartecipanti_3(partecipanti);
                                            g.setUser(SliceAppDB.getUser());


                                            Intent i = new Intent(v2.getContext(), ExpensesActivity.class);


                                            i.putExtra("Gruppo", g);
                                            i.putExtra("User", SliceAppDB.getUser());
                                            SliceAppDB.createListenerOnGroupID(g.getGroupID(), g);
                                            v2.getContext().startActivity(i);
                                            progressDialog.dismiss();
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
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });


                }
            }

        });
    }
}













