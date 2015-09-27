package com.integratehackathon;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by T on 2015/09/26.
 */
public class TabFragment2 extends Fragment {

    private View _view;

    private TextView txvArea, txvDate, txvTo, txvOn;

    public String area, date;

    private boolean nodata;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.tab_fragment_2, container, false);

        findViews();
        createHeader();

        return _view;
    }

    void findViews() {
        txvArea = (TextView) _view.findViewById(R.id.txv_area);
        txvDate = (TextView) _view.findViewById(R.id.txv_date);
        txvTo = (TextView) _view.findViewById(R.id.txv_to);
        txvOn = (TextView) _view.findViewById(R.id.txv_on);

    }

    public void createHeader() {
        if ("Choose Area".equals(area) || "Choose Date".equals(date) ||
                area == null || date == null) {
            txvTo.setText("No data");
            txvOn.setText("");
            txvDate.setText("");
            txvArea.setText("");
            nodata = true;
        }
        else {
            txvTo.setText("To ");
            txvArea.setText(area);
            txvOn.setText(" on ");
            txvDate.setText(date);
        }
    }
}