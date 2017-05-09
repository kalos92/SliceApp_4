package it.polito.mad17.viral.sliceapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Random;

/**
 * Created by Kalos on 13/04/2017.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    TheListener listener;
    String[] EasterEgg= new String[]{"You came from the future? I hope cars can finally fly", "You came from the future? I hope Half Life 3 is out","You came from the future? I hope Kingdom Hearts 3 is out",
            "Hey Marty McFly! Where's Doc?", "You came from the future? So have you seen the end of One Piece?", "Wibbly wobbly timey wimey... eh?", "So have robots conquered Earth?" ,
            "So there is a Time Machine somewhere, interesting","So, do Americans boil water to cook pasta in the future?", "You came from the future? How is New New New New New New...New York?", "Nobody can play with time",
            "Please future Trunks bring the medicine to Goku as soon as possible", "You came from the future? Who won WWIII?", "You came from the future? Sorry, I'm not Sarah Connor","So...was John Titor right?",
            "In the future, who has conquered the world Google or Apple?", "You came from the future? Please give me lottery numbers!", "Are you Hiro Nakamura?", "You came from the future? Are still producing Fast and Furious films?","Welcome to the world of tomorrow"};

    public interface TheListener{
       void returnDate(GregorianCalendar date);
    }

    private Calendar c2;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        listener = (TheListener) getActivity();
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        final Calendar c = Calendar.getInstance();
        c2= new GregorianCalendar(year,month,day);

            if(c2.after(c)) {
                Random randomGenerator = new Random();
                int randomInt = randomGenerator.nextInt(EasterEgg.length);
                Toast.makeText(getContext(), EasterEgg[randomInt], Toast.LENGTH_LONG).show();
                if (listener != null){
                listener.returnDate((GregorianCalendar) c);
                    return;
                }}



        if (listener != null)
            listener.returnDate((GregorianCalendar) c2);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener  = (DatePickerFragment.TheListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }



}
