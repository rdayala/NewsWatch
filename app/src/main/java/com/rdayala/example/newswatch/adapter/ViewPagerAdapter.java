package com.rdayala.example.newswatch.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.rdayala.example.newswatch.fragments.BusinessFragment;
import com.rdayala.example.newswatch.fragments.EditorialsFragment;
import com.rdayala.example.newswatch.fragments.NationFragment;
import com.rdayala.example.newswatch.fragments.ScienceTechFragment;
import com.rdayala.example.newswatch.fragments.SportsFragment;
import com.rdayala.example.newswatch.fragments.TopNewsFragment;
import com.rdayala.example.newswatch.fragments.WorldFragment;

/**
 * Created by rdayala on 8/18/2016.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private Fragment mCurrentFragment;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new TopNewsFragment();
            case 1:
                return new NationFragment();
            case 2:
                return new WorldFragment();
            case 3:
                return new BusinessFragment();
            case 4:
                return new EditorialsFragment();
            case 5:
                return new ScienceTechFragment();
            case 6:
                return new SportsFragment();
            default:
                return null;
        }
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            mCurrentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public int getCount() {
        return 7;
    }
}