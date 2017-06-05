package it.polito.mad17.viral.sliceapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by abdel on 16/05/2017.
 */

public class FirebaseBackgroundService extends Service {

    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
    private ChildEventListener groupsListener;
    private Map<String, List<String>> groupsExpenses = new HashMap<String, List<String>>();
    private ArrayList <String> groupsID = new ArrayList<String>();
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor prefEditor;
    private Long lastTimestampGroup;
    private Long lastTimestampExpense;
    private Long lastTimestampContestation;
    private Long lastTimestampComment;
    private Long lastTimestampDeletedExpense;
    private ArrayList<String> removedExpenses = new ArrayList<String>();

    @Nullable
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPref= getSharedPreferences("data",MODE_PRIVATE);


        lastTimestampGroup = sharedPref.getLong("lastTimestampGroup", 0);

        if(lastTimestampGroup == 0) {
            lastTimestampGroup = System.currentTimeMillis();
            prefEditor = sharedPref.edit();
            prefEditor.putLong("lastTimestampGroup", lastTimestampGroup);
            prefEditor.commit();
        }
        lastTimestampExpense = sharedPref.getLong("lastTimestampExpense", 0);
        if(lastTimestampExpense == 0){
            lastTimestampExpense = System.currentTimeMillis();
            prefEditor = sharedPref.edit();
            prefEditor.putLong("lastTimestampExpense", lastTimestampExpense);
            prefEditor.commit();
        }
        lastTimestampContestation = sharedPref.getLong("lastTimestampContestation", 0);
        if(lastTimestampContestation == 0){
            lastTimestampContestation = System.currentTimeMillis();
            prefEditor = sharedPref.edit();
            prefEditor.putLong("lastTimestampContestation", lastTimestampContestation);
            prefEditor.commit();
        }

        lastTimestampComment = sharedPref.getLong("lastTimestampComment", 0);
        if(lastTimestampComment == 0){
            lastTimestampComment = System.currentTimeMillis();
            prefEditor = sharedPref.edit();
            prefEditor.putLong("lastTimestampComment", lastTimestampComment);
            prefEditor.commit();
        }

        lastTimestampDeletedExpense = sharedPref.getLong("lastTimestampDeletedExpense", 0);
        if(lastTimestampDeletedExpense == 0){
            lastTimestampDeletedExpense = System.currentTimeMillis();
            prefEditor = sharedPref.edit();
            prefEditor.putLong("lastTimestampDeletedExpense", lastTimestampDeletedExpense);
            prefEditor.commit();
        }


        final String userTelephone = sharedPref.getString("telefono", null);

        DatabaseReference groupsRef = database.getReference().child("groups_prova");

        final DatabaseReference contestationsRef = database.getReference().child("users_prova").child(userTelephone).child("contestazioni");
        contestationsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                long contestationTimestamp = (long) dataSnapshot.child("timestamp").getValue();
                final String contestator,expenseID,groupID,contestationID;
                expenseID = (String) dataSnapshot.child("expenseID").getValue();
                groupID = (String) dataSnapshot.child("groupID").getValue();
                contestationID = (String) dataSnapshot.child("contestID").getValue();
                contestator = (String) dataSnapshot.child("userName").getValue();//contestator

