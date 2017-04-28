package it.polito.mad17.viral.sliceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.


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
            long telefono = sharedPref.getLong("telefono", 0);
            Persona p = new Persona(nome, cognome, username, dob, telefono);
            SliceAppDB.setUser(p);

            Intent i = new Intent(SplashScreen.this, List_Pager_Act.class);
            startActivity(i);
            finish();
        }
    }
}
