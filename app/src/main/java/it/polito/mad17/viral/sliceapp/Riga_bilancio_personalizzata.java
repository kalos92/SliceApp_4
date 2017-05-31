package it.polito.mad17.viral.sliceapp;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Kalos on 27/05/2017.
 */

public class Riga_bilancio_personalizzata implements Serializable {

    public HashMap<String,Double> getImporto() {
        return importo;
    }

    public void setImporto(HashMap<String,Double> importo) {
        this.importo = importo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    String nome;

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    String tel;
    HashMap<String,Double> importo;

   public Riga_bilancio_personalizzata(){}

    public  Riga_bilancio_personalizzata(String nome,HashMap<String,Double> importo,String tel){

        this.tel=tel;
        this.nome=nome;
        this.importo=importo;
    }

    public Double calculate(){

        Double f=0d;

        for(Double d:importo.values())
        {
            f+=d;
        }

        return f;

    }
}
