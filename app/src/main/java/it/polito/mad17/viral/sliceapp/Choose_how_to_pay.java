package it.polito.mad17.viral.sliceapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.GregorianCalendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

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
    private int tipo_policy;


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

            tipo_policy=savedInstanceState.getInt("Tipo");
        }

        Button save = (Button) v.findViewById(R.id.save_btn);

        TextView tv1 = (TextView) v.findViewById(R.id.summary_name_show);
        TextView tv2= (TextView) v.findViewById(R.id.buyer_show);
        TextView tv3= (TextView) v.findViewById(R.id.import_show);
        TextView tv4= (TextView) v.findViewById(R.id.date_show);
        TextView tv5= (TextView) v.findViewById(R.id.photo_txt);
        TextView tv6= (TextView) v.findViewById(R.id.pdf_txt);
        TextView tv7= (TextView) v.findViewById(R.id.policy_show);

        tv1.setText(nome);
        tv2.setText(buyer.getName()+" "+buyer.getSurname());
        tv3.setText(price+" "+gruppo.getCurr().getChoosencurr());

        String data_ss;

        if(data!=null){
            int month = data.get(Calendar.MONTH);
            month++;
            data_ss = data.get(Calendar.DAY_OF_MONTH)+"/"+month+"/"+data.get(Calendar.YEAR);
        }
        else{
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            month++;
            int day = c.get(Calendar.DAY_OF_MONTH);
            data_ss=day+"/"+month+"/"+year;
            }

        tv4.setText(data_ss);

        if(b!=null)
            tv5.setText("Attached Photo:         Yes");
        else
            tv5.setText("Attached Photo:         No");

        if(uri!=null)
            tv6.setText("Attached PDF:         Yes");
        else
            tv6.setText("Attached PDF:         No");

        if(tipo_policy==1)
            tv7.setText("Equal Division");
        else if(tipo_policy==2)
            tv7.setText("Custom Division");
        else if(tipo_policy==0)
            tv7.setText("Group Policy");


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SpotsDialog dialog = new SpotsDialog(getContext(),"Saving Expense");
                dialog.show();


                final FirebaseDatabase database =FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
                final DatabaseReference expensesRef = database.getReference().child("expenses");
                final DatabaseReference expense = expensesRef.push();
                final String expenseID = expense.getKey();
                final DatabaseReference groups_prova_2 = database.getReference().child("groups_prova");


                if(b!=null && uri==null) {                                              //SOLO IMG
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

                            gruppo.refreshC();

                            final ArrayList<Persona> partecipanti = new ArrayList<Persona>(gruppo.obtainPartecipanti().values());

                            for(Persona p: partecipanti){

                                if(!p.getTelephone().equals(user.getTelephone()))
                                    p.plusOneUnread(gruppo.getGroupID());

                                user.updateLast(gruppo.getGroupID(),buyer.getName(),s1.getNome_spesa()); //Why? non capisco ma funziona -> niente domande
                                p.updateLast(gruppo.getGroupID(),buyer.getName(),s1.getNome_spesa());
                                p.refreshTimeOfGroup(gruppo.getGroupID());

                            }


                            //Fino a qui ho solo fatto roba in locale

                            //Devo aggiornare il bilancio del gruppo e il bilancio totale su amici

                            final DatabaseReference groups_prova = database.getReference().child("groups_prova").child(gruppo.getGroupID()).child("spese").child(s1.getExpenseID());
                            Gson gson = new Gson();
                            Spesa g1 = gson.fromJson(gson.toJson(s1),Spesa.class);
                            groups_prova.setValue(g1);

                            final DatabaseReference users_prova = database.getReference().child("users_prova");
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
                                        i++;
                                        p1.setUnread(i);
                                        mutableData.setValue(p1);
                                        SliceAppDB.setUser(user);

                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                                    }
                                });
                                //Aggiornamento di amici che posso fare senza transaction
                                String key =users_prova.child(p.getTelephone()).child("amici").child(p.getTelephone()+";"+gruppo.getCurr().getChoosencurr()).push().getKey();
                                if(!p.getTelephone().equals(s1.getPagante().getTelephone()))
                                    users_prova.child(p.getTelephone()).child("amici").child(s1.getPagante().getTelephone()+";"+gruppo.getCurr().getChoosencurr()).child("importo").child(key).setValue(s1.getDivisioni().get(p.getTelephone()).getImporto()*-1);
                                else
                                {
                                    for(Persona altri : partecipanti){
                                        if(!altri.getTelephone().equals(s1.getPagante().getTelephone()))
                                            users_prova.child(s1.getPagante().getTelephone()).child("amici").child(altri.getTelephone()+";"+gruppo.getCurr().getChoosencurr()).child("importo").child(key).setValue(s1.getDivisioni().get(p.getTelephone()).getImporto());

                                    }
                                }


                                //ORA devo aggiornare tutti i bilanci relativi e gli ID spesa e importo, tel pagante;
                                if(!p.getTelephone().equals(s1.getPagante().getTelephone())) {
                                    //Io sono una persona che non É il pagante quindi mi devo mettere il mio bilancio con una spesa -
                                    String key_g = groups_prova.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(p.getTelephone()).child("bilancio_relativo").child(s1.getPagante().getTelephone()).child("importo").push().getKey();
                                    groups_prova_2.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(p.getTelephone()).child("bilancio_relativo").child(s1.getPagante().getTelephone()).child("importo").child(key_g).setValue(s1.getDivisioni().get(p.getTelephone()).getImporto() * -1);
                                    groups_prova_2.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(s1.getPagante().getTelephone()).child("bilancio_relativo").child(p.getTelephone()).child("importo").child(key_g).setValue(s1.getDivisioni().get(p.getTelephone()).getImporto());
                                }
                                    users_prova.child(p.getTelephone()).child("gruppi_partecipo").child(gruppo.getGroupID()).child("time").setValue(gruppo.getC()*-1);


                            }//for

                            String key_s =  groups_prova_2.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(s1.getPagante().getTelephone()).child("importo").push().getKey();
                            groups_prova_2.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(s1.getPagante().getTelephone()).child("importo").child(key_s).setValue(s1.getImporto());
                            //dialog.dismiss();
                            getActivity().startActivity(intent);
                            //
                            //getActivity().finish();
                }

                });
                }                       //FINE SOLO PDF

                if(b==null && uri!=null) {                  //SOLO PDF

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
                            s1.setUri(downloadUrl.toString());

                            gruppo.refreshC();

                            final ArrayList<Persona> partecipanti = new ArrayList<Persona>(gruppo.obtainPartecipanti().values());

                            for(Persona p: partecipanti){

                                if(!p.getTelephone().equals(user.getTelephone()))
                                    p.plusOneUnread(gruppo.getGroupID());

                                user.updateLast(gruppo.getGroupID(),buyer.getName(),s1.getNome_spesa()); //Why? non capisco ma funziona -> niente domande
                                p.updateLast(gruppo.getGroupID(),buyer.getName(),s1.getNome_spesa());
                                p.refreshTimeOfGroup(gruppo.getGroupID());

                            }


                            //Fino a qui ho solo fatto roba in locale

                            //Devo aggiornare il bilancio del gruppo e il bilancio totale su amici

                            final DatabaseReference groups_prova = database.getReference().child("groups_prova").child(gruppo.getGroupID()).child("spese").child(s1.getExpenseID());
                            Gson gson = new Gson();
                            Spesa g1 = gson.fromJson(gson.toJson(s1),Spesa.class);
                            groups_prova.setValue(g1);

                            final DatabaseReference users_prova = database.getReference().child("users_prova");

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
                                        i++;
                                        p1.setUnread(i);
                                        mutableData.setValue(p1);
                                        SliceAppDB.setUser(user);

                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                                    }
                                });
                                //Aggiornamento di amici che posso fare senza transaction
                                String key =users_prova.child(p.getTelephone()).child("amici").child(p.getTelephone()+";"+gruppo.getCurr().getChoosencurr()).push().getKey();
                                if(!p.getTelephone().equals(s1.getPagante().getTelephone()))
                                    users_prova.child(p.getTelephone()).child("amici").child(s1.getPagante().getTelephone()+";"+gruppo.getCurr().getChoosencurr()).child("importo").child(key).setValue(s1.getDivisioni().get(p.getTelephone()).getImporto()*-1);
                                else
                                {
                                    for(Persona altri : partecipanti){
                                        if(!altri.getTelephone().equals(s1.getPagante().getTelephone()))
                                            users_prova.child(s1.getPagante().getTelephone()).child("amici").child(altri.getTelephone()+";"+gruppo.getCurr().getChoosencurr()).child("importo").child(key).setValue(s1.getDivisioni().get(p.getTelephone()).getImporto());

                                    }
                                }


                                //ORA devo aggiornare tutti i bilanci relativi e gli ID spesa e importo, tel pagante;
                                if(!p.getTelephone().equals(s1.getPagante().getTelephone())) {
                                    //Io sono una persona che non É il pagante quindi mi devo mettere il mio bilancio con una spesa -
                                    String key_g = groups_prova.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(p.getTelephone()).child("bilancio_relativo").child(s1.getPagante().getTelephone()).child("importo").push().getKey();
                                    groups_prova_2.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(p.getTelephone()).child("bilancio_relativo").child(s1.getPagante().getTelephone()).child("importo").child(key_g).setValue(s1.getDivisioni().get(p.getTelephone()).getImporto() * -1);
                                    groups_prova_2.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(s1.getPagante().getTelephone()).child("bilancio_relativo").child(p.getTelephone()).child("importo").child(key_g).setValue(s1.getDivisioni().get(p.getTelephone()).getImporto());
                                }
                                users_prova.child(p.getTelephone()).child("gruppi_partecipo").child(gruppo.getGroupID()).child("time").setValue(gruppo.getC()*-1);
                            }//for

                            String key_s =  groups_prova_2.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(s1.getPagante().getTelephone()).child("importo").push().getKey();
                            groups_prova_2.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(s1.getPagante().getTelephone()).child("importo").child(key_s).setValue(s1.getImporto());
                          //  dialog.dismiss();
                            getActivity().startActivity(intent);
                           // getActivity().finish();
                        }

                    });
                }   // FINE SOLO IMMAGINE

                else if(b==null && uri==null) {  //TODO QUESTO é QUELLO CHE é FUNZIONA ORA DEVO ADATTARE A TUTTO

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

                    gruppo.refreshC();

                    final ArrayList<Persona> partecipanti = new ArrayList<Persona>(gruppo.obtainPartecipanti().values());

                    for(Persona p: partecipanti){

                        if(!p.getTelephone().equals(user.getTelephone()))
                            p.plusOneUnread(gruppo.getGroupID());

                        user.updateLast(gruppo.getGroupID(),buyer.getName(),s1.getNome_spesa()); //Why? non capisco ma funziona -> niente domande
                        p.updateLast(gruppo.getGroupID(),buyer.getName(),s1.getNome_spesa());
                        p.refreshTimeOfGroup(gruppo.getGroupID());

            }


            //Fino a qui ho solo fatto roba in locale

                    //Devo aggiornare il bilancio del gruppo e il bilancio totale su amici

                final DatabaseReference groups_prova = database.getReference().child("groups_prova").child(gruppo.getGroupID()).child("spese").child(s1.getExpenseID());
                Gson gson = new Gson();
                Spesa g1 = gson.fromJson(gson.toJson(s1),Spesa.class);
                groups_prova.setValue(g1);

                    final DatabaseReference users_prova = database.getReference().child("users_prova");

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
                            i++;
                            p1.setUnread(i);
                            mutableData.setValue(p1);
                            SliceAppDB.setUser(user);

                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                        }
                    });
                    //Aggiornamento di amici che posso fare senza transaction
                    String key =users_prova.child(p.getTelephone()).child("amici").child(p.getTelephone()+";"+gruppo.getCurr().getChoosencurr()).push().getKey();
                    if(!p.getTelephone().equals(s1.getPagante().getTelephone())) {

                        users_prova.child(p.getTelephone()).child("amici").child(s1.getPagante().getTelephone() + ";" + gruppo.getCurr().getChoosencurr()).child("importo").child(key).setValue(s1.getDivisioni().get(p.getTelephone()).getImporto() * -1);
                    }
                        else
                    {
                        for(Persona altri : partecipanti){
                            if(!altri.getTelephone().equals(s1.getPagante().getTelephone()))
                            users_prova.child(s1.getPagante().getTelephone()).child("amici").child(altri.getTelephone()+";"+gruppo.getCurr().getChoosencurr()).child("importo").child(key).setValue(s1.getDivisioni().get(p.getTelephone()).getImporto());

                        }
                    }


                        if(!p.getTelephone().equals(s1.getPagante().getTelephone())) {
                            //Io sono una persona che non É il pagante quindi mi devo mettere il mio bilancio con una spesa -
                            String key_g = groups_prova.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(p.getTelephone()).child("bilancio_relativo").child(s1.getPagante().getTelephone()).child("importo").push().getKey();
                            groups_prova_2.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(p.getTelephone()).child("bilancio_relativo").child(s1.getPagante().getTelephone()).child("importo").child(key_g).setValue(s1.getDivisioni().get(p.getTelephone()).getImporto() * -1);
                            groups_prova_2.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(s1.getPagante().getTelephone()).child("bilancio_relativo").child(p.getTelephone()).child("importo").child(key_g).setValue(s1.getDivisioni().get(p.getTelephone()).getImporto());
                        }


                        users_prova.child(p.getTelephone()).child("gruppi_partecipo").child(gruppo.getGroupID()).child("time").setValue(gruppo.getC()*-1);
                }//for

                    String key_s =  groups_prova_2.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(s1.getPagante().getTelephone()).child("importo").push().getKey();
                    groups_prova_2.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(s1.getPagante().getTelephone()).child("importo").child(key_s).setValue(s1.getImporto());
                   // dialog.dismiss();
                    getActivity().startActivity(intent);
                   // getActivity().finish();
                }// FINE NIENTE

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

                            gruppo.refreshC();

                            final ArrayList<Persona> partecipanti = new ArrayList<Persona>(gruppo.obtainPartecipanti().values());

                            for(Persona p: partecipanti){

                                if(!p.getTelephone().equals(user.getTelephone()))
                                    p.plusOneUnread(gruppo.getGroupID());

                                user.updateLast(gruppo.getGroupID(),buyer.getName(),s1.getNome_spesa()); //Why? non capisco ma funziona -> niente domande
                                p.updateLast(gruppo.getGroupID(),buyer.getName(),s1.getNome_spesa());
                                p.refreshTimeOfGroup(gruppo.getGroupID());

                            }


                            StorageReference images_2 = storageReference.getReference().child(expenseID+"PDF");
                            UploadTask uploadTask = images_2.putFile(uri);
                            final Spesa s2=s1;

                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    s2.setUri(downloadUrl.toString());

                                    final DatabaseReference groups_prova = database.getReference().child("groups_prova").child(gruppo.getGroupID()).child("spese").child(s2.getExpenseID());
                                    Gson gson = new Gson();
                                    Spesa g1 = gson.fromJson(gson.toJson(s2),Spesa.class);
                                    groups_prova.setValue(g1);

                                    final DatabaseReference users_prova = database.getReference().child("users_prova");

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
                                                i++;
                                                p1.setUnread(i);
                                                mutableData.setValue(p1);
                                                SliceAppDB.setUser(user);

                                                return Transaction.success(mutableData);
                                            }

                                            @Override
                                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                                            }
                                        });
                                        //Aggiornamento di amici che posso fare senza transaction
                                        String key =users_prova.child(p.getTelephone()).child("amici").child(p.getTelephone()+";"+gruppo.getCurr().getChoosencurr()).push().getKey();
                                        if(!p.getTelephone().equals(s2.getPagante().getTelephone()))
                                            users_prova.child(p.getTelephone()).child("amici").child(s2.getPagante().getTelephone()+";"+gruppo.getCurr().getChoosencurr()).child("importo").child(key).setValue(s2.getDivisioni().get(p.getTelephone()).getImporto()*-1);
                                        else
                                        {
                                            for(Persona altri : partecipanti){
                                                if(!altri.getTelephone().equals(s2.getPagante().getTelephone()))
                                                    users_prova.child(s2.getPagante().getTelephone()).child("amici").child(altri.getTelephone()+";"+gruppo.getCurr().getChoosencurr()).child("importo").child(key).setValue(s2.getDivisioni().get(p.getTelephone()).getImporto());

                                            }
                                        }


                                        //ORA devo aggiornare tutti i bilanci relativi e gli ID spesa e importo, tel pagante;
                                        if(!p.getTelephone().equals(s2.getPagante().getTelephone())) {
                                            //Io sono una persona che non É il pagante quindi mi devo mettere il mio bilancio con una spesa -
                                            String key_g = groups_prova.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(p.getTelephone()).child("bilancio_relativo").child(s2.getPagante().getTelephone()).child("importo").push().getKey();
                                            groups_prova_2.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(p.getTelephone()).child("bilancio_relativo").child(s2.getPagante().getTelephone()).child("importo").child(key_g).setValue(s2.getDivisioni().get(p.getTelephone()).getImporto() * -1);
                                            groups_prova_2.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(s2.getPagante().getTelephone()).child("bilancio_relativo").child(p.getTelephone()).child("importo").child(key_g).setValue(s2.getDivisioni().get(p.getTelephone()).getImporto());
                                        }
                                        users_prova.child(p.getTelephone()).child("gruppi_partecipo").child(gruppo.getGroupID()).child("time").setValue(gruppo.getC()*-1);
                                    }//for

                                    String key_s =  groups_prova_2.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(s2.getPagante().getTelephone()).child("importo").push().getKey();
                                    groups_prova_2.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(s2.getPagante().getTelephone()).child("importo").child(key_s).setValue(s2.getImporto());

                                    getActivity().startActivity(intent);

                                }


                            });


                            //Fino a qui ho solo fatto roba in locale

                            //Devo aggiornare il bilancio del gruppo e il bilancio totale su amici


                        }

                    });
                }
                dialog.dismiss();
                getActivity().finish();
            }
        });
        return v;
    }



    @Override
    public void returnSelection_2( String cat, GregorianCalendar data, Persona buyer, Bitmap b, Uri uri, String nome, String price, Gruppo gruppo, Persona user, Choose_how_to_pay chtp, Policy policy, int tipo_policy) {

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
        this.tipo_policy=tipo_policy;
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
                tipo_policy= savedInstanceState.getInt("Tipo");
        }
    }
}
