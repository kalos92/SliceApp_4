package it.polito.mad17.viral.sliceapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;


/**
 * Created by Kalos on 29/03/2017.
 */

public class BitmapManager{


    private Context context;
    private int icon;
    private int reqWidth;
     private  int reqHeight;

    public BitmapManager (Context context, int icon,int w, int h){

        this.context=context;
        this.icon=icon;
        this.reqHeight=h;
        this.reqWidth=w;


    }

    public Bitmap scaleDown(int icon, float maxImageSize, boolean filter) {
        Bitmap realImage = BitmapFactory.decodeResource(context.getResources(), icon, null);
        float ratio = Math.min(
                 maxImageSize / realImage.getWidth(),
                 maxImageSize / realImage.getHeight());
        int width = Math.round( ratio * realImage.getWidth());
        int height = Math.round( ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);

        return newBitmap;
    }



    public Drawable scaleDown_draw(int icon, float maxImageSize, boolean filter) {
        Bitmap realImage = BitmapFactory.decodeResource(context.getResources(), icon, null);
        float ratio = Math.min(
                maxImageSize / realImage.getWidth(),
                maxImageSize / realImage.getHeight());
        int width = Math.round( ratio * realImage.getWidth());
        int height = Math.round( ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);


        Drawable draw = new BitmapDrawable(context.getResources(),newBitmap);
        return draw;
    }
}
