package com.martini.memoryGame;

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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LoadActivity extends AppCompatActivity implements View.OnClickListener {

    private Set<ImageButton> selectedImage = new HashSet<>();

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
            EditText editText = findViewById(R.id.uriInput);
            editText.clearFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

            ProgressBar progressBar = findViewById(R.id.progressBar);
            List<View> imageButtonList = ViewGroupUtils.getViewsByTag(findViewById(R.id.imageButton).getRootView(), "imageButton");
            List<View> progressBarList = ViewGroupUtils.getViewsByTag(findViewById(R.id.progressBar1).getRootView(), "progressBar");
            if (URLUtil.isValidUrl(editText.getText().toString())) {
                new DownloadImageTask(imageButtonList, progressBarList, progressBar).execute(editText.getText().toString());
            }
        }
        if (v.getId() == R.id.confirm) {
            // Send images to the other activity
        }
    }
}