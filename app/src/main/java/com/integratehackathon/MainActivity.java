package com.integratehackathon;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    public static final String KEY_MESSAGE_TEXT = "content";

    public static String URL = "http://52.27.180.186:3003/Item";

    ViewPager viewPager;

    PagerAdapter adapter;

    TabFragment1 tabFragment1;
    TabFragment2 tabFragment2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("INPUT"));
        tabLayout.addTab(tabLayout.newTab().setText("LIST"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager)findViewById(R.id.pager);
        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                               @Override
                                               public void onTabSelected(TabLayout.Tab tab) {
                                                   viewPager.setCurrentItem(tab.getPosition());

                                                   if (tab.getText().toString().equals("LIST")) {
                                                       //adapter.getFragment2().area = adapter.getFragment1().area;
                                                       //adapter.getFragment2().date = adapter.getFragment1().date;
                                                       adapter.getFragment2().prepare();
                                                   }
                                                   else {

                                                   }
                                               }

                                               @Override
                                               public void onTabUnselected(TabLayout.Tab tab) {

                                               }

                                               @Override
                                               public void onTabReselected(TabLayout.Tab tab) {

                                               }
                                           }
        );
    }


    public void doPublish(final View view) {
        tabFragment2 = adapter.getFragment2();
        HashMap<String, String> content = new HashMap<String, String>();
        content.put(KEY_MESSAGE_TEXT, tabFragment2.mPublishText.getText().toString());
        tabFragment2.mChannel.publish(content, new MMXChannel.OnFinishedListener<String>() {
            @Override
            public void onSuccess(String s) {
                Toast.makeText(MainActivity.this, "Published successfully.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Toast.makeText(MainActivity.this, "Unable to publish message: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        tabFragment2.mPublishText.setText(null);
        tabFragment2.mScrollToBottom.set(true);
        tabFragment2.updateChannelItems();
    }

    public ViewPager getViewPager() {
        if (null == viewPager) {
            viewPager = (ViewPager) findViewById(R.id.pager);
        }
        return viewPager;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
