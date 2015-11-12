package com.travelcircle;

import android.content.Intent;
//import android.app.FragmentManager;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.magnet.mmx.client.api.MMX;
import com.parse.Parse;

public class MainActivity extends AppCompatActivity
        /*implements NavigationDrawerFragment.NavigationDrawerCallbacks*/ {
    private static final String TAG_FRAGMENT_MAP = "tag_map";
    private static final String TAG_FRAGMENT_CHAT = "tag_chat";
    private static final String TAG_FRAGMENT_LIST = "tag_list";
    private static final String TAG_FRAGMENT_PROFILE = "tag_profile";

    public static final String KEY_MESSAGE_TEXT = "content";
    private static final int REQUEST_LOGIN = 1;

    private Toolbar toolbar;
    private DrawerLayout mDrawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private MyProfile mProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "GkmNnoUZGBwfQJd3xsGgPIUachckL2eddHN3wrvR", "EPMcSnJnN8MhBBz7TkLKEyBwwOx0xNWHm6Blg9jW");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.setDrawerListener(drawerToggle);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        setupDrawerContent(navigationView);

        setupProfileOnDrawer();

        mDrawer.openDrawer(GravityCompat.START);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_frame, PageMapFragment.newInstance(this)).commit();
    }

    private void setupProfileOnDrawer() {
        mProfile = MyProfile.getInstance(this);
        ImageView imvProfile = (ImageView) findViewById(R.id.imv_profile);
        imvProfile.setImageBitmap(mProfile.getPhoto());
        TextView txvUsername = (TextView) findViewById(R.id.txv_username);
        txvUsername.setText(mProfile.getUserName());
        TextView txvMessage = (TextView) findViewById(R.id.txv_message);
        txvMessage.setText(mProfile.getMessage());
    }

    @Override
    protected void onPostCreate(Bundle savedInstaceState) {
        super.onPostCreate(savedInstaceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                }
        );
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;

        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_map_fragment:
                fragmentClass = PageMapFragment.class;
                break;
            case R.id.nav_channels_fragment:
                fragmentClass = PageChannelsFragment.class;
                break;
            case R.id.nav_chatroom_fragment:
                fragmentClass = PageChatRoomFragment.class;
                break;
            case R.id.nav_profile_fragment:
                fragmentClass = PageProfileFragment.class;
                break;
            case R.id.nav_settings_fragment:
                fragmentClass = PageMapFragment.class;
                break;
            case R.id.nav_logout:
                return;
            default:
                fragmentClass = PageMapFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_frame, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onNavigationDrawerItemSelected(int position) {
//        mFragmentManager = getSupportFragmentManager();
//
//        if (position == 0) {
//            mFragmentManager.beginTransaction()
//                    .replace(R.id.container_frame, PageMapFragment.newInstance(this), TAG_FRAGMENT_MAP)
//                    .commit();
//        }
//        else if (position == 1) {
//            mFragmentManager.beginTransaction()
//                    .replace(R.id.container_frame, PageChatRoomFragment.newInstance(this), TAG_FRAGMENT_CHAT)
//                    .commit();
//        }
//        else if (position == 2) {
//            mFragmentManager.beginTransaction()
//                    .replace(R.id.container_frame, PageChannelListFragment.newInstance(this), TAG_FRAGMENT_LIST)
//                    .commit();
//        }
//        else if (position == 3) {
//            mFragmentManager.beginTransaction()
//                    .replace(R.id.container_frame, PageProfileFragment.newInstance(this), TAG_FRAGMENT_PROFILE)
//                    .commit();
//        }
//    }

    protected void onResume() {
        super.onResume();

        if (MMX.getCurrentUser() == null) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivityForResult(loginIntent, REQUEST_LOGIN);
        }
    }

    /* called from NavigationDrawerFragment.java */
    public void saveUpdatedProfile() {
//        PageProfileFragment fragment = (PageProfileFragment) mFragmentManager.findFragmentByTag(TAG_FRAGMENT_PROFILE);
//        fragment.saveUpdatedProfile();
//
//        mFragmentManager.beginTransaction()
//                .replace(R.id.container_frame, PageProfileFragment.newInstance(this))
//                .commit();
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
