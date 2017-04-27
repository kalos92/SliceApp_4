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

/**
 * Created by Kalos on 13/04/2017.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    TheListener listener;

    public interface TheListener{
        public void returnDate(GregorianCalendar date);
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

        c2= new GregorianCalendar(year,month,day);



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
