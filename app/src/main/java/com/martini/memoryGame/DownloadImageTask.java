package com.martini.memoryGame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageButton;

import java.io.InputStream;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = "DownloadImageTask";

    ImageButton imageButton;
    public DownloadImageTask(ImageButton imageButton) {
        System.out.println("Download Image Task started");
        this.imageButton = imageButton;
    }

    protected Bitmap doInBackground(String... urls) {
        String urlDisplay = urls[0];
        Bitmap bmp = null;
        try {
            InputStream in = new java.net.URL(urlDisplay).openStream();
            bmp = BitmapFactory.decodeStream(in);
            System.out.println("I get the image");
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return bmp;
    }
    protected void onPostExecute(Bitmap result) {
        imageButton.setImageBitmap(result);
    }
}