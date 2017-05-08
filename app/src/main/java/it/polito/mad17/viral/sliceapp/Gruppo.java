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

/**
 * Created by Kalos on 25/03/2017.
 */

public class Gruppo implements Serializable, Observer {

    private String groupName;
    private int n_partecipanti;
    private HashMap<String, Persona> partecipanti = new HashMap<String,Persona>();
    public  Persona getUser() {
        return user;
    }
    private Persona user;
    private Spesa spesa;
    private HashMap<String, Spesa> spese = new HashMap <String, Spesa>();
    private ArrayList<Spesa> listaSpeseGruppo = new ArrayList<Spesa>();
    private Gestore gestore;
    private String groupID;
    private Policy policy;
    private int img;

    public Gruppo(){
        // needed for FirebaseListAdapter
    }

    public Gruppo(String groupName, int n, ArrayList<Persona> partecipanti, Policy policy ){
        this.groupName=groupName;
        this.n_partecipanti=n;
        int i=0;
        for(Persona p: partecipanti){
            this.partecipanti.put(p.getTelephone(),p);
            p.setPosizione_inGroup(this, i);
            p.AddToGroup(this,i);
            i++;
        }
        this.policy=policy;
        img= R.drawable.default_img;
        setIcon(img);
    }

    public String getName(){
        return groupName;
    }
    public String getGroupID(){ return groupID; }
    public int getIcon(){
        return img;
    }
    public HashMap<String, Persona> getPartecipanti() {
        return partecipanti;
    }
    public int getN_partecipanti(){
        return n_partecipanti;
    }
    public Policy getPolicy() {
        return policy;
    }
    public HashMap<String,Spesa> getMappaSpese() {
        return spese;
    }

    public void setUser(Persona user){
        this.user=user;
    }
    public void setGroupID(String groupID){ this.groupID = groupID; }
    public void setIcon(int icon){ img = icon; }
    public void setPolicy(Policy policy) {this.policy = policy;
    }
    public Spesa getSpesa(String expenseID){ return spese.get(expenseID); }

        public Spesa AddSpesa_and_try_repay(Persona pagante,Policy policy,String nome_spesa, String data, Double importo){

            if(user.getHasDebts()){
            gestore=new Gestore();
            spesa= new Spesa(nome_spesa,data,policy,pagante,importo,this);

            spesa.setParti(gestore.Calculate_Credits_To_Buyer_With_Repaing(pagante,policy, spesa.getImporto(),partecipanti, partecipanti.size(),spese,user,this));
            //metto il debito a tutti

            spese.put(spesa.getExpenseID(),spesa);
            listaSpeseGruppo.add(spesa);
            return spesa;}
            else
                return null;

        }


    public Spesa AddSpesa(Persona pagante,Policy policy, String nome_spesa, String data, Double importo){
        gestore=new Gestore();
        spesa = new Spesa(nome_spesa,data,policy,pagante,importo,this);
        spesa.setParti(gestore.Calculate_Credits(pagante,policy, spesa.getImporto(),partecipanti, partecipanti.size(),user,this));
        //metto il debito a tutti

        spese.put(spesa.getExpenseID(),spesa);
        listaSpeseGruppo.add(spesa);
        return spesa;

    }

    public Double getAllDebts(){ //i debiti sono calcolati nelle spese che NON ho fatto io e devo ancora pagare
        Double f= new Double(0);
        for(Spesa s: spese.values()){
            if(!s.getPagante().getTelephone().equals(user.getTelephone())){
                for(Soldo soldo: s.getDivisioni().values())
                    if(!soldo.getHaPagato() && soldo.getPersona().getTelephone().equals(user.getTelephone()))
                        f+=soldo.getImporto();
            }
        }
        return f;
    }

    public Double getAllCredits(){  //i crediti sono calcolti nelle spese che HO fatto io e gli altri non mi hanno pagato
        Double f= new Double(0);
        for(Spesa s: spese.values()){
            if(s.getPagante().getTelephone().equals(user.getTelephone())){
                for(Soldo soldo: s.getDivisioni().values())
                    if(!soldo.getHaPagato() && !soldo.getPersona().getTelephone().equals(user.getTelephone()))
                        f+=soldo.getImporto();
            }
        }
        return f;
    }

    private void AddPartecipant(Persona p){
        partecipanti.put(p.getTelephone(), p);
    }






    public ArrayList<Spesa> getSpese(){
        ArrayList<Spesa> spese_arr = new ArrayList<Spesa>(spese.values());
        return spese_arr;
    }



    public Persona getPartecipante(String telephone){
     return partecipanti.get(telephone);
    }





    @Override
    public void update(Observable o, Object arg) {
        if(o.hasChanged())
            o.notifyObservers();
    }
}
