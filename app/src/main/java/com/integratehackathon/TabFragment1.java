package com.integratehackathon;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.common.TopicExistsException;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by T on 2015/09/26.
 */
public class TabFragment1 extends Fragment {
    private EditText mChannelName = null;
//    private ListView mTagList = null;
//    private String[] mTagArray = null;
    private AtomicBoolean mSaving = new AtomicBoolean(false);

    private final static String[] areaArray = {"Choose Area", "Africa", "Americas", "Asia", "Europe", "Pacific Area"};
    private final static String[] countries_america = {"USA", "Canada", "Mexico"};
    private final static String[] countries_europe = {"UK", "France", "Germany"};
    private final static String[] countries_asia = {"China", "Japan", "Korea"};
    private final static String[] countries_africa = {"South Africa", "Algeria", "Egypt"};
    private final static String[] countries_pacific = {"Australia", "New Zealand", "Indonesia"};

    private View _view;

    private Button btnDate, btnGo;
    private Spinner spnPlace;
    private List<String> listArea;

    private Item item;

    public String area, date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.tab_fragment_1, container, false);

        findViews();
        setListeners();

        item = new Item();
        listArea = new ArrayList<String>();
        for (int i = 0; i < areaArray.length; i++) {
            listArea.add(areaArray[i]);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listArea);
        spnPlace.setAdapter(dataAdapter);


        //mChannelName = (EditText) _view.findViewById(R.id.channelName);
//        mTagList = (ListView) findViewById(R.id.tagList);
//        mTagList.setAdapter(new ArrayAdapter<String>(this, R.layout.simple_list_item_checked, mTagArray));

        return _view;
    }

    void findViews() {
        btnDate = (Button) _view.findViewById(R.id.btn_date);
        btnGo = (Button) _view.findViewById(R.id.btn_go);
        spnPlace = (Spinner) _view.findViewById(R.id.spn_place);
    }

    void setListeners() {
        btnDate.setOnClickListener(new ButtonClickListener());
        btnGo.setOnClickListener(new ButtonClickListener());
        spnPlace.setOnItemSelectedListener(new SpinnerItemClickListener());
    }

    class ButtonClickListener implements View.OnClickListener {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_date:
                    showDatePickerDialog();
                    break;
                case R.id.btn_go:
                    saveData();
                    area = item.getArea();
                    date = item.getDate();
                    Log.d("test", date);
                    ((MainActivity)getActivity()).getViewPager().setCurrentItem(1);
                    break;
            }
        }
    }

    class SpinnerItemClickListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.d("Selected item: ", parent.getItemAtPosition(position).toString());
            item.setArea(parent.getItemAtPosition(position).toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }
    }

    void showDatePickerDialog() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker picker, int year, int month, int day) {
                GregorianCalendar cal = new GregorianCalendar(year, month, day);
                Date date = cal.getTime();
                String str = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(date);
                btnDate.setText(str);
            }
        }, year, month - 1, day);
        dialog.show();
    }

    boolean readyToSave() {
        if (item.getArea().equals("Choose Area")) { // must be equal to the stringArray[0]!!!!
            Toast.makeText(getActivity(), "Please choose area you want to travel.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    void saveData() {
        if (readyToSave()) {
            item.setDate(btnDate.getText().toString()); // area is set in spinner
            new postData().execute();
        }
    }

    class postData extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... arg0) {
            Log.d("Fragment1", "Posting data");

            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put(Item.ITEM_NAME, item.getName());
                jsonObject.put(Item.ITEM_DATE, item.getDate());
                jsonObject.put(Item.ITEM_AREA, item.getArea());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(MainActivity.URL)
                        .post(RequestBody.create(
                                MediaType.parse("application/json; charset=utf-8"),
                                jsonObject.toString())
                        )
                        .build();
                Response response = client.newCall (request).execute();
                Log.d("responce", response.toString());
                if (response.isSuccessful()) {
                    response.body().string();
                    Log.d("Post Request", "Post success");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Fragment1", "Post failed");
            }

            return "";
        }

        protected void onPostExecute(String str) {
        }
    }

    public void doSave(View view) {
        Log.d("Fragment1", "dosave() called");
        if (mSaving.compareAndSet(false, true)) {
            final String channelName = "ChannelName";//mChannelName.getText().toString();
            if (channelName.isEmpty()) {
                mChannelName.setError(getString(R.string.error_channel_name_required));
                return;
            }
            MMXChannel.create(channelName, channelName, true, new MMXChannel.OnFinishedListener<MMXChannel>() {
                        public void onSuccess(MMXChannel mmxChannel) {
                            //add tags
                            //SparseBooleanArray checkedPositions = mTagList.getCheckedItemPositions();
//                            final HashSet<String> tags = new HashSet<String>();
//                            for (int i = 0; i < checkedPositions.size(); i++) {
//                                int position = checkedPositions.keyAt(i);
//                                boolean checked = checkedPositions.valueAt(i);
//                                if (checked) {
//                                    Log.d("Fragment1", "create(): adding tag: " + mTagArray[position]);
//                                    tags.add(mTagArray[position]);
//                                }
//                            }
//                            if (tags.size() > 0) {
//                                mmxChannel.setTags(tags, new MMXChannel.OnFinishedListener<Void>() {
//                                    public void onSuccess(Void aVoid) {
//                                        mSaving.set(false);
//                                        getActivity().finish();
//
//                                    }
//
//                                    public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
//                                        Toast.makeText(getActivity(), "Channel '" + channelName + "' created, but unable to add tags: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
//                                        mSaving.set(false);
//                                        getActivity().finish();
//                                    }
//                                });
//                                updateView();
//                            } else
                            {
                                mSaving.set(false);
                                getActivity().finish();
                            }
                        }

                        public void onFailure(MMXChannel.FailureCode failureCode, final Throwable throwable) {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    if (throwable instanceof TopicExistsException) {
                                        mChannelName.setError(getString(R.string.error_channel_already_exists));
                                    } else if (throwable.getCause() instanceof TopicExistsException) {
                                        mChannelName.setError(throwable.getMessage());
                                    }
                                    updateView();
                                }
                            });
                            mSaving.set(false);
                        }
                    });
            updateView();
        }
    }

    public void updateView() {
        Log.d("Fragment1", "updateView() called");
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                boolean disableViews = mSaving.get();
                mChannelName.setEnabled(!disableViews);
                //mTagList.setEnabled(!disableViews);
            }
        });
    }
}