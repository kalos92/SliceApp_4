package it.polito.mad17.viral.sliceapp;
import android.app.ProgressDialog;
import android.content.Intent;;
import android.content.SharedPreferences;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
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


public class LoginActivity extends AppCompatActivity{

    private AutoCompleteTextView mPhoneView;
    private EditText mPasswordView;
    private String phone, password;
    private ProgressDialog progressDialog;
    private Spinner prefix;
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
        mPhoneView = (AutoCompleteTextView) findViewById(R.id.phoneNumber);
        mPasswordView = (EditText) findViewById(R.id.password);
         prefix = (Spinner) findViewById(R.id.prefix_log);



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
            }

        });
    }

    private void attemptLogin() {
        // Reset errors.
        mPhoneView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        phone = mPhoneView.getText().toString();
        password = mPasswordView.getText().toString();

        String phone_good = prefix.getSelectedItem().toString().substring(1)+phone;
        phone = phone_good;
        // Check for a valid phone number.
        if (phone.isEmpty()) {
            mPhoneView.requestFocus();
            mPhoneView.setError(getString(R.string.error_field_required));
            return;
        }
        if (isPhoneValid(phone) == false) {
            mPhoneView.requestFocus();
            mPhoneView.setError(getString(R.string.error_invalid_phone));
            return;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.requestFocus();
            mPasswordView.setError(getString(R.string.error_empty_password));
            return;
        }
        //Check for a password having the right length 8 >=  pwd <= 16
        if (isPasswordValid(password) == false) {
            mPasswordView.requestFocus();
            mPasswordView.setError(getString(R.string.error_short_password));
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog = ProgressDialog.show(this, "","Please wait while loading your data...", true);
        DatabaseReference usersRef = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/").getReference("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(phone)) {
                    String pwdDB = (String) dataSnapshot.child(phone).child("password").getValue();
                    if (!pwdDB.equals(password)) {
                        progressDialog.dismiss();
                        mPasswordView.requestFocus();
                        mPasswordView.setError("The password is wrong!");
                        return;
                    }
                    // Inserted data ae correct
                    //Open the login activity and set this so that next it value is 1 then this conditin will be false.
                    SharedPreferences sharedPref = getSharedPreferences("data",MODE_PRIVATE);
                    SharedPreferences.Editor prefEditor = sharedPref.edit();
                    prefEditor.putInt("isLogged", 1);

                    DataSnapshot ds = dataSnapshot.child(phone);
                    String nome = (String) ds.child("name").getValue();
                    String cognome = (String) ds.child("surname").getValue();
                    String username = (String) ds.child("username").getValue();
                    String dob = (String) ds.child("birthdate").getValue();
                    String telefono = (String) ds.child("telephone").getValue();
                    Persona p = new Persona(nome, cognome, username, dob, telefono);
                    SliceAppDB.setUser(p);

                    prefEditor.putString("nome", nome);
                    prefEditor.putString("cognome", cognome);
                    prefEditor.putString("username", username);
                    prefEditor.putString("dob", dob);
                    prefEditor.putString("telefono", telefono);
                    prefEditor.commit();
                    progressDialog.dismiss();

                    Intent intent  = getIntent();
                    Intent i = new Intent(LoginActivity.this, SplashScreen.class);
                    startActivity(i);
                    finish();
                } else {
                    progressDialog.dismiss();
                    mPhoneView.requestFocus();
                    mPhoneView.setError("This number is not registered, please register to SliceApp, is very easy!");
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FAILURE", databaseError.toString());
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