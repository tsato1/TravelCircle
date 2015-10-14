package com.travelcircle;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.vision.barcode.Barcode;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    private Barcode.GeoPoint mLocation = null;
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

    public byte[] getPassword() {
        return mPassword;
    }

    public void setUsername(String username) {
        super.setUsername(username);
        mSharedPrefs.edit().putString(PREF_USERNAME, username).apply();
    }

    public void setPassword(byte[] password) {
        mPassword = password;
        mSharedPrefs.edit().putString(PREF_PASSWORD, new String(password)).apply();
    }

    public void setLocation(Barcode.GeoPoint location) {
        mLocation = location;
        mSharedPrefs.edit().putFloat(PREF_LAT, (float)location.lat);
        mSharedPrefs.edit().putFloat(PREF_LNG, (float)location.lng);
    }

    public Barcode.GeoPoint getLocation() {
        return mLocation;
    }

    public void setPhoto(Bitmap photo) {
        mPhoto = photo;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        String bitmapString = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        mSharedPrefs.edit().putString(PREF_PHOTO, bitmapString).commit();
    }

    public Bitmap getPhoto() {
        byte[] bytes = Base64.decode(mSharedPrefs.getString(PREF_PHOTO, "").getBytes(), Base64.DEFAULT);
        mPhoto = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return mPhoto;
    }

    public void setMessage(String message) {
        mMessage = message;
        mSharedPrefs.edit().putString(PREF_MESSAGE, message);
    }

    public String getMessage() {
        return mMessage;
    }


    class postDataToParse extends AsyncTask<String, String, String>
    {
        @Override
        protected String doInBackground(String... arg0) {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put(PREF_USERNAME, getUserName());
                jsonObject.put(PREF_LOCATION, getLocation());
                jsonObject.put(PREF_MESSAGE, getMessage());
                jsonObject.put(PREF_PHOTO, getPhoto());
            } catch(JSONException e) {
                e.printStackTrace();
            }

            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(MainActivity.URL)
                        .post(RequestBody.create(
                                        MediaType.parse("application/json; charset=utf-8"),
                                        jsonObject.toString()
                                )
                        )
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()){
                    response.body().string();
                    Log.i("Section1Fragment", "doPost success");
                }
            }catch(IOException e){
                Log.e("Section1Fragment", "error orz:" + e.getMessage(), e);
            }

            return "";
        }

        protected void onPostExecute(String str) {

        }
    }
}
