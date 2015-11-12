package com.travelcircle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.common.TopicExistsException;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseObject;


/**
 * Created by T on 2015/09/26.
 */
public class PageMapFragment extends Fragment {
    private static final LatLng TOKYO = new LatLng(35.681382, 139.766084);
    private static final LatLng HAKATA = new LatLng(33.590002, 130.42062199999998);

    private GoogleMap googleMap;
    private MapView mMapView;

    private EditText mChannelName = null;
    private AtomicBoolean mSaving = new AtomicBoolean(false);

    private View _view;

    public static PageMapFragment newInstance(Context context) {
        PageMapFragment fragment = new PageMapFragment();
        Bundle args = new Bundle();
        args.putInt(context.getString(R.string.section_number), 1);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_page_map, container, false);

        mMapView = (MapView) _view.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setUpMapIfNeeded();
        return _view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void setUpMapIfNeeded() {
        if (googleMap == null) {
            googleMap = mMapView.getMap();

            if (googleMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        googleMap.setIndoorEnabled(false);
        googleMap.setMyLocationEnabled(true);

        showTestUsersOnMap();

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                moveCameraToLatLun(true, HAKATA);
            }
        });
    }

    private void createTestUsers() {
        ParseObject userObject = new ParseObject("UserObject");
        userObject.put("username", "testuser");
        userObject.put("latitude", TOKYO.latitude);
        userObject.put("longitude", TOKYO.longitude);
        userObject.put("message", "Hello, I am a test user.");
        userObject.saveInBackground();
    }

    private void showTestUsersOnMap() {
        ParseObject userObject = new ParseObject("UserObject");
        userObject.put("username", "testuser");
        userObject.put("latitude", TOKYO.latitude);
        userObject.put("longitude", TOKYO.longitude);
        userObject.put("message", "Hello, I am a test user.");
        userObject.saveInBackground();

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng((Double) userObject.get("latitude"), (Double) userObject.get("longitude")))
                .title((String) userObject.get("username"))
                .snippet((String) userObject.get("message"))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)) // todo replace with userObject.get("photo")
                .anchor(0.5f, 0.5f));

        googleMap.setInfoWindowAdapter(new MapUserInfoAdapter(getActivity()));
        googleMap.setOnInfoWindowClickListener(new InfoWindowClickListener());

        moveCameraToLatLun(false, TOKYO);
    }

    class InfoWindowClickListener implements GoogleMap.OnInfoWindowClickListener {
        @Override
        public void onInfoWindowClick(Marker marker) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("Do you want to open a chat room with this person?");
            dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface i, int which) {
                    //todo check if the channel exists
                    //todo if not make one
                    //todo navigate to the chatroom fragment

                }
            });
            dialog.setNegativeButton(R.string.no, null);
            dialog.show();
        }
    }

    private void moveCameraToLatLun(boolean isAnimation, LatLng target) {
        CameraUpdate camera = CameraUpdateFactory
                .newCameraPosition(new CameraPosition.Builder()
                .target(target)
                .zoom(15.0f).build());

        if (isAnimation) {
            googleMap.animateCamera(camera);
        } else {
            googleMap.moveCamera(camera);
        }
    }
}