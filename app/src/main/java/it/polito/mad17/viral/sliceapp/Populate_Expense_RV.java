package it.polito.mad17.viral.sliceapp;

import android.content.Context;
import android.os.AsyncTask;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import dmax.dialog.SpotsDialog;

/**
 * Created by Kalos on 02/06/2017.
 */

public class Populate_Expense_RV extends AsyncTask<Void,Void,ExpenseRecyclerAdapter>{

    private Gruppo gruppo;
    private Context context;
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://sliceapp-a55d6.firebaseio.com/");
    private DatabaseReference rootRef = database.getReference();
    private DatabaseReference groups_ref = rootRef.child("groups_prova");
    private DatabaseReference users_prova= rootRef.child("users_prova");
    private SpotsDialog ds=null;

    public Populate_Expense_RV(Gruppo gruppo, Context context){
        this.gruppo=gruppo;
        this.context=context;
    }

    @Override
    protected ExpenseRecyclerAdapter doInBackground(Void... params) {
        Query ref = groups_ref.child(gruppo.getGroupID()).child("spese");
        ExpenseRecyclerAdapter adapter= new ExpenseRecyclerAdapter(Spesa.class,R.layout.listview_expense_row, ExpensesActivity.ExpenseHolder.class,ref,context,gruppo);
        return adapter;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ds = new SpotsDialog(context,"Loading Expenses");
    }

    @Override
    protected void onPostExecute(ExpenseRecyclerAdapter expenseRecyclerAdapter) {
        super.onPostExecute(expenseRecyclerAdapter);
        ds.dismiss();
    }
}
