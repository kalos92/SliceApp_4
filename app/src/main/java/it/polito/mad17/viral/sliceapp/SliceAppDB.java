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
    final static DatabaseReference group_ref = rootRef.child("groups_prova");

    final static HashMap<String, Gruppo> listeners = new HashMap<>();
    final static HashMap<DatabaseReference, ValueEventListener> referenze = new HashMap<>();
    private static Persona user;


    // Getters
    public static Persona getUser(){
        return user;
    }

    // Setters
    public static void setUser(Persona p){

        user_ref.child(p.getTelephone()).addListenerForSingleValueEvent(new ValueEventListener() {
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


    public static void createListenerOnGroupID(String ID, Gruppo g){

        referenze.put(group_ref.child(ID), group_ref.addValueEventListener(new ListenGroupID(ID)));
        listeners.put(ID,g);
    }

    public static Gruppo getGroup(String chiave) {
        Gruppo g =listeners.get(chiave);
        if(g!=null)
            g.setUser(user);
        return g;

    }


    public static class ListenGroupID implements ValueEventListener{
        String ID;

        public ListenGroupID(String ID){
            this.ID=ID;
        };

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

           if(listeners.containsKey(ID)){
               listeners.put(ID,dataSnapshot.child(ID).getValue(Gruppo.class));
           }
           else
               listeners.put(ID,dataSnapshot.child(ID).getValue(Gruppo.class));

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }



}

