package it.polito.mad17.viral.sliceapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;


public class SplashScreen extends AppCompatActivity {
    Persona user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        AlphaAnimation animation = new AlphaAnimation(0.3f,1.5f);
        animation.setDuration(900);
        animation.setRepeatCount(Animation.INFINITE);
        ImageView logo = (ImageView)  findViewById(R.id.logo_to_fade);
        ColorFilter filter = new LightingColorFilter(Color.rgb(249,105,14), Color.rgb(249,105,14));
        logo.setColorFilter(filter);

        logo.setAlpha(1f);
        logo.startAnimation(animation);

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
            String telefono = sharedPref.getString("telefono", null);

            final FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
            final DatabaseReference rootRef = database.getReference();
            final DatabaseReference user_ref = rootRef.child("users_prova");

            user_ref.child(telefono).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    user = dataSnapshot.getValue(Persona.class);
                    SliceAppDB.setUser(user);
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


