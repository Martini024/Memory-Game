package com.martini.memoryGame.asyncTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class DownloadImageTask extends AsyncTask<String, Object, Void> {
    private static final String TAG = "DownloadImageTask";

    List<View> imageButtons = new LinkedList<>();
    List<View> progressBars = new LinkedList<>();
    ProgressBar progressBar;

    public DownloadImageTask(List<View> imageButtons, List<View> progressBars, ProgressBar progressBar) {
        this.imageButtons = imageButtons;
        this.progressBars = progressBars;
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
        imageButtons.forEach(el -> {
            ImageButton imageButton = (ImageButton) el;
            imageButton.setImageResource(android.R.color.transparent);
        });
        progressBars.forEach(el -> {
            ProgressBar progressBar = (ProgressBar) el;
            progressBar.setVisibility(View.VISIBLE);
        });
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        progressBar.setProgress(((int) values[0] + 1) * 5);

        ImageButton tmpImgBtn = (ImageButton) imageButtons.get((int) values[0]);
        tmpImgBtn.setAlpha(1f);
        tmpImgBtn.setImageBitmap((Bitmap) values[1]);
        tmpImgBtn.setEnabled(true);

        ProgressBar tmpProgressBar = (ProgressBar) progressBars.get((int) values[0]);
        tmpProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected Void doInBackground(String... pageUrls) {
        try {
            Document doc = Jsoup.connect(pageUrls[0]).get();
            Elements links = doc.select(".photo-grid-item img[src]");
            for (int i = 0; i < 20; i++) {
                // imageButton loading effect
                InputStream in = new java.net.URL(links.get(i).attr("src")).openStream();
                publishProgress(i, BitmapFactory.decodeStream(in));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    protected void onPostExecute(Void result) {
        AlphaAnimation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);//fade from 1 to 0 alpha
        fadeOutAnimation.setDuration(2000);
        fadeOutAnimation.setFillEnabled(false);
        progressBar.startAnimation(fadeOutAnimation);
        progressBar.setVisibility(View.INVISIBLE);
//        if (result.size() != 0) {
//            for (int i = 0; i < imageButtons.size(); i++) {
//                imageButtons.get(i).setAlpha(1);
//                ImageButton tmp = (ImageButton) imageButtons.get(i);
//                tmp.setImageBitmap(result.get(i));
//                tmp.setEnabled(true);
//            }
//            System.out.println(result.size());
//        }
    }
}