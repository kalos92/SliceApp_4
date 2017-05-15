package it.polito.mad17.viral.sliceapp;



import java.util.Calendar;
import com.google.gson.annotations.Expose;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;



/**
 * Created by Kalos on 25/03/2017.
 */

public class Gruppo implements Serializable,Cloneable {

    @Expose
    private String groupName;
    @Expose
    private int n_partecipanti;

    @Expose
    private HashMap<String, Persona> partecipanti = new HashMap<String,Persona>();
    @Expose
    private HashMap<String,String> partecipanti_numero_cnome = new HashMap<String,String>();

    @Expose
    private Persona user;

    @Expose
    private HashMap<String, Spesa> spese = new HashMap <String, Spesa>();
    @Expose
    private ArrayList<Spesa> listaSpeseGruppo = new ArrayList<Spesa>();
    @Expose
    private String groupID;
    @Expose
    private Policy policy;
    @Expose
    private int img;

    @Expose
    private long c;


    @Expose
    private boolean hasDone=false;

    public Gruppo(){
        // needed for FirebaseListAdapter
    }

    public Gruppo(String groupID, String groupName, int n, final ArrayList<Persona> partecipanti_array, Policy policy ){
        this.groupID=groupID;
        this.groupName=groupName;
        this.n_partecipanti=n;

        int i=0;
        this.policy=policy;
        img= R.drawable.default_img;
        setImg(img);

        for(final Persona p: partecipanti_array) {

            partecipanti_numero_cnome.put(p.getTelephone(), p.getName() + " " + p.getSurname());
            p.AddToGroup(this, i,img);
            partecipanti.put(p.getTelephone(),p);
            i++;
        }

        c = Calendar.getInstance().getTimeInMillis();
    }


    public Gruppo(String groupID, String groupName, int n,  HashMap<String,Persona> partecipanti_array, Policy policy,Persona persona ){
        this.groupID=groupID;
        this.groupName=groupName;
        this.n_partecipanti=n;

        int i=0;
        this.policy=policy;
        img= R.drawable.default_img;
        setImg(img);

        this.partecipanti.putAll(partecipanti_array);
        this.user=persona;

    }

    public Spesa getSpesa(String expenseID){ return spese.get(expenseID); }

        public Spesa AddSpesa_and_try_repay(String spesaId,Persona pagante,Policy policy,String nome_spesa, String data, Double importo){

            Spesa spesa;
            if(user.getHasDebts()){
            Gestore gestore=new Gestore();
            spesa= new Spesa(nome_spesa,data,policy,pagante,importo,this);
            spesa.setExpenseID(spesaId);
            spesa.setParti(gestore.Calculate_Credits_To_Buyer_With_Repaing(pagante,policy, spesa.getImporto(),partecipanti, partecipanti.size(),spese,user,this));
            //metto il debito a tutti

            spese.put(spesaId,spesa);
            listaSpeseGruppo.add(spesa);
            return spesa;}
            else
                return null;

        }


    public Spesa AddSpesa(String spesaId,Persona pagante,Policy policy, String nome_spesa, String data, Double importo){

        Spesa spesa;
        Gestore gestore=new Gestore();
        spesa = new Spesa(nome_spesa,data,policy,pagante,importo,this);
        spesa.setExpenseID(spesaId);
        spesa.setParti(gestore.Calculate_Credits(pagante,policy, spesa.getImporto(),partecipanti, partecipanti.size(),user,this));
        //metto il debito a tutti

        spese.put(spesaId,spesa);
        listaSpeseGruppo.add(spesa);
        return spesa;

    }

    public Double getAllDebts(){ //i debiti sono calcolati nelle spese che NON ho fatto io e devo ancora pagare
        Double f= new Double(0);
        for(Spesa s: spese.values()){
            if(!s.getPagante().getTelephone().equals(user.getTelephone())){
                for(Soldo soldo: s.getDivisioni().values())
                    if(!soldo.getHaPagato() && soldo.getPersona().getTelephone().equals(user.getTelephone()))
                        f+=soldo.getImporto();
            }
        }
        return f;
    }

    public Double getAllCredits(){  //i crediti sono calcolti nelle spese che HO fatto io e gli altri non mi hanno pagato
        Double f= new Double(0);
        for(Spesa s: spese.values()){
            if(s.getPagante().getTelephone().equals(user.getTelephone())){
                for(Soldo soldo: s.getDivisioni().values())
                    if(!soldo.getHaPagato() && !soldo.getPersona().getTelephone().equals(user.getTelephone()))
                        f+=soldo.getImporto();
            }
        }
        return f;
    }

    private void AddPartecipant(Persona p){
        partecipanti.put(p.getTelephone(), p);
    }

    public ArrayList<Spesa> Obtain_spese_array(){
        ArrayList<Spesa> spese_arr = new ArrayList<Spesa>(spese.values());
        return spese_arr;
    }




    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getN_partecipanti() {
        return n_partecipanti;
    }

    public void setN_partecipanti(int n_partecipanti) {
        this.n_partecipanti = n_partecipanti;
    }


    public HashMap<String, Persona> obtainPartecipanti() {
        return partecipanti;
    }


    public void setPartecipanti(HashMap<String, Persona> partecipanti) {
        this.partecipanti = partecipanti;
    }

    public void setPartecipanti_3(HashMap<String, Persona> partecipanti){
        this.partecipanti.putAll(partecipanti);
    }

    public void setUser(Persona user) {
        this.user = user;
    }





    public HashMap<String, Spesa> getSpese() {
        return spese;
    }

    public void setSpese(HashMap<String, Spesa> spese) {
        this.spese = spese;
    }

    public ArrayList<Spesa> getListaSpeseGruppo() {
        return listaSpeseGruppo;
    }

    public void setListaSpeseGruppo(ArrayList<Spesa> listaSpeseGruppo) {
        this.listaSpeseGruppo = listaSpeseGruppo;
    }


    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public Persona getPartecipante(String telephone){
        return partecipanti.get(telephone);
    }

    public  Persona obtainUser() {
        return user;
    }

    public HashMap<String, String> getPartecipanti_numero_cnome() {
        return partecipanti_numero_cnome;
    }

    public void setPartecipanti_numero_cnome(HashMap<String, String> partecipanti_numero_cnome) {
        this.partecipanti_numero_cnome = partecipanti_numero_cnome;
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




