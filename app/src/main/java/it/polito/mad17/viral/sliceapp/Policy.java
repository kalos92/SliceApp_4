package it.polito.mad17.viral.sliceapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Kalos on 25/03/2017.
 */

public class Policy implements Serializable {

    private HashMap<String, Double> percentuali = new HashMap<String, Double>();

    public Policy(HashMap<String,Double> percentuali) {
        this.percentuali=percentuali;
    }

    public Policy(){
        // needed for FirebaseListAdapter
    }

    public HashMap<String, Double> getPercentuali() {
        return percentuali;
    }

    public void setPercentuali(HashMap<String, Double> percentuali) {
        this.percentuali = percentuali;
    }

    public double percentuale_user(String n_tel){
       return percentuali.get(n_tel);
    }
}
