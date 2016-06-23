package com.travelcircle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
//import android.app.FragmentManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.app.Fragment;
import android.app.FragmentManager;
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
import android.widget.Toast;
import android.util.Log;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.magnet.max.android.ApiCallback;
import com.magnet.max.android.ApiError;
import com.magnet.max.android.Max;
import com.magnet.max.android.User;
import com.magnet.mmx.client.api.MMX;
import com.parse.Parse;

public class MainActivity extends AppCompatActivity {
    public static final String KEY_MESSAGE_TEXT = "content";
    private static final int REQUEST_LOGIN = 1;

    private String fragmentCurrentTag;
    private Toolbar toolbar;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle drawerToggle;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.setDrawerListener(drawerToggle);

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        setupDrawerContent(mNavigationView);

//        ImageView imvProfile = (ImageView) findViewById(R.id.imv_profile);
//        imvProfile.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
//        TextView txvUsername = (TextView) findViewById(R.id.txv_username);
//        txvUsername.setText(mProfile.getUserName());
//        TextView txvMessage = (TextView) findViewById(R.id.txv_message);
//        txvMessage.setText(getResources().getString(R.string.default_user_message));

        mDrawer.openDrawer(GravityCompat.START);
        fragmentCurrentTag = PageChannelsFragment.class.getSimpleName();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_frame, PageChannelsFragment.newInstance(this), fragmentCurrentTag).commit();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void setupProfileOnDrawer() {
        MyProfile profile = MyProfile.getInstance(this);
        ImageView imvProfile = (ImageView) findViewById(R.id.imv_profile);
        imvProfile.setImageBitmap(profile.getPhoto());
        TextView txvUsername = (TextView) findViewById(R.id.txv_username);
        txvUsername.setText(profile.getUserName());
        TextView txvMessage = (TextView) findViewById(R.id.txv_message);
        String str = profile.getMessage();
        if (str != null) Log.d("asdf", str);//todo have to be able to read from parse on login
        txvMessage.setText(str);
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
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                PageChannelsFragment fragment = (PageChannelsFragment) getFragmentManager().findFragmentByTag(PageChannelsFragment.class.getSimpleName());

                if (fragment != null) {
                    fragment.resetSearchFilter();
                }
            }
        };
    }

    public void openDrawer() {
        mDrawer.openDrawer(GravityCompat.START);
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
        String fragmentNewTag = "";
        switch (menuItem.getItemId()) {
            case R.id.nav_map_fragment:
                fragmentClass = PageMapFragment.class;
                fragmentNewTag = PageMapFragment.class.getSimpleName();
                break;
            case R.id.nav_channels_fragment:
                fragmentClass = PageChannelsFragment.class;
                fragmentNewTag = PageChannelsFragment.class.getSimpleName();
                break;
            case R.id.nav_chatroom_fragment:
                fragmentClass = PageChatRoomFragment.class;
                fragmentNewTag = PageChatRoomFragment.class.getSimpleName();
                break;
            case R.id.nav_profile_fragment:
                fragmentClass = PageProfileFragment.class;
                fragmentNewTag = PageProfileFragment.class.getSimpleName();
                break;
            case R.id.nav_settings_fragment:
                fragmentClass = PageMapFragment.class;
                fragmentNewTag = PageMapFragment.class.getSimpleName();
                break;
            case R.id.nav_logout:
                doLogout();
                return;
            default:
                fragmentClass = PageChannelsFragment.class;
                fragmentNewTag = PageChannelsFragment.class.getSimpleName();
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentByTag(fragmentCurrentTag);
        fragmentManager.beginTransaction()
                .remove(currentFragment)
                .add(R.id.container_frame, fragment, fragmentNewTag)
                .commit();
        fragmentCurrentTag = fragmentNewTag;

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    //from onclicklistener in PageChannelsFragment.java)
    public void gotoChatroom(String channelName) {
        MenuItem menuItem = mNavigationView.getMenu().findItem(R.id.nav_chatroom_fragment);

        fragmentCurrentTag = PageChatRoomFragment.class.getSimpleName();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container_frame, PageChatRoomFragment.newInstance(this, channelName), fragmentCurrentTag)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
    }

    public void gotoProfilePage() {
        MenuItem menuItem = mNavigationView.getMenu().findItem(R.id.nav_profile_fragment);

        fragmentCurrentTag = PageProfileFragment.class.getSimpleName();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container_frame, PageProfileFragment.newInstance(this), fragmentCurrentTag)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
    }

    public void gotoChannels() {
        MenuItem menuItem = mNavigationView.getMenu().findItem(R.id.nav_chatroom_fragment);

        fragmentCurrentTag = PageChannelsFragment.class.getSimpleName();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container_frame, PageChannelsFragment.newInstance(this), fragmentCurrentTag)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
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
        if (User.getCurrentUser() == null){
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivityForResult(loginIntent, REQUEST_LOGIN);
        }
    }

    public void doLogout() {
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        User.logout(new ApiCallback<Boolean>() {
                            public void success(Boolean aBoolean) {
                                Max.deInitModule(MMX.getModule(), new ApiCallback<Boolean>() {
                                    public void success(Boolean aBoolean) {
                                        Toast.makeText(MainActivity.this, "Logout successful.", Toast.LENGTH_SHORT).show();
                                        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivityForResult(loginIntent, REQUEST_LOGIN);
                                    }

                                    public void failure(ApiError apiError) {
                                        MainActivity.this.finish();
                                    }
                                });
                            }

                            public void failure(ApiError apiError) {
                                Toast.makeText(MainActivity.this, "Logout failed: " + apiError +
                                        ", " + apiError.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog)
                .setTitle(R.string.dlg_signout_title)
                .setMessage(R.string.dlg_signout_message)
                .setPositiveButton(R.string.ok, clickListener)
                .setNegativeButton(R.string.cancel, clickListener);
        builder.create().show();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.travelcircle/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.travelcircle/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_LOGIN) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                setupProfileOnDrawer();
            }
        }
    }
}
