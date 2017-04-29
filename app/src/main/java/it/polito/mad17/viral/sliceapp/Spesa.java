package it.polito.mad17.viral.sliceapp;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

/**
 * Created by Kalos on 25/03/2017.
 *
 */

public class Spesa extends Observable implements Serializable {

    private Categoria cat;
    private Gruppo gruppo;
    private Persona pagante;
    private Policy policy;
    private String data;
    private String nome_spesa;
    private Bitmap b;
    private Uri uri;
    private String valuta;
    Double importo;
    private HashMap<String, Soldo> divisioni = new HashMap<String,Soldo>();



    private String expenseID;

    public Spesa(String nome_spesa, String data, Policy policy, Persona pagante, Double importo, Gruppo gruppo){
        cat=new Categoria("General Expenditure");
        this.gruppo=gruppo;
        this.importo=importo;
        this.pagante=pagante;
        this.policy=policy;
        this.data=data;
        this.nome_spesa=nome_spesa;
    }

    public String getData() {

        return data;
    }

    public String getNome() {

        return nome_spesa;
    }

    public Double getImporto() {

        return importo;
    }

    public Gruppo getGruppo(){ return gruppo; }

    public void setParti(Soldo[] calcolo_debiti) {

        for(Soldo s: calcolo_debiti){
            if(s.getImporto()==0){
                s.setHaPagato(true);
            }
            divisioni.put(s.getPersona().getUsername(), s);

        }
    }

    public HashMap<String,Soldo> getDivisioni(){
        return divisioni;
    }

    public Persona getPagante() {

        return pagante;
    }

    public Categoria getCat(){
        return cat;
    }

    public String getValuta() {
        return valuta;
    }

    public void setValuta(String valuta) {
        this.valuta = valuta;
    }

    public void setCat(String cat){
       this.cat = new Categoria(cat);

    }

    public void setUri (Uri uri){
        this.uri=uri;
    }

    public void setBitmap_spesa (Bitmap b){
        this.b=b;
    }

    public String getExpenseID() {
        return expenseID;
    }

    public void setExpenseID(String expenseID) {
        this.expenseID = expenseID;
    }
}
