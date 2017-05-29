package it.polito.mad17.viral.sliceapp;

import java.io.Serializable;

/**
 * Created by Kalos on 27/05/2017.
 */

public class Riga_bilancio_personalizzata implements Serializable {


    public String getIDspesa() {
        return IDspesa;
    }

    public void setIDspesa(String IDspesa) {
        this.IDspesa = IDspesa;
    }

    public String getTel_pagante() {
        return tel_pagante;
    }

    public void setTel_pagante(String tel_pagante) {
        this.tel_pagante = tel_pagante;
    }

    public Double getImporto() {
        return importo;
    }

    public void setImporto(Double importo) {
        this.importo = importo;
    }

    String IDspesa;
    String tel_pagante;
    Double importo;

   public Riga_bilancio_personalizzata(){}

    public  Riga_bilancio_personalizzata(String IDspesa, String tel_pagante, Double importo){
        this.IDspesa=IDspesa;
        this.tel_pagante=tel_pagante;
        this.importo=importo;
    }
}
