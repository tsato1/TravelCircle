package com.travelcircle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by T on 2015/09/26.
 */
public class PagerAdapter extends FragmentPagerAdapter
{
    int mNumOfTabs;
    ArrayList<Fragment> fragmentList;
    TabFragment1 tab1;
    TabFragment2 tab2;

    public PagerAdapter(FragmentManager fm, int NumOfTabs)
    {
        super (fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                tab1 = new TabFragment1();
                return tab1;
            case 1:
                tab2 = new TabFragment2();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount()
    {
        return mNumOfTabs;
    }

    public TabFragment1 getFragment1(){
        return tab1;
    }

    public TabFragment2 getFragment2(){
        return tab2;
    }
}