package it.polito.mad17.viral.sliceapp;

import java.io.Serializable;

/**
 * Created by Kalos on 05/06/2017.
 */

public class Balance_transaction implements Serializable{



    private long timeStamp;
    private String ID;
    private String beneficiario;
    private String pagante;
    private Double importo;
    private String simbol;
    private int digits;


    public String getSimbol() {
        return simbol;
    }

    public void setSimbol(String simbol) {
        this.simbol = simbol;
    }

    public int getDigits() {
        return digits;
    }

    public void setDigits(int digits) {
        this.digits = digits;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    private String nome;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }

    public String getPagante() {
        return pagante;
    }

    public void setPagante(String pagante) {
        this.pagante = pagante;
    }

    public Double getImporto() {
        return importo;
    }

    public void setImporto(Double importo) {
        this.importo = importo;
    }




    public Balance_transaction(){}

    public Balance_transaction(String id, String beneficiario, String pagante, Double importo, long timeStamp, String nome,mCurrency curr){

        this.ID=id;
        this.beneficiario=beneficiario;
        this.pagante=pagante;
        this.importo=importo;//bb
        this.timeStamp = timeStamp;
        this.nome=nome;
        this.simbol=curr.getSymbol();
        this.digits=curr.getDigits();


    }




}
