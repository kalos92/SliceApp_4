package it.polito.mad17.viral.sliceapp;

import android.graphics.Bitmap;
import java.util.GregorianCalendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;


public class Select_Policy_Fragment extends Fragment implements AddExpenseFragment.ReturnSelection, Little_fragment_1.GetPercentages, Little_fragment_2.AlltheSame{

    private ReturnSelection_2 returnSelection_2;
    public interface ReturnSelection_2{
        void returnSelection_2(String cat, GregorianCalendar data, Persona buyer, Bitmap b, Uri uri, String price, String nome, Gruppo gruppo, Persona user, Choose_how_to_pay chtp, Policy policy,int tipo);
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
    private int tipo;





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
                    tipo=1;
                    policy = null;
                    fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.f1, Little_fragment_2.newInstance(gruppo));
                    ft.addToBackStack(null);
                    ft.commit();
                }

                if(group.getCheckedRadioButtonId()== R.id.b2){
                    tipo=2;
                    policy=null;
                    fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.f1, Little_fragment_1.newInstance(gruppo));
                    ft.addToBackStack(null);
                    ft.commit();

                }

                if(group.getCheckedRadioButtonId()== R.id.b3){
                    tipo=0;
                    policy=gruppo.getPolicy();
                    fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    if(fm.findFragmentById(R.id.f1)!=null){
                    ft.remove(fm.findFragmentById(R.id.f1));
                    ft.addToBackStack(null);
                    ft.commit();
                }}
            }
        });

        FloatingActionButton bnb = (FloatingActionButton) v.findViewById(R.id.fab_exp2);
       // MenuItem ibnb = bnb.getMenu().getItem(1);
       // BottomNavigationMenuView menuView = (BottomNavigationMenuView) bnb.getChildAt(0);
       // for (int i = 0; i < menuView.getChildCount(); i++) {
       //     final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
       //     final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
       //     final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            // set your height here
       //     layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, displayMetrics);
            // set your width here
         //   layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, displayMetrics);
       //     iconView.setLayoutParams(layoutParams);
       // }


        bnb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(policy!=null) {
                    if (returnSelection_2 != null)
                        returnSelection_2.returnSelection_2(cat, data, buyer, b, uri, nome, price, gruppo, user, chtp, policy,tipo);


                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    transaction.replace(R.id.fragment, chtp);
                    transaction.addToBackStack("TRE");


                    transaction.commit();
                    return;
                }
                else{
                    Toast.makeText(getContext(),"Policy is not setted",Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        });
        return v;
    }


    @Override
    public void returnSelection( String cat, GregorianCalendar data, Persona buyer, Bitmap b, Uri uri, String price, String nome, Gruppo g, Persona user, Select_Policy_Fragment spf) {

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


        outState.putParcelable("Uri", uri);
        outState.putParcelable("Bitmap", b);
        outState.putSerializable("Date", data);
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
