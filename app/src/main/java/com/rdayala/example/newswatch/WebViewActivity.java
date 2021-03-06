package com.rdayala.example.newswatch;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rdayala.example.newswatch.model.FavoriteNewsItem;

import io.realm.Realm;

/**
 * Created by rdayala on 8/9/2016.
 */

public class WebViewActivity extends AppCompatActivity {


    private WebView webView1;
    private Toolbar toolbar;
    private String title = null;
    private String url = null;
    private FavoriteNewsItem feedItem = null;
    private FloatingActionButton fab = null;
    private String mDefaultTag = null;
    private Intent intent;
    private Context mContext;
    private LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;
        mInflater = LayoutInflater.from(mContext);

        intent = getIntent();
        title = getIntent().getExtras().getString("title");
        url = getIntent().getExtras().getString("url");
        mDefaultTag = getIntent().getExtras().getString("defaultTag");
        feedItem = getIntent().getExtras().getParcelable("feedItem");

        fab = (FloatingActionButton) findViewById(R.id.fab);

        if(feedItem.isAddedFavorite()) {
            fab.setImageResource(R.drawable.ic_star_white);
        } else {
            fab.setImageResource(R.drawable.ic_star_border);
        }



        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action

                if(feedItem.isAddedFavorite()) {

                    // save rssFeed data member to Realm DB
                    feedItem.setAddedFavorite(false);

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(feedItem);
                    realm.commitTransaction();
                    realm.close();
                    fab.setImageResource(R.drawable.ic_star_border);

                    Toast.makeText(getApplicationContext(), "Removed from Bookmarks!!", Toast.LENGTH_SHORT).show();
                } else {

                    // add to book marks
                    final String oldTagsString;

                    if (TextUtils.isEmpty(feedItem.getmTags())) {
                        oldTagsString = feedItem.getmCategory();
                    } else {
                        oldTagsString = feedItem.getmTags().toString().trim();
                    }

                    AlertDialog.Builder alertDialog;
                    final EditText et_Tags;

                    alertDialog = new AlertDialog.Builder(mContext);
                    View tagView = mInflater.inflate(R.layout.dialog_layout, null);
                    alertDialog.setView(tagView);

                    alertDialog.setTitle("Tags");
                    et_Tags = (EditText) tagView.findViewById(R.id.et_tags);
                    et_Tags.setText(oldTagsString);

                    alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newTagsString;
                            if(TextUtils.isEmpty(et_Tags.getText())) {
                                newTagsString = feedItem.getmCategory();
                            } else {
                                newTagsString = et_Tags.getText().toString().trim();
                            }

                            feedItem.setmTags(newTagsString);
                            feedItem.setAddedFavorite(true);

                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(feedItem);
                            realm.commitTransaction();
                            realm.close();

                            Toast.makeText(mContext, "Added tags and saved to Bookmarks!!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            fab.setImageResource(R.drawable.ic_star_white);

                        }
                    });
                    alertDialog.show();
                }
            }
        });

        // Style text views
        Typeface titleTypeFace = Typeface.createFromAsset(getAssets(),
                "fonts/knowledge-regular-webfont.ttf"); // JosefinSans-Bold.ttf

        TextView textview = new TextView(WebViewActivity.this);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                getSupportActionBar().getHeight());
        textview.setLayoutParams(layoutParams);

        textview.setMaxLines(1);
        textview.setText(title);
        textview.setEllipsize(TextUtils.TruncateAt.END);
        textview.setTypeface(titleTypeFace);
        textview.setTextSize(18);
        textview.setTextColor(Color.WHITE);
        // textview.setGravity(Gravity.CENTER);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(textview);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // getSupportActionBar().setTitle(title);

        if (savedInstanceState != null) {
            ((WebView) findViewById(R.id.webView1)).restoreState(savedInstanceState);
        } else {

            webView1 = (WebView) findViewById(R.id.webView1);
            webView1.getSettings().setJavaScriptEnabled(true);
            webView1.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            webView1.getSettings().setTextZoom(110);
            // webView1.getSettings().setTextSize(WebSettings.TextSize.LARGER);

            final Activity activity = this;

            webView1.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view,
                                                        String url) {
                    // TODO Auto-generated method stub
                    view.loadUrl(url);
                    return true;
                }
            });

            webView1.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    // Activities and WebViews measure progress with different scales.
                    // The progress meter will automatically disappear when we reach 100%
                    activity.setProgress(progress * 1000);
                }
            });

            webView1.setWebViewClient(new WebViewClient() {
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
                }
            });

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Code for WebView goes here
                    webView1.loadUrl(url);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_webview, menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        ShareActionProvider myShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        Intent myShareIntent = new Intent();
        myShareIntent.setAction(Intent.ACTION_SEND);
        myShareIntent.setType("text/plain");
        myShareIntent.putExtra(Intent.EXTRA_TEXT, url);

        myShareActionProvider.setShareIntent(myShareIntent);

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        ((WebView) findViewById(R.id.webView1)).saveState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView1.canGoBack()) {
            webView1.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpTo(this, intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}