package it.polito.mad17.viral.sliceapp;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;


public class AddNewGroup extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private static final String TAG = AddNewGroup.class.getSimpleName();
 //   private ArrayList<Persona> contacts = new ArrayList<Persona>();
    private Map<String,Persona> contactsMap = new HashMap<String,Persona>();
    private FirebaseDatabase database ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_group);

       // Log.d(TAG, "Response: " + data.toString());

       Button b = (Button) findViewById(R.id.phoneBook);

       b.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
               while(phones.moveToNext()) {
                   String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                   String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
/*I made this control to manage the situation in which there is a number with +39, 39 and withou 39
Example: +393466950530, 3466950530, 393466950530. In this case these numbers are the same, but the map
does not understand this. That's the reason why I made this control*/
                 //  Log.d("TruePhoneNumber",phoneNumber);
                   if(phoneNumber.contains("+")){
                       phoneNumber.replace("+"," ");
                       Log.d("PIU",phoneNumber);
                   }else{
                       StringBuilder s = new StringBuilder();
                       s.append("39");
                       s.append(phoneNumber);
                       phoneNumber = s.toString();
                       Log.d("SENZA",phoneNumber);
                   }
                   phoneNumber = phoneNumber.replaceAll("[^0-9]", "");

                   Persona p = new Persona(null,null,null,null,Long.parseLong(phoneNumber));
                   contactsMap.put(phoneNumber,p);
                   Log.d("Phone", phoneNumber);
                 //  Log.d("Name", name);
               }
               phones.close();

               //Firebase connection:
               database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
               DatabaseReference users = database.getReference().child("otherusers");


               users.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       for(Persona p: contactsMap.values()){
                           String telephone = String.valueOf(p.getTelephone());
                         //  Log.d("TelefonoLista",telephone);
                           Object userDB = dataSnapshot.child(telephone).getValue();
                        //   Log.d("TelephoneDB",telephoneDB.getKey());
                           if(userDB==null){
                               //User is not into the DB
                         //       Log.d("Falso","FALSE "+telephone);
                                p.setisInDB(0);
                           }else{
                         //      Log.d("Vero","VERO "+ telephone);
                               p.setisInDB(1); //User is into the DB.
                           }
                       }
                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });
           }
       });
        Log.d("Fine","SIAMO FUORI");
    }

}