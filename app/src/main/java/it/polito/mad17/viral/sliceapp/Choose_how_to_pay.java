package it.polito.mad17.viral.sliceapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.GregorianCalendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class Choose_how_to_pay extends Fragment implements Select_Policy_Fragment.ReturnSelection_2{
    private View v;
    private String cat;
    private GregorianCalendar data;
    private Persona buyer;
    private Uri uri;
    private String price;
    private String nome;
    private String groupID;
    private Gruppo gruppo;
    private Persona user =SliceAppDB.getUser();
    private Policy policy;
    private byte[] datas;
    private Bitmap b;
    private FirebaseStorage storageReference = FirebaseStorage.getInstance("gs://sliceapp-a55d6.appspot.com/");
    private StorageReference images;


    public static Choose_how_to_pay newInstance(Gruppo g) {
        Choose_how_to_pay fragment = new Choose_how_to_pay();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) { }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_choose_how_to_pay, container, false);
        setRetainInstance(true);
        if(savedInstanceState!=null){
            if(savedInstanceState.getParcelable("Uri")!=null)
                uri = savedInstanceState.getParcelable("Uri");

            if(savedInstanceState.getParcelable("Bitmap")!=null)
                b = savedInstanceState.getParcelable("Bitmap");

            if(savedInstanceState.getSerializable("Date")!=null)
                data = (GregorianCalendar) savedInstanceState.getSerializable("Date");

            if(savedInstanceState.getSerializable("Cat")!=null)
                cat = (String) savedInstanceState.getSerializable("Cat");

            if(savedInstanceState.getSerializable("Buyer")!=null)
                buyer = (Persona) savedInstanceState.getSerializable("Buyer");

            if(savedInstanceState.getSerializable("Price")!=null)
                price = (String) savedInstanceState.getSerializable("Price");

            if(savedInstanceState.getSerializable("Name")!=null)
                nome = (String) savedInstanceState.getSerializable("Name");

            if(savedInstanceState.getSerializable("Policy")!=null)
                policy = (Policy) savedInstanceState.getSerializable("Policy");
        }
        Button save = (Button) v.findViewById(R.id.save_b);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // if(gruppo.getN_partecipanti()!= gruppo_backup.getN_partecipanti() || !confronta_map(gruppo.getPolicy(),gruppo_backup.getPolicy())){
               // Toast.makeText(getContext(),"Group info are changed, Expense NOT saved!",Toast.LENGTH_LONG).show();

                final FirebaseDatabase database =FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
                final DatabaseReference expensesRef = database.getReference().child("expenses");
                final DatabaseReference expense = expensesRef.push();
                final String expenseID = expense.getKey();
                // else{
                if(b!=null && uri==null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    datas = baos.toByteArray();
                    images = storageReference.getReference().child(expenseID);
                    UploadTask uploadTask = images.putBytes(datas);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            final Intent intent = new Intent(getActivity(), ExpensesActivity.class);
                            String data_s;
                            intent.putExtra("Gruppo", gruppo);
                            intent.putExtra("User", user);

                            if(data!=null){
                                int month = data.get(Calendar.MONTH);
                                month++;
                                data_s = data.get(Calendar.DAY_OF_MONTH)+"/"+month+"/"+data.get(Calendar.YEAR);}
                            else {
                                final Calendar c = Calendar.getInstance();
                                int year = c.get(Calendar.YEAR);
                                int month = c.get(Calendar.MONTH);
                                month++;
                                int day = c.get(Calendar.DAY_OF_MONTH);
                                data_s=day+"/"+month+"/"+year;
                            }
                            // aggiungo key della spesa
                            Spesa s1 = gruppo.AddSpesa(expenseID,buyer, policy, nome, data_s, Double.parseDouble(price));
                            s1.setValuta(gruppo.getCurr().getSymbol());
                            s1.setDigit(gruppo.getCurr().getDigits());
                            s1.setCat_string(cat);
                            s1.setImg(downloadUrl.toString());
                            s1.setChosenCurr(gruppo.getCurr().getChoosencurr());
                            gruppo.refreshC();




                            ArrayList<Persona> partecipanti = new ArrayList<Persona>(gruppo.obtainPartecipanti().values());

                            Collection<Soldo> parti = s1.getDivisioni().values();
                            for(Persona p: partecipanti){
                                if(!p.getTelephone().equals(user.getTelephone()))
                                    p.plusOneUnread(gruppo.getGroupID());

                                user.updateLast(gruppo.getGroupID(),buyer.getName(),s1.getNome_spesa()); //Why? non capisco ma funziona -> niente domande
                                p.updateLast(gruppo.getGroupID(),buyer.getName(),s1.getNome_spesa());
                                p.refreshTimeOfGroup(gruppo.getGroupID());

                            }


                            final DatabaseReference groups_prova = database.getReference().child("groups_prova").child(gruppo.getGroupID()).child("spese").child(s1.getExpenseID());
                            Gson gson = new Gson();
                            Spesa g1 = gson.fromJson(gson.toJson(s1),Spesa.class);
                            groups_prova.setValue(g1);



                            final DatabaseReference users_prova = database.getReference().child("users_prova");


                            final Spesa s2=s1;
                            final Collection<Soldo> parti2 = s1.getDivisioni().values();
                            for(final Persona p: partecipanti) {
                                users_prova.child(p.getTelephone()).child("gruppi_partecipo").child(groupID).runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {

                                        Dettagli_Gruppo dg = mutableData.getValue(Dettagli_Gruppo.class);

                                        if (dg == null) {

                                            return Transaction.success(mutableData);
                                        }
                                        int i = dg.getUnread();
                                        Gson gson = new Gson();
                                        Dettagli_Gruppo p1 = gson.fromJson(gson.toJson(p.getGruppi_partecipo().get(groupID)), Dettagli_Gruppo.class);
                                        //users_prova.child(p.getTelephone()).child("gruppi_partecipo").child(groupID).setValue(p1);
                                        i++;
                                        p1.setUnread(i);
                                        mutableData.setValue(p1);
                                        SliceAppDB.setUser(user);
                                        getActivity().startActivity(intent);
                                        getActivity().finish();

                                        users_prova.child(p.getTelephone()).child("amici").runTransaction(new Transaction.Handler() {

                                            @Override
                                            public Transaction.Result doTransaction(MutableData mutableData) {
                                                HashMap<String, Riga_Bilancio> amici_took = (HashMap<String, Riga_Bilancio>) mutableData.getValue();

                                                //if (amici_took != null)
                                                  //  p.setAmici(amici_took);

                                                if (s2.getPagante().getTelephone().equals(p.getTelephone())) { //Se il pagante sono io

                                                    for (Soldo s : parti2) {
                                                        if (!s.getPersona().getTelephone().equals(p.getTelephone())) {// per tutti i soldi che non sono io
                                                            p.addTobalance(s.getPersona(), s.getImporto(), gruppo.getCurr());
                                                        }
                                                    }
                                                } else { //non lo sono

                                                    for (Soldo s : parti2) {
                                                        if (s.getPersona().getTelephone().equals(p.getTelephone())) {// solo il mio soldo
                                                            p.addTobalance(s.getPagante(), (s.getImporto()) * -1d, gruppo.getCurr()); //prendo la mia parte in negativo per quella persona e andrò a sommarla al pagante
                                                        }

                                                    }
                                                }

                                                mutableData.setValue(p.getAmici());
                                                return Transaction.success(mutableData);
                                            }

                                            @Override
                                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                                getActivity().startActivity(intent);
                                                getActivity().finish();
                                            }
                                        });

                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                                    }
                                });

                        }
                }

                });
                }

                if(b==null && uri!=null) {

                    images = storageReference.getReference().child(expenseID+"PDF");
                    UploadTask uploadTask = images.putFile(uri);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            final Intent intent = new Intent(getActivity(), ExpensesActivity.class);
                            String data_s;
                            intent.putExtra("Gruppo", gruppo);
                            intent.putExtra("User", user);

                            if(data!=null){
                                int month = data.get(Calendar.MONTH);
                                month++;
                                data_s = data.get(Calendar.DAY_OF_MONTH)+"/"+month+"/"+data.get(Calendar.YEAR);}
                            else {
                                final Calendar c = Calendar.getInstance();
                                int year = c.get(Calendar.YEAR);
                                int month = c.get(Calendar.MONTH);
                                month++;
                                int day = c.get(Calendar.DAY_OF_MONTH);
                                data_s=day+"/"+month+"/"+year;
                            }
                            // aggiungo key della spesa
                            Spesa s1 = gruppo.AddSpesa(expenseID,buyer, policy, nome, data_s, Double.parseDouble(price));
                            s1.setValuta(gruppo.getCurr().getSymbol());
                            s1.setDigit(gruppo.getCurr().getDigits());
                            s1.setCat_string(cat);
                            s1.setUri(downloadUrl);
                            s1.setChosenCurr(gruppo.getCurr().getChoosencurr());
                            gruppo.refreshC();




                            ArrayList<Persona> partecipanti = new ArrayList<Persona>(gruppo.obtainPartecipanti().values());

                            Collection<Soldo> parti = s1.getDivisioni().values();
                            for(Persona p: partecipanti){
                                if(!p.getTelephone().equals(user.getTelephone()))
                                    p.plusOneUnread(gruppo.getGroupID());

                                user.updateLast(gruppo.getGroupID(),buyer.getName(),s1.getNome_spesa()); //Why? non capisco ma funziona -> niente domande
                                p.updateLast(gruppo.getGroupID(),buyer.getName(),s1.getNome_spesa());
                                p.refreshTimeOfGroup(gruppo.getGroupID());

                            }


                            final DatabaseReference groups_prova = database.getReference().child("groups_prova").child(gruppo.getGroupID()).child("spese").child(s1.getExpenseID());
                            Gson gson = new Gson();
                            Spesa g1 = gson.fromJson(gson.toJson(s1),Spesa.class);
                            groups_prova.setValue(g1);



                            final DatabaseReference users_prova = database.getReference().child("users_prova");


                            final Spesa s2=s1;
                            final Collection<Soldo> parti2 = s1.getDivisioni().values();
                            for(final Persona p: partecipanti) {
                                users_prova.child(p.getTelephone()).child("gruppi_partecipo").child(groupID).runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {

                                        Dettagli_Gruppo dg = mutableData.getValue(Dettagli_Gruppo.class);

                                        if (dg == null) {

                                            return Transaction.success(mutableData);
                                        }
                                        int i = dg.getUnread();
                                        Gson gson = new Gson();
                                        Dettagli_Gruppo p1 = gson.fromJson(gson.toJson(p.getGruppi_partecipo().get(groupID)), Dettagli_Gruppo.class);
                                        //users_prova.child(p.getTelephone()).child("gruppi_partecipo").child(groupID).setValue(p1);
                                        i++;
                                        p1.setUnread(i);
                                        mutableData.setValue(p1);
                                        SliceAppDB.setUser(user);
                                        getActivity().startActivity(intent);
                                        getActivity().finish();

                                        users_prova.child(p.getTelephone()).child("amici").runTransaction(new Transaction.Handler() {

                                            @Override
                                            public Transaction.Result doTransaction(MutableData mutableData) {
                                                HashMap<String, Riga_Bilancio> amici_took = (HashMap<String, Riga_Bilancio>) mutableData.getValue();

                                                //if (amici_took != null)
                                                   // p.setAmici(amici_took);

                                                if (s2.getPagante().getTelephone().equals(p.getTelephone())) { //Se il pagante sono io

                                                    for (Soldo s : parti2) {
                                                        if (!s.getPersona().getTelephone().equals(p.getTelephone())) {// per tutti i soldi che non sono io
                                                            p.addTobalance(s.getPersona(), s.getImporto(), gruppo.getCurr());
                                                        }
                                                    }
                                                } else { //non lo sono

                                                    for (Soldo s : parti2) {
                                                        if (s.getPersona().getTelephone().equals(p.getTelephone())) {// solo il mio soldo
                                                            p.addTobalance(s.getPagante(), (s.getImporto()) * -1d, gruppo.getCurr()); //prendo la mia parte in negativo per quella persona e andrò a sommarla al pagante
                                                        }

                                                    }
                                                }

                                                mutableData.setValue(p.getAmici());
                                                return Transaction.success(mutableData);
                                            }

                                            @Override
                                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                                getActivity().startActivity(intent);
                                                getActivity().finish();
                                            }
                                        });

                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                                    }
                                });

                            }
                        }

                    });
                }

                else if(b==null && uri==null) {

                final Intent intent = new Intent(getActivity(), ExpensesActivity.class);
                String data_s;
                intent.putExtra("Gruppo", gruppo);
                intent.putExtra("User", user);

                if(data!=null){
                    int month = data.get(Calendar.MONTH);
                month++;
                    data_s = data.get(Calendar.DAY_OF_MONTH)+"/"+month+"/"+data.get(Calendar.YEAR);}
                else {
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    month++;
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    data_s=day+"/"+month+"/"+year;
                }
                // aggiungo key della spesa
                Spesa s1 = gruppo.AddSpesa(expenseID,buyer, policy, nome, data_s, Double.parseDouble(price));
                    s1.setValuta(gruppo.getCurr().getSymbol());
                    s1.setDigit(gruppo.getCurr().getDigits());
                    s1.setCat_string(cat);
                    s1.setChosenCurr(gruppo.getCurr().getChoosencurr());

                gruppo.refreshC();




                ArrayList<Persona> partecipanti = new ArrayList<Persona>(gruppo.obtainPartecipanti().values());

                Collection<Soldo> parti = s1.getDivisioni().values();
            for(Persona p: partecipanti){
                if(!p.getTelephone().equals(user.getTelephone()))
                    p.plusOneUnread(gruppo.getGroupID());

                user.updateLast(gruppo.getGroupID(),buyer.getName(),s1.getNome_spesa()); //Why? non capisco ma funziona -> niente domande
                p.updateLast(gruppo.getGroupID(),buyer.getName(),s1.getNome_spesa());
                p.refreshTimeOfGroup(gruppo.getGroupID());

            }


                final DatabaseReference groups_prova = database.getReference().child("groups_prova").child(gruppo.getGroupID()).child("spese").child(s1.getExpenseID());
                Gson gson = new Gson();
                Spesa g1 = gson.fromJson(gson.toJson(s1),Spesa.class);
                groups_prova.setValue(g1);



                final DatabaseReference users_prova = database.getReference().child("users_prova");


                    final Spesa s2=s1;
                    final Collection<Soldo> parti2 = s1.getDivisioni().values();
                for(final Persona p: partecipanti) {
                    users_prova.child(p.getTelephone()).child("gruppi_partecipo").child(groupID).runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {

                            Dettagli_Gruppo dg = mutableData.getValue(Dettagli_Gruppo.class);

                            if (dg == null) {

                                return Transaction.success(mutableData);
                            }
                            int i = dg.getUnread();
                            Gson gson = new Gson();
                            Dettagli_Gruppo p1 = gson.fromJson(gson.toJson(p.getGruppi_partecipo().get(groupID)), Dettagli_Gruppo.class);
                            //users_prova.child(p.getTelephone()).child("gruppi_partecipo").child(groupID).setValue(p1);
                            i++;
                            p1.setUnread(i);
                            mutableData.setValue(p1);
                            SliceAppDB.setUser(user);


                            users_prova.child(p.getTelephone()).child("amici").runTransaction(new Transaction.Handler() {

                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    HashMap<String, Riga_Bilancio> amici_took = (HashMap<String, Riga_Bilancio>) mutableData.getValue();

                                   // if (amici_took != null)
                                    //    p.setAmici(amici_took);

                                    if (s2.getPagante().getTelephone().equals(p.getTelephone())) { //Se il pagante sono io

                                        for (Soldo s : parti2) {
                                            if (!s.getPersona().getTelephone().equals(p.getTelephone())) {// per tutti i soldi che non sono io
                                                p.addTobalance(s.getPersona(), s.getImporto(), gruppo.getCurr());
                                            }
                                        }
                                    } else { //non lo sono

                                        for (Soldo s : parti2) {
                                            if (s.getPersona().getTelephone().equals(p.getTelephone())) {// solo il mio soldo
                                                p.addTobalance(s.getPagante(), (s.getImporto()) * -1d, gruppo.getCurr()); //prendo la mia parte in negativo per quella persona e andrò a sommarla al pagante
                                            }

                                        }
                                    }

                                    users_prova.child(p.getTelephone()).child("amici").setValue(p.getAmici());
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                    getActivity().startActivity(intent);
                                    getActivity().finish();
                                }
                            });

                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                        }
                    });
                    //Dettagli_Gruppo p1 = gson.fromJson(gson.toJson(p.getGruppi_partecipo().get(groupID)),Dettagli_Gruppo.class);
                    //users_prova.child(p.getTelephone()).child("gruppi_partecipo").child(groupID).setValue(p1);

                }       }

                if(b!=null && uri!=null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    datas = baos.toByteArray();
                    images = storageReference.getReference().child(expenseID);
                    UploadTask uploadTask = images.putBytes(datas);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            final Intent intent = new Intent(getActivity(), ExpensesActivity.class);
                            String data_s;
                            intent.putExtra("Gruppo", gruppo);
                            intent.putExtra("User", user);

                            if(data!=null){
                                int month = data.get(Calendar.MONTH);
                                month++;
                                data_s = data.get(Calendar.DAY_OF_MONTH)+"/"+month+"/"+data.get(Calendar.YEAR);}
                            else {
                                final Calendar c = Calendar.getInstance();
                                int year = c.get(Calendar.YEAR);
                                int month = c.get(Calendar.MONTH);
                                month++;
                                int day = c.get(Calendar.DAY_OF_MONTH);
                                data_s=day+"/"+month+"/"+year;
                            }
                            // aggiungo key della spesa
                            Spesa s1 = gruppo.AddSpesa(expenseID,buyer, policy, nome, data_s, Double.parseDouble(price));
                            s1.setValuta(gruppo.getCurr().getSymbol());
                            s1.setDigit(gruppo.getCurr().getDigits());
                            s1.setCat_string(cat);
                            s1.setImg(downloadUrl.toString());
                            s1.setChosenCurr(gruppo.getCurr().getChoosencurr());
                            gruppo.refreshC();




                            ArrayList<Persona> partecipanti = new ArrayList<Persona>(gruppo.obtainPartecipanti().values());

                            Collection<Soldo> parti = s1.getDivisioni().values();
                            for(Persona p: partecipanti){
                                if(!p.getTelephone().equals(user.getTelephone()))
                                    p.plusOneUnread(gruppo.getGroupID());

                                user.updateLast(gruppo.getGroupID(),buyer.getName(),s1.getNome_spesa()); //Why? non capisco ma funziona -> niente domande
                                p.updateLast(gruppo.getGroupID(),buyer.getName(),s1.getNome_spesa());
                                p.refreshTimeOfGroup(gruppo.getGroupID());

                            }


                            final DatabaseReference groups_prova = database.getReference().child("groups_prova").child(gruppo.getGroupID()).child("spese").child(s1.getExpenseID());
                            Gson gson = new Gson();
                            Spesa g1 = gson.fromJson(gson.toJson(s1),Spesa.class);
                            groups_prova.setValue(g1);



                            final DatabaseReference users_prova = database.getReference().child("users_prova");


                            final Spesa s2=s1;
                            final Collection<Soldo> parti2 = s1.getDivisioni().values();
                            for(final Persona p: partecipanti) {
                                users_prova.child(p.getTelephone()).child("gruppi_partecipo").child(groupID).runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {

                                        Dettagli_Gruppo dg = mutableData.getValue(Dettagli_Gruppo.class);

                                        if (dg == null) {

                                            return Transaction.success(mutableData);
                                        }
                                        int i = dg.getUnread();
                                        Gson gson = new Gson();
                                        Dettagli_Gruppo p1 = gson.fromJson(gson.toJson(p.getGruppi_partecipo().get(groupID)), Dettagli_Gruppo.class);
                                        //users_prova.child(p.getTelephone()).child("gruppi_partecipo").child(groupID).setValue(p1);
                                        i++;
                                        p1.setUnread(i);
                                        mutableData.setValue(p1);
                                        SliceAppDB.setUser(user);
                                        getActivity().startActivity(intent);
                                        getActivity().finish();

                                        users_prova.child(p.getTelephone()).child("amici").runTransaction(new Transaction.Handler() {

                                            @Override
                                            public Transaction.Result doTransaction(MutableData mutableData) {
                                                HashMap<String, Riga_Bilancio> amici_took = (HashMap<String, Riga_Bilancio>) mutableData.getValue();

                                               // if (amici_took != null)
                                                  //  p.setAmici(amici_took);

                                                if (s2.getPagante().getTelephone().equals(p.getTelephone())) { //Se il pagante sono io

                                                    for (Soldo s : parti2) {
                                                        if (!s.getPersona().getTelephone().equals(p.getTelephone())) {// per tutti i soldi che non sono io
                                                            p.addTobalance(s.getPersona(), s.getImporto(), gruppo.getCurr());
                                                        }
                                                    }
                                                } else { //non lo sono

                                                    for (Soldo s : parti2) {
                                                        if (s.getPersona().getTelephone().equals(p.getTelephone())) {// solo il mio soldo
                                                            p.addTobalance(s.getPagante(), (s.getImporto()) * -1d, gruppo.getCurr()); //prendo la mia parte in negativo per quella persona e andrò a sommarla al pagante
                                                        }

                                                    }
                                                }

                                                mutableData.setValue(p.getAmici());

                                             //TODO aggiungo il PDF e aggiorno solo puntualmente
                                                return Transaction.success(mutableData);
                                            }

                                            @Override
                                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                                getActivity().startActivity(intent);
                                                getActivity().finish();
                                            }
                                        });

                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                       ;
                                    }
                                });

                            }
                        }

                    });
                }

            }
        });

        Button save_r = (Button) v.findViewById(R.id.save_r);

        save_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ExpensesActivity.class);
                String data_s;
                i.putExtra("Gruppo", gruppo);
                i.putExtra("User", user);

                if(data!=null){
                    int month = data.get(Calendar.MONTH);
                    month++;
                    data_s = data.get(Calendar.DAY_OF_MONTH)+"/"+month+"/"+data.get(Calendar.YEAR);}
                else {
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    month++;
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    data_s=day+"/"+month+"/"+year;
                }
                FirebaseDatabase database =FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
                DatabaseReference expensesRef = database.getReference().child("expenses");
                String groupID = gruppo.getGroupID(); // groupID del gruppo in questione
                DatabaseReference expense = expensesRef.push();

                // Siccome il metodo AddSpesa_and_try_repay, mette la spesa nella mappa,
                // con ID: nome_spesa+data, devo modificarlo con l'expenseID ritornato da firebase
                String expenseID = expense.getKey(); // aggiungo key della spesa
                Spesa s1 = gruppo.AddSpesa_and_try_repay(expenseID,buyer, policy, nome, data_s, Double.parseDouble(price));
                if(s1==null) {
                Toast.makeText(getContext(),"There is a good news, you do not have any debts! Be Happy", Toast.LENGTH_LONG).show();
                    return;
                }
                s1.setValuta(gruppo.getCurr().getSymbol());
                s1.setDigit(gruppo.getCurr().getDigits());
                s1.setBitmap_spesa(b);
                s1.setUri(uri);
                s1.setCat_string(cat);
                s1.setChosenCurr(gruppo.getCurr().getChoosencurr());
                gruppo.refreshC();




                ArrayList<Persona> partecipanti = new ArrayList<Persona>(gruppo.obtainPartecipanti().values());

                Collection<Soldo> parti = s1.getDivisioni().values();
                for(Persona p: partecipanti){
                    if(!p.getTelephone().equals(user.getTelephone()))
                        p.plusOneUnread(gruppo.getGroupID());

                    user.updateLast(gruppo.getGroupID(),buyer.getName(),s1.getNome_spesa()); //Why? non capisco ma funziona -> niente domande
                    p.updateLast(gruppo.getGroupID(),buyer.getName(),s1.getNome_spesa());
                    p.refreshTimeOfGroup(gruppo.getGroupID());
                    if(s1.getPagante().getTelephone().equals(p.getTelephone())){ //Se il pagante sono io

                        for(Soldo s: parti){
                            if(!s.getPersona().getTelephone().equals(p.getTelephone())){// per tutti i soldi che non sono io
                                p.addTobalance(s.getPersona(),s.getImporto(),gruppo.getCurr());
                            }}
                    }
                    else { //non lo sono

                        for (Soldo s : parti) {
                            if (s.getPersona().getTelephone().equals(p.getTelephone())) {// solo il mio soldo
                                p.addTobalance(s.getPagante(), (s.getImporto()) * -1d,gruppo.getCurr()); //prendo la mia parte in negativo per quella persona e andrò a sommarla al pagante
                            }

                        }
                    }
                }

                final DatabaseReference groups_prova = database.getReference().child("groups_prova").child(gruppo.getGroupID());
                Gson gson = new Gson();
                Gruppo g1 = gson.fromJson(gson.toJson(gruppo),Gruppo.class);
                groups_prova.setValue(g1);
                final DatabaseReference users_prova = database.getReference().child("users_prova");

                for(Persona p: partecipanti){
                    Persona p1 = gson.fromJson(gson.toJson(p),Persona.class);
                    users_prova.child(p1.getTelephone()).setValue(p1);

                }

                SliceAppDB.setUser(user);

                getActivity().startActivity(i);
                getActivity().finish();
            }
        });

        return v;
    }

    private boolean confronta_map(Policy policy, Policy policy1) {


        for(String s: policy.getPercentuali().keySet()){
            if(!policy1.getPercentuali().containsKey(s))
                return false; //se c'è uno nuovo

            if(!policy1.getPercentuali().get(s).equals(policy.getPercentuali().get(s)))
                return false; //se mi hanno cambiato la policy

        }



                return true;
    }


    @Override
    public void returnSelection_2( String cat, GregorianCalendar data, Persona buyer, Bitmap b, Uri uri, String nome, String price, Gruppo gruppo, Persona user, Choose_how_to_pay chtp, Policy policy) {

        this.cat=cat;
        this.data=data;//
        this.buyer=buyer;//
        this.b=b;
        this.uri=uri;
        this.nome=nome;//
        this.groupID=gruppo.getGroupID();//
        this.gruppo=gruppo;
        this.user=SliceAppDB.getUser();//
        this.policy=policy;//
        this.price=price;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState!=null){
            if(savedInstanceState.getParcelable("Uri")!=null)
                uri = savedInstanceState.getParcelable("Uri");
            if(savedInstanceState.getParcelable("Bitmap")!=null)
                b = savedInstanceState.getParcelable("Bitmap");
            if(savedInstanceState.getSerializable("Date")!=null)
                data = (GregorianCalendar) savedInstanceState.getSerializable("Date");
            if(savedInstanceState.getSerializable("Cat")!=null)
                cat = (String) savedInstanceState.getSerializable("Cat");
            if(savedInstanceState.getSerializable("Buyer")!=null)
                buyer = (Persona) savedInstanceState.getSerializable("Buyer");
            if(savedInstanceState.getSerializable("Price")!=null)
                price = (String) savedInstanceState.getSerializable("Price");
            if(savedInstanceState.getSerializable("Name")!=null)
                nome = (String) savedInstanceState.getSerializable("Name");
            if(savedInstanceState.getSerializable("Policy")!=null)
                policy = (Policy) savedInstanceState.getSerializable("Policy");
        }
    }
}
