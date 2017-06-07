package it.polito.mad17.viral.sliceapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

/**
 * Created by Kalos on 07/06/2017.
 */

public class Upload_group_image extends AsyncTask<Void,Void,Void>{

    private final Context contex;
    private Policy policy;

    private ArrayList<Persona> tmpList = new ArrayList<Persona>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
    private DatabaseReference groups_prova = database.getReference().child("groups_prova");
    private DatabaseReference users_prova = database.getReference().child("users_prova");
    private FirebaseStorage storageReference = FirebaseStorage.getInstance("gs://sliceapp-a55d6.appspot.com/");

    private StorageReference images;
    private Bitmap b;
    private String groupID;
    private String groupName;
    private byte[] datas;
    private mCurrency curr;
    private Group_Details gd;
    private SpotsDialog dialog;

    public Upload_group_image(byte[] datas, String groupID, String groupName, Policy policy, ArrayList<Persona> tmpList, mCurrency curr2, Context c, Group_Details gd, SpotsDialog dialog){
        this.datas=datas;
        this.groupID=groupID;
        this.groupName=groupName;
        this.policy=policy;
        this.tmpList.addAll(tmpList);
        this.curr=curr2;
        this.contex=c;
        this.gd=gd;
        this.dialog=dialog;



    }

    @Override
    protected Void doInBackground(Void... params) {

        images = storageReference.getReference().child(groupID);
        UploadTask uploadTask = images.putBytes(datas);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();


                Gruppo g = new Gruppo(groupID,groupName, tmpList.size(), tmpList, policy,curr,downloadUrl,SliceAppDB.getUser().getTelephone());
                g.setGroupID(groupID);
                g.setUser(SliceAppDB.getUser());

                // setto il flag contested a false online



                Gson gson = new Gson();
                Gruppo g1 = gson.fromJson(gson.toJson(g),Gruppo.class);

                groups_prova.child(g1.getGroupID()).setValue(g1);


                for (Persona p : tmpList) {
                    Persona p1 = gson.fromJson(gson.toJson(p), Persona.class);

                    for(Persona altri: g1.obtainPartecipanti().values()) {
                        if(!p.getTelephone().equals(altri.getTelephone())){

                            users_prova.child(p.getTelephone()).child("amici").child(altri.getTelephone() + ";" + g1.getCurr().getChoosencurr()).child("ncname").setValue(altri.getName() + " " + altri.getSurname());
                            users_prova.child(p.getTelephone()).child("amici").child(altri.getTelephone() + ";" + g1.getCurr().getChoosencurr()).child("symbol").setValue(g1.getCurr().getSymbol());
                            users_prova.child(p.getTelephone()).child("amici").child(altri.getTelephone() + ";" + g1.getCurr().getChoosencurr()).child("digits").setValue(g1.getCurr().getDigits());
                            users_prova.child(p.getTelephone()).child("amici").child(altri.getTelephone() + ";" + g1.getCurr().getChoosencurr()).child("cc").setValue(g1.getCurr().getChoosencurr());
                        }}

                    users_prova.child(p1.getTelephone()).child("gruppi_partecipo").child(g1.getGroupID()).setValue(p1.getGruppi_partecipo().get(groupID));
                    users_prova.child(p1.getTelephone()).child("posizione_gruppi").child(g1.getGroupID()).setValue(p1.getPosizione(g1));
                    users_prova.child(p1.getTelephone()).child("dove_ho_debito").child(g1.getGroupID()).setValue(p1.getDove_ho_debito().get(groupID));
                }



    }

});
        return null;}




    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        dialog.dismiss();
        Intent i = new Intent(gd, List_Pager_Act.class);
        gd.startActivity(i);
        gd.finish();
    }
}
