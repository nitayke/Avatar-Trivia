package com.trivia;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class MyAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;
    public MyAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm, MyAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        context = c;
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment getItem(int position) {
        if (position == 0)
            return new WeekBestFragment();
        if (position == 1)
            return new AllTimeFragment();
        return new Fragment();
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}