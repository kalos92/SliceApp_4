package it.polito.mad17.viral.sliceapp;

import android.graphics.Bitmap;

import android.net.Uri;

import java.io.Serializable;

import java.util.ArrayList;
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
    private String uri;
    private String valuta;
    private Double importo;
    private HashMap<String, Soldo> divisioni = new HashMap<String,Soldo>();
    private String expenseID;

    public boolean getRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public String getRemover() {
        return remover;
    }

    public void setRemover(String remover) {
        this.remover = remover;
    }

    public String getRemoved_msg() {
        return removed_msg;
    }

    public void setRemoved_msg(String removed_msg) {
        this.removed_msg = removed_msg;
    }

    private boolean removed=false;
    private String remover;
    private String removed_msg;


    public HashMap<String,Integer> getFullypayed() {
        return fullypayed;
    }

    public void setFullypayed(HashMap<String,Integer> fullypayed) {
        this.fullypayed = fullypayed;
    }

    public boolean getContested() {
        return contested;
    }

    public void setContested(boolean contested) {
        this.contested = contested;
    }

    private HashMap<String,Integer> fullypayed=new HashMap<String,Integer>();
    private boolean contested;

    public boolean getMethod() {
        return method;
    }

    public void setMethod(boolean method) {
        this.method = method;
    }

    private boolean method=false;
    //true -> repay
    //false -> salvataggio normale

    public int getDigit() {
        return digit;
    }

    public void setDigit(int digit) {
        this.digit = digit;
    }

    private int digit;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    private String img;

    public HashMap<String,HashMap<String,Soldo>> getDebiti_restituiti() {
        return debiti_restituiti;
    }

    public void setDebiti_restituiti(HashMap<String,HashMap<String,Soldo>> debiti_restituiti) {
        this.debiti_restituiti = debiti_restituiti;
    }

    private HashMap<String,HashMap<String,Soldo>> debiti_restituiti = new HashMap<String,HashMap<String,Soldo>>();



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
        this.contested=false;
        this.removed=false;
    }

    public Spesa(String nome, Double importo,String user,String ID,int precision,String valuta){
        this.removed=true;
        this.removed_msg=nome+" Deleted";
        this.importo=importo*-1;
        this.remover=user;
        this.expenseID=ID;
        this.valuta=valuta;
        this.digit=precision;

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

    public void setUri (String uri){
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

    public String getUri() {
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

    public void pay(String telephone){
        Soldo s = divisioni.get(telephone);
        s.setHaPagato(true);
        divisioni.put(telephone,s);

    }

}
