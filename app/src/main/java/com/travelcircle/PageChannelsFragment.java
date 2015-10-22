package com.travelcircle;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by T on 2015/09/26.
 */
public class PageChannelsFragment extends Fragment {

    private View _view;

    public String area, date;

    private boolean nodata = true;


    private MyProfile mProfile;
    public MMXChannel mChannel;
    private List<MMXMessage> mChannelItems;
    private ListView mChannelItemsView;
    private TextView mChannelName;
    public EditText mPublishText;
    public AtomicBoolean mScrollToBottom = new AtomicBoolean(true);

    public static PageChannelsFragment newInstance(Context context) {
        PageChannelsFragment fragment = new PageChannelsFragment();
        Bundle args = new Bundle();
        args.putInt(context.getString(R.string.section_number), 2);
        return fragment;
    }

    private MMX.EventListener mListener = new MMX.EventListener() {
        public boolean onMessageReceived(com.magnet.mmx.client.api.MMXMessage mmxMessage) {
            MMXChannel channel = mmxMessage.getChannel();
            if (channel != null && channel.getName().equals(mChannel.getName())) {
                updateChannelItems();
            }
            return true;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_page_channels, container, false);

        Log.d("TabFragment2", "onCreate() called");

        mChannelItemsView = (ListView) _view.findViewById(R.id.channel_items);
        mChannelName = (TextView) _view.findViewById(R.id.channel_name);
        mPublishText = (EditText) _view.findViewById(R.id.publishMessage);

        return _view;
    }

    public void prepare() {
        final String channelName = "channelname";
        Log.d("TabFragment2", "onCreate(): channelName=" + channelName);
        MMXChannel.findPublicChannelsByName(channelName, 0, 100, new MMXChannel.OnFinishedListener<ListResult<MMXChannel>>() {
            @Override
            public void onSuccess(ListResult<MMXChannel> mmxChannelListResult) {
                for (MMXChannel channel : mmxChannelListResult.items) {
                    if (channel.getName().equalsIgnoreCase(channelName)) {
                        mChannel = channel;
                        updateChannelItems();
                        break;
                    }
                }
                if (mChannel == null) {
                    Toast.makeText(getActivity(), "Unable to load channel: " + channelName, Toast.LENGTH_LONG).show();
                    getActivity().finish();
                    return;
                }
                MMX.registerListener(mListener);
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Toast.makeText(getActivity(), "Failed to load channel: " + channelName + ".  " + failureCode + ", " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("TabFragment2", "Failed to load channel: " + channelName + ".  " + failureCode + ", " + throwable.getMessage());
                getActivity().finish();
            }
        });
        mProfile = MyProfile.getInstance(getActivity());

        mChannelName.setText(channelName);
    }

//    void onDestroy() {
//        MMX.unregisterListener(mListener);
//        super.onDestroy();
//    }
//
//    void onResume() {
//        super.onResume();
//        updateChannelItems();
//    }

    public void updateChannelItems() {
        synchronized (this) {
            if (mChannel != null) {
                mChannel.getMessages(null, null, 0, 25, false,
                        new MMXChannel.OnFinishedListener<ListResult<com.magnet.mmx.client.api.MMXMessage>>() {
                            public void onSuccess(ListResult<com.magnet.mmx.client.api.MMXMessage> mmxMessages) {
                                //reverse the list
                                mChannelItems = new ArrayList<MMXMessage>();
                                for (int i = mmxMessages.items.size(); --i >= 0; ) {
                                    mChannelItems.add(mmxMessages.items.get(i));
                                }
                                mScrollToBottom.set(true);
                                updateListView();
                            }

                            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                                Toast.makeText(getActivity(), "Unable to retrieve items: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }
    }

    private void updateListView() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (mChannelItems != null) {
                    ChannelItemsAdapter adapter = new ChannelItemsAdapter(getActivity(), mChannelItems, mProfile);
                    mChannelItemsView.setAdapter(adapter);
                    if (mScrollToBottom.compareAndSet(true, false)) {
                        mChannelItemsView.setSelection(adapter.getCount() - 1);
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mPublishText.getWindowToken(), 0);
                    }
                }
            }
        });
    }

    private static class ChannelItemsAdapter extends ArrayAdapter<MMXMessage> {
        private static final int[] COLOR_IDS = {R.color.chat_1, R.color.chat_2, R.color.chat_3, R.color.chat_4, R.color.chat_5, R.color.chat_6};
        private static final int TYPE_ME = 0;
        private static final int TYPE_THEM = 1;
        private MyProfile mProfile;
        private LayoutInflater mInflater;
        private DateFormat mFormatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

        public ChannelItemsAdapter(Context context, List<MMXMessage> messages, MyProfile profile) {
            super(context, 0, messages);
            mProfile = profile;
            mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            MMXMessage message = getItem(position);
            int colorResId = 0;
            String authorStr = message.getSender().getUsername();
            if (authorStr == null) {
                authorStr = getContext().getString(R.string.chat_unknown);
            }
            switch (type) {
                case TYPE_ME:
                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.channel_item_me, null);
                    }
                    colorResId = R.color.chat_me;
                    break;
                case TYPE_THEM:
                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.channel_item_them, null);
                    }
                    //set author and color
                    colorResId = COLOR_IDS[Math.abs(authorStr.hashCode() % COLOR_IDS.length)];

                    TextView author = (TextView) convertView.findViewById(R.id.author);
                    author.setText(authorStr + " - ");
                    break;
            }
            TextView datePosted = (TextView) convertView.findViewById(R.id.datePosted);
            datePosted.setText(mFormatter.format(message.getTimestamp()));
            TextView messageText = (TextView) convertView.findViewById(R.id.messageText);
            messageText.setBackgroundResource(colorResId);
            messageText.setText(message.getContent().get(MainActivity.KEY_MESSAGE_TEXT));
            return convertView;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            MMXMessage message = getItem(position);
            if (mProfile.getUsername().equals(message.getSender().getUsername())) {
                //me
                return TYPE_ME;
            } else {
                //them
                return TYPE_THEM;
            }
        }
    }



}