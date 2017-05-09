package it.polito.mad17.viral.sliceapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.icu.util.GregorianCalendar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

public class RegisterActivity extends AppCompatActivity implements Register_fragment_1.Reg_1_save/*, Register_fragment_2.Reg_2_save*/, DatePickerFragment.TheListener {

    private android.support.v4.app.FragmentManager fm;
    private Register_fragment_1 mContent;
    private Register_fragment_2 mContent2;
    private Bundle bundle_1;
    private Bundle bundle_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        ActivityManager.TaskDescription taskDescription = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            taskDescription = new ActivityManager.TaskDescription("SliceApp", null, getResources().getColor(R.color.colorPrimary));

            ((Activity) this).setTaskDescription(taskDescription);
        }



        if (savedInstanceState != null){
            bundle_1=savedInstanceState.getBundle("Bundle_1");
            if(getSupportFragmentManager().getFragment(savedInstanceState, "mContent") instanceof Register_fragment_1)
            {
                //Restore the fragment's instance
                mContent = (Register_fragment_1) getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
                fm= getSupportFragmentManager();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_reg, mContent);
                ft.addToBackStack(null);
                ft.commit();

            }

            if(getSupportFragmentManager().getFragment(savedInstanceState, "mContent") instanceof Register_fragment_2)
            {
                //Restore the fragment's instance
                mContent2 = (Register_fragment_2) getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
                fm= getSupportFragmentManager();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_reg, mContent2);
                ft.addToBackStack(null);
                ft.commit();

            }
        }
        else {

            fm = getSupportFragmentManager();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_reg, Register_fragment_1.newInstance(null));
            ft.addToBackStack(null);
            ft.commit();}



    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_reg);
        if(f instanceof Register_fragment_2) {
            super.onBackPressed();
            fm = getSupportFragmentManager();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_reg, Register_fragment_1.newInstance(bundle_1));
            ft.addToBackStack(null);
            ft.commit();
            return;
        }

        else if(f instanceof Register_fragment_1) {
            super.onBackPressed();
            finish();
            return;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "mContent", getSupportFragmentManager().findFragmentById(R.id.fragment_reg));
        outState.putBundle("Bundle_1", bundle_1);
    }



    @Override
    public void getReg1_bundle(Bundle bundle) {
        bundle_1 = bundle;
    }

   // @Override
    public void getReg2_bundle(Bundle bundle) {
        bundle_2 = bundle;
    }


    @Override
    public void returnDate(GregorianCalendar date) {
        Register_fragment_2 rf2 = (Register_fragment_2) getSupportFragmentManager().findFragmentById(R.id.fragment_reg);
        rf2.returnDate(date);
    }
}