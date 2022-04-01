package com.example.sdaassign4_2021;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * @author Chris Coughlan 2019
 * @author Edited by Rafael Izarra 2022
 *
 * A ViewAdapter class using RecycleView
 */
public class ViewPageAdapter extends FragmentPagerAdapter {

    private Context context;

    ViewPageAdapter(FragmentManager fm, int behavior, Context nContext) {
        super(fm, behavior);
        context = nContext;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        Fragment fragment = new Fragment();

        //finds the tab position (note array starts at 0)
        position = position+1;

        //finds the fragment
        switch (position)
        {
            case 1:
                //code
                fragment = new Welcome();
                break;
            case 2:
                //code
                fragment = new ProductActivity();
                break;
            case 3:
                fragment = new Settings();
                break;
            case 4:
                fragment = new CartActivity();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }


}
