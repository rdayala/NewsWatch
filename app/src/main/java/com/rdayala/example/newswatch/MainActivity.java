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
import com.rdayala.example.newswatch.model.FeedItem;
import com.rdayala.example.newswatch.service.NotificationService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    private ViewPager viewPager;
    private DrawerLayout drawer;
    private CustomTabLayout tabLayout;
    private String[] pageTitle = {"Current Affairs", "National", "World", "Economy", "Editorials", "Science & Tech", "Sports"};
    private int tabSelectedPosition = 0;
    ArrayList<Fragment> fr_list;
    List<FeedItem> mData;
    NewsItemAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager)findViewById(R.id.view_pager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawerLayout);

        setSupportActionBar(toolbar);

        SpannableString s = new SpannableString("News Watch");
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

        //set viewpager adapter
        fr_list = new ArrayList<Fragment>();
        fr_list.add(new TopNewsFragment());
        fr_list.add(new NationFragment());
        fr_list.add(new WorldFragment());
        fr_list.add(new BusinessFragment());
        fr_list.add(new EditorialsFragment());
        fr_list.add(new ScienceTechFragment());
        fr_list.add(new SportsFragment());

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fr_list);
        viewPager.setAdapter(pagerAdapter);

        //change Tab selection when swipe ViewPager
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //change ViewPager page when tab selected
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabSelectedPosition = tab.getPosition();
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, 1200000, pendingIntent );
    }

    public void setData(List<FeedItem> feedData) {
        this.mData = feedData;
    }

    public void setAdapter(NewsItemAdapter adapter) {
        this.mAdapter = adapter;
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
        } else if (id == R.id.close) {
            finish();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        switch (tabSelectedPosition) {
            case 0:
            case -1:
                ((TopNewsFragment)fr_list.get(tabSelectedPosition)).setSearchFilterData();
                break;
            case 1:
                ((NationFragment)fr_list.get(tabSelectedPosition)).setSearchFilterData();
                break;
            case 2:
                ((WorldFragment)fr_list.get(tabSelectedPosition)).setSearchFilterData();
                break;
            case 3:
                ((BusinessFragment)fr_list.get(tabSelectedPosition)).setSearchFilterData();
                break;
            case 4:
                ((EditorialsFragment)fr_list.get(tabSelectedPosition)).setSearchFilterData();
                break;
            case 5:
                ((ScienceTechFragment)fr_list.get(tabSelectedPosition)).setSearchFilterData();
                break;
            case 6:
                ((SportsFragment)fr_list.get(tabSelectedPosition)).setSearchFilterData();
                break;
            default:
                break;
        }

        final List<FeedItem> filteredModelList = filter(mData, newText);
        mAdapter.setFilter(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<FeedItem> filter(List<FeedItem> feeds, String query) {
        query = query.toLowerCase();

        final List<FeedItem> filteredModelList = new ArrayList<>();
        for (FeedItem item : feeds) {
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
