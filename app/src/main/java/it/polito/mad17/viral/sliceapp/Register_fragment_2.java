package it.polito.mad17.viral.sliceapp;


import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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




            if(savedInstanceState.getSerializable("Data")!=null) {
                data= (String) savedInstanceState.getSerializable("Data");
                TextView tv = (TextView) v.findViewById(R.id.dob_edt);
                tv.setText(data);
            }


        }
        database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
        progressDialog = new ProgressDialog(getActivity());

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

                    String phonenum_complex = prefix+number;
                    final String phonenum_good = phonenum_complex.substring(1);
                    progressDialog = ProgressDialog.show(getContext(), "", "In seconds you will be an user of SliceApp, have fun!");
                    final DatabaseReference users = database.getReference("users");
                    final Persona p = new Persona(name,surname,username, data,  phonenum_good );
                    users.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                                // register user
                                p.setPassword(password);
                                users.child(phonenum_good).setValue(p);
                                users.child(phonenum_good).child("belongsToGroups").setValue("");
                                users.child(phonenum_good).child("isInDB").setValue(1);
                                progressDialog.dismiss();
                                Toast.makeText(getContext(),"Registation successed!", Toast.LENGTH_SHORT).show();

                                SharedPreferences sharedPref = getActivity().getSharedPreferences("data",MODE_PRIVATE);
                                SharedPreferences.Editor prefEditor = sharedPref.edit();
                                prefEditor.putInt("isLogged", 1);

                                SliceAppDB.setUser(p);

                                prefEditor.putString("nome", name);
                                prefEditor.putString("cognome", surname);
                                prefEditor.putString("username", username);
                                prefEditor.putString("dob", data);
                                prefEditor.putString("telefono", phonenum_good);
                                prefEditor.commit();


                                Intent i = new Intent(getActivity(), List_Pager_Act.class);
                                startActivity(i);
                                getActivity().finish();
                            }



                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getContext(),"Error during the registration", Toast.LENGTH_SHORT).show();
                        }
                    });

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


    }


}
