package it.polito.mad17.viral.sliceapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Group_balance extends AppCompatActivity {

    Gruppo gruppo;
    Persona user = SliceAppDB.getUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_balance);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            this.gruppo = (Gruppo) extra.getSerializable("Gruppo");

        }


        TextView total = (TextView) findViewById(R.id.total_show);

        Double f = 0d;
        for (Spesa s : gruppo.getSpese().values()) {
            f += s.getImporto();

        }

        String str = String.format("%." + gruppo.getCurr().getDigits() + "f", f);

        total.setText(str);





    final Button balance_all = (Button) findViewById(R.id.button_bal);

        balance_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final HashMap<String,HashMap<String,Riga_bilancio_personalizzata>> balance_map = new HashMap<>();

                final FirebaseDatabase database =FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
                final DatabaseReference groups_prova_2 = database.getReference().child("groups_prova");

                groups_prova_2.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(user.getTelephone()).child("bilancio_relativo").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        balance_map.putAll((Map<String,HashMap<String, Riga_bilancio_personalizzata>>) dataSnapshot.getValue());

                        Log.d("E guardala sta mappa", balance_map.toString());

                        for(String persona : balance_map.keySet()){

                            Double f=0d;

                            for(Riga_bilancio_personalizzata rbp: balance_map.get(persona).values()){
                                f+=rbp.getImporto();
                            }

                        String key = groups_prova_2.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(user.getTelephone()).toString();//TODO

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

}
}
