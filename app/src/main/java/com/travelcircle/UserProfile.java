package com.travelcircle;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by T on 2015/09/27.
 */
public class UserProfile {
    private String mUsername = null;
    private LatLng mLocation = null;
    private Bitmap mPhoto = null;
    private String mMessage = null;
    private Date mCreationDate = null;

    protected UserProfile() {}

    public UserProfile(String username, LatLng location, Bitmap photo, String message, Date creationDate) {
        mUsername = username;
        mLocation = location;
        mPhoto = photo;
        mMessage = message;
        mCreationDate = creationDate;
    }

    public final String getUsername() {
        return mUsername;
    }

    protected void setUsername(String username) {
        mUsername = username;
    }

    public LatLng getLocation() {
        return mLocation;
    }

    protected void setLocation(LatLng location) {
        mLocation = location;
    }

    public Bitmap getPhoto() {
        return mPhoto;
    }

    protected void setPhoto(Bitmap photo) {
        mPhoto = photo;
    }

    public String getMessage() {
        return mMessage;
    }

    protected void setMessage(String message) {
        mMessage = message;
    }

    public final Date getCreationDate() {
        return mCreationDate;
    }

    protected final void setCreationDate(Date creationDate) {
        mCreationDate = creationDate;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof UserProfile) || ((UserProfile) o).getUsername() == null) {
            return false;
        }
        return ((UserProfile)o).getUsername().equals(mUsername);
    }
}
