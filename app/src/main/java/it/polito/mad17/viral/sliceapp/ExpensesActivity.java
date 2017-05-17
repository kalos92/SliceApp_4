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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

public class ExpensesActivity extends AppCompatActivity implements View.OnClickListener {

    Gruppo gruppo;
    Persona user;
    FragmentManager fm;
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
    private DatabaseReference rootRef = database.getReference();
    private DatabaseReference groups_ref = rootRef.child("groups_prova");
    private DatabaseReference users_prova= rootRef.child("users_prova");
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2,fab3;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    //private TextView tv1,tv2,tv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription("SliceApp",null, getResources().getColor(R.color.colorPrimary));
        ((Activity)this).setTaskDescription(taskDescription);

        Bundle extra =getIntent().getExtras();
        if(extra!= null) {

            gruppo = (Gruppo) extra.get("Gruppo");
            user = SliceAppDB.getUser();
        }
        user.resetUnread(gruppo.getGroupID());
        SliceAppDB.setUser_1(user);
        users_prova.child(user.getTelephone()).child("gruppi_partecipo").child(gruppo.getGroupID()).setValue(user.obtainDettaglio(gruppo.getGroupID()));
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
        final RecyclerView mylist = (RecyclerView) findViewById(R.id.listView2);



        Query ref = groups_ref.child(gruppo.getGroupID()).child("spese");
        ExpenseRecyclerAdapter adapter= new ExpenseRecyclerAdapter(Spesa.class,R.layout.listview_expense_row, ExpensesActivity.ExpenseHolder.class,ref,getBaseContext());



        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab1 = (FloatingActionButton)findViewById(R.id.fab1);
        fab2 = (FloatingActionButton)findViewById(R.id.fab2);
        fab3 = (FloatingActionButton)findViewById(R.id.fab3);


        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_foward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);

       // final ExpensesAdapter adapter_2 = new ExpensesAdapter(ExpensesActivity.this, R.layout.listview_expense_row, speseGruppo, user);
        LinearLayoutManager llm = new LinearLayoutManager(getBaseContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mylist.setLayoutManager(llm);
        DividerItemDecoration verticalDecoration = new DividerItemDecoration(mylist.getContext(), DividerItemDecoration.VERTICAL);
        Drawable verticalDivider = getBaseContext().getDrawable(R.drawable.horizontal_divider);
        verticalDecoration.setDrawable(verticalDivider);
        mylist.addItemDecoration(verticalDecoration);
        mylist.setAdapter(adapter);
        mylist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 ){
                    fab.hide();
                    fab.setClickable(false);
                    if(isFabOpen) {
                        fab1.hide();
                        fab1.hide();
                        fab2.hide();
                        fab3.hide();
                        fab1.setClickable(false);
                        fab2.setClickable(false);
                        fab3.setClickable(false);
                    }

                }
                else if (dy < 0) {

                    fab.show();
                    fab.setClickable(true);
                    if(isFabOpen){
                    fab1.show();
                    fab2.show();
                    fab3.show();
                    fab1.setClickable(true);
                    fab2.setClickable(true);
                    fab3.setClickable(true);
                    }}
            }
        });


        // What to do when user press the toolbar back button
        Toolbar toolbar = (Toolbar) findViewById(R.id.expenseToolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.resetUnread(gruppo.getGroupID());
                users_prova.child(user.getTelephone()).child("gruppi_partecipo").child(gruppo.getGroupID()).setValue(user.obtainDettaglio(gruppo.getGroupID()));
                finish();
            }
        });


      //  tv1.setOnClickListener(this);
       // tv2.setOnClickListener(this);
       // tv3.setOnClickListener(this);


        // What to do when the user long press an item from the expenses list
       /* mlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
        });*/

        /*BottomNavigationView bottomBar = (BottomNavigationView)findViewById(R.id.bottom_nav_bar);
        bottomBar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        user.resetUnread(gruppo.getGroupID());
                        users_prova.child(user.getTelephone()).child("gruppi_partecipo").child(gruppo.getGroupID()).setValue(user.obtainDettaglio(gruppo.getGroupID()));
                        Intent appInfo= new Intent(ExpensesActivity.this, AddExpenseActivity.class);
                        appInfo.putExtra("Gruppo",gruppo);

                        ExpensesActivity.this.startActivity(appInfo);
                        finish();
                        return false;
                }
        });*/
    }

    @Override
    public void onBackPressed()
    {
        user.resetUnread(gruppo.getGroupID());
        users_prova.child(user.getTelephone()).child("gruppi_partecipo").child(gruppo.getGroupID()).setValue(user.obtainDettaglio(gruppo.getGroupID()));
        finish();
    }

    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);

            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;


        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);

            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;


        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab:

                animateFAB();
                break;
            case R.id.fab1:
                Intent appInfo= new Intent(ExpensesActivity.this, AddExpenseActivity.class);
                appInfo.putExtra("Gruppo",gruppo);
                ExpensesActivity.this.startActivity(appInfo);
                finish();

                break;
            case R.id.fab2:

                break;

            case R.id.fab3:

                break;
        }
    }


    public static class ExpenseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView expIcon;
        TextView expName;
        TextView expCurrency;
        TextView expPrice;
        TextView buyer;

        public ExpenseHolder(View itemView) {
            super(itemView);
            expCurrency = (TextView) itemView.findViewById(R.id.expCurrency);
            expName= (TextView)itemView.findViewById(R.id.expName);
            expPrice= (TextView)itemView.findViewById(R.id.expPrice);
            buyer= (TextView)itemView.findViewById(R.id.buyer);
            expIcon= (ImageView)itemView.findViewById(R.id.expIcon);

        }

        @Override
        public void onClick(View v) {

        }
    }
}
