package it.polito.mad17.viral.sliceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RegisterActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private ProgressDialog progressDialog;
    private long tel;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
        progressDialog = new ProgressDialog(this);

        final EditText phonenumber = (EditText) findViewById(R.id.phone_number);
        final EditText password = (EditText) findViewById(R.id.password);
        final EditText confirmedPassword = (EditText) findViewById(R.id.confirmed_password);
        final EditText firstName = (EditText) findViewById(R.id.first_name);
        final EditText secondName = (EditText) findViewById(R.id.second_name);
        final EditText username = (EditText) findViewById(R.id.username);
        final EditText birthdate = (EditText) findViewById(R.id.birthdate);
        Button signButton = (Button)findViewById(R.id.sign_button);
        signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check fields(nome, cognome, username, dob,tel);
                try {
                    tel = Long.parseLong(phonenumber.getText().toString());
                }
                catch (NumberFormatException notLong){
                    phonenumber.setError("Only number are accepted");
                    return;

                }
                final String telephone = Long.toString(tel);
                final String pass = password.getText().toString();
                int passLenght = pass.length();
                String confPass = confirmedPassword.getText().toString();
                final String nome = firstName.getText().toString();
                final String cognome = secondName.getText().toString();
                final String nomeutente = username.getText().toString();
                final String dob = birthdate.getText().toString();

                if(telephone.length() < 10 || telephone.length() > 13 || telephone.isEmpty()) {
                    phonenumber.requestFocus();
                    phonenumber.setError("Invalid Phone number!");
                    return;
                }
                else if(passLenght < 8 || passLenght > 16) {
                    password.requestFocus();
                    password.setError("Choose a password from 8 to 16 charachetrs");
                    return;
                }
                else if(!confPass.equals(pass)){
                    confirmedPassword.requestFocus();
                    confirmedPassword.setError("Password don't match!");
                    return;
                } else if(nomeutente.isEmpty()){
                    username.requestFocus();
                    username.setError("Invalid username!");
                    return;
                }

                progressDialog = ProgressDialog.show(RegisterActivity.this, null, "Please wait while registering...");
                final DatabaseReference users = database.getReference("otherusers");
                final Persona p = new Persona(nome, cognome, nomeutente, dob, tel);
                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(telephone)) {
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(), "Phone number already exists", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            p.setPassword(pass);
                            users.child(telephone).setValue(p);
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(),"Registation successed!", Toast.LENGTH_SHORT).show();
                            SliceAppDB.setUser(p);
                            finish();
                            Intent i = new Intent(RegisterActivity.this, List_Pager_Act.class);
                            i.putExtra("userphone", telephone);
                            startActivity(new Intent(RegisterActivity.this, List_Pager_Act.class));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getBaseContext(),"Error during the registration", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }




}