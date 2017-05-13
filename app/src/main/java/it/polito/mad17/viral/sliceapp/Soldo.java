package it.polito.mad17.viral.sliceapp;

import java.io.Serializable;

/**
 * Created by Kalos on 24/03/2017.
 */

public class Soldo implements Serializable {



    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public void setImporto(Double importo) {
        this.importo = importo;
    }


    private Persona persona;
    private Double  importo;
    private boolean haPagato=false;
    private String valuta;
    private Persona pagante;

public Soldo(){}

    public Soldo(Persona persona2, double f, boolean haPagato, Persona pagante) {
        this.importo=f;
        this.persona=persona2;
        this.haPagato=haPagato;
        this.pagante=pagante;
    }

    public void setHaPagato(boolean b){
        haPagato=b;
    }

    public boolean getHaPagato(){
        return haPagato;
    }

    public Double getImporto() {

        return importo;
    }

    public void sottraiImporto(Double soldo) {

        importo-=soldo;
    }

    public void aggiungiImporto(Double importo2) {
        importo+=importo2;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setValuta(String v){
        valuta=v;
    }

    public String getValuta(){

        return valuta;
    }


    public Persona getPagante() {
        return pagante;
    }

    public void setPagante(Persona pagante) {
        this.pagante = pagante;
    }



}
