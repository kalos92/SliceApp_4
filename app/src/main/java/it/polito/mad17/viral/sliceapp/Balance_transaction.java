package it.polito.mad17.viral.sliceapp;

import java.io.Serializable;

/**
 * Created by Kalos on 05/06/2017.
 */

public class Balance_transaction implements Serializable{

    String ID;
    String beneficiario;
    String pagante;
    Double importo;

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

    public Balance_transaction(String id, String beneficiario, String pagante, Double importo ){

        this.ID=id;
        this.beneficiario=beneficiario;
        this.pagante=pagante;
        this.importo=importo;//bb

    }




}
