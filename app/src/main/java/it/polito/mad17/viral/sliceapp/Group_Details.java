package it.polito.mad17.viral.sliceapp;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class Group_Details extends AppCompatActivity implements LittleFragment3.GetPercentages_2{

    ArrayList<Persona> listP = new ArrayList<Persona>();
    private HashMap<String,Double> percentuali = new HashMap<String,Double>();
    private Policy policy = null;
    private FragmentManager fm;
    private boolean RequestAllTheSame=true;
    private ArrayList<Persona> tmpList = new ArrayList<Persona>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
    private DatabaseReference groups_prova = database.getReference().child("groups_prova");
    private DatabaseReference users_prova = database.getReference().child("users_prova");
    private Spinner curr;
    private static final int PICK_IMAGE_ID = 234;
    private ImageButton photoAttack;
    private FirebaseStorage storageReference = FirebaseStorage.getInstance("gs://sliceapp-a55d6.appspot.com/");
    private byte[] datas;
    private StorageReference images;
    private Bitmap b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        if(savedInstanceState!=null){

            b=savedInstanceState.getParcelable("bitmap");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            datas = baos.toByteArray();
            photoAttack= (ImageButton) findViewById(R.id.add_groupicon);
            photoAttack.setImageBitmap(b);

        }

        Bundle extra =getIntent().getExtras();

        if(extra!=null){
                listP = (ArrayList<Persona>) extra.get("ListaPersone");
        }

       photoAttack= (ImageButton) findViewById(R.id.add_groupicon);
        photoAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(getBaseContext());
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);

            }

        });

        curr=(Spinner) findViewById(R.id.currencies_spinner);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.country_arrays, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        curr.setAdapter(adapter);



        final ArrayList<String> numeri = new ArrayList<>();

        for(int i=0; i<listP.size();i++){
            numeri.add(listP.get(i).getTelephone());
        }


        users_prova.addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                while(it.hasNext()) {
                    DataSnapshot ds = it.next();
                    if(numeri.contains(ds.getKey())){
                        Persona per = ds.getValue(Persona.class);
                        tmpList.add(per);}
                }

                //CREO GRUPPO
                tmpList.add(SliceAppDB.getUser());




                RadioGroup rg = (RadioGroup) findViewById(R.id.rg3);
                rg.check(R.id.b5);

                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                        if(group.getCheckedRadioButtonId()== R.id.b6){
                            RequestAllTheSame=false;
                            policy=null;
                            fm = getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.replace(R.id.f2, LittleFragment3.newInstance(tmpList));
                            ft.addToBackStack(null);
                            ft.commit();
                        }

                        if(group.getCheckedRadioButtonId()== R.id.b5){

                            RequestAllTheSame=true;
                            fm = getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            if(fm.findFragmentById(R.id.f2)!=null){
                                ft.remove(fm.findFragmentById(R.id.f2));
                                ft.addToBackStack(null);
                                ft.commit();


                            }}
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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


        final EditText groupName =  (EditText) findViewById(R.id.groupTitle);



        if(id == R.id.action_continue){

            final String groupID = groups_prova.push().getKey();

            if(RequestAllTheSame){
                for(Persona p: tmpList){
                    percentuali.put(p.getTelephone(), (double)100/tmpList.size());
                }
                policy=new Policy(percentuali);
            }

            if(policy==null){
                Toast.makeText(getBaseContext(),"You do not have insert a valid Policy", Toast.LENGTH_LONG).show();
                return false;
            }
            if(groupName.getText().equals("") || groupName.getText().length()==0 ){
                groupName.requestFocus();
                groupName.setError("Please insert a group Name");
                return false;
            }


            if(datas!=null){
                images = storageReference.getReference().child(groupID);
                UploadTask uploadTask = images.putBytes(datas);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("Error_mio",exception.getMessage());
                    // Handle unsuccessful uploads
                }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    mCurrency curr2 = new mCurrency(curr.getSelectedItem().toString());
                    Gruppo g = new Gruppo(groupID,groupName.getText().toString(), tmpList.size(), tmpList, policy,curr2,downloadUrl);
                    g.setGroupID(groupID);
                    g.setUser(SliceAppDB.getUser());



                    Gson gson = new Gson();
                    Gruppo g1 = gson.fromJson(gson.toJson(g),Gruppo.class);

                    groups_prova.child(g1.getGroupID()).setValue(g1);

                    for(Persona p: tmpList){
                        Persona p1 = gson.fromJson(gson.toJson(p),Persona.class);
                        users_prova.child(p1.getTelephone()).setValue(p1);
                    }


                    Intent i = new Intent(Group_Details.this, List_Pager_Act.class);
                    startActivity(i);


                }
            });}
            else {
                mCurrency curr2 = new mCurrency(curr.getSelectedItem().toString());
                Gruppo g = new Gruppo(groupID, groupName.getText().toString(), tmpList.size(), tmpList, policy, curr2,null);
                g.setGroupID(groupID);
                g.setUser(SliceAppDB.getUser());


                Gson gson = new Gson();
                Gruppo g1 = gson.fromJson(gson.toJson(g), Gruppo.class);

                groups_prova.child(g1.getGroupID()).setValue(g1);

                for (Persona p : tmpList) {
                    Persona p1 = gson.fromJson(gson.toJson(p), Persona.class);

                    for(Persona altri: g1.obtainPartecipanti().values()) {
                        if(!p.getTelephone().equals(altri.getTelephone())){

                            users_prova.child(p.getTelephone()).child("amici").child(altri.getTelephone() + ";" + g1.getCurr().getChoosencurr()).child("ncname").setValue(altri.getName() + " " + altri.getSurname());
                            users_prova.child(p.getTelephone()).child("amici").child(altri.getTelephone() + ";" + g1.getCurr().getChoosencurr()).child("symbol").setValue(g1.getCurr().getSymbol());
                            users_prova.child(p.getTelephone()).child("amici").child(altri.getTelephone() + ";" + g1.getCurr().getChoosencurr()).child("digits").setValue(g1.getCurr().getDigits());
                            users_prova.child(p.getTelephone()).child("amici").child(altri.getTelephone() + ";" + g1.getCurr().getChoosencurr()).child("cc").setValue(g1.getCurr().getChoosencurr());
                    }}

                    users_prova.child(p1.getTelephone()).child("gruppi_partecipo").child(g1.getGroupID()).setValue(p1.getGruppi_partecipo().get(groupID));
                    users_prova.child(p1.getTelephone()).child("posizione_gruppi").child(g1.getGroupID()).setValue(p1.getPosizione(g1));
                    users_prova.child(p1.getTelephone()).child("dove_ho_debito").child(g1.getGroupID()).setValue(p1.getDove_ho_debito().get(groupID));
                }


                Intent i = new Intent(Group_Details.this, List_Pager_Act.class);
                startActivity(i);
            }

                }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void getPercentages(Policy policy) {
        this.policy=policy;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode) {

            case PICK_IMAGE_ID:

                b = ImagePicker.getImageFromResult(this, resultCode, data);
                if(b!=null){
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    datas = baos.toByteArray();
                    photoAttack.setImageBitmap(b);

               }



                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }





    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // If there's an upload in progress, save the reference so you can query it later
        if (images != null) {
            outState.putString("reference", images.toString());
        }

        if(b!=null)
            outState.putParcelable("bitmap",b);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // If there was an upload in progress, get its reference and create a new StorageReference
        final String stringRef = savedInstanceState.getString("reference");
        if (stringRef == null) {
            return;
        }
        images = FirebaseStorage.getInstance("gs://sliceapp-a55d6.appspot.com/").getReferenceFromUrl(stringRef);

        // Find all UploadTasks under this StorageReference (in this example, there should be one)
        List<UploadTask> tasks = images.getActiveUploadTasks();
        if (tasks.size() > 0) {
            // Get the task monitoring the upload
            UploadTask task = tasks.get(0);

            // Add new listeners to the task using an Activity scope
            task.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot state) {
                    Toast.makeText(getBaseContext(),"Upload done",Toast.LENGTH_LONG).show(); //call a user defined function to handle the event.
                }
            });
        }
    }

}
