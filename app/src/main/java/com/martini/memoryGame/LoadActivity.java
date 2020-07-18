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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.martini.memoryGame.asyncTask.DownloadImageTask;
import com.martini.memoryGame.util.ViewGroupUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoadActivity extends AppCompatActivity implements View.OnClickListener {

    private List<ImageButton> selectedImage = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        List<View> imageButtonList = ViewGroupUtils.getViewsByTag(findViewById(R.id.imageButton).getRootView(), "imageButton");
        imageButtonList.forEach(el -> {
            el.setOnClickListener(this);
            el.setEnabled(false);
            el.setAlpha(0.75f);
        });
        findViewById(R.id.fetch).setOnClickListener(this);
        findViewById(R.id.confirm).setOnClickListener(this);
        findViewById(R.id.confirm).setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() != null && v.getTag().toString().equals("imageButton")) {
            ImageButton imageButton = (ImageButton) findViewById(v.getId());
            if (selectedImage.contains(imageButton)) {
                selectedImage.remove(imageButton);
                imageButton.setForeground(null);
                imageButton.setImageAlpha(255);
                findViewById(R.id.confirm).setEnabled(false);
            } else if (selectedImage.size() == 6) {
                Toast toast = Toast.makeText(this, "Cannot select image more than 6",
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
        if (v.getId() == R.id.fetch) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setProgress(0);
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
            List<View> progressBarList = ViewGroupUtils.getViewsByTag(findViewById(R.id.progressBar1).getRootView(), "progressBar");
            if (URLUtil.isValidUrl(editText.getText().toString())) {
                new DownloadImageTask(imageButtonList, progressBarList, progressBar).execute(editText.getText().toString());
            }
        }
        if (v.getId() == R.id.confirm) {
            ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
            File directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE);
            System.out.println(getFilesDir());
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