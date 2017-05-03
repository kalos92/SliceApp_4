package it.polito.mad17.viral.sliceapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.Manifest;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class AddNewGroup extends AppCompatActivity {


    private static final int PERMISSION_REQUEST_CONTACT = 1;
    private static final String TAG = AddNewGroup.class.getSimpleName();
 //   private ArrayList<Persona> contacts = new ArrayList<Persona>();
    private Map<String,Persona> contactsMap = new HashMap<String,Persona>();
    private FirebaseDatabase database ;
    private  ContactsAdapter adapter;
    private  Map<String,Persona> tmpMap = new TreeMap<>();
    private Toolbar toolbar;
    private EditText groupName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_group);
        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription("SliceApp",null, getResources().getColor(R.color.colorPrimary));
        ((Activity)this).setTaskDescription(taskDescription);

        toolbar = (Toolbar) findViewById(R.id.groupToolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.slider_tab));

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        askForContactPermission();
       // Log.d(TAG, "Response: " + data.toString());


    }

    public void askForContactPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Contacts access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("please confirm Contacts access");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CONTACT);
                        }
                    });
                    builder.show();
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSION_REQUEST_CONTACT);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }else{
                getContact();
            }
        }
        else{
            getContact();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CONTACT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContact();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, "No permission for contacts", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

        public void getContact(){


                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                while(phones.moveToNext()) {
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if(phoneNumber.contains("+")){
                        phoneNumber.replace("+","");
                        Log.d("PIU",phoneNumber);
                    }else{
                        StringBuilder s = new StringBuilder();
                        s.append("39");
                        s.append(phoneNumber);
                        phoneNumber = s.toString();
                        Log.d("SENZA",phoneNumber);
                    }
                    phoneNumber = phoneNumber.replaceAll("[^0-9]", "");

                    Persona p = new Persona(name,null,null,null,phoneNumber);
                    contactsMap.put(phoneNumber,p);
                    Log.d("Phone", phoneNumber);
                    //  Log.d("Name", name);
                }
                phones.close();

                //Firebase connection:
                database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
                DatabaseReference users = database.getReference().child("users");


                users.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(Persona p: contactsMap.values()){
                            String telephone = String.valueOf(p.getTelephone());
                            //  Log.d("TelefonoLista",telephone);
                            Object userDB = dataSnapshot.child(telephone).getValue();
                            //   Log.d("TelephoneDB",telephoneDB.getKey());
                            if(userDB==null){
                                //User is not into the DB
                                Log.d("Falso","FALSE "+telephone);
                                p.setIsInDB(0);
                                tmpMap.put(telephone,p);
                            }else{
                                Log.d("Vero","VERO "+ telephone);
                                p.setIsInDB(1); //User is into the DB.
                                tmpMap.put(telephone,p);
                            }
                        }
                        createListViewContatcs(tmpMap);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
      //      }

        Log.d("Fine","SIAMO FUORI");

        //createListViewContatcs(tmpMap);
    }

    public void createListViewContatcs(Map<String, Persona> tmpMap){

        ArrayList<Persona> listP = new ArrayList<Persona>();
        final ListView list = (ListView) findViewById(R.id.listViewContacts);
        if(tmpMap.size()!=0){
            listP.addAll(tmpMap.values());
            adapter = new ContactsAdapter(this.getBaseContext(),R.layout.contactsrow,R.layout.contactsrowbutton,listP);

            list.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contacts_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        ArrayList<Persona> listPersone = new ArrayList<Persona>();

        if(id == R.id.action_continue){
            listPersone.addAll(adapter.getGroupMembers().values());
            if(listPersone.size()!=0){
            Intent i = new Intent(AddNewGroup.this,Group_Details.class);
            i.putExtra("ListaPersone",listPersone);
              //  i.putExtra("NomeGruppo",)
            startActivity(i);}
            else{
                Toast.makeText(getBaseContext(),"You have to select at least one contact", Toast.LENGTH_LONG).show();
                return false;}

        }
        return super.onOptionsItemSelected(item);
    }

}