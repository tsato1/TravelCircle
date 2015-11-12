package com.travelcircle;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.util.Log;

/**
 * Created by T on 2015/10/20.
 */
public class PageProfileFragment extends Fragment {
    private View _view;
    private MyProfile mProfile = null;
    private ImageView profileImageView;
    private AutoCompleteTextView usernameTextView;
    private AutoCompleteTextView messageTextView;

    public static PageProfileFragment newInstance(Context context) {
        PageProfileFragment fragment = new PageProfileFragment();
        Bundle args = new Bundle();
        args.putInt(context.getString(R.string.section_number), 4);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_page_profile, container, false);

        mProfile = MyProfile.getInstance(getActivity());

        findViews();

        return _view;
    }

    private void findViews() {
        profileImageView = (ImageView) _view.findViewById(R.id.imv_photo);
        profileImageView.setImageBitmap(mProfile.getPhoto());
        usernameTextView = (AutoCompleteTextView) _view.findViewById(R.id.txv_username);
        usernameTextView.setText(mProfile.getUserName());
        messageTextView = (AutoCompleteTextView) _view.findViewById(R.id.txv_message);
        messageTextView.setText(mProfile.getMessage());
    }

    public void saveUpdatedProfile() {
        mProfile.setMessage(messageTextView.getText().toString());
    }

}
