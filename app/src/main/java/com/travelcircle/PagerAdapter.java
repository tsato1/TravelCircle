//package com.travelcircle;
//
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//
//import java.util.ArrayList;
//
///**
// * Created by T on 2015/09/26.
// */
//public class PagerAdapter extends FragmentPagerAdapter
//{
//    int mNumOfTabs;
//    ArrayList<Fragment> fragmentList;
//    PageMapFragment pageMapFragment;
//    PageChannelListFragment pageChannelListFragment;
//    PageChatRoomFragment pageChatRoomFragment;
//    PageProfileFragment pageProfileFragment;
//
//    public PagerAdapter(FragmentManager fm, int NumOfTabs)
//    {
//        super (fm);
//        this.mNumOfTabs = NumOfTabs;
//    }
//
//    @Override
//    public Fragment getItem(int position) {
//        switch (position) {
//            case 0:
//                pageMapFragment = new PageMapFragment();
//                return pageMapFragment;
//            case 1:
//                pageChatRoomFragment = new PageChatRoomFragment();
//                return pageChatRoomFragment;
//            case 2:
//                pageChannelListFragment = new PageChannelListFragment();
//                return pageChannelListFragment;
//            case 3:
//                pageProfileFragment = new PageProfileFragment();
//                return pageProfileFragment;
//            default:
//                return null;
//        }
//    }
//
//    @Override
//    public int getCount()
//    {
//        return mNumOfTabs;
//    }
//
//    public PageMapFragment getPageMapFragment(){
//        return pageMapFragment;
//    }
//
//    public PageChatRoomFragment getPageChatRoomFragment(){
//        return pageChatRoomFragment;
//    }
//
//    public PageChannelListFragment getPageChannelListFragment() {
//        return pageChannelListFragment;
//    }
//
//    public PageProfileFragment getPageProfileFragment() {
//        return pageProfileFragment;
//    }
//}