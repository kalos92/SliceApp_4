package it.polito.mad17.viral.sliceapp;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Kalos on 14/05/2017.
 */

public class Dettagli_Gruppo implements Serializable {


    private String nome_gruppo;
    private String chiave;
    private long time;
    private String last;

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    private int unread =0;



    public Dettagli_Gruppo(){}

    public Dettagli_Gruppo(String nome_gruppo,String chiave,int unread){
        this.nome_gruppo=nome_gruppo;
        this.chiave=chiave;
        time= Calendar.getInstance().getTimeInMillis();
        this.unread+=unread;
        last="null_$";

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
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void reset_unread(){
        unread=0;
    }

    public void plus_unread(){
        unread++;
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

}