                if (contestationTimestamp > lastTimestampContestation) {
                        if (!dataSnapshot.child("phoneNumber").getValue(String.class).equals(userTelephone)) {

                            lastTimestampContestation = contestationTimestamp;
                            prefEditor = sharedPref.edit();
                            prefEditor.putLong("lastTimestampContestation", lastTimestampContestation);
                            prefEditor.commit();
                            // get data to show in the notification
                            String expenseName = (String) dataSnapshot.child("nameExpense").getValue();
                            String groupName = (String) dataSnapshot.child("groupName").getValue();
                            String contestTitle = (String) dataSnapshot.child("title").getValue();
                            // notification
                            Intent notificationIntent = new Intent(getApplicationContext(), CommentsActivity.class);
                            notificationIntent.putExtra("contestator", contestator);
                            notificationIntent.putExtra("groupID", groupID);
                            notificationIntent.putExtra("expenseID", expenseID);
                            notificationIntent.putExtra("contestationID", contestationID);

                            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                            android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.added_contestation)
                                    .setContentTitle(contestator + " has contested expense " + expenseName + " of group " + groupName)
                                    .setContentText(contestTitle)
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                    .setContentIntent(contentIntent);
                            Notification noti = builder.build();
                            noti.flags = Notification.FLAG_AUTO_CANCEL;
                            // Add notification
                            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            manager.notify((int) System.currentTimeMillis(), noti);
                        }

                    }
                // add listener for comment
                contestationsRef.child(dataSnapshot.getKey()).child("commenti").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        long commentTimestamp = (long) dataSnapshot.child("timestamp").getValue();

                        if(commentTimestamp > lastTimestampComment) {
                            if(!dataSnapshot.child("userID").getValue(String.class).equals(userTelephone)){

                                String commentsAct = sharedPref.getString("activity","commentsActivity");
                                if(!commentsAct.equals("commentsActivity")) {
                                    lastTimestampComment = commentTimestamp;
                                    prefEditor = sharedPref.edit();
                                    prefEditor.putLong("lastTimestampComment", lastTimestampComment);
                                    prefEditor.commit();

                                    String userName = (String) dataSnapshot.child("userName").getValue();
                                    String commento = (String) dataSnapshot.child("commento").getValue();
                                    // notification
                                    Intent notificationIntent = new Intent(getApplicationContext(), CommentsActivity.class);
                                    notificationIntent.putExtra("contestator", contestator);
                                    notificationIntent.putExtra("groupID", groupID);
                                    notificationIntent.putExtra("expenseID", expenseID);
                                    notificationIntent.putExtra("contestationID", contestationID);

                                    PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT);
                                    android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                                            .setSmallIcon(R.drawable.added_comment)
                                            .setContentTitle(userName + " has commented a contestation over an expense") // mi servirebbe il nome della spesa :(
                                            .setContentText(commento)
                                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                            .setContentIntent(contentIntent);
                                    Notification noti = builder.build();
                                    noti.flags = Notification.FLAG_AUTO_CANCEL;
                                    // Add notification
                                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    manager.notify((int) System.currentTimeMillis(), noti);
                                }
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });



        groupsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {

                // Collego un listener sotto la voce "spese" per rilevare l'eventuale rimozione di una spesa da questo gruppo in futuro
                DatabaseReference groupExpenses = (DatabaseReference) database.getReference().child("groups_prova").child(dataSnapshot.getKey()).child("spese");
                groupExpenses.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(final DataSnapshot dataSnapshots, String s) {
                        // Il codice relativo alla notifica dell'aggiunta di una spesa puo' essere spostato quì
                        System.out.println("groupdExpenses aggiunta spesa " + dataSnapshots);
                        // rilevo la rimozione della spesa dal fatto che alcune voci, tra cui "contestazioni" viene eliminata
                        if(!dataSnapshots.hasChild("cat")) {
                            // riempio i partecipanti del gruppo per poter fare l'intent all'activit giusta
                            final Gruppo g = dataSnapshot.getValue(Gruppo.class);
                            final ArrayList<String> numbers = new ArrayList<String>();
                            final HashMap<String, Persona> partecipanti = new HashMap<String, Persona>();

                            numbers.addAll(g.getPartecipanti_numero_cnome().keySet());
                            DatabaseReference users_prova = database.getReference().child("users_prova");

                            users_prova.addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(DataSnapshot dataSnapshot2) {
                                       Iterator<DataSnapshot> it = dataSnapshot2.getChildren().iterator();

                                       while (it.hasNext()) {
                                           DataSnapshot ds = it.next();
                                           if (numbers.contains(ds.getKey())) {
                                               Persona p = ds.getValue(Persona.class);
                                               partecipanti.put(p.getTelephone(), p);
                                           }
                                       }
                                       g.setPartecipanti_3(partecipanti);
                                       g.setUser(SliceAppDB.getUser());

                                       Long deletedExpenseTimestamp = dataSnapshots.child("c").getValue(Long.class);
                                       if(deletedExpenseTimestamp > lastTimestampDeletedExpense){
                                           lastTimestampDeletedExpense = deletedExpenseTimestamp;
                                           prefEditor = sharedPref.edit();
                                           prefEditor.putLong("lastTimestampDeletedExpense", lastTimestampDeletedExpense);
                                           prefEditor.commit();

                                           Intent notificationIntent = new Intent(getApplicationContext(), ExpensesActivity.class);
                                          // Gruppo g = dataSnapshot.getValue(Gruppo.class);
                                           notificationIntent.putExtra("Gruppo", g);
                                           PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
                                                   PendingIntent.FLAG_UPDATE_CURRENT);
                                           android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                                                   .setSmallIcon(R.drawable.img_removed)
                                                   .setContentTitle("expense " + dataSnapshots.child("removed_msg").getValue(String.class) + " from group " + dataSnapshot.child("groupName").getValue(String.class))
                                                   .setContentText("remover: " + dataSnapshots.child("remover").getValue(String.class))
                                                   .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                                   .setContentIntent(contentIntent);
                                           Notification noti = builder.build();
                                           noti.flags = Notification.FLAG_AUTO_CANCEL;
                                           // Add notification
                                           NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                           manager.notify((int) System.currentTimeMillis(), noti);
                                       }
                                   }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                        // è stata aggiunta una nuova spesa
                        else {

                            final Gruppo g = dataSnapshot.getValue(Gruppo.class);
                            final ArrayList<String> numbers = new ArrayList<String>();
                            final HashMap<String, Persona> partecipanti = new HashMap<String, Persona>();

                            numbers.addAll(g.getPartecipanti_numero_cnome().keySet());
                            DatabaseReference users_prova = database.getReference().child("users_prova");

                            users_prova.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot2) {
                                    Iterator<DataSnapshot> it = dataSnapshot2.getChildren().iterator();

                                    while (it.hasNext()) {
                                        DataSnapshot ds = it.next();
                                        if (numbers.contains(ds.getKey())) {
                                            Persona p = ds.getValue(Persona.class);
                                            partecipanti.put(p.getTelephone(), p);
                                        }
                                    }
                                    g.setPartecipanti_3(partecipanti);
                                    g.setUser(SliceAppDB.getUser());

                                    long expenseTimestamp = (long) dataSnapshots.child("c").getValue();
                                    final String expenseName = (String) dataSnapshots.child("nome_spesa").getValue();
                                    final String groupName = (String) dataSnapshot.child("groupName").getValue();
                                    String usernamePagante = (String) dataSnapshots.child("pagante").child("username").getValue();
                                    String telephonePagante = (String) dataSnapshots.child("pagante").child("telephone").getValue();

                                    if (expenseTimestamp > lastTimestampExpense) {
                                        lastTimestampExpense = expenseTimestamp;
                                        prefEditor = sharedPref.edit();
                                        prefEditor.putLong("lastTimestampExpense", lastTimestampExpense);
                                        prefEditor.commit();
                                        // notifico aggiunta della spesa
                                        if(!telephonePagante.equals(userTelephone)){
                                            Intent notificationIntent = new Intent(getApplicationContext(), ExpenseDetails.class);
                                            Spesa spesa = dataSnapshots.getValue(Spesa.class);
                                           // Gruppo g = dataSnapshot.getValue(Gruppo.class);
                                            notificationIntent.putExtra("Spesa", spesa);
                                            notificationIntent.putExtra("Gruppo", g);
                                            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
                                                    PendingIntent.FLAG_UPDATE_CURRENT);
                                            android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                                                    .setSmallIcon(R.drawable.added_expense)
                                                    .setContentTitle("Expense " + expenseName + " has been added to the group " + groupName)
                                                    .setContentText("Payer is " + usernamePagante)
                                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                                    .setContentIntent(contentIntent);
                                            Notification noti = builder.build();
                                            noti.flags = Notification.FLAG_AUTO_CANCEL;
                                            // Add notification
                                            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                            manager.notify((int) System.currentTimeMillis(), noti);
                                        }

                                        // attacco un listener per ogni divisione
                                        final DatabaseReference divisioniRef = dataSnapshots.child("divisioni").getRef();
                                        divisioniRef.addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                System.out.println("division added" + dataSnapshot);
                                            }
                                            @Override
                                            public void onChildChanged(final DataSnapshot dataSnapshotd, String s) {
                                                System.out.println("division changed" + dataSnapshotd);
                                                // La notifica arriva a coloro che fanno parte del gruppo
                                                final String user2 = (String) dataSnapshotd.child("persona").child("username").getValue();
                                                final double importo = dataSnapshotd.child("importo").getValue(Double.class);

                                                if(dataSnapshots.child("divisioni").hasChild(userTelephone)){
                                                    if(dataSnapshotd.child("haPagato").getValue(Boolean.class)) {
                                                        Intent notificationIntent = new Intent(getApplicationContext(), Group_balance.class);
                                                        //Spesa spesa = dataSnapshots.getValue(Spesa.class);
                                                      //  Gruppo g = dataSnapshot.getValue(Gruppo.class);
                                                        //notificationIntent.putExtra("Spesa", spesa);
                                                        notificationIntent.putExtra("Gruppo", g);
                                                        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
                                                                PendingIntent.FLAG_UPDATE_CURRENT);
                                                        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                                                                .setSmallIcon(R.drawable.expense_paid)
                                                                .setContentTitle("The user " + user2 + " has paid his part (" + importo + ")")
                                                                .setContentText("expense " + expenseName + " - group " + groupName)
                                                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                                                .setContentIntent(contentIntent);

                                                        Notification noti = builder.build();
                                                        noti.flags = Notification.FLAG_AUTO_CANCEL;
                                                        // Add notification
                                                        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                                        manager.notify((int) System.currentTimeMillis(), noti);
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onChildRemoved(DataSnapshot dataSnapshot) {}
                                            @Override
                                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {}
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshote, String s) {}
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                // riempio i partecipanti del gruppo per poter fare l'intent all'activit giusta
                final Gruppo g = dataSnapshot.getValue(Gruppo.class);
                final ArrayList<String> numbers = new ArrayList<String>();
                final HashMap<String, Persona> partecipanti = new HashMap<String, Persona>();

                numbers.addAll(g.getPartecipanti_numero_cnome().keySet());
                DatabaseReference users_prova = database.getReference().child("users_prova");

                users_prova.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot2) {
                        Iterator<DataSnapshot> it = dataSnapshot2.getChildren().iterator();

                        while (it.hasNext()) {
                            DataSnapshot ds = it.next();
                            if (numbers.contains(ds.getKey())) {
                                Persona p = ds.getValue(Persona.class);
                                partecipanti.put(p.getTelephone(), p);
                            }
                        }
                        g.setPartecipanti_3(partecipanti);
                        g.setUser(SliceAppDB.getUser());

                        // Mando la notifica solo se l'utente da parte del gruppo
                        final long groupTimestamp = (long) dataSnapshot.child("c").getValue();
                        if(dataSnapshot.child("partecipanti_numero_cnome").hasChild(userTelephone)){
                            if(groupTimestamp > lastTimestampGroup){
                                lastTimestampGroup = groupTimestamp;
                                prefEditor = sharedPref.edit();
                                prefEditor.putLong("lastTimestampGroup", lastTimestampGroup);
                                prefEditor.commit();
                                String groupName = (String) dataSnapshot.child("groupName").getValue();

                                // creation of the notification
                                // la notifica non deve arrivare al creatore del gruppo
                                String groupCreatorTelephone = (String) dataSnapshot.child("groupCreator").getValue();
                                if(!groupCreatorTelephone.equals(userTelephone)){
                                    Intent notificationIntent = new Intent(getApplicationContext(), ExpensesActivity.class);
                                    notificationIntent.putExtra("Gruppo",g);

                                    PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT);
                                    android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                                            .setSmallIcon(R.drawable.added_to_group)
                                            .setContentTitle("You have been added to a new group!")
                                            .setContentText(groupName)
                                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                            //.setPriority(NotificationCompat.PRIORITY_HIGH)
                                            .setContentIntent(contentIntent);
                                    Notification noti = builder.build();
                                    noti.flags = Notification.FLAG_AUTO_CANCEL;
                                    // Add notification
                                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    manager.notify((int) System.currentTimeMillis(), noti);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                // Questo modo di cancellare la spesa non funziona più perché la spesa non viene eleminata dal database
                // L'idea è aggiungere un listener quando viene creato un gruppo, alla voce "spese" sotto ogni "groupID" e lavorare su onChildChanged

                /////////////////////// Caso aggiunta spesa /////////////////////////////
                // invio la notifica solo a chi appartiene a gruppo
                /*
                if(dataSnapshot.child("partecipanti_numero_cnome").hasChild(userTelephone)) {
                    Iterator<DataSnapshot> expenses = dataSnapshot.child("spese").getChildren().iterator();
                    //Individuo la spesa che è stata aggiunta
                    while (expenses.hasNext()) {
                        final DataSnapshot currentExpense = expenses.next();
                        long expenseTimestamp = (long) currentExpense.child("c").getValue();
                        Spesa spesa = currentExpense.getValue(Spesa.class);
                        final String expenseID = currentExpense.getKey();
                        final String groupID = (String) currentExpense.child("gruppo").getValue();
                        final String expenseName = (String) currentExpense.child("nome").getValue();
                        final String groupName = (String) dataSnapshot.child("groupName").getValue();
                        String usernamePagante = (String) currentExpense.child("pagante").child("username").getValue();
                        String telephonePagante = (String) currentExpense.child("pagante").child("telephone").getValue();

                        if (expenseTimestamp > lastTimestampExpense) {
                            lastTimestampExpense = expenseTimestamp;
                            prefEditor = sharedPref.edit();
                            prefEditor.putLong("lastTimestampExpense", lastTimestampExpense);
                            prefEditor.commit();
                            // attacco un listener per ogni divisione
                            final DatabaseReference divisioniRef = currentExpense.child("divisioni").getRef();
                            divisioniRef.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {}
                                @Override
                                public void onChildChanged(final DataSnapshot dataSnapshot, String s) {
                                    // La notifica arriva a coloro che fanno parte del gruppo
                                    final String user2 = (String) dataSnapshot.child("persona").child("username").getValue();
                                    final double importo = dataSnapshot.child("importo").getValue(Double.class);
                                    divisioniRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot ds) {
                                            if(ds.hasChild(userTelephone)){
                                                if((Boolean) dataSnapshot.child("haPagato").getValue() == true) {
                                                    Intent notificationIntent = new Intent(getApplicationContext(), SplashScreen.class);
                                                    PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
                                                            PendingIntent.FLAG_UPDATE_CURRENT);
                                                    android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                                                            .setSmallIcon(R.drawable.expense_paid)
                                                            .setContentTitle("The user " + user2 + " has paid his part (" + importo + ")")
                                                            .setContentText("expense " + expenseName + " - group " + groupName)
                                                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                                            //.setPriority(NotificationCompat.PRIORITY_HIGH)
                                                            .setContentIntent(contentIntent);

                                                    Notification noti = builder.build();
                                                    noti.flags = Notification.FLAG_AUTO_CANCEL;
                                                    // Add notification
                                                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                                    manager.notify((int) System.currentTimeMillis(), noti);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {}
                                    });
                                }
                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });

                            //Riempimento della mappa di gestione dei pagamenti dell'utente i-esimo.


                            if(groupsID.contains(groupID)){//è stata aggiunta una nuova spesa al gruppo appena creato
                                ArrayList<String> listaSpese = new ArrayList<String>();
                                listaSpese.add(expenseID + " " + expenseName);
                                groupsExpenses.put(groupID, listaSpese);
                                groupsID.remove(groupID);
                            }else groupsExpenses.get(groupID).add(expenseID + " " + expenseName);

                            if(!telephonePagante.equals(userTelephone)){
                                Intent notificationIntent = new Intent(getApplicationContext(), SplashScreen.class);
                                PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);
                                android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon(R.drawable.added_expense)
                                        .setContentTitle("An expense has been added to the group " + groupName)
                                        .setContentText(expenseName + " - Payer is " + usernamePagante)
                                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                        //.setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setContentIntent(contentIntent);
                                Notification noti = builder.build();
                                noti.flags = Notification.FLAG_AUTO_CANCEL;
                                // Add notification
                                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                manager.notify((int) System.currentTimeMillis(), noti);
                            }
                            break;
                        }
                    }
                }
                */
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        groupsRef.addChildEventListener(groupsListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}
