package it.polito.mad17.viral.sliceapp;


import android.*;
import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Kalos on 14/04/2017.
 */

public class AddExpenseFragment extends Fragment implements DatePickerFragment.TheListener {




    public interface ReturnSelection{
         void returnSelection(Bundle bundle);
    }
    private static final int PERMISSION_REQUEST_STORAGE_2 = 1;
    private Gruppo gruppo;
    private static final int PICK_IMAGE_ID = 234;
    private static final int SELECT_PDF = 1212;
    private Persona user;
    private Bitmap b;
    private Uri uri;
    private boolean first_time=true;
    private View v;
    private GregorianCalendar data;
    private int pos=0;
    private int me=0;
    private String cat = "General expenditure";
    private Persona buyer;
    private Spinner sp_ct;
    private ReturnSelection returnSelection;
    private EditText et1;
    private EditText et2;
    private int selection=-1;
    private int selection_payer=-1;
    private Bundle bundle;
    private String nome = "$";
    private String prezzo = "$";

    public AddExpenseFragment() {
        // Required empty public constructor
    }



    public static AddExpenseFragment newInstance(Bundle bundle, Persona user, Gruppo gruppo) {
        AddExpenseFragment fragment = new AddExpenseFragment();
        Bundle args = new Bundle();
        args.putSerializable("User",user);
        args.putSerializable("Gruppo",gruppo);

        if(bundle!=null)
            args.putBundle("Bundle",bundle);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            user = SliceAppDB.getUser();
            gruppo = (Gruppo) getArguments().getSerializable("Gruppo");
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_add_expense, container, false);
        setRetainInstance(true);
        returnSelection = (ReturnSelection) getActivity();

        if(getArguments().getBundle("Bundle")!=null){
            bundle= new Bundle();

            bundle= getArguments().getBundle("Bundle");

            buyer= (Persona) bundle.getSerializable("Buyer");


           selection_payer = bundle.getInt("Buyer_sel");

            if(data!=null)
                data= (GregorianCalendar) bundle.getSerializable("Data");

           if(bundle.getParcelable("Uri")!=null) {
               uri = bundle.getParcelable("Uri");
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
            if(bundle.getParcelable("Bitmap")!=null) {
                b = bundle.getParcelable("Bitmap");
                ImageView preview= (ImageView)v.findViewById(R.id.preview);
                preview.setImageBitmap(b);
            }
            nome = (String) bundle.getSerializable("Nome_s");

            prezzo = (String) bundle.getSerializable("Prezzo_s");

            cat= (String) bundle.getSerializable("Cat");

            selection =bundle.getInt("Cat_sel");



        }



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
            if (savedInstanceState.getSerializable("Date") != null) {
                data = (GregorianCalendar) savedInstanceState.getSerializable("Date");
                TextView data_show = (TextView) v.findViewById(R.id.data_spesa);
                if(data!=null){
                    int month = data.get(Calendar.MONTH);
                    month++;
                    String data_ss = data.get(Calendar.DAY_OF_MONTH)+"/"+month+"/"+data.get(Calendar.YEAR);
                    data_show.setText(data_ss);
                }
                else{
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    month++;
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    String data_ss=day+"/"+month+"/"+year;
                    data_show.setText(data_ss);
                }

            }
            if(savedInstanceState.getBoolean("flag"))
                first_time = savedInstanceState.getBoolean("flag");

        }
        TextView curr = (TextView) v.findViewById(R.id.currencies_gruppo);
        curr.setText(gruppo.getCurr().getSymbol());

        // change the title and the icon of the button in the bottom navigation bar
        FloatingActionButton bnb= (FloatingActionButton) v.findViewById(R.id.fab_exp);

        et1 = (EditText) v.findViewById((R.id.expense_description));
        et2 = (EditText) v.findViewById(R.id.expense_price);

        if(!nome.equals("$"))
            et1.setText(nome);

        if(!prezzo.equals("$"))
            et2.setText(prezzo);

        TextView data_show = (TextView) v.findViewById(R.id.data_spesa);
        if(data!=null){
            int month = data.get(Calendar.MONTH);
            month++;
            String data_ss = data.get(Calendar.DAY_OF_MONTH)+"/"+month+"/"+data.get(Calendar.YEAR);
            data_show.setText(data_ss);
        }
        else{
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            month++;
            int day = c.get(Calendar.DAY_OF_MONTH);
            String data_ss=day+"/"+month+"/"+year;
            data_show.setText(data_ss);
        }


        sp_ct= (Spinner) v.findViewById(R.id.s_category);
        sp_ct.setAdapter(new Category_adapter(getContext()));
        if(selection==-1)
            sp_ct.setId(0);
        else
            sp_ct.setId(selection);

        sp_ct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                cat=(String) adapterView.getItemAtPosition(position);
                selection=position;
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




        final List<String> names = new ArrayList<>();
        for(Persona p : gruppo.obtainPartecipanti().values()){
            names.add(p.getTelephone());

        }


