package it.polito.mad17.viral.sliceapp;

import android.graphics.Bitmap;
import android.icu.util.GregorianCalendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;



public class Select_Policy_Fragment extends Fragment implements AddExpenseFragment.ReturnSelection, Little_fragment_1.GetPercentages, Little_fragment_2.AlltheSame{

    private ReturnSelection_2 returnSelection_2;
    public interface ReturnSelection_2{
        public void returnSelection_2(String values, String cat, GregorianCalendar data, Persona buyer, Bitmap b, Uri uri, String price, String nome, Gruppo gruppo, Persona user, Choose_how_to_pay chtp, Policy policy);
    }

    private String values;
    private String cat;
    private GregorianCalendar data;
    private Persona buyer;
    private Bitmap b;
    private Uri uri;
    private String price;
    private String nome;
    private Gruppo gruppo;
    private Persona user;
    private FragmentManager fm;
    private View v;
    private Policy policy;
    private Choose_how_to_pay chtp = new Choose_how_to_pay();
    private RadioGroup rg;





    public Select_Policy_Fragment() {
        // Required empty public constructor
    }


    public static Select_Policy_Fragment newInstance(Gruppo gruppo, Persona user) {
        Select_Policy_Fragment fragment = new Select_Policy_Fragment();
        Bundle args = new Bundle();
        args.putSerializable("User",user);
        args.putSerializable("Gruppo",gruppo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (Persona) getArguments().getSerializable("User");
            gruppo = (Gruppo) getArguments().getSerializable("Gruppo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_select_policy, container, false);
        setRetainInstance(true);
        if(savedInstanceState!=null){
            if(savedInstanceState.getParcelable("Uri")!=null)
                uri = savedInstanceState.getParcelable("Uri");

            if(savedInstanceState.getParcelable("Bitmap")!=null)
                b = savedInstanceState.getParcelable("Bitmap");

            if(savedInstanceState.getSerializable("Date")!=null)
                data = (GregorianCalendar) savedInstanceState.getSerializable("Date");

            if(savedInstanceState.getSerializable("Value")!=null)
                values = (String) savedInstanceState.getSerializable("Value");

            if(savedInstanceState.getSerializable("Cat")!=null)
                cat = (String) savedInstanceState.getSerializable("Cat");

            if(savedInstanceState.getSerializable("Buyer")!=null)
                buyer = (Persona) savedInstanceState.getSerializable("Buyer");

            if(savedInstanceState.getSerializable("Price")!=null)
                price = (String) savedInstanceState.getSerializable("Price");

            if(savedInstanceState.getSerializable("Name")!=null)
                nome = (String) savedInstanceState.getSerializable("Name");
        }
        returnSelection_2 = (ReturnSelection_2) getActivity();
        rg = (RadioGroup) v.findViewById(R.id.rg);

       rg.check(R.id.b3);
        policy=gruppo.getPolicy();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {


                if(group.getCheckedRadioButtonId()== R.id.b1){


                    fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.f1, Little_fragment_2.newInstance(gruppo));
                    ft.addToBackStack(null);
                    ft.commit();
                }

                if(group.getCheckedRadioButtonId()== R.id.b2){

                    fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.f1, Little_fragment_1.newInstance(gruppo));
                    ft.addToBackStack(null);
                    ft.commit();

                }

                if(group.getCheckedRadioButtonId()== R.id.b3){

                    policy=gruppo.getPolicy();
                    fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    if(fm.findFragmentById(R.id.f1)!=null){
                    ft.remove(fm.findFragmentById(R.id.f1));
                    ft.addToBackStack(null);
                    ft.commit();}


                }
            }
        });

        BottomNavigationView bnb = (BottomNavigationView) v.findViewById(R.id.bottom_nav_bar);
        MenuItem ibnb = bnb.getMenu().getItem(1);
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


        ibnb.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                if (returnSelection_2 != null)
                    returnSelection_2.returnSelection_2(values, cat, data, buyer, b, uri, nome, price, gruppo, user,chtp,policy);


                FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
                transaction.replace(R.id.fragment, chtp);
                transaction.addToBackStack("TRE");

// Commit the transaction
                transaction.commit();
                return true;

            }
        });



        return v;


    }


    @Override
    public void returnSelection(String values, String cat, GregorianCalendar data, Persona buyer, Bitmap b, Uri uri, String price, String nome, Gruppo g, Persona user, Select_Policy_Fragment spf) {
        this.values= values;
        this.cat = cat;
        this.data =data;
        this.buyer = buyer;
        this.b = b;
        this.uri = uri;
        this.price =price;
        this.nome= nome;
        this.gruppo = g;
        this.user = user;
    }

    @Override
    public void getPercentages(Policy policy) {
        this.policy = policy;
    }

    @Override
    public void AlltheSame(Policy policy) {
        this.policy = policy;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int[] checks = new int[rg.getChildCount()];

        outState.putParcelable("Uri", uri);
        outState.putParcelable("Bitmap", b);
        outState.putSerializable("Date", data);
        outState.putSerializable("Value",values);
        outState.putSerializable("Cat",cat);
        outState.putSerializable("Buyer",buyer);
        outState.putSerializable("Price",price);
        outState.putSerializable("Name",nome);




    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if(savedInstanceState!=null){
            if(savedInstanceState.getParcelable("Uri")!=null)
                uri = savedInstanceState.getParcelable("Uri");

            if(savedInstanceState.getParcelable("Bitmap")!=null)
                b = savedInstanceState.getParcelable("Bitmap");

            if(savedInstanceState.getSerializable("Date")!=null)
                data = (GregorianCalendar) savedInstanceState.getSerializable("Date");

            if(savedInstanceState.getSerializable("Value")!=null)
                values = (String) savedInstanceState.getSerializable("Value");

            if(savedInstanceState.getSerializable("Cat")!=null)
                cat = (String) savedInstanceState.getSerializable("Cat");

            if(savedInstanceState.getSerializable("Buyer")!=null)
                buyer = (Persona) savedInstanceState.getSerializable("Buyer");

            if(savedInstanceState.getSerializable("Price")!=null)
                price = (String) savedInstanceState.getSerializable("Price");

            if(savedInstanceState.getSerializable("Name")!=null)
                nome = (String) savedInstanceState.getSerializable("Name");



        }
    }

}
