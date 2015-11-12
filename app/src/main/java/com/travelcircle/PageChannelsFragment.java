package com.travelcircle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.util.List;

/**
 * Created by T on 2015/10/22.
 */
public class PageChannelsFragment extends Fragment {
    static final int REQUEST_LOGIN = 1;

    private View _view;

    private ChannelsManager mChannelsManager = null;
    private Handler mSearchHandler = new Handler();
    private ListView mListView = null;
    private EditText mSearchFilter = null;
    private ChannelHeaderListAdapter mAdapter = null;
    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String channelName = (String) view.getTag();
            if (channelName != null) {
                //todo show the chatroom page
            }
        }
    };

    public static PageChannelsFragment newInstance(Context context) {
        PageChannelsFragment fragment = new PageChannelsFragment();
        Bundle args = new Bundle();
        args.putInt(context.getString(R.string.section_number), 3);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_page_channels, container, false);

        mSearchFilter = (EditText) _view.findViewById(R.id.search);
        mSearchFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSearchHandler.removeCallbacks(null);
                mSearchHandler.postDelayed(new Runnable() {
                    public void run() {
                        updateChannelList();
                    }
                }, 700);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mChannelsManager = ChannelsManager.getInstance(getActivity());
        mAdapter = new ChannelHeaderListAdapter(getActivity(),
                mChannelsManager.getSubscribedChannels(mSearchFilter.getText().toString()),
                mChannelsManager.getOtherChannels(mSearchFilter.getText().toString()));
        mListView = (ListView) _view.findViewById(R.id.channels_list);
        mListView.setOnItemClickListener(mOnItemClickListener);
        MMX.registerListener(mListener);

        return _view;
    }

    public void onDestroy() {
        MMX.unregisterListener(mListener);
        super.onDestroy();

    }

    public void onResume() {
        super.onResume();
        if (MMX.getCurrentUser() != null) {
            updateChannelList();
        }
    }


    private MMX.EventListener mListener = new MMX.EventListener() {
        public boolean onMessageReceived(MMXMessage mmxMessage) {
            updateChannelList();
            return false;
        }
    };

    private synchronized void updateChannelList() {
        MMXChannel.getAllPublicChannels(0, 100, new MMXChannel.OnFinishedListener<ListResult<MMXChannel>>() {
            public void onSuccess(ListResult<MMXChannel> mmxChannelListResult) {
                ChannelsManager.getInstance(getActivity()).setChannels(mmxChannelListResult.items);
                updateView();
            }

            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Toast.makeText(getActivity(), "Exception: " + throwable.getMessage(),
                        Toast.LENGTH_LONG).show();
                updateView();
            }
        });
    }

    private void updateView() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                mAdapter = new ChannelHeaderListAdapter(getActivity(),
                        mChannelsManager.getSubscribedChannels(mSearchFilter.getText().toString()),
                        mChannelsManager.getOtherChannels(mSearchFilter.getText().toString()));
                mListView.setAdapter(mAdapter);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("ChannelsFragment", "onActivityResult() request=" + requestCode + ", result=" + resultCode);
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == getActivity().RESULT_OK) {
                updateChannelList();
            } else {
                getActivity().finish();
            }
        }
    }

    public void doLogout(View view) {
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        MMX.logout(new MMX.OnFinishedListener<Void>() {
                            public void onSuccess(Void aVoid) {
                                getActivity().finish();
                            }

                            public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {
                                Toast.makeText(getActivity(), "Logout failed: " + failureCode +
                                        ", " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Dialog)
                .setTitle(R.string.dlg_signout_title)
                .setMessage(R.string.dlg_signout_message)
                .setPositiveButton(R.string.ok, clickListener)
                .setNegativeButton(R.string.cancel, clickListener);
        builder.create().show();
    }

    public void doAddChannel(View view) {
        //todo from add channel
    }

    private static class ChannelHeaderListAdapter extends BaseAdapter {
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_CHANNEL = 1;
        private Context mContext;
        private List<MMXChannel> mSubscriptions;
        private List<MMXChannel> mOtherChannels;
        private LayoutInflater mLayoutInflater;
        private int mOtherChannelsHeaderPosition;

        public ChannelHeaderListAdapter(Context context, List<MMXChannel> subscriptions, List<MMXChannel> otherChannels) {
            super();
            mContext = context;
            mLayoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            mSubscriptions = subscriptions;
            mOtherChannels = otherChannels;
            mOtherChannelsHeaderPosition = mSubscriptions.size() + 1;
        }

        @Override
        public int getCount() {
            return mSubscriptions.size() + mOtherChannels.size() + 2; //two header rows
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return mContext.getString(R.string.channel_list_header_subscriptions) + " (" + mSubscriptions.size() + ")";
            } else if (position == mOtherChannelsHeaderPosition) {
                return mContext.getString(R.string.channel_list_header_other_channels) + " (" + mOtherChannels.size() + ")";
            } else {
                if (position < mOtherChannelsHeaderPosition) {
                    int subscriptionsIndex = position - 1;
                    return mSubscriptions.get(subscriptionsIndex);
                } else {
                    //look into other channels
                    int otherChannelsIndex = position - mSubscriptions.size() - 2;
                    return mOtherChannels.get(otherChannelsIndex);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int viewType = getItemViewType(position);
            switch (viewType) {
                case TYPE_HEADER:
                    if (convertView == null) {
                        convertView = mLayoutInflater.inflate(R.layout.channel_list_header, null);
                    }
                    String headerStr = (String) getItem(position);
                    TextView headerText = (TextView) convertView.findViewById(R.id.headerText);
                    headerText.setText(headerStr);
                    convertView.setTag(null);
                    convertView.setEnabled(false);
                    break;
                case TYPE_CHANNEL:
                    if (convertView == null) {
                        convertView = mLayoutInflater.inflate(R.layout.channel_list_item, null);
                    }
                    MMXChannel channel = (MMXChannel) getItem(position);
                    populateChannelView(convertView, channel);
                    break;
            }
            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mOtherChannelsHeaderPosition) {
                return TYPE_HEADER;
            }
            return TYPE_CHANNEL;
        }

        @Override
        public int getViewTypeCount() {
            //header view and channel view
            return 2;
        }

        private void populateChannelView(View view, MMXChannel channel) {
            TextView channelNameView = (TextView) view.findViewById(R.id.channel_name);
            String channelName = channel.getName();
            channelNameView.setText(channelName);

            TextView countView = (TextView) view.findViewById(R.id.new_item_count);
            int count = channel.getNumberOfMessages();
            if (count > 0) {
                countView.setVisibility(View.VISIBLE);
                countView.setText(String.valueOf(count));
            } else {
                countView.setVisibility(View.INVISIBLE);
                countView.setText(null);
            }
            view.setTag(channelName);
        }
    }

//    public void doSave(View view) {
//        Log.d("MapFragment", "dosave() called");
////        if (mSaving.compareAndSet(false, true)) {
////            final String channelName = "channelname";//mChannelName.getText().toString();
////            if (channelName.isEmpty()) {
////                mChannelName.setError(getString(R.string.error_channel_name_required));
////                return;
////            }
////            MMXChannel.create(channelName, channelName, true, new MMXChannel.OnFinishedListener<MMXChannel>() {
////                public void onSuccess(MMXChannel mmxChannel) {
////                    //add tags
////                    SparseBooleanArray checkedPositions = mTagList.getCheckedItemPositions();
////                    final HashSet<String> tags = new HashSet<String>();
////                    for (int i = 0; i < checkedPositions.size(); i++) {
////                        int position = checkedPositions.keyAt(i);
////                        boolean checked = checkedPositions.valueAt(i);
////                        if (checked) {
////                            Log.d("Fragment1", "create(): adding tag: " + mTagArray[position]);
////                            tags.add(mTagArray[position]);
////                        }
////                    }
////                    if (tags.size() > 0) {
////                        mmxChannel.setTags(tags, new MMXChannel.OnFinishedListener<Void>() {
////                            public void onSuccess(Void aVoid) {
////                                mSaving.set(false);
////                                getActivity().finish();
////
////                            }
////
////                            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
////                                Toast.makeText(getActivity(), "Channel '" + channelName + "' created, but unable to add tags: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
////                                mSaving.set(false);
////                                getActivity().finish();
////                            }
////                        });
////                        updateView();
////                    } else {
////                        mSaving.set(false);
////                        getActivity().finish();
////                    }
////                }
////
////                public void onFailure(MMXChannel.FailureCode failureCode, final Throwable throwable) {
////                    getActivity().runOnUiThread(new Runnable() {
////                        public void run() {
////                            if (throwable instanceof TopicExistsException) {
////                                mChannelName.setError(getString(R.string.error_channel_already_exists));
////                            } else if (throwable.getCause() instanceof TopicExistsException) {
////                                mChannelName.setError(throwable.getMessage());
////                            }
////                            updateViewAddChannel();
////                        }
////                    });
////                    mSaving.set(false);
////                }
////            });
////            updateViewAddChannel();
////        }
//    }
//
//    public void updateViewAddChannel() {
//        Log.d("Fragment1", "updateView() called");
//        getActivity().runOnUiThread(new Runnable() {
//            public void run() {
//                //boolean disableViews = mSaving.get();
//                //mChannelName.setEnabled(!disableViews);
//                //mTagList.setEnabled(!disableViews);
//            }
//        });
//    }
//
//    public void updateViewUpdateChannel() {
//        getActivity().runOnUiThread(new Runnable() {
//            public void run() {
////                mAdapter = new ChannelHeaderListAdapter(ChannelListActivity.this,
////                        mChannelsManager.getSubscribedChannels(mSearchFilter.getText().toString()),
////                        mChannelsManager.getOtherChannels(mSearchFilter.getText().toString()));
////                mListView.setAdapter(mAdapter);
//            }
//        });
//    }
}
