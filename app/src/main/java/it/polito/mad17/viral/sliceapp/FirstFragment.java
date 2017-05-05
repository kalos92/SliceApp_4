package it.polito.mad17.viral.sliceapp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by Kalos on 27/03/2017.
 */

public class FirstFragment extends Fragment{

    private DataSnapshot users;

    public static FirstFragment newInstance() {
        FirstFragment fragmentFirst = new FirstFragment();
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.slide_groups, container, false);
        ListView mylist = (ListView) v.findViewById(R.id.listView1);
        GroupAdapter adapter = new GroupAdapter(v.getContext(), R.layout.listview_group_row, SliceAppDB.getListaGruppi());
        mylist.setAdapter(adapter);

        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent appInfo= new Intent(getActivity(), ExpensesActivity.class);
                appInfo.putExtra("Gruppo", SliceAppDB.getGruppoArray(position));
                appInfo.putExtra("User", SliceAppDB.getUser());
                startActivity(appInfo);
                //getActivity().finish();
            }
        });
        return v;
    }
}