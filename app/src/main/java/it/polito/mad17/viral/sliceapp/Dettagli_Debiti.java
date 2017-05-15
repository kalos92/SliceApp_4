package it.polito.mad17.viral.sliceapp;

import java.io.Serializable;

/**
 * Created by Kalos on 15/05/2017.
 */

class Dettagli_Debiti implements Serializable{



    private String expenseId_ripagata;
    private String telefono_persona;
    private Double debito;
    private String expenseId_fatta;

    public String getTelefono_persona_sdebitata() {
        return telefono_persona_sdebitata;
    }

    public void setTelefono_persona_sdebitata(String telefono_persona_sdebitata) {
        this.telefono_persona_sdebitata = telefono_persona_sdebitata;
    }

    private String telefono_persona_sdebitata;


    public Dettagli_Debiti(){}

    public Dettagli_Debiti(String expenseId,String telefono_persona, Double debito,String expenseId_fatta,String telefono_persona_sdebitata) {
        this.expenseId_ripagata = expenseId;
        this.telefono_persona = telefono_persona;
        this.debito = debito;
        this.expenseId_fatta=expenseId_fatta;
        this.telefono_persona_sdebitata=telefono_persona_sdebitata;
    }

    public String getExpenseId_ripagata() {
        return expenseId_ripagata;
    }

    public void setExpenseId_ripagata(String expenseId_ripagata) {
        this.expenseId_ripagata = expenseId_ripagata;
    }

    public String getExpenseId_fatta() {
        return expenseId_fatta;
    }

    public void setExpenseId_fatta(String expenseId_fatta) {
        this.expenseId_fatta = expenseId_fatta;
    }






    public String getTelefono_persona() {
        return telefono_persona;
    }

    public void setTelefono_persona(String telefono_persona) {
        this.telefono_persona = telefono_persona;
    }

    public Double getDebito() {
        return debito;
    }

    public void setDebito(Double debito) {
        this.debito = debito;
    }


}
