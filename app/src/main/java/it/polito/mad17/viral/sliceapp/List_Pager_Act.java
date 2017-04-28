package it.polito.mad17.viral.sliceapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class List_Pager_Act extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private int[]  tabIcons = {R.drawable.img_contestation, R.drawable.img_gruppi , R.drawable.img_bilancio};
    private ViewPager vpPager;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //final String userphone = String.valueOf(SliceAppDB.getUser().getTelephone());

        /*
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
        DatabaseReference rootRef = database.getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot ds = dataSnapshot.child("otherusers").child(userphone);
                Iterator<DataSnapshot> iterdata = ds.child("belongsToGroups").getChildren().iterator();
                int positionGroup = 0;
                while (iterdata.hasNext()) {
                    String groupID = iterdata.next().getKey();
                    DataSnapshot groups = dataSnapshot.child("othergroups");
                    String groupName = (String) groups.child(groupID).child("name").getValue();
                    long numMembers = (long) groups.child(groupID).child("numMembers").getValue();
                    // estrazione policy di default associata a groupID
                    Iterator<DataSnapshot> policies = groups.child(groupID).child("policy").getChildren().iterator();
                    Double[] percentages = new Double[(int) numMembers];
                    int i = 0;
                    while (policies.hasNext()) {
                        Double percentage = (Double) policies.next().getValue();
                        percentages[i++] = percentage;
                    }
                    Policy policy = new Policy(percentages, (int) numMembers);

                    // iterare sulle persone, crearle e aggiungerle ai gruppi
                    Iterator<DataSnapshot> members = groups.child(groupID).child("members").getChildren().iterator();
                    DataSnapshot users = dataSnapshot.child("otherusers");
                    ArrayList<Persona> partecipanti = new ArrayList<Persona>();
                    while (members.hasNext()) {
                        String phonenumber = members.next().getKey();
                        DataSnapshot member = users.child(phonenumber);
                        String nome = (String) member.child("name").getValue();
                        String cognome = (String) member.child("surname").getValue();
                        String username = (String) member.child("username").getValue();
                        String dob = (String) member.child("birthdate").getValue();
                        long telefono = (long) member.child("telephone").getValue();
                        Persona p = new Persona(nome, cognome, username, dob, telefono);
                        partecipanti.add(p);
                    }
                    // creo il gruppo e setto il groupID
                    Gruppo g = new Gruppo(groupName, (int) numMembers, partecipanti, policy);
                    g.setGroupID(groupID);

                    // setto lo user del gruppo perché viene inviato nell'intent
                    for (Persona p : partecipanti) {
                        if (userphone.equals(String.valueOf(p.getTelephone()))) {
                            g.setUser(p);
                            break;
                        }
                    }

                    // estrazione delle spese e loro aggiunta al gruppo
                    DataSnapshot expenses = groups.child(groupID).child("expenses");
                    Iterator<DataSnapshot> iterExpenses = expenses.getChildren().iterator();
                    while (iterExpenses.hasNext()) {
                        String expenseID = iterExpenses.next().getKey();
                        DataSnapshot expense = expenses.child(expenseID);
                        String payer = (String) expense.child("payer").getValue();
                        String currency = (String) expense.child("currency").getValue();
                        String date = (String) expense.child("date").getValue();
                        String description = (String) expense.child("description").getValue();
                        double price = expense.child("price").getValue(double.class);
                        String category = (String) expense.child("category").getValue();
                        // estrarre categoria successivamente
                        DataSnapshot ps = expense.child("policy");
                        Iterator<DataSnapshot> iterpercentages = ps.getChildren().iterator();
                        Double[] percentuali = new Double[(int) numMembers];
                        int j = 0;
                        while (iterpercentages.hasNext()) {
                            DataSnapshot t = iterpercentages.next();
                            Double percentage = (double) t.getValue();
                            percentuali[j++] = percentage;
                        }
                        Policy expensePolicy = new Policy(percentuali, percentuali.length);
                        // estrazione del payer dai partecipanti
                        Persona pagante = null;
                        for (Persona p : partecipanti) {
                            if (String.valueOf(p.getTelephone()).equals(payer)) {
                                pagante = p;
                                break;
                            }
                        }

                        // estrazione delle divisioni
                        ArrayList<Soldo> divisioni = new ArrayList<Soldo>();
                        DataSnapshot divisions = expense.child("divisions");
                        Iterator<DataSnapshot> iterUsers = divisions.getChildren().iterator();
                        while (iterUsers.hasNext()) {
                            DataSnapshot t = iterUsers.next();
                            String phonenumber = t.getKey();
                            double duePart = t.child("duePart").getValue(double.class);
                            boolean hasPaid = t.child("hasPaid").getValue(boolean.class);
                            Persona currentUser = null;
                            for (Persona p : partecipanti) {
                                if (String.valueOf(p.getTelephone()).equals(phonenumber)) {
                                    currentUser = p;
                                    break;
                                }
                            }
                            Soldo soldo = new Soldo(currentUser, duePart, hasPaid, pagante);
                            divisioni.add(soldo);
                        }
                        // Aggiungo la spesa al gruppo
                        Spesa spesa = new Spesa(description, date, expensePolicy, pagante, price, g);
                        SliceAppDB.getListaSpese().add(spesa);
                        spesa.setExpenseID(expenseID); // setto expenseID
                        spesa.getCat().setName(category); // setto categoria

                        Map<String, Soldo> mappaSoldo = spesa.getDivisioni();
                        for (Soldo soldo : divisioni)
                            mappaSoldo.put(soldo.getPersona().getUserName(), soldo);
                        g.getMappaSpese().put(spesa.getExpenseID(), spesa);
                    }
                    //inserisco il gruppo nella lista dei gruppi e nella mappa dei gruppi
                    SliceAppDB.getListaGruppi().add(g);
                    SliceAppDB.getMappaGruppo().put(positionGroup, g);
                    SliceAppDB.getGruppi().put(groupID, g);
                    positionGroup++;
                }
                System.out.println("onDataChange ha finito il suo lavoro!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */
        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription("SliceApp",null, getResources().getColor(R.color.colorPrimary));
        ((Activity)this).setTaskDescription(taskDescription);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.slider_tab));

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        vpPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(vpPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(vpPager);

        setupTabIcons();
        TabLayout.Tab tab= tabLayout.getTabAt(0);
        tab.select();
        // inietto i dati

        //MenuItem addGroups = toolbar.getMenu().findItem(R.id.action_add);
        //Log.d("Toolbar2","Ciao come stia");
        //addGroups.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
        //    @Override
        //    public boolean onMenuItemClick(MenuItem item) {
                  //Intent intent = new Intent(List_Pager_Act.this,AddNewGroup.class);
                 //startActivity(intent);
        //         Log.d("Toolbar2","Ciao come stia");
        //         return true;
        //     }
        //  });
    }

    private void setupViewPager(ViewPager viewPager) {
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FirstFragment(), "GROUPS");
        adapter.addFragment(SecondFragment.newInstance(SliceAppDB.getUser()), "BALANCE");
        adapter.addFragment(new ThirdFragment(), "QUARRELS");
        viewPager.setAdapter(adapter);
    }


    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public MyPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText(" QUARRELS");
        tabOne.setCompoundDrawablesRelativeWithIntrinsicBounds( scaleImage(tabIcons[0],Images_costant.MENU_ICON_WIDTH,Images_costant.MENU_ICON_HEIGHT),null, null, null);
        tabLayout.getTabAt(2).setCustomView(tabOne);


        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText(" GROUPS");
        tabTwo.setCompoundDrawablesRelativeWithIntrinsicBounds( scaleImage(tabIcons[1],Images_costant.MENU_ICON_WIDTH,Images_costant.MENU_ICON_HEIGHT) ,null,null, null);
        tabLayout.getTabAt(0).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText(" BALANCE");
        tabThree.setCompoundDrawablesRelativeWithIntrinsicBounds( scaleImage(tabIcons[2],Images_costant.MENU_ICON_WIDTH,Images_costant.MENU_ICON_HEIGHT),null, null, null);
        tabLayout.getTabAt(1).setCustomView(tabThree);
    }

    public Drawable scaleImage (int image, int width, int height) {
        BitmapManager bm = new BitmapManager(this,image,width,height);
        return  bm.scaleDown_draw(image,60,true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Log.d("PhoneBook","Prima di phonebook");
            Intent i = new Intent(List_Pager_Act.this,AddNewGroup.class);
            startActivity(i);
            Log.d("WINNER", "Ci siamo");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // temporaneo
    @Override
    public void onBackPressed(){
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}