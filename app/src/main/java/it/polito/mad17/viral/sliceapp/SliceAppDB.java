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

    private static ArrayList<Gruppo> listaGruppi=new ArrayList<Gruppo>();
    public static HashMap<String, Gruppo> gruppi = new HashMap<String, Gruppo>();
    private static HashMap<Integer,Gruppo> mappaGruppi = new HashMap<Integer, Gruppo>();
    private static ArrayList<Spesa> listaSpese = new ArrayList<Spesa>();
    private static Map<String, Spesa> mappaSpese = new HashMap<String, Spesa>();

    // Getters
    public static Persona getUser(){
        return user;
    }
    public static ArrayList<Gruppo> getListaGruppi() { return listaGruppi; }
    public static HashMap<String, Gruppo> getGruppi() {
        return gruppi;
    }
    public static HashMap<Integer, Gruppo> getMappaGruppi() {
        return mappaGruppi;
    }
    public static ArrayList<Spesa> getListaSpese(){
        return listaSpese;
    }
    public static Map<String, Spesa> getMappaSpese() { return mappaSpese; }

    // Setters
    public static void setUser(Persona p){
        user = p;
    }

    // Other methods
    public static Gruppo getGruppo(Integer i){
        return mappaGruppi.get(i);
    }

    public static void addSpesa(Spesa s){
        listaSpese.add(s);
    }

    public static void addGruppo(Gruppo g){
        listaGruppi.add(g);
        gruppi.put(g.getGroupID(), g);
    }
    public static Gruppo getGruppoArray(Integer i){
        return listaGruppi.get(i);
    }
}

