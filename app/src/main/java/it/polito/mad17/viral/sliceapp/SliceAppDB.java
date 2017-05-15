package it.polito.mad17.viral.sliceapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kalos on 05/04/2017.
 */

public class SliceAppDB implements Serializable {

    // fields
    private static Persona user;


    // Getters
    public static Persona getUser(){
        return user;
    }

    // Setters
    public static void setUser(Persona p){
        user = p;
    }

    // Other methods

}

