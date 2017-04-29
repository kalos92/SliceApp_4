package it.polito.mad17.viral.sliceapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.acl.Group;
import java.util.ArrayList;

import static it.polito.mad17.viral.sliceapp.R.attr.title;
import static java.lang.Thread.sleep;
import static java.security.AccessController.getContext;


/**
 * Created by Kalos on 27/03/2017.
 */

public class GroupAdapter extends ArrayAdapter<Gruppo> {

    Context context;
    int layoutResourceId;
    ArrayList<Gruppo> data = null;


    public GroupAdapter(Context context, int layoutResourceId, ArrayList<Gruppo> objects) {
        super(context, layoutResourceId, objects);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = objects;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        GroupHolder holder= null;

        BitmapManager bm = new BitmapManager(context,data.get(position).getImg(),50,70);

        Bitmap b=  bm.scaleDown(data.get(position).getImg(),100,true);


        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new GroupHolder();
            holder.imgIcon = (ImageView) row.findViewById(R.id.imgIcon);
            TextView title = (TextView)row.findViewById(R.id.groupName);

            title.setText(data.get(position).getGroupName());
            holder.txtTitle = title;
            holder.imgIcon.setImageBitmap(b);


            row.setTag(holder);
        }
        else
        {
            holder = (GroupHolder)row.getTag();
        }

        Gruppo gruppo = data.get(position);
        holder.txtTitle.setText(gruppo.getGroupName());
        holder.imgIcon.setImageBitmap(b);



        return row;
    }


    @Override
    public int getViewTypeCount() {

        return 1;
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    static class GroupHolder
    {
        ImageView imgIcon;
        TextView txtTitle;

    }



}
