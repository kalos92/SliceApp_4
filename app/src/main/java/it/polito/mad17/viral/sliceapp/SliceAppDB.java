package it.polito.mad17.viral.sliceapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kalos on 05/04/2017.
 */

public class SliceAppDB implements Serializable {

    // fields
    private static HashMap<Integer,Gruppo> gruppo = new HashMap<Integer, Gruppo>();
    private static ArrayList<Gruppo> lista_gruppi=new ArrayList<Gruppo>();
    private static ArrayList<Spesa> listaSpese = new ArrayList<Spesa>();
    public static HashMap<String, Gruppo> gruppi = new HashMap<String, Gruppo>();
    private static Persona user;

    // Getters
    public static ArrayList<Gruppo> getListaGruppi() {
        return lista_gruppi;
    }
    public static HashMap<Integer, Gruppo> getMappaGruppo() {
        return gruppo;
    }
    public static Persona getUser(){
        return user;
    }
    public static ArrayList<Spesa> getListaSpese(){
        return listaSpese;
    }
    public static HashMap<String, Gruppo> getGruppi() {
        return gruppi;
    }

    // Setters
    public static void setMappaGruppo(HashMap<Integer, Gruppo> gruppo) { SliceAppDB.gruppo = gruppo; }
    public static void setUser(Persona p){
        user = p;
    }

    // Other methods
    public static Gruppo getGruppo(Integer i){ return gruppo.get(i); }
    public static void addSpesa(Spesa s){
        listaSpese.add(s);
    }
    public static long getUserPhoneNumber(){ return user.getTelephone(); }

}

