package it.polito.mad17.viral.sliceapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class List_Pager_Act extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private int[]  tabIcons = {R.drawable.img_contestation, R.drawable.img_gruppi , R.drawable.img_bilancio};
    private ViewPager vpPager;


    private DataSnapshot users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start the background Firebase activity //
        startService(new Intent(getBaseContext(), FirebaseBackgroundService.class));

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
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            int value = extras.getInt("three");
            if(value==2){

                TabLayout.Tab tab= tabLayout.getTabAt(value);
                tab.select();
            }
        }else{
            TabLayout.Tab tab= tabLayout.getTabAt(0);
            tab.select();
            // inietto i dati
        }
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
        tabOne.setTextSize(13);
        tabOne.setCompoundDrawablesRelativeWithIntrinsicBounds( scaleImage(tabIcons[0],Images_costant.MENU_ICON_WIDTH,Images_costant.MENU_ICON_HEIGHT),null, null, null);
        tabLayout.getTabAt(2).setCustomView(tabOne);


        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText(" GROUPS");
        tabOne.setTextSize(13);
        tabTwo.setCompoundDrawablesRelativeWithIntrinsicBounds( scaleImage(tabIcons[1],Images_costant.MENU_ICON_WIDTH,Images_costant.MENU_ICON_HEIGHT) ,null,null, null);
        tabLayout.getTabAt(0).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText(" BALANCE");
        tabOne.setTextSize(13);
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

        if(id == R.id.action_settings){

            SharedPreferences sharedPref = getSharedPreferences("data",MODE_PRIVATE);
            SharedPreferences.Editor prefEditor = sharedPref.edit();
            prefEditor.putInt("isLogged", 0);
            prefEditor.commit();
            // pulire lista gruppi e lista spese, altrimenti la visualizzazione è doppia

            Intent i = new Intent(List_Pager_Act.this,  LoginActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // temporaneo
    @Override
    public void onBackPressed(){

        if(tabLayout.getSelectedTabPosition()!=0){
            TabLayout.Tab tab= tabLayout.getTabAt(0);
            tab.select();}
        else{
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }}
}