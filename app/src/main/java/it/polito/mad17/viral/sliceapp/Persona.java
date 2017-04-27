package it.polito.mad17.viral.sliceapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kalos on 24/03/2017.
 */

public class Persona implements Serializable {


    private HashMap<String,Integer> posizione_gruppi = new HashMap<String,Integer>();
    private HashMap<String,Integer> dove_ho_debito = new HashMap<String,Integer>(); // hashMap in cui ho
    // gruppo a : 0/1 se è 0 allora non ho nessun debito
    //                se è 1 allora ho almeno 1 debito
    private ArrayList<Gruppo> gruppi_partecipo = new ArrayList<Gruppo>();
    private String nome;
    private String cognome;
    private String username;
    private String  dob;
    private long   telefono;
    private boolean haDebiti=false;
    private String password;

    public int getisInDB() {
        return isInDB;
    }

    public void setisInDB(int isInDB) {
        this.isInDB = isInDB;
    }

    private int isInDB;

    public Persona(String nome, String cognome, String username, String dob, long telefono){
        this.nome=nome;
        this.cognome=cognome;
        this.username=username;
        this.dob=dob;
        this.telefono=telefono;
    }

    public Drawable getProPic(Context context){
        Drawable d = context.getResources().getDrawable( R.drawable.clubbing);

        return d;
    }

    public String getUserName(){
        return username;
    }

    public boolean getHaDebiti(){
        return haDebiti;
    }

    public void setHaDebiti(boolean haDebiti){
        this.haDebiti=haDebiti;
    }
    public int getPosizione(Gruppo g) {

        return posizione_gruppi.get(g.getGroupName());
    }

    public Integer CheckIfHasDebts(Gruppo g){
      Integer result= new Integer(0);

        ArrayList<Spesa> spese = g.getSpese();
            for (Spesa s: spese){
                if(!s.getPagante().getUsername().equals(username)){ //controllo solo le spese che NON ho fatto io
                     if(!s.getDivisioni().get(this.getUserName()).getHaPagato()) {
                         result=1;
                         break;
                     }
                }


            }
        return result;




    }

    public void setPosizione_inGroup(Gruppo g, int  i){
        posizione_gruppi.put(g.getGroupName(),new Integer(i));
    }



    public void AddToGroup(Gruppo gruppo,int pos ){

        gruppi_partecipo.add(gruppo);
        posizione_gruppi.put(gruppo.getGroupName(), new Integer(pos));
        dove_ho_debito.put(gruppo.getGroupName(),new Integer(0));



    }

    public ArrayList<Gruppo> getGruppi() {
        return gruppi_partecipo;
    }

    public void setGruppi(ArrayList<Gruppo> gruppi_partecipo) {
        this.gruppi_partecipo = gruppi_partecipo;
    }


    public String getUsername() {
        return username;
    }
    public String getDob() {
        return dob;
    }
    public String getName() {
        return nome;
    }
    public String getSurname() {
        return cognome;
    }
    public String getPassword() {
        return password;
    }
    public long getTelephone() {
        return telefono;
    }



    public HashMap<String, Integer> getDove_Ho_Debito() {
        return dove_ho_debito;
    }

    public void setDove_Ho_debito(Gruppo g , Integer i) {
        this.dove_ho_debito.put(g.getGroupName(),i);
    }

    public void setPassword(String password){
        this.password=password;
    }
}
