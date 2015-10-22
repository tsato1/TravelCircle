package com.travelcircle;

import android.app.Fragment;
import android.content.Intent;
//import android.app.FragmentManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.util.Log;

import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.parse.Parse;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    public static final String KEY_MESSAGE_TEXT = "content";
    private static final int REQUEST_LOGIN = 1;
    public static String URL = "http://52.27.180.186:3003/Item";

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;


//    private DrawerLayout mDrawerLayout;
//    private ListView mDrawerList;

//
//    private PagerAdapter adapter;
//    private PageMapFragment pageMapFragment;
//    private PageChannelsFragment pageChannelFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "GkmNnoUZGBwfQJd3xsGgPIUachckL2eddHN3wrvR", "EPMcSnJnN8MhBBz7TkLKEyBwwOx0xNWHm6Blg9jW");

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (position == 0) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container_frame, PageMapFragment.newInstance(this))
                    .commit();
        }
        else if (position == 1) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container_frame, PageChannelsFragment.newInstance(this))
                    .commit();
        }
        else if (position == 2) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container_frame, PageProfileFragment.newInstance(this))
                    .commit();
        }
    }

    protected void onResume() {
        super.onResume();
        if (MMX.getCurrentUser() == null) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivityForResult(loginIntent, REQUEST_LOGIN);
        } else {
            //populate or update the view
            updateChannelList();
        }
    }

    private synchronized void updateChannelList() {
//        MMXChannel.getAllPublicChannels(0, 100, new MMXChannel.OnFinishedListener<ListResult<MMXChannel>>() {
//            public void onSuccess(ListResult<MMXChannel> mmxChannelListResult) {
//                ChannelsManager.getInstance(MainActivity.this).setChannels(mmxChannelListResult.items);
//                pageMapFragment.updateViewUpdateChannel();
//            }
//
//            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
//                Toast.makeText(MainActivity.this, "Exception: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
//                pageMapFragment.updateViewUpdateChannel();
//            }
//        });
    }

    public void doPublish(final View view) {
//        pageChannelFragment = adapter.getFragment2();
//        HashMap<String, String> content = new HashMap<String, String>();
//        content.put(KEY_MESSAGE_TEXT, pageChannelFragment.mPublishText.getText().toString());
//        pageChannelFragment.mChannel.publish(content, new MMXChannel.OnFinishedListener<String>() {
//            @Override
//            public void onSuccess(String s) {
//                Toast.makeText(MainActivity.this, "Published successfully.", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
//                Toast.makeText(MainActivity.this, "Unable to publish message: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//        pageChannelFragment.mPublishText.setText(null);
//        pageChannelFragment.mScrollToBottom.set(true);
//        pageChannelFragment.updateChannelItems();
    }
}
