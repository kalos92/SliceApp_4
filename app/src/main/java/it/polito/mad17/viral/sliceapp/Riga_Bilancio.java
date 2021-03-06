package it.polito.mad17.viral.sliceapp;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by abdel on 28/04/2017.
 */

public class Riga_Bilancio implements Serializable {

    private String ncname;
    private HashMap<String,Double> importo= new HashMap<>();
    private String symbol;
    private int digits;
    private String cc;

    public Riga_Bilancio(){};

    public Riga_Bilancio(String ncname,HashMap<String,Double> importo,String symbol, int digits, String cc){
        this.ncname= ncname;
        this.importo=importo;
        this.symbol=symbol;
        this.digits=digits;
        this.cc=cc;
    }


    public String getNcname() {
    return ncname;
    }

    public void setNcname(String ncname) {
        this.ncname = ncname;
    }

    public HashMap<String,Double> getImporto() {
        return importo;
    }

    public void setImporto(HashMap<String,Double> importo) {
        this.importo = importo;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getDigits() {
        return digits;
    }

    public void setDigits(int digits) {
        this.digits = digits;
    }

    public Double calculate(){

        Double f = new Double(0d);


        for(Double d :importo.values())
            f+=d;

        return f;

    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

}
