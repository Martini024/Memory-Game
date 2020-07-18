package com.martini.memoryGame;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.martini.memoryGame.adapter.LoadAdapter;
import com.martini.memoryGame.asyncTask.DownloadImageTask;
import com.martini.memoryGame.util.ViewGroupUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoadActivity extends AppCompatActivity implements View.OnClickListener {

    private List<ImageButton> selectedImage = new ArrayList<>();
    private DownloadImageTask downloadImageTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getBaseContext(), 4, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new LoadAdapter(20));
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(final RecyclerView rv, MotionEvent e) {
                final View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && child.findViewById(R.id.imageButton) != null && child.findViewById(R.id.imageButton).isEnabled()) {
                    final int position = rv.getChildAdapterPosition(child);
                    ImageButton imageButton = (ImageButton) child.findViewById(R.id.imageButton);
                    if (selectedImage.contains(imageButton)) {
                        selectedImage.remove(imageButton);
                        imageButton.setForeground(null);
                        imageButton.setImageAlpha(255);
                        findViewById(R.id.confirm).setEnabled(false);
                    } else if (selectedImage.size() == 6) {
                        Toast toast = Toast.makeText(getBaseContext(), "Cannot select image more than 6",
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        selectedImage.add(imageButton);
                        Drawable drawable = (VectorDrawable) getDrawable(R.drawable.ic_check_circle_outline_black);
                        imageButton.setForeground(drawable);
                        imageButton.setImageAlpha(128);
                    }
                    if (selectedImage.size() == 6) {
                        findViewById(R.id.confirm).setEnabled(true);
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
        findViewById(R.id.fetch).setOnClickListener(this);
        findViewById(R.id.confirm).setOnClickListener(this);
        findViewById(R.id.confirm).setEnabled(false);
        ((ProgressBar) findViewById(R.id.progressBarHorizontal)).setProgress(0);
        ((TextView) findViewById(R.id.loadingText)).setText(R.string.loading_text);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fetch) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarHorizontal);
            progressBar.setProgress(0);
            TextView loadingText = (TextView) findViewById(R.id.loadingText);
            loadingText.setText(R.string.loading_text);
            findViewById(R.id.confirm).setEnabled(false);
            selectedImage.forEach(el -> {
                el.setForeground(null);
                el.setImageAlpha(255);
            });
            selectedImage.clear();
            EditText editText = findViewById(R.id.uriInput);
            editText.clearFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

            List<View> imageButtonList = ViewGroupUtils.getViewsByTag(findViewById(R.id.imageButton).getRootView(), "imageButton");
            List<View> progressBarList = ViewGroupUtils.getViewsByTag(findViewById(R.id.progressBar).getRootView(), "progressBar");
            if (URLUtil.isValidUrl(editText.getText().toString())) {
                if (downloadImageTask != null) {
                    downloadImageTask.cancel(true);
                }
                downloadImageTask = new DownloadImageTask(imageButtonList, progressBarList, progressBar, loadingText);
                downloadImageTask.execute(editText.getText().toString());
            }
        } else if (v.getId() == R.id.confirm) {
            ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
            File directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE);
            FileOutputStream fileOutputStream = null;
            try {
                for (int i = 0; i < selectedImage.size(); i++) {
                    File myPath = new File(directory, i + ".png");
                    fileOutputStream = new FileOutputStream(myPath);
                    Bitmap bitmap = ((BitmapDrawable) selectedImage.get(i).getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("path", directory);
            startActivity(intent);
        }
    }
}