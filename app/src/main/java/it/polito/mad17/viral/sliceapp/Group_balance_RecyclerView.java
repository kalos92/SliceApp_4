package it.polito.mad17.viral.sliceapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

/**
 * Created by Kalos on 26/05/2017.
 */

public class Group_balance_RecyclerView extends FirebaseRecyclerAdapter<Spesa, Group_balance_RecyclerView.BalanceHolder> {
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
    public Group_balance_RecyclerView(Class<Spesa> modelClass, int modelLayout, Class<BalanceHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    @Override
    protected void populateViewHolder(BalanceHolder viewHolder, Spesa model, int position) {

    }










    public class BalanceHolder extends RecyclerView.ViewHolder {



        public BalanceHolder(View itemView) {
            super(itemView);
        }
    }
}
