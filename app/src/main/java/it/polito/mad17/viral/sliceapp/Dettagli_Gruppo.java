package it.polito.mad17.viral.sliceapp;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Kalos on 14/05/2017.
 */

public class Dettagli_Gruppo implements Serializable {


    private String nome_gruppo;
    private String chiave;
    private long time;
    private String last;
    private String uri;
    private String valuta;
    private HashMap<String,Integer> unread = new HashMap<>();

    public Dettagli_Gruppo(){}

    public Dettagli_Gruppo(String nome_gruppo,String chiave,HashMap<String,Integer> unread, String valuta, String uri){
        this.nome_gruppo=nome_gruppo;
        this.chiave=chiave;
        time= Calendar.getInstance().getTimeInMillis();
        this.unread=unread;
        last="null_$";
        this.valuta=valuta;
        this.uri=uri;

    }

    public void refreshTime(){
        time=Calendar.getInstance().getTimeInMillis();
    }

    public String getNome_gruppo() {
        return nome_gruppo;
    }

    public void setNome_gruppo(String nome_gruppo) {
        this.nome_gruppo = nome_gruppo;
    }

    public String getChiave() {
        return chiave;
    }

    public void setChiave(String chiave) {
        this.chiave = chiave;
    }

    public long getTime() {
        return time*-1;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void reset_unread(String key){
        unread.put(key,-1);
    }

    public void plus_unread(String key){
        unread.put(key,+1);
    }

    public void updateLast(String nome_p, String nome_s){
        this.last=nome_p+" has bought "+ nome_s;

    }
    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getValuta() {
        return valuta;
    }

    public void setValuta(String valuta) {
        this.valuta = valuta;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public HashMap<String,Integer> getUnread() {
        return unread;
    }

    public void setUnread(HashMap<String,Integer> unread) {
        this.unread = unread;
    }

    public int calculate(){
        int i =0;

        for(Integer d: unread.values())
                i+=d;

        return i;
    }

}
