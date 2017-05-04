package it.polito.mad17.viral.sliceapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;


public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sharedPref = getSharedPreferences("data",MODE_PRIVATE);
        int number = sharedPref.getInt("isLogged", 0);

        // Se la variablie isLogged è settata a 0, mando l'utente alla pagina di login perché è la prima volta che si logga
        if(number == 0){
            Intent i = new Intent(SplashScreen.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        // altrimenti, lo mando List_pager_activity perché si è già loggato in precedenza
        else {
            String nome = sharedPref.getString("nome", null);
            String cognome = sharedPref.getString("cognome", null);
            String username = sharedPref.getString("username", null);
            String dob = sharedPref.getString("dob", null);
            String telefono = sharedPref.getString("telefono", null);
            Persona p = new Persona(nome, cognome, username, dob, telefono);
            SliceAppDB.setUser(p);

            //Caricamento da Firebase
            final String userphone = telefono;
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
            DatabaseReference rootRef = database.getReference();
            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot ds = dataSnapshot.child("users").child(userphone);
                    Iterator<DataSnapshot> iterdata = ds.child("belongsToGroups").getChildren().iterator();
                    int positionGroup = 0;
                    while (iterdata.hasNext()) {
                        String groupID = iterdata.next().getKey();
                        DataSnapshot groups = dataSnapshot.child("groups");
                        String groupName = (String) groups.child(groupID).child("name").getValue();
                        long numMembers = (long) groups.child(groupID).child("numMembers").getValue();
                        // estrazione policy di default associata a groupID
                        Iterator<DataSnapshot> policies = groups.child(groupID).child("policy").getChildren().iterator();
                        Double[] percentages = new Double[(int) numMembers];
                        int i = 0;
                        while (policies.hasNext()) {
                            Double percentage = (Double) policies.next().getValue();
                            percentages[i++] = percentage;
                        }
                        Policy policy = new Policy(percentages, (int) numMembers);

                        // iterare sulle persone, crearle e aggiungerle ai gruppi
                        Iterator<DataSnapshot> members = groups.child(groupID).child("members").getChildren().iterator();
                        DataSnapshot users = dataSnapshot.child("users");
                        ArrayList<Persona> partecipanti = new ArrayList<Persona>();
                        while (members.hasNext()) {
                            String phonenumber = members.next().getKey();
                            DataSnapshot member = users.child(phonenumber);
                            String nome = (String) member.child("name").getValue();
                            String cognome = (String) member.child("surname").getValue();
                            String username = (String) member.child("username").getValue();
                            String dob = (String) member.child("birthdate").getValue();
                            String telefono = (String) member.child("telephone").getValue();
                            Persona p = new Persona(nome, cognome, username, dob, telefono);
                            partecipanti.add(p);
                        }
                        // creo il gruppo e setto il groupID
                        Gruppo g = new Gruppo(groupName, (int) numMembers, partecipanti, policy);
                        g.setGroupID(groupID);

                        // setto lo user del gruppo perché viene inviato nell'intent
                        for (Persona p : partecipanti) {
                            if (userphone.equals(String.valueOf(p.getTelephone()))) {
                                g.setUser(p);
                                break;
                            }
                        }

                        // estrazione delle spese e loro aggiunta al gruppo
                        DataSnapshot expenses = groups.child(groupID).child("expenses");
                        Iterator<DataSnapshot> iterExpenses = expenses.getChildren().iterator();
                        while (iterExpenses.hasNext()) {
                            String expenseID = iterExpenses.next().getKey();
                            DataSnapshot expense = expenses.child(expenseID);
                            String payer = (String) expense.child("payer").getValue();
                            String currency = (String) expense.child("currency").getValue();
                            String date = (String) expense.child("date").getValue();
                            String description = (String) expense.child("description").getValue();
                            double price = expense.child("price").getValue(double.class);
                            String category = (String) expense.child("category").getValue();
                            // estrarre categoria successivamente
                            DataSnapshot ps = expense.child("policy");
                            Iterator<DataSnapshot> iterpercentages = ps.getChildren().iterator();
                            Double[] percentuali = new Double[(int) numMembers];
                            int j = 0;
                            while (iterpercentages.hasNext()) {
                                DataSnapshot t = iterpercentages.next();
                                Double percentage = (double) t.getValue();
                                percentuali[j++] = percentage;
                            }
                            Policy expensePolicy = new Policy(percentuali, percentuali.length);
                            // estrazione del payer dai partecipanti
                            Persona pagante = null;
                            for (Persona p : partecipanti) {
                                if (String.valueOf(p.getTelephone()).equals(payer)) {
                                    pagante = p;
                                    break;
                                }
                            }

                            // estrazione delle divisioni
                            ArrayList<Soldo> divisioni = new ArrayList<Soldo>();
                            DataSnapshot divisions = expense.child("divisions");
                            Iterator<DataSnapshot> iterUsers = divisions.getChildren().iterator();
                            while (iterUsers.hasNext()) {
                                DataSnapshot t = iterUsers.next();
                                String phonenumber = t.getKey();
                                double duePart = t.child("duePart").getValue(double.class);
                                boolean hasPaid = t.child("hasPaid").getValue(boolean.class);
                                Persona currentUser = null;
                                for (Persona p : partecipanti) {
                                    if (String.valueOf(p.getTelephone()).equals(phonenumber)) {
                                        currentUser = p;
                                        break;
                                    }
                                }
                                Soldo soldo = new Soldo(currentUser, duePart, hasPaid, pagante);
                                divisioni.add(soldo);
                            }
                            // Aggiungo la spesa al gruppo
                            Spesa spesa = new Spesa(description, date, expensePolicy, pagante, price, g);
                            SliceAppDB.getListaSpese().add(spesa);
                            spesa.setExpenseID(expenseID); // setto expenseID
                            spesa.getCat().setName(category); // setto categoria

                            Map<String, Soldo> mappaSoldo = spesa.getDivisioni();
                            for (Soldo soldo : divisioni)
                                mappaSoldo.put(soldo.getPersona().getUsername(), soldo);
                            g.getMappaSpese().put(spesa.getExpenseID(), spesa);
                        }
                        //inserisco il gruppo nella lista dei gruppi e nella mappa dei gruppi
                        SliceAppDB.getListaGruppi().add(g);
                        SliceAppDB.getMappaGruppi().put(positionGroup, g);
                        SliceAppDB.getGruppi().put(groupID, g);
                        positionGroup++;
                    }
                    System.out.println("onDataChange ha finito il suo lavoro!");
                    Intent i = new Intent(SplashScreen.this, List_Pager_Act.class);
                    startActivity(i);
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}