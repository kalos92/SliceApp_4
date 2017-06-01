package it.polito.mad17.viral.sliceapp;
import android.app.ProgressDialog;
import android.content.Intent;;
import android.content.SharedPreferences;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.concurrent.CountDownLatch;
import java.lang.String;

import dmax.dialog.SpotsDialog;


public class LoginActivity extends AppCompatActivity{

    private AutoCompleteTextView mPhoneView;
    private EditText mPasswordView;
    private String phone, password;

    private Spinner prefix;
    private TextInputLayout actw;
    private TextInputLayout actw2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.loginToolbar);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        // Set up the login form.
        prefix = (Spinner) findViewById(R.id.prefix_log);
        mPhoneView = (AutoCompleteTextView) findViewById(R.id.phoneNumber);
        mPasswordView = (EditText) findViewById(R.id.password);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.prefix, R.layout.spinner_item);

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        prefix.setAdapter(adapter);


        actw = (TextInputLayout) findViewById(R.id.input1);
        actw2 = (TextInputLayout) findViewById(R.id.input2);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mPhoneSignInButton = (Button) findViewById(R.id.phone_sign_in_button);
        mPhoneSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        //Execute Registration
        Button registerView = (Button) findViewById(R.id.RegisterView);
        registerView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                finish();
            }

        });
    }

    private void attemptLogin() {
        // Reset errors.
        actw.setError(null);
        actw2.setError(null);

        // Store values at the time of the login attempt.
        phone = mPhoneView.getText().toString();
        password = mPasswordView.getText().toString();



        String phone_good = prefix.getSelectedItem().toString().substring(1)+phone;
        phone = phone_good;
        // Check for a valid phone number.
        if (phone.isEmpty()) {
            actw.requestFocus();
            actw.setError(getString(R.string.error_field_required));
            return;
        }
        if (isPhoneValid(phone) == false) {
            actw.requestFocus();
            actw.setError(getString(R.string.error_invalid_phone));
            return;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            actw2.requestFocus();
            actw2.setError(getString(R.string.error_empty_password));
            return;
        }
        //Check for a password having the right length 8 >=  pwd <= 16
        if (isPasswordValid(password) == false) {
           actw2.requestFocus();
            actw2.setError(getString(R.string.error_short_password));
            return;
        }

        final SpotsDialog progressDialog = new SpotsDialog(this, R.style.Custom_login);
        progressDialog.show();
        DatabaseReference usersRef = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/").getReference("users_prova");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(phone)) {
                    String pwdDB = (String) dataSnapshot.child(phone).child("password").getValue();
                    if (!pwdDB.equals(password)) {
                        progressDialog.dismiss();
                        mPasswordView.requestFocus();
                        actw2.setError("The password is wrong!");
                        return;
                    }
                    // Inserted data are correct
                    //Open the login activity and set this so that next it value is 1 then this conditin will be false.
                    SharedPreferences sharedPref = getSharedPreferences("data",MODE_PRIVATE);
                    SharedPreferences.Editor prefEditor = sharedPref.edit();
                    prefEditor.putInt("isLogged", 1);

                    DataSnapshot ds = dataSnapshot.child(phone);
                    Persona p = dataSnapshot.child(phone).getValue(Persona.class);
                    SliceAppDB.setUser(p);


                    prefEditor.putString("telefono", phone);
                    prefEditor.commit();
                    progressDialog.dismiss();


                    Intent i = new Intent(LoginActivity.this, SplashScreen.class);
                    startActivity(i);
                    finish();
                } else {
                    progressDialog.dismiss();
                    mPhoneView.requestFocus();
                    actw.setError("This number is not registered, please register to SliceApp, is very easy!");
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //It controls that the phone number has only numbers
    private boolean isPhoneValid(String target) {
        if (target == null) {
            return false;
        } else {
            String regexStr= "^[0-9]{10,13}$";
            if(target.length() < 10 || target.length() > 13 || target.matches(regexStr)==false)
                return false;
        }
        return true;
    }

    //It controls the password length
    private boolean isPasswordValid(String password) {
        if(password.length() >= 8 && password.length() <= 16)
            return true;
        return false;
    }
}