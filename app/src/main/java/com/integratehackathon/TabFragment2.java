package com.integratehackathon;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by T on 2015/09/26.
 */
public class TabFragment2 extends Fragment {

    private View _view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.tab_fragment_2, container, false);


        return _view;
    }
}