package com.travelcircle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Region;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

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
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.common.TopicExistsException;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseObject;


/**
 * Created by T on 2015/09/26.
 */
public class PageMapFragment extends Fragment {
    private GoogleMap googleMap;
    private MapView mMapView;
    private Marker mUserMarker;
    private String mUsername;

    private MyProfile mProfile = null;

    private static boolean isChannelExist = false;

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

        mProfile = MyProfile.getInstance(getActivity());

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

        GPSTracker gps = new GPSTracker(getActivity());
        LatLng latLng;
        if (gps.canGetLocation()) {
            latLng = new LatLng((float) gps.getLatitude(), (float) gps.getLongitude());
            moveCameraToLatLun(false, latLng, 15.0f);
        } else {
            latLng = RegionManager.getLatLng(GPSTracker.getUserCountry(getActivity()));
            gps.showSettingsAlert();
            moveCameraToLatLun(false, latLng, 3.0f);
        }

        mProfile.setLocation(latLng);

        anchorUser(mProfile.getLocation());
        showTestUsersOnMap();

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                anchorUser(point);
                moveCameraToLatLun(true, point, 15.0f);
            }
        });
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                //moveCameraToLatLun(true, );
            }
        });
    }

    private void anchorUser(LatLng point) {
        if (mUserMarker != null) {
            mUserMarker.remove();
        }

        mUserMarker = googleMap.addMarker(new MarkerOptions()
                .position(point)
                .title(mProfile.getUserName())
                .snippet(mProfile.getMessage())
                .icon(BitmapDescriptorFactory.fromBitmap(mProfile.getPhoto()))
                .anchor(0.5f, 0.5f));

        googleMap.setInfoWindowAdapter(new MapUserInfoAdapter(getActivity()));
        googleMap.setOnInfoWindowClickListener(new InfoWindowClickListener());
    }

    private void createTestUsers() {
//        ParseObject userObject = new ParseObject("UserObject");
//        userObject.put("username", "testuser");
//        userObject.put("latitude", RegionManager.TOKYO.latitude);
//        userObject.put("longitude", RegionManager.TOKYO.longitude);
//        userObject.put("message", "Hello, I am a test user.");
//        userObject.saveInBackground();
    }

    private void showTestUsersOnMap() {
        ParseObject userObject = new ParseObject("UserObject");
        userObject.put("username", "testuser");
        userObject.put("latitude", RegionManager.getLatLng("TOKYO").latitude);
        userObject.put("longitude", RegionManager.getLatLng("TOKYO").longitude);
        userObject.put("message", "Hello, I am a test user.");
        userObject.saveInBackground();

        mUsername = userObject.get("username").toString();

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng((Double) userObject.get("latitude"), (Double) userObject.get("longitude")))
                .title((String) userObject.get("username"))
                .snippet((String) userObject.get("message"))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)) // todo replace with userObject.get("photo")
                .anchor(0.5f, 0.5f));

        googleMap.setInfoWindowAdapter(new MapUserInfoAdapter(getActivity()));
        googleMap.setOnInfoWindowClickListener(new InfoWindowClickListener());
    }

//    public boolean isChannelExist(String str) {
//        MMXChannel.findPublicChannelsByName(str, 0, 100, new MMXChannel.OnFinishedListener<ListResult<MMXChannel>>() {
//            @Override
//            public void onSuccess(ListResult<MMXChannel> mmxChannelListResult) {
//                isChannelExist = true;
//            }
//
//            @Override
//            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
//                isChannelExist = false;
//            }
//        });
//        return false;
//    }

    class InfoWindowClickListener implements GoogleMap.OnInfoWindowClickListener {
        @Override
        public void onInfoWindowClick(Marker marker) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

            if (marker.getTitle().equalsIgnoreCase(mProfile.getUserName())) {
                dialog.setTitle(R.string.want_to_edit_profile);
                dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface i, int which) {
                        ((MainActivity) getActivity()).gotoProfilePage();
                    }
                });
            } else {
                dialog.setTitle(R.string.want_to_chat_person);
                dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface i, int which) {
                        Toast.makeText(getActivity(), "Chat with " + mUsername + ".", Toast.LENGTH_SHORT).show();
                        doSave();
                        ((MainActivity) getActivity()).gotoChatroom(mUsername);
                    }
                });
            }
            dialog.setNegativeButton(R.string.no, null);
            dialog.show();
        }
    }

    private void doSave() {
        final String channelName = mUsername;
        MMXChannel.create(channelName, channelName, true, new MMXChannel.OnFinishedListener<MMXChannel>() {
            public void onSuccess(MMXChannel mmxChannel) {
                //add tags
//                        SparseBooleanArray checkedPositions = mTagList.getCheckedItemPositions();
//                        final HashSet<String> tags = new HashSet<String>();
//                        for (int i = 0; i < checkedPositions.size(); i++) {
//                            int position = checkedPositions.keyAt(i);
//                            boolean checked = checkedPositions.valueAt(i);
//                            if (checked) {
//                                tags.add(mTagArray[position]);
//                            }
//                        }
//                        if (tags.size() > 0) {
//                            mmxChannel.setTags(tags, new MMXChannel.OnFinishedListener<Void>() {
//                                public void onSuccess(Void aVoid) {
//                                    mSaving.set(false);
//                                }
//
//                                public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
//                                    Toast.makeText(getActivity(), "Channel '" + channelName + "' created, but unable to add tags: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
//                                    mSaving.set(false);
//                                    getActivity().finish();
//                                }
//                            });
//                            updateView();
//                        } else {
//                            mSaving.set(false);
//                            getActivity().finish();
//                        }
            }

            public void onFailure(MMXChannel.FailureCode failureCode, final Throwable throwable) {
//                        getActivity().runOnUiThread(new Runnable() {
//                            public void run() {
//                                if (throwable instanceof TopicExistsException) {
//                                    mChannelName.setError(getString(R.string.error_channel_already_exists));
//                                } else if (throwable.getCause() instanceof TopicExistsException) {
//                                    mChannelName.setError(throwable.getMessage());
//                                }
//                                updateView();
//                            }
//                        });
//                        mSaving.set(false);
            }
        });
        //updateView();
    }

    private void moveCameraToLatLun(boolean isAnimation, LatLng target, float zoom) {
        CameraUpdate camera = CameraUpdateFactory
                .newCameraPosition(new CameraPosition.Builder()
                .target(target)
                .zoom(zoom).build());

        if (isAnimation) {
            googleMap.animateCamera(camera);
        } else {
            googleMap.moveCamera(camera);
        }
    }
}