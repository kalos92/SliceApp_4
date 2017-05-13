package it.polito.mad17.viral.sliceapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

public class ExpensesActivity extends AppCompatActivity {

    Gruppo gruppo;
    Persona user;
    FragmentManager fm;
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
    private DatabaseReference rootRef = database.getReference();
    private DatabaseReference groups_ref = rootRef.child("groups_prova");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription("SliceApp",null, getResources().getColor(R.color.colorPrimary));
        ((Activity)this).setTaskDescription(taskDescription);

        Bundle extra =getIntent().getExtras();
        if(extra!= null) {

            gruppo = (Gruppo) extra.get("Gruppo");
            user = (Persona) extra.get("User");
        }

        fm= getSupportFragmentManager();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment, Fragment_of_money.newInstance(gruppo));
        ft.addToBackStack(null);
        ft.commit();

        Toolbar t = (Toolbar)findViewById(R.id.expenseToolbar);
        t.setTitle(" " + gruppo.getGroupName());
        t.setSubtitle(" " + gruppo.obtainUser().getUsername());
        BitmapManager  bm = new BitmapManager(this,gruppo.getImg(),50,70);

        Bitmap b=  bm.scaleDown(gruppo.getImg(),100,true);
        Drawable d = new BitmapDrawable(getResources(), b);
        t.setLogo(d);
        final ListView mlist = (ListView) findViewById(R.id.listView2);

        final ArrayList<Spesa> speseGruppo = new ArrayList<Spesa>();
        /*for(Spesa s : SliceAppDB.getListaSpese()){
            if(s.getGruppo().getGroupID().equals(gruppo.getGroupID()))
                speseGruppo.add(s);
        }*/
        for(Spesa s : SliceAppDB.getMappaSpese().values()){
            if(s.getGruppo().equals(gruppo.getGroupID()))
                speseGruppo.add(s);
        }

        Query ref = groups_ref.child(gruppo.getGroupID()).child("listaSpeseGruppo");

        FirebaseListAdapter<Spesa> adapter= new FirebaseListAdapter<Spesa>(this, Spesa.class, R.layout.listview_expense_row, ref) {
            @Override
            protected void populateView(View v, Spesa model, int position) {

                ImageView imgIcon = (ImageView) v.findViewById(R.id.expIcon);
                TextView title = (TextView)v.findViewById(R.id.expName);
                TextView price = (TextView)v.findViewById(R.id.expPrice);
                TextView buyer = (TextView)v.findViewById(R.id.buyer);
                TextView currency = (TextView)v.findViewById(R.id.expCurrency);



                if(!model.getDivisioni().get(user.getTelephone()).getHaPagato())
                    v.setBackgroundColor(getBaseContext().getResources().getColor(R.color.row_non_pagate_bck));
                else
                    v.setBackgroundColor(getBaseContext().getResources().getColor(R.color.tab_bck));





                title.setText(model.getNome());
                imgIcon.setImageResource(model.getCat().getImg());
                currency.setText("€");
                price.setText(Double.toString(model.getImporto()));
                buyer.setText(model.getPagante().getName()+" "+model.getPagante().getSurname());

            }
        };


        final ExpensesAdapter adapter_2 = new ExpensesAdapter(ExpensesActivity.this, R.layout.listview_expense_row, speseGruppo, user);
        mlist.setAdapter(adapter);

        // What to do when user press the toolbar back button
        Toolbar toolbar = (Toolbar) findViewById(R.id.expenseToolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // What to do when the user long press an item from the expenses list
        mlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ExpensesActivity.this);
                builder.setTitle("Deleting expense");
                builder.setMessage("Are you sure you want to delete this expense?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        final Spesa sp = (Spesa) mlist.getAdapter().getItem(position);

                        boolean tuttiPagato = true; //tutti hanno pagato
                        if (sp.getPagante().getTelephone().equals(SliceAppDB.getUser().getTelephone())) {
                            // controllo se c'è qualcuno che non ha pagato
                            for (Soldo s : sp.getDivisioni().values()) {
                                if (!s.getHaPagato()) {
                                    tuttiPagato = false; //c'è almeno un user che non ha pagato.
                                    break;
                                }
                            }

                            if (tuttiPagato) {
                                final String expenseID = sp.getExpenseID();

                                // eliminazione spesa dal database locale
                                SliceAppDB.getMappaSpese().remove(expenseID);
                                for (Spesa s : SliceAppDB.getListaSpese()) {
                                    if (s.getExpenseID().equals(sp.getExpenseID())) {
                                        SliceAppDB.getGruppi().get(s.getGruppo()).getSpese().remove(sp.getExpenseID());
                                        SliceAppDB.getListaSpese().remove(s);
                                        break;
                                    }
                                }

                                final DatabaseReference databaseRef = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/").getReference();
                                final DatabaseReference expensesRef = databaseRef.child("expenses");

                                expensesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.getChildrenCount() == 1){
                                            expensesRef.child(expenseID).removeValue(); // rimuovo l'unica spesa che esiste
                                            databaseRef.child("expenses").setValue(""); // e ricreo la chiave "expenses"
                                        } else
                                            expensesRef.child(expenseID).removeValue();
                                        // Ricarico i dati
                                        Intent i = new Intent(ExpensesActivity.this, SplashScreen.class);
                                        startActivity(i);
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {}
                                });


                            } else
                                Toast.makeText(ExpensesActivity.this, "You can't delete this expense because someone has not paid it its part yet", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(ExpensesActivity.this, "You are not the payer and you can not remove this expense", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //dialog.cancel();
                        Toast.makeText(ExpensesActivity.this,"You click on no",Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });

        BottomNavigationView bottomBar = (BottomNavigationView)findViewById(R.id.bottom_nav_bar);
        bottomBar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent appInfo= new Intent(ExpensesActivity.this, AddExpenseActivity.class);
                        appInfo.putExtra("Gruppo",gruppo);
                        appInfo.putExtra("User",user);
                        ExpensesActivity.this.startActivity(appInfo);
                        finish();
                        return false;
                }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}