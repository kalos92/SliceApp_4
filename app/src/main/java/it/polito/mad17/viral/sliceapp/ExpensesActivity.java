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
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Target;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class ExpensesActivity extends AppCompatActivity implements View.OnClickListener {

    String ID;
    Gruppo gruppo;
    Gruppo gruppo_2;
    Persona user;
    FragmentManager fm;
    Toolbar t;
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
    private DatabaseReference rootRef = database.getReference();
    private DatabaseReference groups_ref = rootRef.child("groups_prova");
    private DatabaseReference users_prova= rootRef.child("users_prova");
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private Dettagli_Gruppo unread = new Dettagli_Gruppo();

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

        }
        user = SliceAppDB.getUser();
        ID=gruppo.getGroupID();

        SliceAppDB.setUser_1(user);
        fm= getSupportFragmentManager();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment, Fragment_of_money.newInstance(gruppo));
        ft.addToBackStack(null);
        ft.commit();
        t= (Toolbar)findViewById(R.id.expenseToolbar);

        groups_ref.child(ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gruppo_2 = dataSnapshot.getValue(Gruppo.class);
                gruppo_2.setUser(SliceAppDB.getUser());
                t= (Toolbar)findViewById(R.id.expenseToolbar);
                ImageView img = (ImageView) findViewById(R.id.show_GroupIcon);

                TextView tv1 = (TextView) findViewById(R.id.show_namegroup);
                TextView tv2 = (TextView) findViewById(R.id.show_welcome);
                tv1.setText(" "+gruppo_2.getGroupName());
                tv2.setText(" Welcome: "+user.getUsername());
                t.setTitle(" "+gruppo_2.getGroupName());
                t.setSubtitle(" Welcome: "+user.getUsername());
                setSupportActionBar(t);

                getSupportActionBar().setDisplayUseLogoEnabled(false);
                getSupportActionBar().setDisplayShowCustomEnabled(true);
                t.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        users_prova.child(user.getTelephone()).child("gruppi_partecipo").child(gruppo.getGroupID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                unread =  dataSnapshot.getValue(Dettagli_Gruppo.class);
                                Integer i = unread.calculate();
                                String key_upd= users_prova.child(user.getTelephone()).child("gruppi_partecipo").child(ID).child("unread").push().getKey();
                                users_prova.child(user.getTelephone()).child("gruppi_partecipo").child(gruppo.getGroupID()).child("unread").child(key_upd).setValue(i.intValue()*-1);
                                finish();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                });
                if(gruppo_2.getUri()!=null) {
                    Picasso.with(getBaseContext()).load(gruppo_2.getUri()).placeholder(R.drawable.img_gruppi).transform(new RoundedTransformation(500, 1)).into(img);
                }else
                    Picasso.with(getBaseContext()).load(R.drawable.img_gruppi).transform(new RoundedTransformation(500, 1)).into(img);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        final RecyclerView mylist = (RecyclerView) findViewById(R.id.listView2);

        t= (Toolbar)findViewById(R.id.expenseToolbar);
        ImageView img = (ImageView) findViewById(R.id.show_GroupIcon);
        img.setImageResource(R.drawable.default_img);
        TextView tv1 = (TextView) findViewById(R.id.show_namegroup);
        TextView tv2 = (TextView) findViewById(R.id.show_welcome);
        tv1.setText(" "+gruppo.getGroupName());
        tv2.setText(" Welcome: "+user.getUsername());
        setSupportActionBar(t);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        if(gruppo.getUri()!=null) {
            Picasso.with(getBaseContext()).load(gruppo.getUri()).placeholder(R.drawable.img_gruppi).transform(new RoundedTransformation(500, 1)).into(img);
            t.setTitle(" "+gruppo.getGroupName());
            t.setSubtitle(" Welcome: "+user.getUsername());
        setSupportActionBar(t);
        }
        else
            Picasso.with(getBaseContext()).load(R.drawable.img_gruppi).transform(new RoundedTransformation(500, 1)).into(img);

        t.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                users_prova.child(user.getTelephone()).child("gruppi_partecipo").child(gruppo.getGroupID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        unread =  dataSnapshot.getValue(Dettagli_Gruppo.class);
                        Integer i = unread.calculate();
                        String key_upd= users_prova.child(user.getTelephone()).child("gruppi_partecipo").child(ID).child("unread").push().getKey();
                        users_prova.child(user.getTelephone()).child("gruppi_partecipo").child(gruppo.getGroupID()).child("unread").child(key_upd).setValue(i.intValue()*-1);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        Query ref = groups_ref.child(gruppo.getGroupID()).child("spese");
        ExpenseRecyclerAdapter adapter= new ExpenseRecyclerAdapter(Spesa.class,R.layout.listview_expense_row, ExpensesActivity.ExpenseHolder.class,ref,getBaseContext(),gruppo);



        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab1 = (FloatingActionButton)findViewById(R.id.fab1);
        fab2 = (FloatingActionButton)findViewById(R.id.fab2);



        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_foward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);

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
                        fab1.setClickable(false);
                        fab2.setClickable(false);

                    }

                }
                else if (dy < 0) {

                    fab.show();
                    fab.setClickable(true);
                    if(isFabOpen){
                    fab1.show();
                    fab2.show();
                    fab1.setClickable(true);
                    fab2.setClickable(true);

                    }}
            }
        });

    }

    @Override
    public void onBackPressed()
    {
        users_prova.child(user.getTelephone()).child("gruppi_partecipo").child(gruppo.getGroupID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                unread =  dataSnapshot.getValue(Dettagli_Gruppo.class);
                Integer i = unread.calculate();
                String key_upd= users_prova.child(user.getTelephone()).child("gruppi_partecipo").child(ID).child("unread").push().getKey();
                users_prova.child(user.getTelephone()).child("gruppi_partecipo").child(gruppo.getGroupID()).child("unread").child(key_upd).setValue(i.intValue()*-1);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);


            fab1.setClickable(false);
            fab2.setClickable(false);

            isFabOpen = false;


        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);


            fab1.setClickable(true);
            fab2.setClickable(true);
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
                Intent i =  new Intent (this,Group_balance.class);
                i.putExtra("Gruppo",gruppo);
                startActivity(i);
                finish();
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
