package it.polito.mad17.viral.sliceapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kalos on 26/05/2017.
 */

public class Riga_bilancio_Gruppo implements Serializable {

    String nome;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public HashMap<String,Double> getImporto() {
        return importo;
    }

    public void setImporto(HashMap<String,Double> importo) {
        this.importo = importo;
    }

    public int getDigits() {
        return digits;
    }

    public void setDigits(int digits) {
        this.digits = digits;
    }

    String symbol;
    HashMap<String,Double> importo = new HashMap<String, Double>();
    int digits;

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    String tel;

    public HashMap<String, HashMap<String, Riga_bilancio_personalizzata>> getBilancio_relativo() {
        return bilancio_relativo;
    }

    public void setBilancio_relativo(HashMap<String, HashMap<String, Riga_bilancio_personalizzata>> bilancio_relativo) {
        this.bilancio_relativo = bilancio_relativo;
    }

    private HashMap<String,HashMap<String,Riga_bilancio_personalizzata>> bilancio_relativo = new HashMap<>();




    public Riga_bilancio_Gruppo(){}

    public Riga_bilancio_Gruppo(String nome, String symbol, int digits, Double importo, String tel, ArrayList<Persona> altri){

        this.nome=nome;
        this.importo.put("STARTING POINT",importo);
        this.digits=digits;
        this.tel=tel;
        this.symbol=symbol;

        for(Persona p:altri){
            if(!p.getTelephone().equals(tel)){
                HashMap<String,Riga_bilancio_personalizzata> fakeMap = new HashMap<>();
                fakeMap.put("STARTING POINT", new Riga_bilancio_personalizzata("null_$","null_$",0d));
                bilancio_relativo.put(p.getTelephone(),fakeMap);
            }

        }
    }

    public Double calculate_my_part(){

        Double f = 0d;

        for(Double d :importo.values())
            f+=d;

        return f;
    }




}
