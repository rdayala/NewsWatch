package com.rdayala.example.newswatch.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by rdayala on 8/18/2016.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<Fragment> fr_list;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public ViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fr_list) {
        super(fm);
        this.fr_list = fr_list;
    }

    @Override
    public Fragment getItem(int position) {
        return  fr_list.get(position);
//        switch (position) {
//            case 0:
//                return new TopNewsFragment();
//            case 1:
//                return new NationFragment();
//            case 2:
//                return new WorldFragment();
//            case 3:
//                return new BusinessFragment();
//            case 4:
//                return new EditorialsFragment();
//            case 5:
//                return new ScienceTechFragment();
//            case 6:
//                return new SportsFragment();
//            default:
//                return null;
//        }
    }

    @Override
    public int getCount() {
        return 7;
    }
}