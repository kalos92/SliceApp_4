package it.polito.mad17.viral.sliceapp;


import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;


public class Register_fragment_2 extends Fragment implements DatePickerFragment.TheListener {


    private View v;
    private String prefix;
    private int selection;
    private String number;
    private String password;
    private String data = "null_$";
    private String username;
    private String name;
    private String surname;
    private FirebaseDatabase database;
    private ProgressDialog progressDialog;
    private static final int PICK_IMAGE_ID = 234;
    private ImageButton propic;
    private byte[] datas;
    private Bitmap b;
    private FirebaseStorage storageReference = FirebaseStorage.getInstance("gs://sliceapp-a55d6.appspot.com/");
    private StorageReference images;


    public Register_fragment_2() {
        // Required empty public constructor
    }


    public static Register_fragment_2 newInstance(Bundle bundle) {
        Register_fragment_2 fragment = new Register_fragment_2();
        Bundle args = new Bundle();
        args.putBundle("Bundle", bundle);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if(getArguments().getBundle("Bundle")!=null){

                if(getArguments().getBundle("Bundle").getSerializable("Prefix")!=null)
                    prefix= (String) getArguments().getBundle("Bundle").getSerializable("Prefix");

                if(getArguments().getBundle("Bundle").getSerializable("Number")!=null)
                    number= (String) getArguments().getBundle("Bundle").getSerializable("Number");

                    selection=getArguments().getBundle("Bundle").getInt("Selection");

                if(getArguments().getBundle("Bundle").getSerializable("Password")!=null)
                    password= (String) getArguments().getBundle("Bundle").getSerializable("Password");

        }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_register_fragment_2, container, false);
        setRetainInstance(true);

        if (savedInstanceState != null) {

            if(savedInstanceState.getParcelableArray("propic")!=null){
                b= (Bitmap) savedInstanceState.getParcelable("propic");
                ImageButton propic = (ImageButton) v.findViewById(R.id.add_propic);
                propic.setImageBitmap(b);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                datas = baos.toByteArray();
            }


            if(savedInstanceState.getSerializable("Data")!=null) {
                data= (String) savedInstanceState.getSerializable("Data");
                TextView tv = (TextView) v.findViewById(R.id.dob_edt);
                tv.setText(data);
            }


        }
        database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
        progressDialog = new ProgressDialog(getActivity());

