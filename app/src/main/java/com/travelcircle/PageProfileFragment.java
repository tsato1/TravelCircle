package com.travelcircle;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by T on 2015/10/20.
 */
public class PageProfileFragment extends android.support.v4.app.Fragment {

    public static PageProfileFragment newInstance(Context context) {
        PageProfileFragment fragment = new PageProfileFragment();
        Bundle args = new Bundle();
        args.putInt(context.getString(R.string.section_number), 3);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_profile, container, false);

        return view;
    }
}