        final Spinner buyer_spinner = (Spinner) v.findViewById(R.id.BuyerSpinner);
        buyer_spinner.setAdapter(new Buyer_Adapter(getContext(),names,gruppo));

        if(selection_payer==-1) {
            buyer_spinner.setSelection(getIndex(buyer_spinner, user.getTelephone()));
            selection_payer = getIndex(buyer_spinner, user.getTelephone());
        }else
            buyer_spinner.setSelection(selection_payer);

        buyer_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selection_payer=position;
                buyer = gruppo.getPartecipante((String)parent.getItemAtPosition(selection_payer));


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
               parent.setId(me);
            }
        });




        float[] pers_percentages = new float[gruppo.getN_partecipanti()];

        // preparing dialog to choose members
        final ImageView preview = (ImageView) v.findViewById(R.id.preview);
        final TextView name =(TextView)v.findViewById(R.id.pdfName);

        Button photoAttack= (Button) v.findViewById(R.id.attach_photo);
        photoAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uri=null;
                b=null;
                name.setText(null);
                preview.setImageBitmap(b);
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(getContext());
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);

            }

        });
        Button PDFAttack= (Button) v.findViewById(R.id.attach_pdf);
        PDFAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                android.Manifest.permission.READ_CONTACTS)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Contacts access needed");
                            builder.setPositiveButton(android.R.string.ok, null);
                            builder.setMessage("please confirm Contacts access");
                            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @TargetApi(Build.VERSION_CODES.M)
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    requestPermissions(
                                            new String[]
                                                    {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE_2);
                                }
                            });
                            builder.show();
                            // Show an expanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.

                        } else {

                            // No explanation needed, we can request the permission.

                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    PERMISSION_REQUEST_STORAGE_2);

                            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        }
                    }else{
                        uri=null;
                        b=null;
                        name.setText(null);
                        preview.setImageBitmap(b);
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("application/pdf");
                        startActivityForResult(intent,SELECT_PDF);
                    }
                }
                else{
                    uri=null;
                    b=null;
                    name.setText(null);
                    preview.setImageBitmap(b);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    startActivityForResult(intent,SELECT_PDF);
                }

            }
        });



        bnb.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {

                                       String stringEt1 = et1.getText().toString(); //nome
                                       String stringEt2 = et2.getText().toString(); //prezzo

                                       if (TextUtils.isEmpty(stringEt1) || !stringEt1.matches("^[a-zA-Z0-9,.!'? ]*$") || stringEt1.length() > 30) {
                                           et1.setError("You didn't specify the expense description! Or name is too long");
                                           return;
                                       }

                                       if (TextUtils.isEmpty(stringEt2) || stringEt2.equals("")) {
                                           et2.setError("You didn't specify the price!");
                                           return;
                                       }


                                       cat = (String) sp_ct.getSelectedItem();
                                       //data è settato
                                       //buyer cel'ho da prima
                                       //photo e pdf cel'ho

                                       if (returnSelection != null) {
                                           //returnSelection.returnSelection(cat, data, buyer, b, uri, stringEt2, stringEt1, gruppo, user, spf);
                                            bundle=new Bundle();
                                           bundle.putSerializable("Buyer", buyer);
                                           bundle.putInt("Buyer_sel",selection_payer);

                                           if(data!=null)
                                           bundle.putSerializable("Data",data);

                                           if(uri!=null)
                                           bundle.putParcelable("Uri",uri);

                                           if(b!=null)
                                           bundle.putParcelable("Bitmap",b);

                                           bundle.putSerializable("Nome_s",stringEt1);

                                           bundle.putSerializable("Prezzo_s",stringEt2);

                                           bundle.putSerializable("Cat",cat);
                                           bundle.putInt("Cat_sel",selection);

                                           returnSelection.returnSelection(bundle);

                                       }

                                       FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
                                       transaction.replace(R.id.fragment, Select_Policy_Fragment.newInstance(bundle,gruppo,user));
                                       transaction.addToBackStack(null);

// Commit the transaction
                                       transaction.commit();
                                       return;
                                   }
                               });


    return v;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case PICK_IMAGE_ID:
                b = ImagePicker.getImageFromResult(getActivity(), resultCode, data);
                if(b!=null) {
                    ImageView preview = (ImageView) v.findViewById(R.id.preview);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    b.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                    preview.setImageBitmap(b);
                }
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
        if(data==null)
                data=date;

        TextView data_show = (TextView) v.findViewById(R.id.data_spesa);
        if(data!=null){
            int month = data.get(Calendar.MONTH);
            month++;
            String data_ss = data.get(Calendar.DAY_OF_MONTH)+"/"+month+"/"+data.get(Calendar.YEAR);
            data_show.setText(data_ss);
        }
        else{
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            month++;
            int day = c.get(Calendar.DAY_OF_MONTH);
            String data_ss=day+"/"+month+"/"+year;
            data_show.setText(data_ss);
        }

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
