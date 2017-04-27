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

    public Policy(Double[] percentuali, int n_persone) {
        this.percentuali= new Double[n_persone];
        this.percentuali=percentuali;
        this.n_persone=n_persone;

    }



    public Double[] getPercentage() {
        return percentuali;
    }

    public void setPercentages(Double f, int pos){

        percentuali[pos]=f;


    }







}
