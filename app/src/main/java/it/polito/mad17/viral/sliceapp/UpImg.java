package it.polito.mad17.viral.sliceapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Kalos on 02/06/2017.
 */

public class UpImg extends AsyncTask<Void,Void,Void> {
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
    private DatabaseReference rootRef = database.getReference();
    private FirebaseStorage storageReference = FirebaseStorage.getInstance("gs://sliceapp-a55d6.appspot.com/");
    private StorageReference images;
    private Bitmap b;
    private String gID;
    private ArrayList<Persona> persone ;
    private Context context;
    private byte[] datas;


    public UpImg(Bitmap b, String gID, ArrayList<Persona> persone, Context context){
        this.b=b;
        this.gID=gID;
        this.persone=persone;
        this.context=context;


    }

    @Override
    protected Void doInBackground(Void... params) {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            datas = baos.toByteArray();
            images = storageReference.getReference().child(gID);
            UploadTask uploadTask = images.putBytes(datas);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {


                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests")
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    rootRef.child("groups_prova").child(gID).child("uri").setValue(downloadUrl.toString());


                    for(Persona p2: persone){
                        rootRef.child("users_prova").child(p2.getTelephone()).child("gruppi_partecipo").child(gID).child("uri").setValue(downloadUrl.toString());
                    }



                }


            });
            return null;
    }
}
