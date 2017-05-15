package it.polito.mad17.viral.sliceapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.app.ProgressDialog;
import android.widget.Toast;


public class Register_fragment_1 extends Fragment {

    private View v;
    private int selection;
    private String number;
    private String password;
    private Bundle bundle_2;
    private String prefix;
    private Reg_1_save r1s;
    private Bundle bundle_1;


    private FirebaseDatabase database;
    private ProgressDialog progressDialog;

    public interface Reg_1_save{
        public void getReg1_bundle(Bundle bundle);
    }

    public Register_fragment_1() {
        // Required empty public constructor
    }



    public static Register_fragment_1 newInstance(Bundle bundle) {
        Register_fragment_1 fragment = new Register_fragment_1();
        Bundle args = new Bundle();
        if(bundle!=null) {
            fragment.setArguments(args);
            args.putBundle("Save", bundle);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().invalidateOptionsMenu();


        database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
        progressDialog = new ProgressDialog(getContext());
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_register_fragment_1, container, false);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            bundle_1 = getArguments().getBundle("Save");

            if (bundle_1 != null) {

                selection = bundle_1.getInt("Selection");
                Spinner s_prefix = (Spinner) v.findViewById(R.id.prefix);
                s_prefix.setSelection(selection);


                if(bundle_1.getSerializable("Number")!=null) {
                    number = (String) bundle_1.getSerializable("Number");
                    EditText number2 = (EditText) v.findViewById(R.id.number);
                    number2.setText(number);
                }
                if(bundle_1.getSerializable("Password")!=null) {
                    password = (String) bundle_1.getSerializable("Password");
                    EditText pwd = (EditText) v.findViewById(R.id.pwd);
                    pwd.setText(password);
                }

            }

        }


        setRetainInstance(true);
        if (savedInstanceState != null) {

                selection = savedInstanceState.getInt("SSpinner");
                Spinner s_prefix = (Spinner) v.findViewById(R.id.prefix);
                s_prefix.setSelection(selection);


            if(savedInstanceState.getSerializable("Number")!=null) {
                number = savedInstanceState.getParcelable("Number");
                EditText number = (EditText) v.findViewById(R.id.number);

                number.setText(number.toString());
            }
            if(savedInstanceState.getSerializable("Password")!=null) {
                password = (String) savedInstanceState.getSerializable("Password");
                EditText pwd = (EditText) v.findViewById(R.id.pwd);
                pwd.setText(password);
            }

        }


       final Spinner s_prefix =(Spinner) v.findViewById(R.id.prefix);
        //s_prefix.setSelection(0);
        selection = s_prefix.getSelectedItemPosition();
        prefix = (String) s_prefix.getSelectedItem();

        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.prefix, R.layout.spinner_item);

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        s_prefix.setAdapter(adapter);

        final EditText tv_number = (EditText) v.findViewById(R.id.number);
        number = tv_number.getText().toString();

        final EditText tv_password = (EditText) v.findViewById(R.id.pwd);
        password = tv_password.getText().toString();


        r1s= (Reg_1_save) getActivity();
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.registerToolbar);
        toolbar.inflateMenu(R.menu.register_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                Log.d("Menu", "ci sono io qui");

                if (id == R.id.continue_reg) {
                    bundle_2 = new Bundle();

                    if (tv_number.getText().toString().length() < 10 || tv_number.getText().toString().length() > 13 || tv_number.getText().toString().isEmpty()) {
                        tv_number.requestFocus();
                        tv_number.setError("Invalid Phone number!");
                        return false;
                    } else if (tv_password.getText().toString().length() < 8 || tv_password.getText().toString().length() > 16) {
                        tv_password.requestFocus();
                        tv_password.setError("Choose a password between 8 and 16 characters");
                        return false;
                    }

                    progressDialog = ProgressDialog.show(getActivity(), "", "Just a moment");
                    final DatabaseReference users = database.getReference("users_prova");
                    users.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String phonenum_complex = s_prefix.getSelectedItem()+tv_number.getText().toString();
                            String phonenum_good = phonenum_complex.substring(1);


                            if (dataSnapshot.hasChild(phonenum_good)) {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Phone number already exists", Toast.LENGTH_SHORT).show();

                                return;
                            } else {
                                bundle_2.putSerializable("Prefix", (String) s_prefix.getSelectedItem());
                                bundle_2.putSerializable("Number", tv_number.getText().toString());
                                bundle_2.putInt("Selection", s_prefix.getSelectedItemPosition());
                                bundle_2.putSerializable("Password", tv_password.getText().toString());

                                r1s.getReg1_bundle(bundle_2);
                                progressDialog.dismiss();
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
                                transaction.replace(R.id.fragment_reg, Register_fragment_2.newInstance(bundle_2));
                                transaction.addToBackStack(null);

// Commit the transaction
                                transaction.commit();
                                return;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getContext(), "Error during the registration", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });

                }

                return true;
            }

             });


        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelable("SSpinner") != null)
                selection =savedInstanceState.getInt("SSpinner");

            if(savedInstanceState.getParcelable("Number")!=null)
                number = (String) savedInstanceState.getSerializable("Number");

            if(savedInstanceState.getSerializable("Password")!=null)
                password = (String)savedInstanceState.getSerializable("Password");


        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        outState.putInt("SSpinner", selection);
        outState.putSerializable("Number", number);
        outState.putSerializable("Password", password);


    }







    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.register_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }


}
