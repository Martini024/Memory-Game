package com.martini.memoryGame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.martini.memoryGame.adapter.MainAdapter;
import com.martini.memoryGame.util.ViewGroupUtils;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private List<Bitmap> bitmaps = new ArrayList<>();
    private List<View> imageViews = new ArrayList<>();
    private int lastClicked = -1;
    private int progress = -1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView) findViewById(R.id.score)).setText("0 / 12 matches");
        ((Button) findViewById(R.id.fetchImage)).setOnClickListener(this);
        ((Button) findViewById(R.id.playAgain)).setOnClickListener(this);
        ((ConstraintLayout) findViewById(R.id.completeStage)).setVisibility(View.GONE);
        imageViews = ViewGroupUtils.getViewsByTag(findViewById(R.id.imageView).getRootView(), "imageView");

        Bundle extras = getIntent().getExtras();
        File path = null;
        if (extras != null) {
            path = (File) extras.get("path");
        }
        for (int i = 0; i < 12; i++) {
            try {
                File file = new File(path.getAbsolutePath(), i / 2 + ".png");
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                bitmaps.add(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getBaseContext(), 3, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        Collections.shuffle(bitmaps);
        recyclerView.setAdapter(new MainAdapter(bitmaps));
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(final RecyclerView rv, MotionEvent e) {
                final View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null) {
                    final int position = rv.getChildAdapterPosition(child);
                    if (rv.isEnabled() && ((EasyFlipView) child).isEnabled()) {
                        Chronometer chronometer = (Chronometer) findViewById(R.id.chronometer);
                        if (progress == -1) {
                            progress = 0;
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            chronometer.start();
                        }
                        EasyFlipView currentButton = (EasyFlipView) child;
                        if (lastClicked == -1) {
                            if (currentButton.isFrontSide())
                                currentButton.flipTheView();
                            lastClicked = position;
                        } else if (lastClicked != position) {
                            EasyFlipView lastClickedButton = (EasyFlipView) rv.getChildAt(lastClicked);
                            if (bitmaps.get(lastClicked).sameAs(bitmaps.get(position))) {
                                imageViews.get(progress / 2).setVisibility(View.VISIBLE);
                                if (currentButton.isFrontSide())
                                    currentButton.flipTheView();
                                currentButton.setEnabled(false);
                                if (lastClickedButton.isFrontSide())
                                    lastClickedButton.flipTheView();
                                lastClickedButton.setEnabled(false);
                                progress += 2;
                                TextView textView = (TextView) findViewById(R.id.score);
                                textView.setText(MessageFormat.format("{0} / 12 matches", progress));
                                if (progress == 12) {
                                    chronometer.stop();
                                    rv.setVisibility(View.INVISIBLE);
                                    ((ConstraintLayout) findViewById(R.id.completeStage)).setVisibility(View.VISIBLE);
                                    ((ImageView) findViewById(R.id.dinosaur)).startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.swing));
                                }
                            } else {
                                rv.setEnabled(false);
                                if (currentButton.isFrontSide())
                                    currentButton.flipTheView();
                                currentButton.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.shake));
                                lastClickedButton.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.shake));
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                rv.setEnabled(true);
                                                if (currentButton.isBackSide())
                                                    currentButton.flipTheView();
                                                if (lastClickedButton.isBackSide())
                                                    lastClickedButton.flipTheView();
                                            }
                                        });
                                    }
                                }, 500);
                            }
                            lastClicked = -1;
                        }
                    }
                }
                return true;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.fetchImage:
                intent = new Intent(this, LoadActivity.class);
                startActivity(intent);
                break;
            case R.id.playAgain:
                lastClicked = -1;
                progress = -1;
                Collections.shuffle(bitmaps);
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                recyclerView.setAdapter(new MainAdapter(bitmaps));
                recyclerView.setVisibility(View.VISIBLE);
                imageViews.forEach(el -> el.setVisibility(View.INVISIBLE));
                ((TextView) findViewById(R.id.score)).setText("0 / 12 matches");
                ((Chronometer) findViewById(R.id.chronometer)).setBase(SystemClock.elapsedRealtime());
                ((ConstraintLayout) findViewById(R.id.completeStage)).setVisibility(View.GONE);
                break;
        }
    }
}