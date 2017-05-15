package it.polito.mad17.viral.sliceapp;

import java.io.Serializable;

/**
 * Created by Kalos on 25/03/2017.
 */

public class Categoria implements Serializable {

    public String getName() {
        return name;
    }

    public void setImg(int img) {
        this.img = img;
    }

    //private ATTACHMENT symbol;
    private String name="default";
    private int img= R.drawable.default_spese;



    public int getImg(){
        return img;
    }

    public void setName(String name){
        this.name=name;
    }

    public Categoria(){}

    public Categoria(String cat){
        this.name = cat;

        if(name.equals("General expenditure"))
            img =R.drawable.dollar;
        if(name.equals("BirthDay"))
            img =R.drawable.birthday;
        if(name.equals("Hotels"))
            img =R.drawable.hotel;
        if(name.equals("Amusement"))
            img =R.drawable.amusement;
        if(name.equals("Abroad Journey"))
            img =R.drawable.trip_aboard;
        if(name.equals("Clubbing"))
            img =R.drawable.clubbing;
        if(name.equals("Medical"))
            img =R.drawable.medic;
        if(name.equals("Priority"))
            img =R.drawable.urgent;
        if(name.equals("Food"))
            img =R.drawable.food;
        if(name.equals( "Tecnology"))
            img =R.drawable.tech;
        if(name.equals("Travel"))
            img =R.drawable.travelling;






    }



}
