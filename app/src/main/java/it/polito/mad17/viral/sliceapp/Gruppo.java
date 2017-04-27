package it.polito.mad17.viral.sliceapp;



import android.graphics.Bitmap;
import android.widget.Toast;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * Created by Kalos on 25/03/2017.
 */

public class Gruppo extends Observable implements Serializable, HashMap_Subject {
    protected ArrayList<Observer> observers;
    private String groupName;
    private int n_partecipanti;
    private HashMap<String, Persona> partecipanti = new HashMap<String,Persona>();
    public  Persona getUser() {
        return user;
    }
    private Persona user;
    private Spesa spesa;
    private HashMap<String, Spesa> spese = new HashMap <String, Spesa>();
    private Gestore gestore;
    private String groupID;



    private Policy policy;
    private int img;

    public Gruppo(String groupName, int n, ArrayList<Persona> partecipanti, Policy policy ){
        this.observers = new ArrayList<Observer>();
        this.groupName=groupName;
        this.n_partecipanti=n;
        int i=0;
        for(Persona p: partecipanti){
            this.partecipanti.put(p.getUserName(),p);
            p.setPosizione_inGroup(this, i);
            p.AddToGroup(this,i);
            i++;
        }
        this.policy=policy;
        img= R.drawable.default_img;
        setImg(img);
    }

    public void setUser(Persona user){
        this.user=user;
    }

    public String getGroupID(){ return groupID; }
    public void setGroupID(String groupID){ this.groupID = groupID; }

    public Spesa getSpesa(String expenseID){ return spese.get(expenseID); }

    public Spesa AddSpesa_and_try_repay(Persona pagante,Policy policy,String nome_spesa, String data, Double importo){
        if(user.getHaDebiti()){
        gestore=new Gestore();
        spesa= new Spesa(nome_spesa,data,policy,pagante,importo,this);

        spesa.setParti(gestore.Calculate_Credits_To_Buyer_With_Repaing(pagante,policy, spesa.getImporto(),partecipanti, partecipanti.size(),spese,user,this));
        //metto il debito a tutti

        spese.put(spesa.getNome()+spesa.getData(),spesa);

        setChanged();
        notifyObservers();
        return spesa;}

       else{
           return null;
        }

    }


    public Spesa AddSpesa(Persona pagante,Policy policy, String nome_spesa, String data, Double importo){
        gestore=new Gestore();
        spesa = new Spesa(nome_spesa,data,policy,pagante,importo,this);
        spesa.setParti(gestore.Calculate_Credits(pagante,policy, spesa.getImporto(),partecipanti, partecipanti.size(),user,this));
        //metto il debito a tutti

        spese.put(spesa.getNome()+spesa.getData(),spesa);

        setChanged();
        notifyObservers();
        return spesa;

    }

    public Double getAllDebts(){ //i debiti sono calcolati nelle spese che NON ho fatto io e devo ancora pagare
        Double f= new Double(0);
        for(Spesa s: spese.values()){
            if(!s.getPagante().getUserName().equals(user.getUserName())){
                for(Soldo soldo: s.getDivisioni().values())
                    if(!soldo.getHaPagato() && soldo.getPersona().getUserName().equals(user.getUserName()))
                        f+=soldo.getImporto();


            }

        }

        return f;
    }

    public Double getAllCredits(){  //i crediti sono calcolti nelle spese che HO fatto io e gli altri non mi hanno pagato
        Double f= new Double(0);
        for(Spesa s: spese.values()){
            if(s.getPagante().getUserName().equals(user.getUserName())){
                for(Soldo soldo: s.getDivisioni().values())
                    if(!soldo.getHaPagato() && !soldo.getPersona().getUserName().equals(user.getUserName()))
                        f+=soldo.getImporto();
            }

        }
        return f;
    }

    private void AddPartecipant(Persona p){

        partecipanti.put(p.getUserName(), p);

    }

    public void setImg(int icon){

        img=icon;
    }

    public String getGroupName(){
        return groupName;
    }
    public int getImg(){
        return img;
    }

    public ArrayList<Spesa> getSpese(){

        ArrayList<Spesa> spese_arr = new ArrayList<Spesa>(spese.values());
        return spese_arr;
    }

    public HashMap<String, Persona> getPartecipanti() {
        return partecipanti;
    }

    public Persona getPartecipante(String username){
     return partecipanti.get(username);

    }
    public int getN_partecipanti(){
        return n_partecipanti;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public HashMap<String,Spesa> getMappaSpese() {
        return spese;
    }


    @Override
    public void register(Observer subscriber) {
        observers.add(subscriber);
    }

    @Override
    public void unregister(Observer unsubscriber) {
        int observerIndex = observers.indexOf(unsubscriber);
        observers.remove(observerIndex);
    }

    @Override
    public void notityObserver() {
        notifyObservers();
    }
}
