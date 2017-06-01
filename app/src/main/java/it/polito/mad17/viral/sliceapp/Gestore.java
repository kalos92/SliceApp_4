package it.polito.mad17.viral.sliceapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

/**
 * Created by Kalos on 05/04/2017.
 */

public class Gestore implements Serializable {

    public Soldo[] Calculate_Credits(Persona pagante, Policy policy, Double importo, HashMap<String,Persona> persone, int n_persone, Persona user, Gruppo g) {
        Soldo[] crediti = new Soldo[n_persone];
        HashMap<String, Double> percentages;
        //ogni casella coincide con 1 partecipante
        percentages=policy.getPercentuali();
        Soldo c;
        int i=0;

        //suppongo che ci sia corrispondenza 1:1 prima casella di ogni cosa rappresenta il primo username nell'hashmap e così via;
        for(Persona p: persone.values()){
            if(p.getTelephone().equals(pagante.getTelephone())){
                Double parte= importo*(percentages.get(p.getTelephone())/100);
                c= new Soldo(p, parte,true,pagante);
                crediti[p.getPosizione(g)]=c;

            }
            else {
                p.setHasDebts(true);
                p.setDove_Ho_debito(g,new Integer(1));
                Double parte= importo*(percentages.get(p.getTelephone())/100);
                c= new Soldo(p, parte,false,pagante);
                crediti[p.getPosizione(g)]=c;
                p.setHasDebts(true);
            }
        }

        return crediti;
    }
}
