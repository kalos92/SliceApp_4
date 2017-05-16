package it.polito.mad17.viral.sliceapp;

import android.content.Intent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kalos on 05/04/2017.
 */

public class SliceAppDB implements Serializable {
    final static FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
    final static DatabaseReference rootRef = database.getReference();
    final static DatabaseReference user_ref = rootRef.child("users_prova");
    private static HashMap<String, Gruppo> cache = new HashMap<String,Gruppo>();
    // fields
    private static Persona user;


    // Getters
    public static Persona getUser(){
        return user;
    }

    // Setters
    public static void setUser(Persona p){

        user_ref.child(p.getTelephone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(Persona.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Other methods

    public static void setUser_1(Persona p){

            user=p;
    }




}

