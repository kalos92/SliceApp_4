package it.polito.mad17.viral.sliceapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ExpenseDetails extends AppCompatActivity {

    private Spesa s;
    private Gruppo g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_details);

        // setto la toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbarExpenseDetail);
        setSupportActionBar(toolbar);

        // definisco il tasto indietro
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // pesco spesa selezionata e suo gruppo dall'intent
        s = (Spesa)getIntent().getExtras().get("spesa");
        g = (Gruppo)getIntent().getExtras().get("gruppo");

        // Riempio la toolbar
        String nomeSpesa = s.getNome_spesa();
        String currency = s.getValuta();
        String importo = s.getImporto().toString();
        // String payer = s.getPagante().getName()+" "+s.getPagante().getSurname();
        TextView nameExpense = (TextView) findViewById(R.id.ExpenseDetailsName);
        nameExpense.setText(nomeSpesa);
        TextView valueExpense = (TextView) findViewById(R.id.TextExpenseDetailsValue);
        valueExpense.setText(currency+" "+importo);

        // rimepio la listview  con la suddivisione del pagamento
        List<String> items = new ArrayList<String>();
        Collection<Soldo> soldi = s.getDivisioni().values();
        for(Soldo soldo: soldi){
            Persona p = soldo.getPersona();
            String nome =  p.getName();
            String cognome = p.getSurname();
            Boolean haPagato = soldo.getHaPagato();
            String t;
            if(haPagato)
                t = nome +" "+cognome+" "+"has already payed his part " + "(" + soldo.getImporto().toString()+ " " + currency +")";
            else t = nome +" "+cognome+" "+"has to pay " + soldo.getImporto().toString() + " " + currency;
            // la valuta non c'è in soldo
            items.add(t);
        }
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                return view;
            }
        };
        ListView mList = (ListView) findViewById(R.id.listViewExpenseDetails);
        mList.setAdapter(itemsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_expense_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final String groupID = g.getGroupID();
        final String expenseID = s.getExpenseID();

        // controllo prima se colui che vuole pagare la spesa è il pagante della spesa stessa
        // il pagante, già quando carica la spesa, paga la sua parte
        if(s.getPagante().getTelephone().equals(SliceAppDB.getUser().getTelephone())){
            Toast.makeText(getApplicationContext(), "You have already payed your part (you have added this expense in the past)", Toast.LENGTH_SHORT).show();
            return true;
        }

        final DatabaseReference databaseRef = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/").getReference();
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Individuo la spesa in questione
                DataSnapshot spesa = dataSnapshot.child("groups_prova").child(groupID).child("spese").child(expenseID);
                DatabaseReference spesaRef = databaseRef.child("groups_prova").child(groupID).child("spese").child(expenseID);

                // controllo se la spesa è stata già pagata: se è stata già pagata, esco e stampo un toast all'utente
                Boolean giaPagata = (Boolean) spesa.child("divisioni").child(SliceAppDB.getUser().getTelephone()).child("haPagato").getValue();
                if(giaPagata == true){
                    Toast.makeText(getApplicationContext(), "You have already payed your part", Toast.LENGTH_SHORT).show();
                    return;
                }
                // se non è stata pagata, setto il campo "haPgato" a true...
                spesaRef.child("divisioni").child(SliceAppDB.getUser().getTelephone()).child("haPagato").setValue(true);

                // ... e aggiorno crediti e debiti che ho nei confronti del pagante della spesa

                // estraggo la parte che devo
                double myPart = spesa.child("divisioni").child(SliceAppDB.getUser().getTelephone()).child("importo").getValue(Double.class);
                // gestraggo credito/debito attuale nei confronti del pagante
                double creditsDebts = dataSnapshot.child("users_prova").child(SliceAppDB.getUser().getTelephone()).child("amici")
                                                  .child(s.getPagante().getTelephone()).child("importo").getValue(Double.class);
                // aggiorno credito/debito
                creditsDebts += myPart;
                // setto il nuovo credito/debito nei confronti del pagante
                databaseRef.child("users_prova").child(SliceAppDB.getUser().getTelephone()).child("amici").child(s.getPagante().getTelephone()).child("importo").setValue(creditsDebts);

                // le modifiche fatte sotto "users_prova" devono essere propagate anche in locale
                //SliceAppDB.getUser().getAmici().get(s.getPagante().getTelephone()).setImporto(creditsDebts); //
                Toast.makeText(getApplicationContext(), "Expense payed with success!", Toast.LENGTH_SHORT).show();

                // ...se non è stata ancora pagata...
                // ...controllo se ci sono altre spese da pagare
                Iterator<DataSnapshot> listaSpeseIter2 = dataSnapshot.child("groups_prova").child(groupID).child("spese").getChildren().iterator();
                Boolean hasAtLeastOneExpenseToPay = false;
                while(listaSpeseIter2.hasNext()){
                    DataSnapshot sp = listaSpeseIter2.next();
                    if(!sp.child("expenseID").getKey().equals(expenseID)){ // Se considerassi la spesa in questione, hasDebts è true
                        Boolean haPagato = (Boolean) sp.child("divisioni").child(SliceAppDB.getUser().getTelephone()).child("haPagato").getValue();
                        if(!haPagato) {
                            hasAtLeastOneExpenseToPay = true; // almeno un'altra spesa non è stata pagata => hasDebst se è true, deve rimanere true
                            break;
                        }
                    }
                }
                // se c'è ne almento una, setto a true il campo "hasDebts"...
                if(hasAtLeastOneExpenseToPay == true){
                    Iterator<DataSnapshot> listaSpeseIter3 = dataSnapshot.child("groups_prova").child(groupID).child("spese").getChildren().iterator();
                    while(listaSpeseIter3.hasNext()){
                        DataSnapshot sp = listaSpeseIter3.next();
                        DatabaseReference spe = databaseRef.child("groups_prova").child(groupID).child("spese").child(sp.getKey());
                        spe.child("divisioni").child(SliceAppDB.getUser().getTelephone()).child("persona").child("hasDebts").setValue(true);
                    }
                    // setto a true il campo "hasDebts" anche in users_prova
                    databaseRef.child("users_prova").child(SliceAppDB.getUser().getTelephone()).child("hasDebts").setValue(false);
                }
                // ...altrimenti, lo setto a false
                else {
                    Iterator<DataSnapshot> listaSpeseIter3 = dataSnapshot.child("groups_prova").child(groupID).child("spese").getChildren().iterator();
                    while (listaSpeseIter3.hasNext()) {
                        DataSnapshot sp = listaSpeseIter3.next();
                        DatabaseReference spe = databaseRef.child("groups_prova").child(groupID).child("spese").child(sp.getKey());
                        spe.child("divisioni").child(SliceAppDB.getUser().getTelephone()).child("persona").child("hasDebts").setValue(false);
                    }
                    // setto a true il campo "hasDebts" anche in users_prova
                    databaseRef.child("users_prova").child(SliceAppDB.getUser().getTelephone()).child("hasDebts").setValue(false);

                    // Siccome non ho trovato altre spese in cui ho debito, quella pagata era l'ultima
                    // posso togliere il gruppo della spesa dai gruppo in cui ho debito (il value del groupID sotto "dove_ho_debito" va settato a 0
                    databaseRef.child("users_prova").child(SliceAppDB.getUser().getTelephone()).child("dove_ho_debito").child(groupID).setValue(0);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        return true;
    }



}
