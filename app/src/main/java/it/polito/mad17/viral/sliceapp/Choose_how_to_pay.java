package it.polito.mad17.viral.sliceapp;


import android.content.Intent;
import android.graphics.Bitmap;
import java.util.GregorianCalendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;



public class Choose_how_to_pay extends Fragment implements Select_Policy_Fragment.ReturnSelection_2{
    private View v;
    private String values;
    private String cat;
    private GregorianCalendar data;
    private Persona buyer;
    private Bitmap b;
    private Uri uri;
    private String price;
    private String nome;
    private Gruppo gruppo;
    private Persona user;
    private Policy policy;

    public static Choose_how_to_pay newInstance(Gruppo g) {
        Choose_how_to_pay fragment = new Choose_how_to_pay();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) { }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_choose_how_to_pay, container, false);
        setRetainInstance(true);
        if(savedInstanceState!=null){
            if(savedInstanceState.getParcelable("Uri")!=null)
                uri = savedInstanceState.getParcelable("Uri");

            if(savedInstanceState.getParcelable("Bitmap")!=null)
                b = savedInstanceState.getParcelable("Bitmap");

            if(savedInstanceState.getSerializable("Date")!=null)
                data = (GregorianCalendar) savedInstanceState.getSerializable("Date");

            if(savedInstanceState.getSerializable("Value")!=null)
                values = (String) savedInstanceState.getSerializable("Value");

            if(savedInstanceState.getSerializable("Cat")!=null)
                cat = (String) savedInstanceState.getSerializable("Cat");

            if(savedInstanceState.getSerializable("Buyer")!=null)
                buyer = (Persona) savedInstanceState.getSerializable("Buyer");

            if(savedInstanceState.getSerializable("Price")!=null)
                price = (String) savedInstanceState.getSerializable("Price");

            if(savedInstanceState.getSerializable("Name")!=null)
                nome = (String) savedInstanceState.getSerializable("Name");

            if(savedInstanceState.getSerializable("Policy")!=null)
                policy = (Policy) savedInstanceState.getSerializable("Policy");
        }
        Button save = (Button) v.findViewById(R.id.save_b);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ExpensesActivity.class);
                String data_s;
                i.putExtra("Gruppo", gruppo);
                i.putExtra("User", user);

                if(data!=null){
                    int month = data.get(Calendar.MONTH);
                month++;
                    data_s = data.get(Calendar.DAY_OF_MONTH)+"/"+month+"/"+data.get(Calendar.YEAR);}
                else {
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    month++;
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    data_s=day+"/"+month+"/"+year;
                }
                FirebaseDatabase database =FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
                DatabaseReference expensesRef = database.getReference().child("expenses");
                String groupID = gruppo.getGroupID(); // groupID del gruppo in questione
                DatabaseReference expense = expensesRef.push();

                // Siccome il metodo AddSpesa_and_try_repay, mette la spesa nella mappa,
                // con ID: nome_spesa+data, devo modificarlo con l'expenseID ritornato da firebase
                String expenseID = expense.getKey(); // aggiungo key della spesa
                Spesa s1 = gruppo.AddSpesa(expenseID,buyer, policy, nome, data_s, Double.parseDouble(price));
                s1.setValuta(values);
                s1.setBitmap_spesa(b);
                s1.setUri(uri);
                s1.setCat_string(cat);

                gruppo.refreshC();




                ArrayList<Persona> partecipanti = new ArrayList<Persona>(gruppo.obtainPartecipanti().values());

                Collection<Soldo> parti = s1.getDivisioni().values();
            for(Persona p: partecipanti){
                if(!p.getTelephone().equals(user.getTelephone()))
                    p.plusOneUnread(gruppo.getGroupID());

                user.updateLast(gruppo.getGroupID(),buyer.getName(),s1.getNome_spesa()); //Why? non capisco ma funziona -> niente domande
                p.updateLast(gruppo.getGroupID(),buyer.getName(),s1.getNome_spesa());
                p.refreshTimeOfGroup(gruppo.getGroupID());
                if(s1.getPagante().getTelephone().equals(p.getTelephone())){ //Se il pagante sono io

                    for(Soldo s: parti){
                        if(!s.getPersona().getTelephone().equals(p.getTelephone())){// per tutti i soldi che non sono io
                        p.addTobalance(s.getPersona(),s.getImporto());
                    }}
                }
                else { //non lo sono

                    for (Soldo s : parti) {
                        if (s.getPersona().getTelephone().equals(p.getTelephone())) {// solo il mio soldo
                            p.addTobalance(s.getPagante(), (s.getImporto()) * -1d); //prendo la mia parte in negativo per quella persona e andrò a sommarla al pagante
                        }

                    }
                }
            }

                final DatabaseReference groups_prova = database.getReference().child("groups_prova").child(gruppo.getGroupID());
                Gson gson = new Gson();
                Gruppo g1 = gson.fromJson(gson.toJson(gruppo),Gruppo.class);
                groups_prova.setValue(g1);
                final DatabaseReference users_prova = database.getReference().child("users_prova");

                for(Persona p: partecipanti){
                    Persona p1 = gson.fromJson(gson.toJson(p),Persona.class);
                    users_prova.child(p1.getTelephone()).setValue(p1);

                }


                SliceAppDB.setUser(user);
                getActivity().startActivity(i);
                getActivity().finish();
            }
        });

        Button save_r = (Button) v.findViewById(R.id.save_r);

        save_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ExpensesActivity.class);
                String data_s;
                i.putExtra("Gruppo", gruppo);
                i.putExtra("User", user);

                if(data!=null){
                    int month = data.get(Calendar.MONTH);
                    month++;
                    data_s = data.get(Calendar.DAY_OF_MONTH)+"/"+month+"/"+data.get(Calendar.YEAR);}
                else {
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    month++;
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    data_s=day+"/"+month+"/"+year;
                }
                FirebaseDatabase database =FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
                DatabaseReference expensesRef = database.getReference().child("expenses");
                String groupID = gruppo.getGroupID(); // groupID del gruppo in questione
                DatabaseReference expense = expensesRef.push();

                // Siccome il metodo AddSpesa_and_try_repay, mette la spesa nella mappa,
                // con ID: nome_spesa+data, devo modificarlo con l'expenseID ritornato da firebase
                String expenseID = expense.getKey(); // aggiungo key della spesa
                Spesa s1 = gruppo.AddSpesa_and_try_repay(expenseID,buyer, policy, nome, data_s, Double.parseDouble(price));
                if(s1==null) {
                Toast.makeText(getContext(),"There is a good news, you do not have any debts! Be Happy", Toast.LENGTH_LONG).show();
                    return;
                }
                s1.setValuta(values);
                s1.setBitmap_spesa(b);
                s1.setUri(uri);
                s1.setCat_string(cat);

                gruppo.refreshC();




                ArrayList<Persona> partecipanti = new ArrayList<Persona>(gruppo.obtainPartecipanti().values());

                Collection<Soldo> parti = s1.getDivisioni().values();
                for(Persona p: partecipanti){
                    if(!p.getTelephone().equals(user.getTelephone()))
                        p.plusOneUnread(gruppo.getGroupID());

                    user.updateLast(gruppo.getGroupID(),buyer.getName(),s1.getNome_spesa()); //Why? non capisco ma funziona -> niente domande
                    p.updateLast(gruppo.getGroupID(),buyer.getName(),s1.getNome_spesa());
                    p.refreshTimeOfGroup(gruppo.getGroupID());
                    if(s1.getPagante().getTelephone().equals(p.getTelephone())){ //Se il pagante sono io

                        for(Soldo s: parti){
                            if(!s.getPersona().getTelephone().equals(p.getTelephone())){// per tutti i soldi che non sono io
                                p.addTobalance(s.getPersona(),s.getImporto());
                            }}
                    }
                    else { //non lo sono

                        for (Soldo s : parti) {
                            if (s.getPersona().getTelephone().equals(p.getTelephone())) {// solo il mio soldo
                                p.addTobalance(s.getPagante(), (s.getImporto()) * -1d); //prendo la mia parte in negativo per quella persona e andrò a sommarla al pagante
                            }

                        }
                    }
                }

                final DatabaseReference groups_prova = database.getReference().child("groups_prova").child(gruppo.getGroupID());
                Gson gson = new Gson();
                Gruppo g1 = gson.fromJson(gson.toJson(gruppo),Gruppo.class);
                groups_prova.setValue(g1);
                final DatabaseReference users_prova = database.getReference().child("users_prova");

                for(Persona p: partecipanti){
                    Persona p1 = gson.fromJson(gson.toJson(p),Persona.class);
                    users_prova.child(p1.getTelephone()).setValue(p1);

                }


                SliceAppDB.setUser(user);
                getActivity().startActivity(i);
                getActivity().finish();
            }
        });

        return v;
    }


    @Override
    public void returnSelection_2(String values, String cat, GregorianCalendar data, Persona buyer, Bitmap b, Uri uri, String nome, String price, Gruppo gruppo, Persona user, Choose_how_to_pay chtp, Policy policy) {
        this.values=values;//
        this.cat=cat;
        this.data=data;//
        this.buyer=buyer;//
        this.b=b;
        this.uri=uri;
        this.nome=nome;//
        this.gruppo=gruppo;//
        this.user=user;//
        this.policy=policy;//
        this.price=price;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState!=null){
            if(savedInstanceState.getParcelable("Uri")!=null)
                uri = savedInstanceState.getParcelable("Uri");
            if(savedInstanceState.getParcelable("Bitmap")!=null)
                b = savedInstanceState.getParcelable("Bitmap");
            if(savedInstanceState.getSerializable("Date")!=null)
                data = (GregorianCalendar) savedInstanceState.getSerializable("Date");
            if(savedInstanceState.getSerializable("Value")!=null)
                values = (String) savedInstanceState.getSerializable("Value");
            if(savedInstanceState.getSerializable("Cat")!=null)
                cat = (String) savedInstanceState.getSerializable("Cat");
            if(savedInstanceState.getSerializable("Buyer")!=null)
                buyer = (Persona) savedInstanceState.getSerializable("Buyer");
            if(savedInstanceState.getSerializable("Price")!=null)
                price = (String) savedInstanceState.getSerializable("Price");
            if(savedInstanceState.getSerializable("Name")!=null)
                nome = (String) savedInstanceState.getSerializable("Name");
            if(savedInstanceState.getSerializable("Policy")!=null)
                policy = (Policy) savedInstanceState.getSerializable("Policy");
        }
    }
}
