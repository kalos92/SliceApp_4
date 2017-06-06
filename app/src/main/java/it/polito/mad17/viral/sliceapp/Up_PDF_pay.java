package it.polito.mad17.viral.sliceapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Kalos on 06/06/2017.
 */

public class Up_PDF_pay   extends AsyncTask<Void, Integer, Void> {

    private GregorianCalendar data;
    private Gruppo gruppo;
    private Persona buyer;
    private String nome;
    private String price;
    private String cat;
    private Policy policy;
    private String groupID;
    private FirebaseDatabase database =FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
    private DatabaseReference expensesRef = database.getReference().child("expenses");
    private DatabaseReference expense = expensesRef.push();
    private String expenseID = expense.getKey();
    private DatabaseReference groups_prova_2 = database.getReference().child("groups_prova");
    private FragmentActivity fa;
    private Uri uri;
    private FirebaseStorage storageReference = FirebaseStorage.getInstance("gs://sliceapp-a55d6.appspot.com/");
    private StorageReference images;
    private byte[] datas;

    public Up_PDF_pay(GregorianCalendar data, Gruppo gruppo, Persona buyer, String nome, String price, String cat, Policy policy, Persona user, Context context, FragmentActivity fa, Uri uri){
        this.buyer=buyer;
        this.cat=cat;
        this.price=price;
        this.nome=nome;
        this.gruppo=gruppo;
        this.data=data;
        this.policy=policy;
        this.groupID=gruppo.getGroupID();
        this.fa=fa;
        this.uri=uri;

    }

    @Override
    protected Void doInBackground(Void... params) {
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


                String data_s;


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

                //Fino a qui ho solo fatto roba in locale

                //Devo aggiornare il bilancio del gruppo e il bilancio totale su amici

                final DatabaseReference groups_prova = database.getReference().child("groups_prova").child(gruppo.getGroupID()).child("spese").child(s1.getExpenseID());
                Gson gson = new Gson();
                Spesa g1 = gson.fromJson(gson.toJson(s1),Spesa.class);
                groups_prova.setValue(g1);
                final Spesa s2=s1;
                final DatabaseReference users_prova = database.getReference().child("users_prova");

                for(final Persona p: partecipanti) {
                    String key_upd= users_prova.child(p.getTelephone()).child("gruppi_partecipo").child(groupID).child("unread").push().getKey();
                    users_prova.child(p.getTelephone()).child("gruppi_partecipo").child(groupID).child("unread").child(key_upd).setValue(1);
                    users_prova.child(p.getTelephone()).child("gruppi_partecipo").child(groupID).child("last").setValue(buyer.getName()+" has bought "+s2.getNome_spesa());
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


            }

        });

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        fa.finish();

    }
}
