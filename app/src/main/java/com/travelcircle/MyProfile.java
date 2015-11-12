package com.travelcircle;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.barcode.Barcode;
//import com.squareup.okhttp.MediaType;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.RequestBody;
//import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Blob;

/**
 * Created by T on 2015/09/27.
 */
public class MyProfile extends UserProfile{
    private static final String TAG = MyProfile.class.getSimpleName();
    private static final String PREFERENCES_NAME = "MyProfile";
    private static final String PREF_ID = "id";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_PHOTO = "photo";
    private static final String PREF_LOCATION = "location";
    private static final String PREF_LAT = "lat";
    private static final String PREF_LNG = "lng";
    private static final String PREF_MESSAGE = "message";

    private static MyProfile sInstance = null;
    private Context mContext = null;
    private SharedPreferences mSharedPrefs = null;
    private int mId = 0;
    private byte[] mPassword = null;
    private Bitmap mPhoto = null;
    private LatLng mLocation = null;
    private String mMessage = "";

    private MyProfile(Context context) {
        super();
        mContext = context;
        mSharedPrefs = mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        loadProfile();
    }

    private synchronized void loadProfile() {
        setUsername(mSharedPrefs.getString(PREF_USERNAME, null));
        String password = mSharedPrefs.getString(PREF_PASSWORD, null);
        mPassword = password != null ? password.getBytes() : null;
        setPhoto(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher));
        setMessage("Hi, I am a new user!");
    }

    public static synchronized MyProfile getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MyProfile(context.getApplicationContext());
        }
        return sInstance;
    }

    public int getId() {
        return mId;
    }

    public String getUserName() {
        return super.getUsername();
    }

    public void setUsername(String username) {
        super.setUsername(username);
        mSharedPrefs.edit().putString(PREF_USERNAME, username).apply();
    }

    public byte[] getPassword() {
        return mPassword;
    }

    public void setPassword(byte[] password) {
        mPassword = password;
        mSharedPrefs.edit().putString(PREF_PASSWORD, new String(password)).apply();
    }

    public LatLng getLocation() {
        return mLocation;
    }

    public void setLocation(LatLng location) {
        mLocation = location;
        mSharedPrefs.edit().putFloat(PREF_LAT, (float)location.latitude);
        mSharedPrefs.edit().putFloat(PREF_LNG, (float)location.longitude);
    }

    public Bitmap getPhoto() {
        byte[] bytes = Base64.decode(mSharedPrefs.getString(PREF_PHOTO, "").getBytes(), Base64.DEFAULT);
        mPhoto = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return mPhoto;
    }

    public void setPhoto(Bitmap photo) {
        mPhoto = photo;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        String bitmapString = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        mSharedPrefs.edit().putString(PREF_PHOTO, bitmapString).commit();
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
        mSharedPrefs.edit().putString(PREF_MESSAGE, message);
    }
}
