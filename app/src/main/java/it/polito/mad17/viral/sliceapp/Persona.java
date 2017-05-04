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
    private String name;
    private String surname;
    private String username;
    private String  birthdate;
    private String telephone;
    private boolean hasDebts = false;
    private String password;
    private int isInDB = 0;

    // Constructor
    public Persona(String name, String surname, String username, String birthdate, String telephone){
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.birthdate = birthdate;
        this.telephone = telephone;
        //int n;
    }

    // Getters
    public String getName() {
        return name;
    }
    public String getSurname() {
        return surname;
    }
    public String getUsername(){
        return username;
    }
    public String getBirthdate() {
        return birthdate;
    }
    public String getTelephone() {
        return telephone;
    }
    public String getPassword() { return password; }
    public boolean getHasDebts(){ return hasDebts; }
    public int getIsInDB() {
        return isInDB;
    }

    public ArrayList<Gruppo> getGruppi() {
        return gruppi_partecipo;
    }
    public HashMap<String, Integer> getDove_Ho_Debito() {
        return dove_ho_debito;
    }

    // Setters
    public void setHasDebts(boolean haDebiti){
        this.hasDebts=haDebiti;
    }
    public void setIsInDB(int isInDB) {
        this.isInDB = isInDB;
    }
    public void setGruppi(ArrayList<Gruppo> gruppi_partecipo) { this.gruppi_partecipo = gruppi_partecipo; }
    public void setPassword(String password) { this.password = password; }
    public void setBirthdate(String birthdate){ this.birthdate = birthdate; }
    // Other methods
    public int getPosizione(Gruppo g) {
        return posizione_gruppi.get(g.getName());
    }

    public Integer CheckIfHasDebts(Gruppo g){
        Integer result= new Integer(0);
        ArrayList<Spesa> spese = g.getSpese();
        for (Spesa s: spese){
            if(!s.getPagante().getUsername().equals(username)){ //controllo solo le spese che NON ho fatto io
                if(!s.getDivisioni().get(this.getUsername()).getHaPagato()) {
                    result=1;
                    break;
                }
            }
        }
        return result;
    }

    public void setPosizione_inGroup(Gruppo g, int  i){
        posizione_gruppi.put(g.getName(),new Integer(i));
    }

    public void AddToGroup(Gruppo gruppo,int pos ){
        gruppi_partecipo.add(gruppo);
        posizione_gruppi.put(gruppo.getName(), new Integer(pos));
        dove_ho_debito.put(gruppo.getName(),new Integer(0));
    }

    public void setDove_Ho_debito(Gruppo g , Integer i) {
        this.dove_ho_debito.put(g.getName(),i);
    }

    public Drawable getProPic(Context context){
        Drawable d = context.getResources().getDrawable( R.drawable.clubbing);
        return d;
    }
}