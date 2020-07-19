package com.martini.memoryGame.asyncTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.martini.memoryGame.R;

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
    Context context;

    public DownloadImageTask(Context context, List<View> imageButtons, List<View> progressBars, ProgressBar progressBar, TextView loadingText) {
        this.context = context;
        this.imageButtons = imageButtons;
        this.progressBars = progressBars;
        this.progressBar = progressBar;
        this.loadingText = loadingText;
    }

    @Override
    protected void onPreExecute() {
        imageButtons.forEach(el -> ((ImageButton) el).setImageResource(android.R.color.transparent));
        progressBars.forEach(el -> ((ProgressBar) el).setVisibility(View.VISIBLE));
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onProgressUpdate(Object... values) {
        if ((int) values[0] == 0) {
            for (int i = (int) values[2]; i < 20; i++) {
                ((ImageButton) imageButtons.get(i)).setImageResource(R.drawable.ic_question_mark);
                ((ImageButton) imageButtons.get(i)).setEnabled(false);
                ((ProgressBar) progressBars.get(i)).setVisibility(View.GONE);
            }
        }
        if ((int) values[0] == -1) {
            imageButtons.forEach(el -> {
                ((ImageButton) el).setImageResource(R.drawable.ic_question_mark);
                ((ImageButton) el).setEnabled(false);
            });
            progressBars.forEach(el -> ((ProgressBar) el).setVisibility(View.GONE));
            Toast toast = Toast.makeText(context, "Not enough images from the url",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            loadingText.setText("Enter the url to fetch the images");
        } else {
            progressBar.setProgress(((int) values[0] + 1) * (100 / (int) values[2]));
            loadingText.setText(MessageFormat.format("Downloading {0} of {1} images...", (int) values[0] + 1, (int) values[2]));
            ImageButton tmpImgBtn = (ImageButton) imageButtons.get((int) values[0]);
            tmpImgBtn.setAlpha(1f);
            tmpImgBtn.setImageBitmap((Bitmap) values[1]);
            tmpImgBtn.setEnabled(true);

            ProgressBar tmpProgressBar = (ProgressBar) progressBars.get((int) values[0]);
            tmpProgressBar.setVisibility(View.GONE);
        }

    }

    @Override
    protected Void doInBackground(String... pageUrls) {
        try {
            Document doc = Jsoup.connect(pageUrls[0]).get();
            Elements links = doc.select(".photo-grid-item img[src]");
            if (links.size() < 6) {
                publishProgress(-1);
            } else {
                int size = (Math.min(links.size(), 20));
                for (int i = 0; i < size; i++) {
                    // imageButton loading effect
                    if (isCancelled()) break;
                    InputStream in = new java.net.URL(links.get(i).attr("src")).openStream();
                    publishProgress(i, BitmapFactory.decodeStream(in), size);
                }
            }
        } catch (IOException e) {
            publishProgress(-1);
            System.out.println(e.getMessage());
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    protected void onPostExecute(Void v) {
        progressBar.setProgress(100);
        loadingText.setText("Download completed!!!");

    }
}