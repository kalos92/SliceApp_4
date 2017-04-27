package it.polito.mad17.viral.sliceapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static it.polito.mad17.viral.sliceapp.FirstFragment.newInstance;

public class ExpensesActivity extends AppCompatActivity {

    Gruppo gruppo;
    Persona user;
    FragmentManager fm;
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
       // if (null == fm.findFragmentById(R.id.fragment) ){
            ft.replace(R.id.fragment, Fragment_of_money.newInstance(gruppo));
        //}

        //Fragment fragment = Fragment_of_money.newInstance(gruppo);
        ft.addToBackStack(null);
        //ft.replace(R.id.my_money, fragment);
        ft.commit();

        Toolbar t = (Toolbar)findViewById(R.id.expenseToolbar);
        t.setTitle(" "+gruppo.getGroupName());
        t.setSubtitle(" "+gruppo.getUser().getUserName());
        BitmapManager  bm = new BitmapManager(this,gruppo.getImg(),50,70);

        Bitmap b=  bm.scaleDown(gruppo.getImg(),100,true);
        Drawable d = new BitmapDrawable(getResources(), b);
        t.setLogo(d);
        ListView mlist = (ListView) findViewById(R.id.listView2);



        ExpensesAdapter adapter = new ExpensesAdapter(ExpensesActivity.this, R.layout.listview_expense_row, gruppo.getSpese(),user);

        mlist.setAdapter(adapter);

        // What to do whene user press the toolbar back button
        Toolbar toolbar = (Toolbar) findViewById(R.id.expenseToolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appInfo= new Intent(ExpensesActivity.this, List_Pager_Act.class);
                startActivity(appInfo);
            }
        });

        // What to do when the user long press an item from the expenses list
        mlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getBaseContext(),"Item " + position + " selected", Toast.LENGTH_SHORT).show();
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
                        return false;

    }
});

        // raccolgo spesa proveninete da AddExpenseActiity
        Intent i = getIntent();
        Spesa s = (Spesa)i.getSerializableExtra("Spesa");
        SliceAppDB.addSpesa(s);
        adapter = new ExpensesAdapter(ExpensesActivity.this, R.layout.listview_expense_row, gruppo.getSpese(),user);
        mlist.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }


    @Override
    public void onBackPressed(){
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
