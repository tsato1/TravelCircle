package com.travelcircle;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.barcode.Barcode;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
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
import java.util.List;

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
    private String mParseId = null;
    private byte[] mPassword = null;
    private Bitmap mPhoto = null;
    private LatLng mLocation = null;
    private String mMessage = "";
    private ParseObject mParseUser;

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

//        Bitmap bitmap;
//        if (mSharedPrefs.getString(PREF_PHOTO, null) == null) {
//            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        } else {
//            byte [] encodeByte = Base64.decode(mSharedPrefs.getString(PREF_PHOTO, null), Base64.DEFAULT);
//            bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
//        }
//        setPhoto(bitmap);

        setMessage(mSharedPrefs.getString(PREF_MESSAGE, null));
    }

    public void checkOverlapParse (String str) {
        final ParseQuery query = new ParseQuery("User");
        query.whereEqualTo("username", str);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject person, ParseException e) {
                if (e == null) {
                    mParseUser = person;
                } else {
                }
            }
        });
    }

    public synchronized void setParseUser() {
        mParseUser = new ParseObject("User");
        mParseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    mParseId = mParseUser.getObjectId();
                    setParseId(mParseId);
                }
            }
        });
    }

    public static synchronized MyProfile getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MyProfile(context.getApplicationContext());
        }
        return sInstance;
    }

    private String getParseId() {
        return mSharedPrefs.getString(PREF_ID, null);
    }

    private void setParseId(String id) {
        mSharedPrefs.edit().putString(PREF_ID, id).apply();
    }

    public String getUserName() {
        return super.getUsername();
    }

    public void setUsername(String username) {
        super.setUsername(username);
        mSharedPrefs.edit().putString(PREF_USERNAME, username).apply();
    }

    public void setUsernameToParse(String username) {
        mParseUser.put("username", username);
        mParseUser.saveInBackground();
        setUsername(username);
    }

    public void setPassword(byte[] password) {
        mPassword = password;
        mSharedPrefs.edit().putString(PREF_PASSWORD, new String(password)).apply();
    }

    public void setPasswordToParse(String password) {
        mParseUser.put("password", password);
        mParseUser.saveInBackground();
    }

    public LatLng getLocation() {
        return super.getLocation();
    }

    private void setLocation(ParseGeoPoint point) {
        super.setLocation(mLocation);
        mSharedPrefs.edit().putFloat(PREF_LAT, (float) point.getLatitude());
        mSharedPrefs.edit().putFloat(PREF_LNG, (float) point.getLongitude());
    }

    public void setLocationToParse(ParseGeoPoint point) {
        mParseUser.put("location", point);
        mParseUser.saveInBackground();
        setLocation(point);
    }

    public Bitmap getPhoto() {
        return super.getPhoto();
    }

    public void setPhoto(Bitmap photo) {
        super.setPhoto(photo);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        String bitmapString = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        mSharedPrefs.edit().putString(PREF_PHOTO, bitmapString).commit();
    }

    public void setPhotoToParse(Bitmap photo) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        String bitmapString = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        mParseUser.put("photo", bitmapString);
        mParseUser.saveInBackground();
        setPhoto(photo);
    }

    public String getMessage() {
        if (super.getMessage() != null) Log.d("asdf", super.getMessage());
        return super.getMessage();
    }

    public void setMessage(String message) {
        super.setMessage(message);
        mSharedPrefs.edit().putString(PREF_MESSAGE, message);
    }

    public void setMessageToParse(String message) {
        mParseUser.put("message", message);
        mParseUser.saveInBackground();
        setMessage(message);
    }

    public synchronized void setParametersFromParse() {
        byte[] bytes = Base64.decode(mParseUser.getString("photo").getBytes(), Base64.DEFAULT);
        mPhoto = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        mMessage = mParseUser.getString("message");
    }
}
