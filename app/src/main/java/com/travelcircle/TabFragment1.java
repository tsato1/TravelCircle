package com.travelcircle;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by T on 2015/09/26.
 */
public class TabFragment1 extends Fragment {
    private static final LatLng TOKYO = new LatLng(35.681382, 139.766084);
    private static final LatLng HAKATA = new LatLng(33.590002, 130.42062199999998);

    private GoogleMap mMap;

    private EditText mChannelName = null;
    private AtomicBoolean mSaving = new AtomicBoolean(false);

    private View _view;

    private List<String> listArea;

    private Item item;

    public String area, date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (_view != null) {
            ViewGroup parent = (ViewGroup)_view.getParent();
            if (parent != null) {
                parent.removeView(_view);
            }
        }

        try {
            _view = inflater.inflate(R.layout.tab_fragment_1, container, false);
        } catch (InflateException e) {
            e.printStackTrace();
        }

        setUpMapIfNeeded();
        return _view;
    }

    private void setUpMapIfNeeded() {
        /*** Do a null check to confirm that we have not already instantiated the map. ***/
        if (mMap == null) {
            /*** Try to obtain the map from the SupportMapFragment.***/
            mMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.fragment_map)).getMap();
            /*** Check if we were successful in obtaining the map. ***/
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mMap.setIndoorEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.addMarker(new MarkerOptions()
                .position(TOKYO)
                .title("Tokyo Station")
                .snippet("testteset")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
                .anchor(0.5f, 0.5f));

        moveCameraToLatLun(false, TOKYO);

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                moveCameraToLatLun(true, HAKATA);
            }
        });
    }

    private void moveCameraToLatLun(boolean isAnimation, LatLng target) {
        CameraUpdate camera = CameraUpdateFactory
                .newCameraPosition(new CameraPosition.Builder()
                .target(target)
                .zoom(15.0f).build());

        if (isAnimation) {
            mMap.animateCamera(camera);
        } else {
            mMap.moveCamera(camera);
        }
    }



//    class postData extends AsyncTask<String, String, String> {
//        @Override
//        protected String doInBackground(String... arg0) {
//            Log.d("Fragment1", "Posting data");
//
//            JSONObject jsonObject = new JSONObject();
//
//            try {
//                jsonObject.put(Item.ITEM_NAME, item.getName());
//                jsonObject.put(Item.ITEM_DATE, item.getDate());
//                jsonObject.put(Item.ITEM_AREA, item.getArea());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            try {
//                OkHttpClient client = new OkHttpClient();
//                Request request = new Request.Builder()
//                        .url(MainActivity.URL)
//                        .post(RequestBody.create(
//                                MediaType.parse("application/json; charset=utf-8"),
//                                jsonObject.toString())
//                        )
//                        .build();
//                Response response = client.newCall (request).execute();
//                Log.d("responce", response.toString());
//                if (response.isSuccessful()) {
//                    response.body().string();
//                    Log.d("Post Request", "Post success");
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.e("Fragment1", "Post failed");
//            }
//
//            return "";
//        }
//
//        protected void onPostExecute(String str) {
//        }
// }

    public void doSave(View view) {
        Log.d("Fragment1", "dosave() called");
        if (mSaving.compareAndSet(false, true)) {
            final String channelName = "channelname";//mChannelName.getText().toString();
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
                                    updateViewAddChannel();
                                }
                            });
                            mSaving.set(false);
                        }
                    });
            updateViewAddChannel();
        }
    }

    public void updateViewAddChannel() {
        Log.d("Fragment1", "updateView() called");
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                boolean disableViews = mSaving.get();
                mChannelName.setEnabled(!disableViews);
                //mTagList.setEnabled(!disableViews);
            }
        });
    }

    public void updateViewUpdateChannel() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
//                mAdapter = new ChannelHeaderListAdapter(ChannelListActivity.this,
//                        mChannelsManager.getSubscribedChannels(mSearchFilter.getText().toString()),
//                        mChannelsManager.getOtherChannels(mSearchFilter.getText().toString()));
//                mListView.setAdapter(mAdapter);
            }
        });
    }
}