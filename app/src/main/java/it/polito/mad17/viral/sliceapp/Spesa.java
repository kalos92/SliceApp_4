package it.polito.mad17.viral.sliceapp;

import android.graphics.Bitmap;

import android.net.Uri;

import java.io.Serializable;

import java.util.Calendar;
import java.util.HashMap;


/**
 * Created by Kalos on 25/03/2017.
 *
 */

public class Spesa implements Serializable {

    private Categoria cat;
    private String gruppo;
    private Persona pagante;
    private Policy policy;
    private String data;
    private String nome_spesa;
    private Bitmap b;
    private Uri uri;
    private String valuta;
    private Double importo;
    private HashMap<String, Soldo> divisioni = new HashMap<String,Soldo>();
    private String expenseID;



    private long c;


    public Spesa(){}

    public Spesa(String nome_spesa, String data, Policy policy, Persona pagante, Double importo, Gruppo gruppo){
        cat=new Categoria("General Expenditure");
        this.gruppo=gruppo.getGroupID();
        this.importo=importo;
        this.pagante=pagante;
        this.policy=policy;
        this.data=data;
        this.nome_spesa=nome_spesa;
        c= Calendar.getInstance().getTimeInMillis();
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

    public String getGruppo(){ return gruppo; }

    public void setParti(Soldo[] calcolo_debiti) {

        for(Soldo s: calcolo_debiti){
            if(s.getImporto()==0){
                s.setHaPagato(true);
            }
            divisioni.put(s.getPersona().getTelephone(), s);

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

    public void setCat_string(String cat){
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

    public void setCat(Categoria cat) {
        this.cat = cat;
    }

    public void setGruppo(String gruppo) {
        this.gruppo = gruppo;
    }

    public void setPagante(Persona pagante) {
        this.pagante = pagante;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getNome_spesa() {
        return nome_spesa;
    }

    public void setNome_spesa(String nome_spesa) {
        this.nome_spesa = nome_spesa;
    }

    public Bitmap getB() {
        return b;
    }

    public void setB(Bitmap b) {
        this.b = b;
    }

    public Uri getUri() {
        return uri;
    }

    public void setImporto(Double importo) {
        this.importo = importo;
    }

    public void setDivisioni(HashMap<String, Soldo> divisioni) {
        this.divisioni = divisioni;
    }

    public void refreshC(){
        c=Calendar.getInstance().getTimeInMillis();
    }

    public long getC() {
        return c;
    }

    public void setC(long c) {
        this.c = c;
    }
}