        propic = (ImageButton) v.findViewById(R.id.add_propic);
        propic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(getContext());
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);

            }

        });
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.registerToolbar2);
        toolbar.inflateMenu(R.menu.register_menu);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemid = item.getItemId();
                EditText username_edt= (EditText) v.findViewById(R.id.username_edt);
                username = username_edt.getText().toString();

                EditText name_edt= (EditText) v.findViewById(R.id.name_edt);
                name = name_edt.getText().toString();

                EditText surname_edt= (EditText) v.findViewById(R.id.surname_edt);
                surname = surname_edt.getText().toString();



                if(itemid == R.id.continue_reg){

                    if(username.equals("") || username.equals(null) || username.length()>16 || username.length()<8 || username.isEmpty()){
                        username_edt.requestFocus();
                        username_edt.setError("You do not insert a valid username. Username must be between 8 and 16 characters");
                        return false;
                    }

                    if(name.equals("") || name.equals(null) || name.length()>20 || name.isEmpty()){
                        name_edt.requestFocus();
                        name_edt.setError("You do not insert your name, or your name is longer than 20 characters");
                        return false;
                    }

                    if(surname.equals("") || surname.equals(null) || surname.length()>20 || surname.isEmpty()){
                        surname_edt.requestFocus();
                        surname_edt.setError("You do not insert your surname, or your surname is longer than 20 characters");
                        return false;
                    }



                    if(data.equals("null_$")) {
                        Toast.makeText(getContext(),"You do not select your BirthDate", Toast.LENGTH_LONG).show();
                    return false;
                    }


//faccio l'upload su firebase
                    String phonenum_complex = prefix+number;
                    final String phonenum_good = phonenum_complex.substring(1);

                    if(datas!=null){
                        images = storageReference.getReference().child(phonenum_good);
                        UploadTask uploadTask = images.putBytes(datas);

                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.d("Error_mio",exception.getMessage());
                                progressDialog = ProgressDialog.show(getContext(), "", "In seconds you will be an user of SliceApp, have fun!");
                                final DatabaseReference users2 = database.getReference("users_prova");
                                final Persona p = new Persona(name,surname,username, data, phonenum_good, password,1,prefix,null );

                                Gson gson = new Gson();

                                Persona p1 = gson.fromJson(gson.toJson(p),Persona.class);
                                p1.setIsInDB(1);
                                users2.child(p1.getTelephone()).setValue(p1);
                                DatabaseReference userRef = database.getReference().push().child(p1.getTelephone());
                                userRef.child("contestazioni").setValue("");

                                SliceAppDB.setUser(p1);
                                progressDialog.dismiss();
                                Toast.makeText(getContext(),"Registation successed!", Toast.LENGTH_SHORT).show();

                                SharedPreferences sharedPref = getActivity().getSharedPreferences("data",MODE_PRIVATE);
                                SharedPreferences.Editor prefEditor = sharedPref.edit();
                                prefEditor.putInt("isLogged", 1);
                                prefEditor.putString("telefono", phonenum_good);
                                prefEditor.commit();


                                Intent i = new Intent(getActivity(), List_Pager_Act.class);
                                startActivity(i);
                                getActivity().finish();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                progressDialog = ProgressDialog.show(getContext(), "", "In seconds you will be an user of SliceApp, have fun!");
                                final DatabaseReference users2 = database.getReference("users_prova");
                                final Persona p = new Persona(name,surname,username, data, phonenum_good, password,1,prefix,downloadUrl);

                                Gson gson = new Gson();

                                Persona p1 = gson.fromJson(gson.toJson(p),Persona.class);
                                p1.setIsInDB(1);
                                users2.child(p1.getTelephone()).setValue(p1);
                                database.getReference().push().child(p1.getTelephone());

                                SliceAppDB.setUser(p1);
                                progressDialog.dismiss();
                                Toast.makeText(getContext(),"Registation successed!", Toast.LENGTH_SHORT).show();

                                SharedPreferences sharedPref = getActivity().getSharedPreferences("data",MODE_PRIVATE);
                                SharedPreferences.Editor prefEditor = sharedPref.edit();
                                prefEditor.putInt("isLogged", 1);
                                prefEditor.putString("telefono", phonenum_good);
                                prefEditor.commit();


                                Intent i = new Intent(getActivity(), List_Pager_Act.class);
                                startActivity(i);
                                getActivity().finish();

                            }
                        });}

                    else{
                        progressDialog = ProgressDialog.show(getContext(), "", "In seconds you will be an user of SliceApp, have fun!");
                        final DatabaseReference users = database.getReference("users");
                        final DatabaseReference users2 = database.getReference("users_prova");
                        final Persona p = new Persona(name,surname,username, data, phonenum_good, password,1,prefix,null );

                        Gson gson = new Gson();

                        Persona p1 = gson.fromJson(gson.toJson(p),Persona.class);
                        p1.setIsInDB(1);
                        users2.child(p1.getTelephone()).setValue(p1);
                        database.getReference().push().child(p1.getTelephone());

                        SliceAppDB.setUser(p1);
                        progressDialog.dismiss();
                        Toast.makeText(getContext(),"Registation successed!", Toast.LENGTH_SHORT).show();

                        SharedPreferences sharedPref = getActivity().getSharedPreferences("data",MODE_PRIVATE);
                        SharedPreferences.Editor prefEditor = sharedPref.edit();
                        prefEditor.putInt("isLogged", 1);
                        prefEditor.putString("telefono", phonenum_good);
                        prefEditor.commit();


                        Intent i = new Intent(getActivity(), List_Pager_Act.class);
                        startActivity(i);
                        getActivity().finish();
                    }






                }


                return true;
            }
        });

        ImageButton cal = (ImageButton) v.findViewById(R.id.cal_reg);
        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        return v;
    }


    public void showDatePickerDialog(){
        DialogFragment dialogFragment = new DatePickerFragment();
        dialogFragment.show(getActivity().getFragmentManager(), "start_date_picker");

    }

    @Override
    public void returnDate(GregorianCalendar date){
        data = new String (date.get(Calendar.DATE)+"/"+(date.get(Calendar.MONTH)+1)+"/"+date.get(Calendar.YEAR));
        TextView tv = (TextView) v.findViewById(R.id.dob_edt);
        tv.setText(data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);



        outState.putSerializable("Data", data);
        outState.putSerializable("Password", password);
        outState.putParcelable("Propic",b);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode) {

            case PICK_IMAGE_ID:

                b = ImagePicker.getImageFromResult(getActivity(), resultCode, data);
                if(b!=null){
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    datas = baos.toByteArray();
                    propic.setImageBitmap(b);

                }



                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
