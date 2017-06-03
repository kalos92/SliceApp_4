package it.polito.mad17.viral.sliceapp;

import android.os.AsyncTask;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Kalos on 02/06/2017.
 */

public class UpName extends AsyncTask<Void,Void,Void> {
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
    private DatabaseReference rootRef = database.getReference();
    ArrayList<Persona> p;
    String gID;
    String nname;

    public UpName(ArrayList<Persona> p, String gID, String nname){
        this.p=p;
        this.gID = gID;
        this.nname=nname;

    }

    @Override
    protected Void doInBackground(Void... params) {

        for(Persona p2 : p){
           rootRef.child("users_prova").child(p2.getTelephone()).child("gruppi_partecipo").child(gID).child("nome_gruppo").setValue(nname);
        }

        return null;
    }
}
