package it.polito.mad17.viral.sliceapp;


import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
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
import java.util.ArrayList;
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
        if (getArguments() != null) {

        }
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

                if(data!=null)
                    data_s = data.get(Calendar.DAY_OF_MONTH)+"/"+data.get((Calendar.MONTH)+1)+"/"+data.get(Calendar.YEAR);
                else {
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    data_s=day+"/"+month+1+"/"+year;
                }
                Spesa s1 = gruppo.AddSpesa(buyer, policy, nome, data_s, Double.parseDouble(price));
                s1.setValuta(values);
                s1.setBitmap_spesa(b);
                s1.setUri(uri);
                s1.setCat(cat);

                double importo = Double.parseDouble(price);
                // Uso la policy del gruppo visto che la policy deve ancora essere implementata
                HashMap<String, Soldo> divisioni = s1.getDivisioni();
                Collection<Soldo> parti = divisioni.values();

                FirebaseDatabase database =FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
                DatabaseReference groupsRef = database.getReference().child("groups");
                String groupID = gruppo.getGroupID(); // groupID del gruppo in questione
                DatabaseReference group = groupsRef.child(groupID); // gruppo associato a groupID
                DatabaseReference expense = group.child("expenses").push();

                // Siccome il metodo AddSpesa_and_try_repay, mette la spesa nella mappa,
                // con ID: nome_spesa+data, devo modificarlo con l'expenseID ritornato da firebase
                String expenseID = expense.getKey(); // aggiungo key della spesa
                gruppo.getMappaSpese().remove(s1.getNome()+s1.getData());
                s1.setExpenseID(expenseID); // leggo key della spesa e lo setto nella spesa
                gruppo.getMappaSpese().put(expenseID, s1);

                // setto i dati della spesa
                expense.child(expenseID).push();
                expense.child("category").setValue(cat); //String valuta = spi.getSelectedItem().toString()
                expense.child("currency").setValue(""); // Manca l'adapter del spinner
                expense.child("date").setValue(data_s);
                expense.child("description").setValue(nome);
                expense.child("payer").setValue(String.valueOf(user.getTelephone())); // l'utente dell'app che sta aggiungendo la spesa
                expense.child("recepitPDF").setValue(""); // Devo capire come salvare i bit
                expense.child("receiptPhoto").setValue("");
                expense.child("policy").setValue(""); // Manca l'alert dialog per scegliere le percentuali
                expense.child("price").setValue(importo);
                // carico le divisioni nella spesa
                for(Soldo soldo : parti){
                    String phone = String.valueOf(soldo.getPersona().getTelephone());
                    DatabaseReference t = expense.child("divisions").child(phone);
                    Double duePart =soldo.getImporto().doubleValue();
                    t.child("duePart").setValue(duePart);
                    boolean pagato = soldo.getHaPagato();
                    t.child("hasPaid").setValue(pagato);
                }

                SliceAppDB.getListaSpese().add(s1);

                getActivity().startActivity(i);
                getActivity().finish();
            }
        });

        Button save_r = (Button) v.findViewById(R.id.save_r);

        save_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data_s;
                Intent i = new Intent(getActivity(), ExpensesActivity.class);

                i.putExtra("Gruppo", gruppo);
                i.putExtra("User", user);

                if(data!=null)
                data_s = data.get(Calendar.DAY_OF_MONTH)+"/"+data.get((Calendar.MONTH)+1)+"/"+data.get(Calendar.YEAR);
                else {
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    data_s=day+"/"+month+1+"/"+year;
                }
                Spesa s1 = gruppo.AddSpesa_and_try_repay(buyer, policy, nome, data_s, Double.parseDouble(price));
                if(s1==null){
                    Toast.makeText(getContext(),"You do not have any debts", Toast.LENGTH_LONG).show();
                    return;
                }
                s1.setValuta(values);
                s1.setBitmap_spesa(b);
                s1.setUri(uri);
                s1.setCat(cat);

                double importo = Double.parseDouble(price);

                HashMap<String, Soldo> divisioni = s1.getDivisioni();
                Collection<Soldo> parti = divisioni.values();

                FirebaseDatabase database =FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
                DatabaseReference groupsRef = database.getReference().child("groups");
                String groupID = gruppo.getGroupID(); // groupID del gruppo in questione
                DatabaseReference group = groupsRef.child(groupID); // gruppo associato a groupID
                DatabaseReference expense = group.child("expenses").push();

                // Siccome il metodo AddSpesa_and_try_repay, mette la spesa nella mappa,
                // con ID: nome_spesa+data, devo modificarlo con l'expenseID ritornato da firebase
                String expenseID = expense.getKey(); // aggiungo key della spesa
                gruppo.getMappaSpese().remove(s1.getNome()+s1.getData());
                s1.setExpenseID(expenseID); // leggo key della spesa e lo setto nella spesa
                gruppo.getMappaSpese().put(expenseID, s1);

                // setto i dati della spesa
                expense.child("category").setValue(cat); //String valuta = spi.getSelectedItem().toString()
                expense.child("currency").setValue(""); // Manca l'adapter del spinner
                expense.child("date").setValue(data_s);
                expense.child("description").setValue(nome);
                expense.child("payer").setValue(String.valueOf(user.getTelephone())); // l'utente dell'app che sta aggiungendo la spesa
                expense.child("recepitPDF").setValue(""); // Devo capire come salvare i bit
                expense.child("receiptPhoto").setValue("");
                expense.child("policy").setValue(""); // Manca l'alert dialog per scegliere le percentuali
                expense.child("price").setValue(importo);
                // carico le divisioni nella spesa
                for(Soldo soldo : parti){
                    String phone = String.valueOf(soldo.getPersona().getTelephone());
                    DatabaseReference t = expense.child("divisions").child(phone);
                    Double duePart =soldo.getImporto().doubleValue();
                    t.child("duePart").setValue(duePart);
                    boolean pagato = soldo.getHaPagato();
                    t.child("hasPaid").setValue(pagato);
                }

                // aggiorno le divisioni delle spese del gruppo in questione perhé eventualmente sono
                // state modificate dal metodo AddSpesa_and_try_repay
                DatabaseReference expenses = group.child("expenses");
                ArrayList<Spesa> spese = gruppo.getSpese();
                for(Spesa s : spese){
                    DatabaseReference exp = expenses.child(s.getExpenseID());
                    DatabaseReference div = exp.child("divisions");
                    Collection<Soldo> divisions = s.getDivisioni().values();
                    for(Soldo soldo : divisions){
                        String phoneNumber = String.valueOf(soldo.getPersona().getTelephone());
                        Double duePart = soldo.getImporto();
                        Boolean hasPaid = soldo.getHaPagato();
                        DatabaseReference member = div.child(phoneNumber);
                        member.child("duePart").setValue(duePart);
                        member.child("hasPaid").setValue(hasPaid);
                    }
                }

                // Aggiungo la spesa alla lista delle spese globali
                SliceAppDB.getListaSpese().add(s1);

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
