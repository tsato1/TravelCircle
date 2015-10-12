package com.travelcircle;

import android.app.Application;
import android.content.Context;

import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXMessage;

/**
 * Created by T on 2015/09/27.
 */
public class MyAplication extends Application {

    Context context;

    public void onCreate() {
        super.onCreate();

        context = this;

        MMX.init(this, R.raw.travelcircle);
        MMX.registerListener(new MMX.EventListener() {
            @Override
            public boolean onMessageReceived(MMXMessage mmxMessage) {
                return false;
            }
        });

    }
}
