package it.polito.mad17.viral.sliceapp;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Group_balance extends AppCompatActivity {

    Gruppo gruppo;
    Persona user = SliceAppDB.getUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
    private DatabaseReference rootRef = database.getReference();
    private DatabaseReference groups_ref = rootRef.child("groups_prova");
    private DatabaseReference user_ref = rootRef.child("users_prova");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_balance);

        final RecyclerView mylist = (RecyclerView) findViewById(R.id.balance_list);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            this.gruppo = (Gruppo) extra.getSerializable("Gruppo");

        }


        TextView total = (TextView) findViewById(R.id.total_show);

        Double f = 0d;
        for (Spesa s : gruppo.getSpese().values()) {
            if(s.getImporto()>=0)
            f += s.getImporto();

        }

        String str = String.format("%." + gruppo.getCurr().getDigits() + "f", f);

        total.setText(str+" "+gruppo.getCurr().getSymbol());

        Query ref = groups_ref.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(SliceAppDB.getUser().getTelephone()).child("bilancio_relativo");

        FirebaseRecyclerAdapter<Riga_bilancio_personalizzata,BilancioHolder> adapter= new FirebaseRecyclerAdapter<Riga_bilancio_personalizzata, BilancioHolder>(Riga_bilancio_personalizzata.class, R.layout.group_balance_row, BilancioHolder.class, ref) {
            @Override
            protected void populateViewHolder(BilancioHolder viewHolder, final Riga_bilancio_personalizzata model, int position) {


                viewHolder.namep.setText(model.getNome());
                String str = String.format("%."+gruppo.getCurr().getDigits()+"f",model.calculate());
                viewHolder.money.setText(str+" "+gruppo.getCurr().getSymbol());
                if(model.calculate().compareTo(0d)<0)
                    viewHolder.money.setTextColor(getResources().getColor(R.color.debiti));
                else
                    viewHolder.money.setTextColor(getResources().getColor(R.color.colorPrimary));

                viewHolder.balance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SharedPreferences sharedPref = getSharedPreferences("data",MODE_PRIVATE);
                        final String userTelephone = sharedPref.getString("telefono", null);

                        groups_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                HashMap<String,Boolean> contested = new HashMap<String, Boolean>();
                                contested = (HashMap<String, Boolean>) dataSnapshot.child(gruppo.getGroupID()).child("contested").getValue();
                                Boolean atLeastOne=false;

                                for(Boolean b: contested.values())
                                        if(b==true){
                                            atLeastOne=true;
                                        }

                                if(!atLeastOne){
                                    for(Spesa s: gruppo.getSpese().values() ){
                                        if(!s.getRemoved()) {
                                            if (s.getPagante().getTelephone().equals(userTelephone))
                                                groups_ref.child(gruppo.getGroupID()).child("spese").child(s.getExpenseID()).child("divisioni").child(model.getTel()).child("haPagato").setValue(true);
                                            else if (s.getPagante().getTelephone().equals(model.getTel()))
                                                groups_ref.child(gruppo.getGroupID()).child("spese").child(s.getExpenseID()).child("divisioni").child(userTelephone).child("haPagato").setValue(true);

                                            groups_ref.child(gruppo.getGroupID()).child("spese").child(s.getExpenseID()).child("fullypayed").child(model.getTel()).setValue(1);
                                        }
                                    }

                                    String key_es= groups_ref.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(model.getTel()).child("bilancio_relativo").child(userTelephone).child("importo").push().getKey();
                                    if(model.calculate()!=0){

                                        if(model.calculate()<0) {

                                            //vado nel mio bilancio e gli butto un *-1
                                            //Lui vede un - quindi a lui metto il prezzo giusto
                                            groups_ref.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(model.getTel()).child("bilancio_relativo").child(userTelephone).child("importo").child(key_es).setValue((model.calculate()));
                                            groups_ref.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(userTelephone).child("bilancio_relativo").child(model.getTel()).child("importo").child(key_es).setValue((model.calculate()) * -1);
                                            user_ref.child(userTelephone).child("amici").child(model.getTel()+";"+gruppo.getCurr().getChoosencurr()).child("importo").child(key_es).setValue(model.calculate()*-1);
                                            user_ref.child(model.getTel()).child("amici").child(userTelephone+";"+gruppo.getCurr().getChoosencurr()).child("importo").child(key_es).setValue(model.calculate());
                                            Balance_transaction bt = new Balance_transaction(gruppo.getGroupID(),model.getTel(),userTelephone,model.calculate());
                                        }
                                        else {
                                            groups_ref.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(model.getTel()).child("bilancio_relativo").child(userTelephone).child("importo").child(key_es).setValue((model.calculate()));
                                            groups_ref.child(gruppo.getGroupID()).child("dettaglio_bilancio").child(userTelephone).child("bilancio_relativo").child(model.getTel()).child("importo").child(key_es).setValue((model.calculate()) * -1);
                                            user_ref.child(userTelephone).child("amici").child(model.getTel()+";"+gruppo.getCurr().getChoosencurr()).child("importo").child(key_es).setValue(model.calculate()*-1);
                                            user_ref.child(model.getTel()).child("amici").child(userTelephone+";"+gruppo.getCurr().getChoosencurr()).child("importo").child(key_es).setValue(model.calculate());
                                            Balance_transaction bt = new Balance_transaction(gruppo.getGroupID(),userTelephone,model.getTel(),model.calculate()*-1);
                                        }


                                    }
                                } else {

                                    Toast.makeText(getApplicationContext(),
                                            "A group has as least one contested expense. You cannot balance",
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                                // bisogna mettere "contested" a false, quando l'ultima spesa non è più contestata
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });

                        }
                });



            }

        };

        LinearLayoutManager llm = new LinearLayoutManager(getBaseContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mylist.setLayoutManager(llm);
        DividerItemDecoration verticalDecoration = new DividerItemDecoration(mylist.getContext(), DividerItemDecoration.VERTICAL);
        Drawable verticalDivider = getBaseContext().getDrawable(R.drawable.horizontal_divider);
        verticalDecoration.setDrawable(verticalDivider);
        mylist.addItemDecoration(verticalDecoration);
        mylist.setAdapter(adapter);//bb



}

    public static class BilancioHolder extends RecyclerView.ViewHolder {

        TextView namep;
        TextView money;
        Button balance;

        public BilancioHolder(View itemView) {
            super(itemView);
            namep = (TextView) itemView.findViewById(R.id.name_person);
            money = (TextView) itemView.findViewById(R.id.money_balance);
            balance = (Button) itemView.findViewById(R.id.button2);


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
