package com.martini.memoryGame;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.martini.memoryGame.util.ViewGroupUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Map<Integer, Bitmap> bitmaps = new HashMap<>();
    private List<View> imageButtons = new ArrayList<>();
    private int lastClicked = -1;
    private int progress = -1;
    private int time = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageButtons = ViewGroupUtils.getViewsByTag(findViewById(R.id.imageButton1).getRootView(), "imageButton");
        Collections.shuffle(imageButtons);

        Bundle extras = getIntent().getExtras();
        File path = (File) extras.get("path");

        for (int i = 0; i < imageButtons.size(); i++) {
            try {
                File file = new File(path.getAbsolutePath(), i / 2 + ".png");
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                ImageButton tmpImageButton = (ImageButton) imageButtons.get(i);
                bitmaps.put(tmpImageButton.getId(), bitmap);
                tmpImageButton.setBackgroundResource(R.drawable.code);
                tmpImageButton.setOnClickListener(this);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        TextView textView = (TextView) findViewById(R.id.score);
        textView.setText("0 / 12");
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() != null && v.getTag().toString().equals("imageButton")) {
            if (progress == -1) {
                progress = 0;
//                Timer timer = new Timer("gameTimer");
//                timer.scheduleAtFixedRate(new TimerTask() {
//                    public void run() {
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                time++;
//                                TextView textView = findViewById(R.id.timer);
//                                textView.setText(time);
//                            }
//                        });
//                    }
//                }, 0, 1000);
            }
            ImageButton tmpImageButton = (ImageButton) v;
            if (lastClicked == -1) {
                tmpImageButton.setImageBitmap(bitmaps.get(v.getId()));
                lastClicked = tmpImageButton.getId();
            } else {
                tmpImageButton.setImageBitmap(bitmaps.get(v.getId()));
                if (bitmaps.get(lastClicked).sameAs(bitmaps.get(tmpImageButton.getId()))) {
                    tmpImageButton.setImageBitmap(bitmaps.get(v.getId()));
                    tmpImageButton.setClickable(false);
                    ImageButton lastClickedButton = (ImageButton) findViewById(lastClicked);
                    lastClickedButton.setImageBitmap(bitmaps.get(lastClicked));
                    lastClickedButton.setClickable(false);
                    lastClicked = -1;
                    progress += 2;
                    TextView textView = (TextView) findViewById(R.id.score);
                    textView.setText(MessageFormat.format("{0} / 12", progress));
                } else {
                    // After a delay set image to code
                    tmpImageButton.setClickable(false);
                    ImageButton lastClickedButton = (ImageButton) findViewById(lastClicked);
                    lastClickedButton.setClickable(false);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    tmpImageButton.setImageDrawable(getDrawable(R.drawable.code));
                                    tmpImageButton.setClickable(true);
                                    lastClickedButton.setImageDrawable(getDrawable(R.drawable.code));
                                    lastClickedButton.setClickable(true);
                                    lastClicked = -1;
                                }
                            });
                        }
                    }, 500);

                }
            }
        }
    }
}