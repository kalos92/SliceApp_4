package it.polito.mad17.viral.sliceapp;


import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.OpenableColumns;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Kalos on 14/04/2017.
 */

public class AddExpenseFragment extends Fragment implements DatePickerFragment.TheListener {




    public interface ReturnSelection{
        public void returnSelection(String values, String cat, GregorianCalendar data, Persona buyer, Bitmap b, Uri uri, String price, String nome, Gruppo gruppo, Persona user, Select_Policy_Fragment spf);
    }

   // Spinner spi = (Spinner) v.findViewById(R.id.currencies_spinner);
   // String valuta =(String) spi.getSelectedItem();
   // String cat = (String) sp_ct.getSelectedItem();
    //data è settato
    //buyer cel'ho da prima
    //photo e pdf cel'ho


    private Gruppo gruppo;
    private static final int PICK_IMAGE_ID = 234;
    private static final int SELECT_PDF = 1212;

    private Persona user;

    private Bitmap b;
    private Uri uri;
    private boolean first_time=true;
    private View v;
    private GregorianCalendar data;
    private MenuItem ibnb2;
    private int pos=0;
    private int me=0;
    private String category = "General expenditure";
    private Persona buyer;
    private Spinner sp_ct;
    private ReturnSelection returnSelection;
    private Select_Policy_Fragment spf = new Select_Policy_Fragment();

    public AddExpenseFragment() {
        // Required empty public constructor
    }



    public static AddExpenseFragment newInstance(Persona user, Gruppo gruppo) {
        AddExpenseFragment fragment = new AddExpenseFragment();
        Bundle args = new Bundle();
        args.putSerializable("User",user);
        args.putSerializable("Gruppo",gruppo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            user = (Persona) getArguments().getSerializable("User");
            gruppo = (Gruppo) getArguments().getSerializable("Gruppo");
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_add_expense, container, false);
        setRetainInstance(true);
        returnSelection = (ReturnSelection) getActivity();
        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelable("Uri") != null) {
                uri = savedInstanceState.getParcelable("Uri");
                Cursor cursor=null;

                cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                TextView name =(TextView)v.findViewById(R.id.pdfName);
                name.setText(displayName);
                name.setOnClickListener(new View.OnClickListener() {
                    @Override
                        public void onClick(View v) {

                            if (uri!=null) {

                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(uri, "application/pdf");
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                try {
                                    startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(getActivity(),
                                            "No Application Available to View PDF",
                                            Toast.LENGTH_SHORT).show();
                                }}
                            else
                                Toast.makeText(getActivity(),
                                        "There is an error with this file",
                                        Toast.LENGTH_SHORT).show();

                        }




                });

            }
            if (savedInstanceState.getParcelable("Bitmap") != null) {
                b = savedInstanceState.getParcelable("Bitmap");
                ImageView preview= (ImageView)v.findViewById(R.id.preview);
                preview.setImageBitmap(b);
            }
            if (savedInstanceState.getSerializable("Date") != null)
                data = (GregorianCalendar) savedInstanceState.getSerializable("Date");

