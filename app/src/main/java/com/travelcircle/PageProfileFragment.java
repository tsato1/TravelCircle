package com.travelcircle;

import android.app.Fragment;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.util.Log;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

/**
 * Created by T on 2015/10/20.
 */
public class PageProfileFragment extends Fragment {
    private View _view;
    private Toolbar mToolbar;
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
        createSaveButton();

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

    private void createSaveButton() {
        mToolbar = ((MainActivity) getActivity()).getToolbar();
        mToolbar.inflateMenu(R.menu.menu_save);

        ImageButton saveButton = (ImageButton) mToolbar.getMenu().findItem(R.id.save_view).getActionView();
        saveButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_save));
        saveButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.ColorPrimaryDark));
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isReadyToSave()) {
                    saveUpdatedProfile();
                    Toast.makeText(getActivity(), "Successfully saved", Toast.LENGTH_SHORT).show();
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    ((MainActivity) getActivity()).openDrawer();
                }
            }
        });
    }

    private boolean isReadyToSave() {
        //todo restriction about username and message
        return true;
    }

    private void saveUpdatedProfile() {
        mProfile.setUsername(usernameTextView.getText().toString());
        mProfile.setMessage(messageTextView.getText().toString());
    }

    public void onDestroy() {
        super.onDestroy();
        mToolbar.getMenu().clear();
    }
}
