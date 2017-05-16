package it.polito.mad17.viral.sliceapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

/**
 * Created by Kalos on 16/05/2017.
 */

public class ExpenseRecyclerAdapter extends FirebaseRecyclerAdapter<Spesa, ExpensesActivity.ExpenseHolder> {
    /**
     * @param modelClass      Firebase will marshall the data at a location into
     *                        an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list.
     *                        You will be responsible for populating an instance of the corresponding
     *                        view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location,
     *                        using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    Context context;

    public ExpenseRecyclerAdapter(Class<Spesa> modelClass, int modelLayout, Class<ExpensesActivity.ExpenseHolder> viewHolderClass, Query ref, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context=context;
    }

    @Override
    protected void populateViewHolder(ExpensesActivity.ExpenseHolder viewHolder, Spesa model, int position) {



            viewHolder.expIcon.setImageResource(model.getCat().getImg());
            viewHolder.expCurrency.setText("€");
            viewHolder.buyer.setText(model.getPagante().getName()+" " +model.getPagante().getSurname());
            viewHolder.expName.setText(model.getNome_spesa());
            String str = String.format("%.2f",model.getImporto());
            viewHolder.expPrice.setText(""+str);


           if(!model.getDivisioni().get(SliceAppDB.getUser().getTelephone()).getHaPagato()){

               viewHolder.expCurrency.setTextColor(Color.rgb(242,38,19));
               viewHolder.buyer.setTextColor(Color.rgb(242,38,19));
               viewHolder.expName.setTextColor(Color.rgb(242,38,19));


               viewHolder.expIcon.setColorFilter(Color.rgb(242,38,19), PorterDuff.Mode.SRC_ATOP);
               viewHolder.expIcon.setBackgroundColor(Color.TRANSPARENT);
               viewHolder.expPrice.setTextColor(Color.rgb(242,38,19));




           }
           else{

               viewHolder.expCurrency.setTextColor(Color.rgb(0,0,0));
               viewHolder.buyer.setTextColor(Color.rgb(0,0,0));
               viewHolder.expName.setTextColor(Color.rgb(0,0,0));


               viewHolder.expIcon.setColorFilter(Color.rgb(0,0,0), PorterDuff.Mode.SRC_ATOP);
               viewHolder.expIcon.setBackgroundColor(Color.TRANSPARENT);
               viewHolder.expPrice.setTextColor(Color.rgb(0,0,0));

           }
    }
}