            if(savedInstanceState.getBoolean("flag"))
                first_time = savedInstanceState.getBoolean("flag");

        }


        // change the title and the icon of the button in the bottom navigation bar
        BottomNavigationView bnb = (BottomNavigationView)v.findViewById(R.id.bottom_nav_bar);
        MenuItem ibnb = bnb.getMenu().getItem(1);
        bnb.setElevation(2);
        bnb.setTranslationZ(2);

        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bnb.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
            final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            // set your height here
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, displayMetrics);
            // set your width here
            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, displayMetrics);
            iconView.setLayoutParams(layoutParams);
        }

        Spinner sp_values = (Spinner)v.findViewById(R.id.currencies_spinner);
        //TODO fare l'adapter se non riesco a cambiare il colore

        sp_ct= (Spinner) v.findViewById(R.id.s_category);
        sp_ct.setAdapter(new Category_adapter(getContext()));
        sp_ct.setId(0);
        sp_ct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                category =(String) adapterView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ImageButton calendar = (ImageButton) v.findViewById(R.id.calendar);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();

            }
        });

        ibnb2=bnb.getMenu().getItem(0);


        final List<String> names = new ArrayList<>();
        for(Persona p : gruppo.getPartecipanti().values()){
            names.add(p.getUserName());

        }


        Spinner buyer_spinner = (Spinner) v.findViewById(R.id.BuyerSpinner);
        buyer_spinner.setAdapter(new Buyer_Adapter(getContext(),names,gruppo));

        buyer_spinner.setSelection(getIndex(buyer_spinner, user.getUserName()));
        buyer_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos=position;

                buyer = gruppo.getPartecipante((String)parent.getItemAtPosition(pos));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
               parent.setId(me);
            }
        });




        float[] pers_percentages = new float[gruppo.getN_partecipanti()];

        // preparing dialog to choose members


        ImageButton photoAttack= (ImageButton) v.findViewById(R.id.attach_photo);
        photoAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(getContext());
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);

            }

        });
        ImageButton PDFAttack= (ImageButton) v.findViewById(R.id.attach_pdf);
        PDFAttack.setElevation(2);
        PDFAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent,SELECT_PDF);
            }
        });

        ibnb.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                EditText et1 = (EditText) v.findViewById((R.id.expense_description));
                EditText et2 = (EditText) v.findViewById(R.id.expense_price);
                String stringEt1 = et1.getText().toString(); //nome
                String stringEt2 = et2.getText().toString(); //prezzo

                if(TextUtils.isEmpty(stringEt1) || !stringEt1.matches("^[a-zA-Z0-9,.!? ]*$") || stringEt1.length()>30){
                    et1.setError("You didn't specify the expense description! Or name is too long");
                    return false;
                }

                if(TextUtils.isEmpty(stringEt2) || stringEt2.equals("")){
                    et2.setError("You didn't specify the price!");
                    return false;
                }


                Spinner spi = (Spinner) v.findViewById(R.id.currencies_spinner);
                String valuta =(String) spi.getSelectedItem();
                String cat = (String) sp_ct.getSelectedItem();
                //data è settato
                //buyer cel'ho da prima
                //photo e pdf cel'ho

                if (returnSelection != null)
                    returnSelection.returnSelection(valuta, cat, data, buyer, b, uri, stringEt2, stringEt1, gruppo, user,spf);


                FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
                transaction.replace(R.id.fragment, spf);
                transaction.addToBackStack("DUE");

// Commit the transaction
                transaction.commit();
                return true;
            }

        });



        ibnb2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                //posto per i commenti...
                return true;
            }
        });

    return v;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case PICK_IMAGE_ID:
                b = ImagePicker.getImageFromResult(getActivity(), resultCode, data);
                ImageView preview= (ImageView) v.findViewById(R.id.preview);
                preview.setImageBitmap(b);

                break;
            case SELECT_PDF:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    uri = data.getData();
                    String uriString = uri.toString();
                    String displayName = null;

                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                TextView name =(TextView)v.findViewById(R.id.pdfName);
                                name.setText(displayName);
                                name.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (uri!=null) {

                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setDataAndType(uri, "application/pdf");
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                            try {
                                                startActivity(intent);
                                            } catch (ActivityNotFoundException e) {
                                                Toast.makeText(getActivity(),
                                                        "No Application Available to View PDF",
                                                        Toast.LENGTH_SHORT).show();
                                            }}
                                        else
                                            Toast.makeText(getActivity(),
                                                    "There is an error with this file",
                                                    Toast.LENGTH_SHORT).show();

                                    }

                                });
                            }} finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = uri.getLastPathSegment();
                        TextView name =(TextView)v.findViewById(R.id.pdfName);
                        name.setText(displayName);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
    public void showDatePickerDialog(){
        DialogFragment dialogFragment = new DatePickerFragment();
        dialogFragment.show(getActivity().getFragmentManager(), "start_date_picker");

    }

    @Override
    public void returnDate(GregorianCalendar date) {
        data=date;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        outState.putParcelable("Uri", uri);
        outState.putParcelable("Bitmap", b);
        outState.putSerializable("Date", data);
        outState.putBoolean("flag", first_time);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


            if (savedInstanceState != null) {
                if (savedInstanceState.getParcelable("Uri") != null)
                    uri =savedInstanceState.getParcelable("Uri");

                if(savedInstanceState.getParcelable("Bitmap")!=null)
                    b = savedInstanceState.getParcelable("Bitmap");

                if(savedInstanceState.getSerializable("Date")!=null)
                    data = (GregorianCalendar)savedInstanceState.getSerializable("Date");

                if(!savedInstanceState.getBoolean("flag"))
                    first_time = savedInstanceState.getBoolean("flag");
            }
        }
    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }



}
