package it.polito.mad17.viral.sliceapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.NotificationCompat;

import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Kalos on 29/05/2017.
 */

public class downloadPDF extends AsyncTask<String, Integer, Void> {

    private static final int REGISTRATION_TIMEOUT = 3 * 1000;
    private static final int WAIT_TIMEOUT = 30 * 1000;


    private Context mContext;
    private int NOTIFICATION_ID = 1;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private String content =  null;
    private boolean error = false;
    private File outputFile;

    public downloadPDF(Context context){

        this.mContext = context;

        //Get the notification manager
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

    }

    protected void onPreExecute() {
        createNotification("Downloading PDF","",false,false);
    }



    @Override
    protected Void doInBackground(String... params) {
        try {

            String fileName=params[1]+"-PDF";
            String fileExtension=".pdf";

//download pdf file.

            URL url = new URL(params[0]);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();

            c.connect();
            String PATH = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS+ "/SliceAppPDF/").toString();
            File file = new File(PATH);
            file.mkdirs();
            int totalSize = c.getContentLength();
            outputFile = new File(file, fileName+fileExtension);
            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream is = c.getInputStream();
            byte[] buffer = new byte[1024*1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) > 0) {
                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();


        } catch (Exception e) {
            e.printStackTrace();
            content = e.getMessage();
            error = true;
            cancel(true);

        }
    return null;
    }

    protected void onCancelled() {
        createNotification("Error occured during data download",content,true,false);
    }


    protected void onPostExecute(Void ab) {
        if (error) {
            createNotification("Something went wrong, sorry",content,true,false);
        } else {
            createNotification("PDF downloaded, Tap to open","",true,true);
        }
    }

    private void createNotification(String contentTitle, String contentText,boolean finish,boolean success) {
        Notification.Builder builder=null;
        //Build the notification using Notification.Builder
        if(!finish && !success) {


            Notification noti = new NotificationCompat.Builder(mContext).setContentTitle(contentText).setSmallIcon(android.R.drawable.stat_sys_download).build();

            noti.flags |= Notification.FLAG_AUTO_CANCEL;

            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, noti);
        }

        else if(finish && !success) {
            Notification noti = new NotificationCompat.Builder(mContext).setContentTitle(contentText).setSmallIcon(android.R.drawable.stat_notify_error).build();

            noti.flags |= Notification.FLAG_AUTO_CANCEL;

            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, noti);

        }else if(success && finish){
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N){
                String url = Uri.fromFile(outputFile).toString();
                String url2 = url.replace("file://","content://");
                Uri url3= Uri.parse(url2);
                intent.setDataAndType(url3, "application/pdf");
            }
        else
            intent.setDataAndType(Uri.fromFile(outputFile), "application/pdf");

            PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

            Notification noti = new NotificationCompat.Builder(mContext).setContentTitle("Download completed").setContentText("PDF was downloaded successfully, Tap to open")
                    .setSmallIcon(android.R.drawable.stat_sys_download_done)
                    .setContentIntent(pIntent).build();

            noti.flags |= Notification.FLAG_AUTO_CANCEL;

            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, noti);
        }



    }

}

