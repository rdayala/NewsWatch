package com.rdayala.example.newswatch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuItem;

import com.rdayala.example.newswatch.adapter.FavoritesRealmAdapter;
import com.rdayala.example.newswatch.adapter.NewsItemAdapter;
import com.rdayala.example.newswatch.fragments.DiplomacyFragment;
import com.rdayala.example.newswatch.fragments.FavoritesFragment;
import com.rdayala.example.newswatch.fragments.NewsAnalysisFragment;
import com.rdayala.example.newswatch.fragments.PIBFeaturesFragment;
import com.rdayala.example.newswatch.fragments.PIBNewsFragment;
import com.rdayala.example.newswatch.model.FavoriteNewsItem;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by rdayala on 8/18/2016.
 */
public class ContentActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    Toolbar toolbar;
    int position;
    FragmentTransaction ft;
    Fragment selectedFragment;
    List<FavoriteNewsItem> mData = null;
    List<FavoriteNewsItem> mRealmData = null;
    NewsItemAdapter mNewsAdapter = null;
    FavoritesRealmAdapter mFavAdapter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navitem_content);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SpannableString s = new SpannableString("Bookmarks");
        s.setSpan(new TypefaceSpan("fonts/knowledge-regular-webfont.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s); // Update the action bar title with the TypefaceSpan instance

        // getSupportActionBar().setTitle("");

        if (getIntent() != null) {
            position = getIntent().getIntExtra("position", 0);
        }

        setTitleManually(position);

        if(savedInstanceState == null) {
            setupFragmentWithContent();
        }
    }

    public void setTitleManually(int pos) {
        String str = null;
        switch (pos) {
            case R.id.favorites :
                str = "Favorites";
                // getSupportActionBar().setTitle("Favorites");
                break;
            case R.id.diplomacy :
                str = "Diplomacy";
                // getSupportActionBar().setTitle("Diplomacy");
                break;
            case R.id.pib :
                str = "PIB News";
                // getSupportActionBar().setTitle("PIB News");
                break;
            case R.id.pib_features :
                str = "PIB Featured Articles";
                // getSupportActionBar().setTitle("PIB Featured Articles");
                break;
            case R.id.news_analysis :
                str = "Daily News Analysis";
            default:
                break;
        }

        SpannableString s = new SpannableString(str);
        s.setSpan(new TypefaceSpan("fonts/Knowledge-Bold.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s); // Update the action bar title with the TypefaceSpan instance

    }

    public void setupFragmentWithContent() {

        ft = getSupportFragmentManager().beginTransaction();
        switch (position) {
            case R.id.favorites:
                selectedFragment = new FavoritesFragment();
                ft.replace(R.id.navitem_content, selectedFragment);
                break;
            case R.id.diplomacy:
                selectedFragment = new DiplomacyFragment();
                ft.replace(R.id.navitem_content, selectedFragment);
                break;
            case R.id.pib :
                selectedFragment = new PIBNewsFragment();
                ft.replace(R.id.navitem_content, selectedFragment);
                break;
            case R.id.pib_features :
                selectedFragment = new PIBFeaturesFragment();
                ft.replace(R.id.navitem_content, selectedFragment);
                break;
            case R.id.news_analysis :
                selectedFragment = new NewsAnalysisFragment();
                ft.replace(R.id.navitem_content, selectedFragment);
            default:
                break;
        }
        // Replace the contents of the container with the new fragment
        // ft.replace(R.id.navitem_content, new PIBFeaturesFragment());
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
    }

    public void setData(List<FavoriteNewsItem> feedData) {
        this.mData = feedData;
        this.mRealmData = null;
    }

    public void setRealmData(List<FavoriteNewsItem> favData) {
        this.mRealmData = favData;
        this.mData = null;
    }

    public void setNewsAdapter(NewsItemAdapter adapter) {
        this.mNewsAdapter = adapter;
        this.mFavAdapter = null;
    }

    public void setFavAdapter(FavoritesRealmAdapter adapter) {
        this.mFavAdapter = adapter;
        this.mNewsAdapter = null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        if(mNewsAdapter != null) {
                            mNewsAdapter.setFilter(mData);
                        }
                        else if(mFavAdapter != null) {
                            setTitleManually(position);
                            mFavAdapter.setFilter(mRealmData);
                        }
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(mData != null) {
            final List<FavoriteNewsItem> filteredModelList = filter(mData, newText);
            mNewsAdapter.setFilter(filteredModelList);
        }
        else if(mRealmData != null) {
            final List<FavoriteNewsItem> filteredFavorites = favoritesFilter(mRealmData, newText);
            mFavAdapter.setFilter(filteredFavorites);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public List<FavoriteNewsItem> favoritesFilter(List<FavoriteNewsItem> favorites, String query) {
        query = query.toLowerCase();

        List<FavoriteNewsItem> filteredModeList = new RealmList<>();

        for(FavoriteNewsItem favItem : favorites) {
            final String title = favItem.getMtitle().toLowerCase();
            final String description = favItem.getMdescription().toLowerCase();
            final String tagsStr = favItem.getmTags();

            if(title.contains(query) || description.contains(query) || tagsStr.contains(query)) {
                filteredModeList.add(favItem);
            }
        }

        return filteredModeList;
    }

    public List<FavoriteNewsItem> favoritesTagFilter(List<FavoriteNewsItem> favorites, String query) {
        query = query.toLowerCase();

        List<FavoriteNewsItem> filteredModeList = new ArrayList<>();

        for(FavoriteNewsItem favItem : favorites) {
            final String tagsStr = favItem.getmTags().toLowerCase();

            if(tagsStr.contains(query)) {
                filteredModeList.add(favItem);
            }
        }

        return filteredModeList;
    }

    private List<FavoriteNewsItem> filter(List<FavoriteNewsItem> feeds, String query) {
        query = query.toLowerCase();

        final List<FavoriteNewsItem> filteredModelList = new ArrayList<>();
        for (FavoriteNewsItem item : feeds) {
            final String title = item.getMtitle().toLowerCase();
            final String description = item.getMdescription().toLowerCase();
            if (title.contains(query) || description.contains(query)) {
                filteredModelList.add(item);
            }
        }
        return filteredModelList;
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
