package it.polito.mad17.viral.sliceapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kalos on 24/03/2017.
 */

public class Persona implements Serializable {



    private HashMap<String,Integer> posizione_gruppi = new HashMap<String,Integer>();
    private HashMap<String,Integer> dove_ho_debito = new HashMap<String,Integer>(); // hashMap in cui ho
    // gruppo a : 0/1 se è 0 allora non ho nessun debito
    //                se è 1 allora ho almeno 1 debito
    private HashMap<String,Dettagli_Gruppo> gruppi_partecipo = new HashMap<String,Dettagli_Gruppo>();
    private String name;
    private String surname;
    private String username;
    private String  birthdate;
    private String telephone;
    private boolean hasDebts = false;
    private String password;
    private int isInDB;
    private String propic;
    private Map<String, Riga_Bilancio> amici = new HashMap<String,Riga_Bilancio>();
    private String prefix;


    public Persona(){}
    // Constructor
    public Persona(String name, String surname, String username, String birthdate, String telephone, String password, int i, String prefix, Uri propic){
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.birthdate = birthdate;
        this.telephone = telephone;
        this.password = password;
        this.isInDB=i;
        this.prefix = prefix;
        if(propic!=null)
        this.propic=propic.toString();
        else
            propic=null;
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



    // Setters
    public void setHasDebts(boolean haDebiti){
        this.hasDebts=haDebiti;
    }
    public void setIsInDB(int isInDB) {
        this.isInDB = isInDB;
    }
    public void setGruppi(HashMap<String,Dettagli_Gruppo> gruppi_partecipo) { this.gruppi_partecipo = gruppi_partecipo; }
    public void setPassword(String password) { this.password = password; }
    public void setBirthdate(String birthdate){ this.birthdate = birthdate; }
    // Other methods
    public int getPosizione(Gruppo g) {
        return posizione_gruppi.get(g.getGroupID());
    }

    public Integer CheckIfHasDebts(Gruppo g){
        Integer result= new Integer(0);
        ArrayList<Spesa> spese = g.Obtain_spese_array();
        for (Spesa s: spese){
            if(!s.getPagante().getUsername().equals(username)){ //controllo solo le spese che NON ho fatto io
                if(!s.getDivisioni().get(this.getTelephone()).getHaPagato()) {
                    result=1;
                    break;
                }
            }
        }
        return result;
    }



    public void AddToGroup(Gruppo gruppo,int pos,int img){
        gruppi_partecipo.put(gruppo.getGroupID(),new Dettagli_Gruppo(gruppo.getGroupName(),gruppo.getGroupID(),0,gruppo.getCurr().getSymbol()+" - "+gruppo.getCurr().getChoosencurr(), gruppo.getUri()));
        posizione_gruppi.put(gruppo.getGroupID(), new Integer(pos));
        dove_ho_debito.put(gruppo.getGroupID(),new Integer(0));
    }

    public void setDove_Ho_debito(Gruppo g , Integer i) {
        this.dove_ho_debito.put(g.getGroupID(),i);
    }

    public Drawable getProPic(Context context){
        Drawable d = context.getResources().getDrawable( R.drawable.clubbing);
        return d;
    }




    public HashMap<String, Integer> getPosizione_gruppi() {
        return posizione_gruppi;
    }

    public void setPosizione_gruppi(HashMap<String, Integer> posizione_gruppi) {
        this.posizione_gruppi = posizione_gruppi;
    }

    public HashMap<String, Integer> getDove_ho_debito() {
        return dove_ho_debito;
    }

    public void setDove_ho_debito(HashMap<String, Integer> dove_ho_debito) {
        this.dove_ho_debito = dove_ho_debito;
    }

    public HashMap<String, Dettagli_Gruppo> getGruppi_partecipo() {
        return gruppi_partecipo;
    }

    public void setGruppi_partecipo(HashMap<String,Dettagli_Gruppo> gruppi_partecipo) {
        this.gruppi_partecipo = gruppi_partecipo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void addTobalance(Persona amico, Double importo, mCurrency curr, String importo_key){

        if(amici.containsKey(amico.getTelephone()+";"+curr.getChoosencurr())) { //se c'è devo accedere e metterlo nel DB
            Riga_Bilancio balance = amici.get(amico.getTelephone()+";"+curr.getChoosencurr());
            balance.getImporto().put(importo_key,importo);
            amici.put(amico.getTelephone()+";"+curr.getChoosencurr(), balance);
        }
        else{ //se non c'è lo aggiungo alla mappa
            HashMap<String,Double> map = new HashMap<>();
            map.put(importo_key,importo);
            Riga_Bilancio balance = new Riga_Bilancio(amico.getName()+" "+amico.getSurname(), map,curr.getSymbol(),curr.getDigits());
            amici.put(amico.getTelephone()+";"+curr.getChoosencurr(), balance);
        }
    }

    public void initiliazeBalance(ArrayList<Persona> amici_array,mCurrency curr, Persona soggetto){

        for(Persona p: amici_array) {
            if (!amici.containsKey(p.getTelephone() + ";" + curr.getChoosencurr()) && !p.getTelephone().equals(soggetto.getTelephone())) {
                //se non c'è lo aggiungo alla mappa
                HashMap<String, Double> map = new HashMap<>();
                map.put("STARTING POINT", 0d);
                Riga_Bilancio balance = new Riga_Bilancio(p.getName() + " " + p.getSurname(), map, curr.getSymbol(), curr.getDigits());
                amici.put(p.getTelephone() + ";" + curr.getChoosencurr(), balance);
            }
        }

    }
    public void addTobalance_2(String amico, Double importo, mCurrency curr,String importo_key){

        if(amici.containsKey(amico)) { //se c'è devo accedere e metterlo nel DB
            Riga_Bilancio balance = amici.get(amico);
            balance.getImporto().put(importo_key,importo);
            amici.put(amico, balance);
        }
        else{ //se non c'è lo aggiungo alla mappa
            HashMap<String,Double> map = new HashMap<>();
            map.put(importo_key,importo);
            Riga_Bilancio balance = new Riga_Bilancio(amico, map,curr.getSymbol(),curr.getDigits());
            amici.put(amico, balance);
        }
    }

    public Map<String, Riga_Bilancio> getAmici() {
        return amici;
    }

    public void setAmici(Map<String, Riga_Bilancio> amici) {
        this.amici = amici;
    }


    public void refreshTimeOfGroup(String groupID) {
        Dettagli_Gruppo dg = gruppi_partecipo.get(groupID);
        dg.refreshTime();
        gruppi_partecipo.put(groupID,dg);
    }

    public void plusOneUnread(String groupID) {
        Dettagli_Gruppo dg = gruppi_partecipo.get(groupID);
        dg.plus_unread();
        gruppi_partecipo.put(groupID,dg);
    }

    public void resetUnread(String groupID) {
        Dettagli_Gruppo dg = gruppi_partecipo.get(groupID);
        dg.reset_unread();
        gruppi_partecipo.put(groupID,dg);
    }

    public Dettagli_Gruppo obtainDettaglio(String groupID) {
        return gruppi_partecipo.get(groupID);
    }

    public void updateLast(String groupID,String nome_p,String nome_s){
        Dettagli_Gruppo dg = gruppi_partecipo.get(groupID);
        dg.updateLast(nome_p,nome_s);
        gruppi_partecipo.put(groupID,dg);


    }
    public Integer obtain_a_debt(String gruppoId){
        return dove_ho_debito.get(gruppoId);
    }

    public String getPropic() {
        return propic;
    }

    public void setPropic(String propic) {
        this.propic = propic;
    }


    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}