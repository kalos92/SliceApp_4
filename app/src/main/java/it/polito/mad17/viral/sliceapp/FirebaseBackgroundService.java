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
    private Long lastTimestampGroup = System.currentTimeMillis();
    private Long lastTimestampExpense  = System.currentTimeMillis();
    private Long lastTimestampContestation = System.currentTimeMillis();
    private Long lastTimestampComment  = System.currentTimeMillis();

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
        System.out.println("lastTimestampGroup" + lastTimestampGroup);
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

        final String userTelephone = sharedPref.getString("telefono", null);

        DatabaseReference groupsRef = database.getReference().child("groups_prova");

        final DatabaseReference contestationsRef = database.getReference().child("users_prova").child(userTelephone).child("contestazioni");
        contestationsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

              //  Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
            //    while(iterator.hasNext()) {
                  //  DataSnapshot contest = iterator.next();
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
              //  }
                // add listener for comment
                contestationsRef.child(dataSnapshot.getKey()).child("commenti").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        long commentTimestamp = (long) dataSnapshot.child("timestamp").getValue();

                        if(commentTimestamp > lastTimestampComment) {
                            if(!dataSnapshot.child("userID").getValue(String.class).equals(userTelephone)){

                                lastTimestampComment = commentTimestamp;
                                prefEditor = sharedPref.edit();
                                prefEditor.putLong("lastTimestampComment", lastTimestampComment);
                                prefEditor.commit();
                                String userName = (String) dataSnapshot.child("userName").getValue();
                                String commento = (String) dataSnapshot.child("commento").getValue();


                                // notification
                                Intent notificationIntent = new Intent(getApplicationContext(),CommentsActivity.class);
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
                System.out.println("onChildAdded " + dataSnapshot);

                // riempio ed eventualmente sovrascrivo la hashMap
                String groupID = dataSnapshot.getKey();
                if(!groupsExpenses.containsKey(groupID)){//ho creato un nuovo gruppo
                    groupsID.add(groupID);//gruppi appena creati aventi 0 spese.
                }
                final long groupTimestamp = (long) dataSnapshot.child("c").getValue();
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
                                    notificationIntent.putExtra("Gruppo",g);//ciao

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
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                System.out.println("onChildChanged " + dataSnapshot);

                String groupId = dataSnapshot.getKey();
                String gName = (String) dataSnapshot.child("groupName").getValue();

                ///////////////////////// Caso: rimozione spesa /////////////////////////////////////////
                long numSpeseRemote = dataSnapshot.child("spese").getChildrenCount(); // dovrebbe essere diminuito
                DataSnapshot speseRemote = dataSnapshot.child("spese");

                List<String> listSpese = groupsExpenses.get(groupId);
                if(listSpese != null){
                    int numSpeseLocali = listSpese.size();
                    if(numSpeseRemote < numSpeseLocali){ // la spesa eliminata viene notificata anche all pagante
                        String expenseName = null;
                        for(String spesaIdPlusName : listSpese){
                            String[] t = spesaIdPlusName.split(" ");
                            String spesaId = t[0];
                            if(!speseRemote.hasChild(spesaId)) {
                                groupsExpenses.get(groupId).remove(spesaIdPlusName);
                                expenseName = t[1];
                            }
                        }

                        Intent notificationIntent = new Intent(getApplicationContext(), SplashScreen.class);
                        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.deleted_expense)
                                .setContentTitle("An expense has been removed from ")
                                .setContentText( expenseName + " - group " + gName) // non riusciamo a ricavare il nome della spesa
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

                /////////////////////// Caso aggiunta spesa /////////////////////////////
                // invio la notifica solo a chi appartiene a gruppo
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
