package it.polito.mad17.viral.sliceapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kalos on 05/04/2017.
 */

public class SliceAppDB implements Serializable {
    private static ArrayList<Spesa> listaSpese = new ArrayList<Spesa>();

    public static HashMap<Integer, Gruppo> getMappaGruppo() {
        return gruppo;
    }

    public static void setMappaGruppo(HashMap<Integer, Gruppo> gruppo) {
        SliceAppDB.gruppo = gruppo;
    }

    public static HashMap<String, Gruppo> getGruppi() {
        return gruppi;
    }


    public static long getUserPhoneNumber(){ return user.getTelephone(); }
    public static HashMap<String, Gruppo> gruppi = new HashMap<String, Gruppo>();
    private static HashMap<Integer,Gruppo> gruppo = new HashMap<Integer, Gruppo>();
    private static Persona user;
    private static boolean flag= false;
    private static ArrayList<Gruppo> lista_gruppi=new ArrayList<Gruppo>();
    private static Policy policy;




    public static ArrayList<Spesa> getListaSpese(){
        return listaSpese;
    }

    public static void addSpesa(Spesa s){
        listaSpese.add(s);
    }


    public static Persona getUser(){
        return user;
    }

    public static void setUser(Persona p){
        user=p;
    }

    public static Gruppo getGruppo(Integer i){
        return gruppo.get(i);

    }


    public static ArrayList<Gruppo> getListaGruppi() {
        return lista_gruppi;
    }

    public static Policy getPolicy() {
        return policy;
    }
}

