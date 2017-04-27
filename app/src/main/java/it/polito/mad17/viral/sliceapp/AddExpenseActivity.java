package it.polito.mad17.viral.sliceapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.icu.util.GregorianCalendar;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;



public class AddExpenseActivity extends FragmentActivity implements DatePickerFragment.TheListener, AddExpenseFragment.ReturnSelection, Little_fragment_1.GetPercentages, Little_fragment_2.AlltheSame, Select_Policy_Fragment.ReturnSelection_2 {
    private static final int PICK_IMAGE_ID = 234;
    private static final int SELECT_PDF = 1212;
    private AlertDialog.Builder adb;
    private AlertDialog ad;
    private Gruppo gruppo;
    private Persona user;
    private Policy policy;
    private Bitmap b;
    private Uri uri;
    private boolean first_time=true;
    private FragmentManager fm;
    private GregorianCalendar data;
    private AddExpenseFragment mContent;
    private Select_Policy_Fragment mContent2;
    private Choose_how_to_pay mContent3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);


        ActivityManager.TaskDescription taskDescription = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            taskDescription = new ActivityManager.TaskDescription("SliceApp", null, getResources().getColor(R.color.colorPrimary));

            ((Activity) this).setTaskDescription(taskDescription);
        }
        Bundle extra = getIntent().getExtras();
        if(extra!= null) {
           gruppo = (Gruppo) extra.get("Gruppo");
           user = (Persona) extra.get("User");
        }


        if (savedInstanceState != null){
            if(getSupportFragmentManager().getFragment(savedInstanceState, "mContent") instanceof AddExpenseFragment)
          {
            //Restore the fragment's instance
            mContent = (AddExpenseFragment)getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
            fm= getSupportFragmentManager();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment, mContent);
            ft.addToBackStack(null);
            ft.commit();

        }

            if(getSupportFragmentManager().getFragment(savedInstanceState, "mContent") instanceof Select_Policy_Fragment)
            {
                //Restore the fragment's instance
                mContent2 = (Select_Policy_Fragment)getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
                fm= getSupportFragmentManager();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment, mContent2);
                ft.addToBackStack(null);
                ft.commit();

            }
            if(getSupportFragmentManager().getFragment(savedInstanceState, "mContent") instanceof Choose_how_to_pay)
            {
                //Restore the fragment's instance
                mContent3 = (Choose_how_to_pay) getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
                fm= getSupportFragmentManager();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment, mContent3);
                ft.addToBackStack(null);
                ft.commit();

            }
        }
    else {

            fm = getSupportFragmentManager();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment, AddExpenseFragment.newInstance(user, gruppo));
            ft.addToBackStack(null);
            ft.commit();}



    }

    @Override
    public void returnDate(GregorianCalendar date) {
       AddExpenseFragment addd=  (AddExpenseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        addd.returnDate(date);
    }


    @Override
    public void onBackPressed() {


        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment);
        if(f instanceof Select_Policy_Fragment) {
            super.onBackPressed();
            fm = getSupportFragmentManager();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment, AddExpenseFragment.newInstance(user, gruppo));
            ft.addToBackStack("UNO");
            ft.commit();
            return;
        }

        else if(f instanceof AddExpenseFragment) {
            super.onBackPressed();
            finish();
            return;
        }

        else if(f instanceof  Choose_how_to_pay){
            super.onBackPressed();
            fm = getSupportFragmentManager();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment, Select_Policy_Fragment.newInstance(gruppo,user));
            ft.addToBackStack("DUE");
            ft.commit();
            return;
        }



    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "mContent", getSupportFragmentManager().findFragmentById(R.id.fragment));
    }

    @Override
    public void returnSelection(String values, String cat, GregorianCalendar data, Persona buyer, Bitmap b, Uri uri, String price, String nome, Gruppo gruppo, Persona user, Select_Policy_Fragment spf) {

        spf.returnSelection(values,cat,data,buyer,b,uri,price,nome, gruppo, user,null);
    }

    @Override
    public void getPercentages(Policy policy) {
        Select_Policy_Fragment spf2=  (Select_Policy_Fragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        spf2.getPercentages(policy);

    }

    @Override
    public void AlltheSame(Policy policy) {
        Select_Policy_Fragment spf2=  (Select_Policy_Fragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        spf2.getPercentages(policy);
    }

    @Override
    public void returnSelection_2(String values, String cat, GregorianCalendar data, Persona buyer, Bitmap b, Uri uri, String price, String nome, Gruppo gruppo, Persona user, Choose_how_to_pay chtp, Policy policy) {
        chtp.returnSelection_2(values,cat,data,buyer,b,uri,price,nome,gruppo,user,chtp,policy);
    }
}
