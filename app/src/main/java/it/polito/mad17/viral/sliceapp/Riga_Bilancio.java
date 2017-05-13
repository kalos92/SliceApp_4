package it.polito.mad17.viral.sliceapp;

import java.io.Serializable;

/**
 * Created by abdel on 28/04/2017.
 */

public class Riga_Bilancio implements Serializable {




    private String ncname;
    private Double importo;

    public Riga_Bilancio(){};

    public Riga_Bilancio(String ncname,Double importo){
        this.ncname= ncname;
        this.importo=importo;
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

}
