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

    //private Persona currentUser = SliceAppDB.getUser();

    @Nullable
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        DatabaseReference groupsRef = database.getReference().child("groups_prova");

        groupsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                System.out.println("onChildAdded " + dataSnapshot);


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
                // invio la notifica solo a chi appartiene a gruppo
                if(dataSnapshot.child("partecipanti_numero_cnome").hasChild(SliceAppDB.getUser().getTelephone())) {
                    Iterator<DataSnapshot> expenses = dataSnapshot.child("spese").getChildren().iterator();
                    //Individuo la spesa che è stata aggiunta
                    while (expenses.hasNext()) {
                        DataSnapshot currentExpense = expenses.next();
                        long expenseTimestamp = (long) currentExpense.child("c").getValue();
                        if (expenseTimestamp > lastTimestampExpense) {
                            lastTimestampExpense = expenseTimestamp;

                            String expenseName = (String) currentExpense.child("nome").getValue();
                            String groupName = (String) dataSnapshot.child("groupName").getValue();
                            String usernamePagante = (String) currentExpense.child("pagante").child("username").getValue();
                            String telephonePagante = (String) currentExpense.child("pagante").child("telephone").getValue();

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
