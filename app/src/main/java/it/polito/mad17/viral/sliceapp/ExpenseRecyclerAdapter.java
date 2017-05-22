package it.polito.mad17.viral.sliceapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Iterator;

import dmax.dialog.SpotsDialog;

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
    protected void populateViewHolder(ExpensesActivity.ExpenseHolder viewHolder, final Spesa model, int position) {



        Picasso.with(context).load(model.getCat().getImg()).into(viewHolder.expIcon);
       // viewHolder.expIcon.setImageResource(model.getCat().getImg());
            viewHolder.expCurrency.setText(model.getValuta());
            viewHolder.buyer.setText(model.getPagante().getName()+" " +model.getPagante().getSurname());
            viewHolder.expName.setText(model.getNome_spesa());
            String str = String.format("%."+model.getDigit()+"f",model.getImporto());
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

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ExpenseDetails.class);
                i.putExtra("spesa", model);
                v.getContext().startActivity(i);
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Deleting expense");
                builder.setMessage("Are you sure you want to delete this expense?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        boolean allHasPaid = true; //tutti hanno pagato
                        if (!model.getPagante().getTelephone().equals(SliceAppDB.getUser().getTelephone())) {
                            Toast.makeText(v.getContext(), "you can not remove this expense because you are not the payer", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // controllo se c'è qualcuno che non ha pagato...
                        for (Soldo s : model.getDivisioni().values()) {
                            if (!s.getHaPagato()) {
                                allHasPaid = false; // c'è almeno un user che non ha pagato.
                                break;
                            }
                        }
                        //... se almeno un membro non ha pagato, la spesa non si può rimuovere
                        if(!allHasPaid){
                            Toast.makeText(v.getContext(),
                                           "You can't delete this expense because someone has not paid its part yet",
                                           Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }
                        // ...altrimenti rimuovo la spesa da firebase perché tutti l'hanno pagata
                        if (allHasPaid) {
                            final String expenseID = model.getExpenseID();
                            final String groupID = model.getGruppo(); // non so se mi dà il groupID della spesa
                            final DatabaseReference databaseRef = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/").getReference();
                            final DatabaseReference expenseRef = databaseRef.child("groups_prova").child(groupID).child("spese").child(expenseID);
                            expenseRef.removeValue();
                            // devo trovare un modo per capire se una spesa è stata eliminata
                            Intent i = new Intent(v.getContext(), SplashScreen.class); // forse conviene mandarlo d qualche altra parte
                            v.getContext().startActivity(i);
                            ((Activity)v.getContext()).finish();
                        }

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //dialog.cancel();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });

    }
}
