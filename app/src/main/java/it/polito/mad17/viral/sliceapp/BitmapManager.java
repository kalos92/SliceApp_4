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


    /*public Bitmap decodeSampledBitmapFromResource() {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), icon, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(context.getResources(), icon, options);
    }


    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public Drawable saveDrawable(int icon){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image


            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeResource(context.getResources(),icon);


            // The new size we want to scale to


            // Find the correct scale value. It should be the power of 2.

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = calculateInSampleSize(o, reqWidth, reqHeight);;


            Bitmap selectedBitmap = BitmapFactory.decodeResource(context.getResources(), icon, o2);


            // here i override the original image file


            Drawable draw = new BitmapDrawable(context.getResources(),selectedBitmap);

            return draw;
        } catch (Exception e) {
            return null;
        }
    }



    public Bitmap saveBitmap(int icon){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image


            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeResource(context.getResources(),icon);


            // The new size we want to scale to
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = calculateInSampleSize(o, reqWidth, reqHeight);;


            Bitmap selectedBitmap = BitmapFactory.decodeResource(context.getResources(), icon, o2);


            // here i override the original image file


          return selectedBitmap;
        } catch (Exception e) {
            return null;
        }
    }*/

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
