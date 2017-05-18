package it.polito.mad17.viral.sliceapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by abdel on 16/05/2017.
 */

public class FirebaseBackgroundService extends Service {

    // TODO notifiche per aggiunta spesa, aggiunta gruppi, rimozione spesa, pagato spesa normale
    // onChild Changed scatta sia quando aggiungo una spesa sia quando un utente aga la tua parte
    // e devo trovare un modo per differenziare il secondo dal primo ( la notifica per il secondo è da implementare)

    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
    private ChildEventListener groupsListener;
    private Long lastTimestampGroup = System.currentTimeMillis();
    private Long lastTimestampExpense = System.currentTimeMillis();
    private Map<String, List<String>> groupsExpenses = new HashMap<String, List<String>>();
    private ArrayList <String> groupsID = new ArrayList<String>();
    //private Persona currentUser = SliceAppDB.getUser();

    @Nullable
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        groupsExpenses = new HashMap<String, List<String>>();

        DatabaseReference groupsRef = database.getReference().child("groups_prova");

        groupsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                System.out.println("onChildAdded " + dataSnapshot);

                // riempio ed eventualmente sovrascrivo la hashMap
                String groupID = dataSnapshot.getKey();
                if(!groupsExpenses.containsKey(groupID)){//ho creato un nuovo gruppo
                    groupsID.add(groupID);//gruppi appena creati aventi 0 spese.
                }

                long groupTimestamp = (long) dataSnapshot.child("c").getValue();

                // Mando la notifica solo se l'utente da parte del gruppo
                if(dataSnapshot.child("partecipanti_numero_cnome").hasChild(SliceAppDB.getUser().getTelephone())){
                    if(groupTimestamp > lastTimestampGroup){
                        lastTimestampGroup = groupTimestamp;
                        String groupName = (String) dataSnapshot.child("groupName").getValue();

                        // creation of the notification
                        // la notifica non deve arrivare al creatore del gruppo
                        String groupCreatorTelephone = (String) dataSnapshot.child("groupCreator").getValue();
                        if(!groupCreatorTelephone.equals(SliceAppDB.getUser().getTelephone())){
                            Intent notificationIntent = new Intent(getApplicationContext(), SplashScreen.class);
                            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                            android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.added_to_group)
                                    .setContentTitle("You have been added to a new group!")
                                    .setContentText(groupName)
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
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                System.out.println("onChildChanged " + dataSnapshot);

                String groupId = dataSnapshot.getKey();
                String gName = (String) dataSnapshot.child("groupName").getValue();

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
                                .setContentIntent(contentIntent);
                        Notification noti = builder.build();
                        noti.flags = Notification.FLAG_AUTO_CANCEL;
                        // Add notification
                        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        manager.notify((int) System.currentTimeMillis(), noti);

                    }
                }

                // invio la notifica solo a chi appartiene a gruppo
                if(dataSnapshot.child("partecipanti_numero_cnome").hasChild(SliceAppDB.getUser().getTelephone())) {
                    Iterator<DataSnapshot> expenses = dataSnapshot.child("spese").getChildren().iterator();
                    //Individuo la spesa che è stata aggiunta
                    while (expenses.hasNext()) {
                        DataSnapshot currentExpense = expenses.next();
                        long expenseTimestamp = (long) currentExpense.child("c").getValue();
                        if (expenseTimestamp > lastTimestampExpense) {
                            lastTimestampExpense = expenseTimestamp;

                            String expenseID = currentExpense.getKey();
                            String groupID = (String) currentExpense.child("gruppo").getValue();
                            String expenseName = (String) currentExpense.child("nome").getValue();
                            String groupName = (String) dataSnapshot.child("groupName").getValue();
                            String usernamePagante = (String) currentExpense.child("pagante").child("username").getValue();
                            String telephonePagante = (String) currentExpense.child("pagante").child("telephone").getValue();

                            if(groupsID.contains(groupID)){//è stata aggiunta una nuova spesa al gruppo appena creato
                                ArrayList<String> listaSpese = new ArrayList<String>();
                                listaSpese.add(expenseID + " " + expenseName);
                                groupsExpenses.put(groupID, listaSpese);
                                groupsID.remove(groupID);
                            }else groupsExpenses.get(groupID).add(expenseID + " " + expenseName);
                            // aggiungo la spesa alla lista di spese del gruppo nella  hashmap
                           // if(!groupsExpenses.containsKey(groupID)){
                            //    ArrayList<String> listaSpese = new ArrayList<String>();
                             //   listaSpese.add(expenseID);
                              //  groupsExpenses.put(groupID, listaSpese);
                            //}
                           // else
                            //    groupsExpenses.get(groupID).add(expenseID);
                            // la notifica arriva a tutti tranne che al pagante
                            if(!telephonePagante.equals(SliceAppDB.getUser().getTelephone())){
                                Intent notificationIntent = new Intent(getApplicationContext(), SplashScreen.class);
                                PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);
                                android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon(R.drawable.added_expense)
                                        .setContentTitle("An expense has been added to the group " + groupName)
                                        .setContentText(expenseName + " - Payer is " + usernamePagante)
                                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
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
    }
}
