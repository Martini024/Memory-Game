package com.martini.memoryGame.asyncTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

public class DownloadImageTask extends AsyncTask<String, Object, Void> {
    private static final String TAG = "DownloadImageTask";

    List<View> imageButtons = new LinkedList<>();
    List<View> progressBars = new LinkedList<>();
    ProgressBar progressBar;
    TextView loadingText;

    public DownloadImageTask(List<View> imageButtons, List<View> progressBars, ProgressBar progressBar, TextView loadingText) {
        this.imageButtons = imageButtons;
        this.progressBars = progressBars;
        this.progressBar = progressBar;
        this.loadingText = loadingText;
    }

    @Override
    protected void onPreExecute() {
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
        loadingText.setText(MessageFormat.format("Downloading {0} of 20 images...", (int) values[0] + 1));
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
                if (isCancelled()) break;
                InputStream in = new java.net.URL(links.get(i).attr("src")).openStream();
                publishProgress(i, BitmapFactory.decodeStream(in));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}