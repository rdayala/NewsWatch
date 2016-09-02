package com.rdayala.example.newswatch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.rdayala.example.newswatch.adapter.NewsItemAdapter;
import com.rdayala.example.newswatch.adapter.ViewPagerAdapter;
import com.rdayala.example.newswatch.fragments.BusinessFragment;
import com.rdayala.example.newswatch.fragments.EditorialsFragment;
import com.rdayala.example.newswatch.fragments.NationFragment;
import com.rdayala.example.newswatch.fragments.ScienceTechFragment;
import com.rdayala.example.newswatch.fragments.SportsFragment;
import com.rdayala.example.newswatch.fragments.TopNewsFragment;
import com.rdayala.example.newswatch.fragments.WorldFragment;
import com.rdayala.example.newswatch.model.FavoriteNewsItem;
import com.rdayala.example.newswatch.service.DeleteOldDataService;
import com.rdayala.example.newswatch.service.NotificationService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    private ViewPager viewPager;
    private DrawerLayout drawer;
    private CustomTabLayout tabLayout;
    private String[] pageTitle = {"Current Affairs", "National", "World", "Economy", "Editorials", "Science & Tech", "Sports"};
    private int tabSelectedPosition = 0;
    List<FavoriteNewsItem> mData;
    NewsItemAdapter mAdapter;
    ViewPagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager)findViewById(R.id.view_pager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawerLayout);

        setSupportActionBar(toolbar);

        SpannableString s = new SpannableString("News Diary");
        s.setSpan(new TypefaceSpan("fonts/knowledge-regular-webfont.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);

        //create default navigation drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //setting Tab layout (number of Tabs = number of ViewPager pages)
        tabLayout = (CustomTabLayout) findViewById(R.id.tab_layout);
        for (int i = 0; i < pageTitle.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(pageTitle[i]));
        }

        //set gravity for tab bar
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        //handling navigation view item event
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        Menu m = navigationView.getMenu();
        Log.d("MainActivity ", "Nav menu size - " + m.size());

        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);
            //the method we have create in activity
            applyFontToMenuItem(mi);
        }

        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        //change Tab selection when swipe ViewPager
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //change ViewPager page when tab selected
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String tag = tab.getText().toString();

                for (int i=0; i< pageTitle.length; i++) {
                    if (pageTitle[i].equals(tag)) {
                        viewPager.setCurrentItem(i);
                    }
                }
                tabSelectedPosition = tab.getPosition();
                // viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        AlarmManager purgeAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent purgeIntent = new Intent(this, DeleteOldDataService.class);
        PendingIntent purgePendingIntent = PendingIntent.getService(this, 543, purgeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        purgeAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, AlarmManager.INTERVAL_DAY, purgePendingIntent );

        AlarmManager refreshAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent refreshIntent = new Intent(this, NotificationService.class);
        PendingIntent refreshPendingIntent = PendingIntent.getService(this, 322, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        refreshAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, AlarmManager.INTERVAL_HALF_HOUR, AlarmManager.INTERVAL_HALF_HOUR, refreshPendingIntent );
    }

    private void applyFontToMenuItem(MenuItem mi) {
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new TypefaceSpan("fonts/knowledge-regularitalic-webfont.ttf"), 0, mNewTitle.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.favorites) {
            Intent intent = new Intent(this, ContentActivity.class);
            intent.putExtra("position", R.id.favorites);
            startActivity(intent);
        } else if (id == R.id.diplomacy) {
            Intent intent = new Intent(this, ContentActivity.class);
            intent.putExtra("position", R.id.diplomacy);
            startActivity(intent);
        } else if (id == R.id.pib) {
            Intent intent = new Intent(this, ContentActivity.class);
            intent.putExtra("position", R.id.pib);
            startActivity(intent);
        } else if (id == R.id.pib_features) {
            Intent intent = new Intent(this, ContentActivity.class);
            intent.putExtra("position", R.id.pib_features);
            startActivity(intent);
        }else if (id == R.id.news_analysis) {
            Intent intent = new Intent(this, ContentActivity.class);
            intent.putExtra("position", R.id.news_analysis);
            startActivity(intent);
        }else if (id == R.id.news_watch_settings) {
            Intent intent = new Intent(this, NewsWatchPreferencesActivity.class);
            intent.putExtra("position", R.id.news_watch_settings);
            startActivity(intent);
        }
        else if (id == R.id.news_watch_about) {
            Intent intent = new Intent(this, DesActivity.class);
            intent.putExtra("string", "Go to other Activity by NavigationView item cliked!");
            startActivity(intent);
        } else if (id == R.id.check_epw) {
            Intent webIntent = new Intent(this, WebViewActivity.class);
            webIntent.putExtra("title", "Economic & Political Weekly");
            webIntent.putExtra("url", "http://www.epw.in/");
            startActivity(webIntent);
        }
//        else if (id == R.id.close) {
//            finish();
//        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setData(List<FavoriteNewsItem> feedData) {
        this.mData = feedData;
    }

    public void setAdapter(NewsItemAdapter adapter) {
        this.mAdapter = adapter;
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
                        mAdapter.setFilter(mData);
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

        Fragment fragment = pagerAdapter.getCurrentFragment();

        switch (tabSelectedPosition) {
            case 0:
            case -1:
                ((TopNewsFragment)fragment).setSearchFilterData();
                break;
            case 1:
                ((NationFragment)fragment).setSearchFilterData();
                break;
            case 2:
                ((WorldFragment)fragment).setSearchFilterData();
                break;
            case 3:
                ((BusinessFragment)fragment).setSearchFilterData();
                break;
            case 4:
                ((EditorialsFragment)fragment).setSearchFilterData();
                break;
            case 5:
                ((ScienceTechFragment)fragment).setSearchFilterData();
                break;
            case 6:
                ((SportsFragment)fragment).setSearchFilterData();
                break;
            default:
                break;
        }

        final List<FavoriteNewsItem> filteredModelList = filter(mData, newText);
        mAdapter.setFilter(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
