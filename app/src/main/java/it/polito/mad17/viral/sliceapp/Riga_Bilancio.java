package it.polito.mad17.viral.sliceapp;

import java.io.Serializable;

/**
 * Created by abdel on 28/04/2017.
 */

public class Riga_Bilancio implements Serializable {




    private String ncname;
    private Double importo;



    private String symbol;
    private int digits;

    public Riga_Bilancio(){};

    public Riga_Bilancio(String ncname,Double importo,String symbol, int digits){
        this.ncname= ncname;
        this.importo=importo;
        this.symbol=symbol;
        this.digits=digits;
    }


    public String getNcname() {
    return ncname;
    }

    public void setNcname(String ncname) {
        this.ncname = ncname;
    }

    public Double getImporto() {
        return importo;
    }

    public void setImporto(Double importo) {
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

}
