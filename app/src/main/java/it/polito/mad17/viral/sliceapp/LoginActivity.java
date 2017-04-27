package it.polito.mad17.viral.sliceapp;
import android.app.ProgressDialog;
import android.content.Intent;;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.concurrent.CountDownLatch;
import java.lang.String;


/*
  A login screen that offers login via phone number/password.
 */
public class LoginActivity extends AppCompatActivity{

    private AutoCompleteTextView mPhoneView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private String pwdDB,phoneDB;
    private boolean cancel = false;
    private View focusView = null;
    private boolean flag=false;
    private  DatabaseReference db;
    private String phone,password;
    private CountDownLatch latch = new CountDownLatch(1);
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getSharedPreferences("data",MODE_PRIVATE);
        int number = sharedPref.getInt("isLogged", 0);
        System.out.println("number " + number);
        if(number == 0) {
            setContentView(R.layout.activity_login);
            //Firebase.setAndroidContext(this);
            // Set up the login form.
            mPhoneView = (AutoCompleteTextView) findViewById(R.id.phoneNumber);
            //  populateAutoComplete();

            mPasswordView = (EditText) findViewById(R.id.password);
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
            TextView registerView = (TextView) findViewById(R.id.RegisterView);
            registerView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(registerIntent);
                }

            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);
        } else {

            //SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
            //preferences.edit().clear();
            String nome = sharedPref.getString("nome", null);
            String cognome = sharedPref.getString("cognome", null);
            String username = sharedPref.getString("username", null);
            String dob = sharedPref.getString("dob", null);
            long telefono = sharedPref.getLong("telefono", 0);

            Persona p = new Persona(nome, cognome, username, dob, telefono);
            SliceAppDB.setUser(p);
           // SliceAppDB.setUserPhoneNumber(String.valueOf(telefono));
            finish();
            Intent i = new Intent(LoginActivity.this, List_Pager_Act.class);
            //i.putExtra("userphone", phone);
            startActivity(i);
        }
    }

    /*
       Attempts to sign in or register the account specified by the login form.
       If there are form errors (invalid phone number, missing fields, etc.), the
       errors are presented and no actual login attempt is done.
     */
    private void attemptLogin() {

        // Reset errors.
        mPhoneView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        phone = mPhoneView.getText().toString();
        password = mPasswordView.getText().toString();


        boolean verifyMail = false;


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
            /// / focusView = mPhoneView;
            // cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.requestFocus();
            mPasswordView.setError(getString(R.string.error_empty_password));
            return;
        }
        //Check for a password having the right length
        //     8 >=  pwd <= 16
        if (isPasswordValid(password) == false) {
            mPasswordView.requestFocus();
            mPasswordView.setError(getString(R.string.error_short_password));
            return;
        }

        //final ProgressDialog progressDialog = ProgressDialog.show(this, "","Please wait while loading your data...", true);
        db = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/").getReference("otherusers");

        System.out.println("AAAAA " + db.child("otherusers").child(phone).toString());

        //     Thread t = new Thread(new Runnable() {
        //        @Override
        //        public void run() {
        db.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("BEFORE FOR CYCLE", "CIAO");
                //   for (DataSnapshot d : dataSnapshot.getChildren()) {
                //      Log.d("DataSnapshot","Key value is: " + d.getKey());
                if (dataSnapshot.hasChild(phone)) {

                    pwdDB = dataSnapshot.child(phone).child("password").getValue().toString();
                    Log.d("Pass", pwdDB);

                    if (!pwdDB.equals(password)) {
                        mPasswordView.requestFocus();
                        mPasswordView.setError("The password is wrong!");
                        return;
                    }

                    //Open the login activity and set this so that next it value is 1 then this conditin will be false.
                    SharedPreferences.Editor prefEditor = sharedPref.edit();
                    prefEditor.putInt("isLogged", 1);


                    DataSnapshot ds = dataSnapshot.child(phone);
                    phoneDB = ds.getKey();
                    String nome = ds.child("name").getValue().toString();
                    String cognome = ds.child("surname").getValue().toString();
                    String username = ds.child("username").getValue().toString();
                    String dob = ds.child("birthdate").getValue().toString();
                    long telefono = ds.child("telephone").getValue(long.class);
                    Persona p = new Persona(nome, cognome, username, dob, telefono);
                    SliceAppDB.setUser(p);
                  //  SliceAppDB.setUserPhoneNumber(String.valueOf(telefono));

                    //prefEditor.putString("userphone", phone);
                    prefEditor.putString("nome", nome);
                    prefEditor.putString("cogome", cognome);
                    prefEditor.putString("username", username);
                    prefEditor.putString("dob", dob);
                    prefEditor.putLong("telefono", telefono);

                    prefEditor.commit();
                    //progressDialog.dismiss();
                    finish();
                    Intent i = new Intent(LoginActivity.this, List_Pager_Act.class);
                    //i.putExtra("userphone", phone);
                    startActivity(i);
                    // Log.d("PhoneValue","Phone Number is " + phoneDB);
                    //pwdDB = db.child("password").
                    //Log.d("PWD Value is: ","The password is " + pwdDB);
                } else {
                    mPhoneView.requestFocus();
                    mPhoneView.setError("The phone number field is wrong!");
                    //progressDialog.dismiss();
                    return;
                }
                Log.d("Successful", "phoneDB: " + phoneDB + " pwdDB: " + pwdDB);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FAILURE", databaseError.toString());
            }

        });

        //   Log.d("Outside","Siamo fuori");
    }

    //It controls that the phone number has only numbers
    private boolean isPhoneValid(String target) {
        if (target == null) {
            return false;
        } else {
            String regexStr= "^[0-9]{10,13}$";
            if(target.length() < 10 || target.length() > 13 || target.matches(regexStr)==false) {
                //Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                return false;
            }
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
