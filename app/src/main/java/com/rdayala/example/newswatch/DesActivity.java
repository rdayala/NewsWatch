package com.rdayala.example.newswatch;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by rdayala on 8/18/2016.
 */

public class DesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_des);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView textView = (TextView)findViewById(R.id.text_view);

        String str = "This is a simple RSS feeds News reader application. " + "This app lets you bookmark and tag " +
                "your news articles. It also let you search for news articles saved offline on your mobile " +
                "using the tags.\n\n" +
                "Note : This app is created as part of " +
                "Android learning experience. \n\n" +
                "Author : Raghunath Dayala\n" +
                "Copyright : 2016\n\n" +
                "For any feedback, please email at rndayala@gmail.com";
        setSupportActionBar(toolbar);
        if (getIntent() != null) {
            // Style text views
            Typeface titleTypeFace = Typeface.createFromAsset(getAssets(),
                    "fonts/knowledge-regular-webfont.ttf"); // JosefinSans-Bold.ttf
            textView.setTypeface(titleTypeFace);
            textView.setText(str);
        }

        SpannableString s = new SpannableString("About us");
        s.setSpan(new TypefaceSpan("fonts/knowledge-regular-webfont.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s); // Update the action bar title with the TypefaceSpan instance
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}