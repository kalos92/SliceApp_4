package it.polito.mad17.viral.sliceapp;



import android.net.Uri;
import android.webkit.HttpAuthHandler;
import android.widget.ImageView;

import java.util.Calendar;
import com.google.gson.annotations.Expose;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;



/**
 * Created by Kalos on 25/03/2017.
 */

public class Gruppo implements Serializable,Cloneable {


    private String groupName;
    private int n_partecipanti;
    private HashMap<String, Persona> partecipanti = new HashMap<String,Persona>();
    private HashMap<String,String> partecipanti_numero_cnome = new HashMap<String,String>();
    private Persona user;
    private HashMap<String, Spesa> spese = new HashMap <String, Spesa>();
    private String groupID;
    private Policy policy;
    private int img;
    private long c;
    private String uri;
    private HashMap<String,Riga_bilancio_Gruppo> dettaglio_bilancio = new HashMap<String,Riga_bilancio_Gruppo>();
    private mCurrency curr;
    private HashMap<String, Boolean> contested = new HashMap<>();
    private String GroupCreator;


    public Gruppo(){
        // needed for FirebaseListAdapter
    }

    public Gruppo(String groupID, String groupName, int n, final ArrayList<Persona> partecipanti_array, Policy policy, mCurrency curr2, Uri uri,String groupCreator){
        this.groupID=groupID;
        this.groupName=groupName;
        this.n_partecipanti=n;
        this.curr=curr2;
        int i=0;
        this.policy=policy;
        if(uri!=null)
        this.uri=uri.toString();
        else
        uri=null;
        img= R.drawable.default_img;
        setImg(img);
        this.GroupCreator=groupCreator;

        for(final Persona p: partecipanti_array) {
            p.initiliazeBalance(partecipanti_array,curr,p);
            partecipanti_numero_cnome.put(p.getTelephone(), p.getName() + " " + p.getSurname());
            p.AddToGroup(this, i,img);
            partecipanti.put(p.getTelephone(),p);
            i++;
            Double f = 0d;
            dettaglio_bilancio.put(p.getTelephone(),new Riga_bilancio_Gruppo(p.getName()+" "+p.getSurname(),curr.getSymbol(),curr.getDigits(),f,p.getTelephone(),partecipanti_array));
        }

        c = Calendar.getInstance().getTimeInMillis();
        contested.put("STARTING POINT", false);
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



    public Spesa AddSpesa(String spesaId,Persona pagante,Policy policy, String nome_spesa, String data, Double importo){

        Spesa spesa;
        Gestore gestore=new Gestore();
        spesa = new Spesa(nome_spesa,data,policy,pagante,importo,this);
        spesa.setExpenseID(spesaId);
        spesa.setParti(gestore.Calculate_Credits(pagante,policy, spesa.getImporto(),partecipanti, partecipanti.size(),user,this));
        //metto il debito a tutti

        spese.put(spesaId,spesa);




        return spesa;

    }


    public Double obtainAllDebts(){ //i debiti sono calcolati nelle spese che NON ho fatto io e devo ancora pagare
        Double f= new Double(0);
        for(Spesa s: spese.values()){
            if(!s.getRemoved()){
            if(!s.getPagante().getTelephone().equals(user.getTelephone())){
                for(Soldo soldo: s.getDivisioni().values())
                    if(!soldo.getHaPagato() && soldo.getPersona().getTelephone().equals(user.getTelephone()))
                        f+=soldo.getImporto();
            }
        }}
        return f;
    }

    public Double obtainAllCredits(){  //i crediti sono calcolti nelle spese che HO fatto io e gli altri non mi hanno pagato
        Double f= new Double(0);
        for(Spesa s: spese.values()){
            if(!s.getRemoved()){
            if(s.getPagante().getTelephone().equals(user.getTelephone())){
                for(Soldo soldo: s.getDivisioni().values())
                    if(!soldo.getHaPagato() && !soldo.getPersona().getTelephone().equals(user.getTelephone()))
                        f+=soldo.getImporto();
            }
        }}
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

    public String getGroupCreator() {
        return GroupCreator;
    }

    public void setGroupCreator(String groupCreator) {
        GroupCreator = groupCreator;
    }


    public mCurrency getCurr() {
        return curr;
    }

    public void setCurr(mCurrency curr) {
        this.curr = curr;
    }


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Spesa addFake(String ID, String name, Double importo, String user,int precision, String currenc){
        Spesa s = new Spesa(name,importo,user,ID,precision,currenc);
        s.setC(System.currentTimeMillis()); // aggiunto da abdel per evitare l'ondata di notifiche (uso timestamp)
        spese.put(ID,s);

            return s;
    }


    public Riga_bilancio_Gruppo obtainbalanceofuser(String telephone) {
        return dettaglio_bilancio.get(telephone);
    }


    public HashMap<String, Boolean> getContested() {
        return contested;
    }

    public void setContested(HashMap<String, Boolean> contested) {
        this.contested = contested;
    }

    public HashMap<String, Riga_bilancio_Gruppo> getDettaglio_bilancio() {
        return dettaglio_bilancio;
    }

    public void setDettaglio_bilancio(HashMap<String, Riga_bilancio_Gruppo> dettaglio_bilancio) {
        this.dettaglio_bilancio = dettaglio_bilancio;
    }
}




