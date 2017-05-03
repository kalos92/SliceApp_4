package it.polito.mad17.viral.sliceapp;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Kalos on 25/03/2017.
 */

public class Policy implements Serializable {

    private HashMap<String, Double> percentuali_persona = new HashMap<String, Double>();

    Double[] percentuali;
    int n_persone;
    long[] telefono;

    public Policy(Double[] percentuali, int n_persone) {
        this.percentuali= new Double[n_persone];
        this.percentuali=percentuali;
        this.n_persone=n_persone;
    }

    public Policy(){
        // needed for FirebaseListAdapter
    }
    public Policy(Double[] percentuali, int n_persone,long[] telefono) {
        this.percentuali= new Double[n_persone];
        this.percentuali=percentuali;
        this.n_persone=n_persone;
        telefono = new long[n_persone];
        this.telefono=telefono;
    }

    public Double[] getPercentage() {
        return percentuali;
    }

    public void setPercentages(Double f, int pos){
        percentuali[pos]=f;

    }

    public Double getMyPolicy(long tel){
        for(int i=0;i<telefono.length;i++)
            if(tel==telefono[i])
                return percentuali[i];
        return (double)0;
    }
}
