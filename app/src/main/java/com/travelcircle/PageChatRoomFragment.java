package com.travelcircle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by T on 2015/09/26.
 */
public class PageChatRoomFragment extends Fragment {
    private static final String KEY_MESSAGE_TEXT = "content";

    private View _view;

    public MMXChannel mChannel;
    private Menu mSubscriptionMenu;
    private MyProfile mProfile;
    private List<MMXMessage> mChannelItems;
    private ListView mChannelItemsView;
    private TextView mChannelName;
    private EditText mPublishText;
    private ImageButton mSendButton;
    private ImageButton mBackButton;
    private ImageButton mSubscribeButton;
    public AtomicBoolean mScrollToBottom = new AtomicBoolean(true);
    private static String channelName = "";

    public static PageChatRoomFragment newInstance(Context context, String channel) {
        PageChatRoomFragment fragment = new PageChatRoomFragment();
        Bundle args = new Bundle();
        args.putInt(context.getString(R.string.section_number), 2);
        channelName = channel;
        return fragment;
    }

    private MMX.EventListener mListener = new MMX.EventListener() {
        public boolean onMessageReceived(com.magnet.mmx.client.api.MMXMessage mmxMessage) {
            MMXChannel channel = mmxMessage.getChannel();
            if (channel != null && channel.getName().equals(mChannel.getName())) {
                updateChannelList();
            }
            return true;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_page_chatroom, container, false);

        findViews();
        setListeners();
        prepare();

        return _view;
    }

    private void findViews() {
        mChannelItemsView = (ListView) _view.findViewById(R.id.channel_items);
        mChannelName = (TextView) _view.findViewById(R.id.channel_name);
        mPublishText = (EditText) _view.findViewById(R.id.publishMessage);
        mSendButton = (ImageButton) _view.findViewById(R.id.imv_send);
        mBackButton = (ImageButton) _view.findViewById(R.id.btn_back);
        mSubscribeButton = (ImageButton) _view.findViewById(R.id.btn_subscribe);
    }

    private void setListeners() {
        mSubscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doShowMenu(v);
            }
        });
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToChannels();
            }
        });
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPublish(v);
            }
        });
    }

    private void prepare() {
        MMXChannel.findPublicChannelsByName(channelName, 0, 100, new MMXChannel.OnFinishedListener<ListResult<MMXChannel>>() {
            @Override
            public void onSuccess(ListResult<MMXChannel> mmxChannelListResult) {
                for (MMXChannel channel : mmxChannelListResult.items) {
                    if (channel.getName().equalsIgnoreCase(channelName)) {
                        mChannel = channel;
                        updateChannelList();
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
                Toast.makeText(getActivity(), "Failed to load channel", Toast.LENGTH_LONG).show();
                Log.e("ChatroomFragment", "Failed to load channel: " + channelName + ".  " + failureCode + ", " + throwable.getMessage());
                ((MainActivity) getActivity()).openDrawer();
            }
        });

        mProfile = MyProfile.getInstance(getActivity());

        mChannelName.setText(channelName);
    }

    public void onDestroy() {
        MMX.unregisterListener(mListener);
        super.onDestroy();
    }

    public void onResume() {
        super.onResume();
        updateChannelList();
    }

    private void updateChannelList() {
        synchronized (this) {
            if (mChannel != null) {
                mChannel.getMessages(null, null, 0, 25, false, new MMXChannel.OnFinishedListener<ListResult<com.magnet.mmx.client.api.MMXMessage>>() {
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

                if (mChannel.isSubscribed()) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_action_important);
                    mSubscribeButton.setImageBitmap(bitmap);
                } else {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_action_not_important);
                    mSubscribeButton.setImageBitmap(bitmap);
                }
            }
        });
    }

    public void doPublish(final View view) {
        HashMap<String, String> content = new HashMap<String, String>();
        content.put(KEY_MESSAGE_TEXT, mPublishText.getText().toString());
        mChannel.publish(content, new MMXChannel.OnFinishedListener<String>() {
            @Override
            public void onSuccess(String s) {
                Toast.makeText(getActivity(), "Published successfully.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Toast.makeText(getActivity(), "Unable to publish message: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        mPublishText.setText(null);
        mScrollToBottom.set(true);
        updateChannelList();
    }

    public void doShowMenu(View view) {
        PopupMenu popup = new PopupMenu(getActivity(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_chatroom, popup.getMenu());

        //decide which items to hide
        mSubscriptionMenu = popup.getMenu();

        if (mChannel.isSubscribed()) {
            //remove subcribe
            mSubscriptionMenu.removeItem(R.id.action_subscribe);
        } else {
            //remove unsubscribe
            mSubscriptionMenu.removeItem(R.id.action_unsubscribe);
        }

        if (!mChannel.getOwnerUsername().equalsIgnoreCase(MMX.getCurrentUser().getUsername())) {
            mSubscriptionMenu.removeItem(R.id.action_delete);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_subscribe:
                        doSubscribe();
                        break;
                    case R.id.action_unsubscribe:
                        doUnsubscribe();
                        break;
                    case R.id.action_delete:
                        doDelete();
                        break;
                    default:
                }
                return true;
            }
        });

        popup.show();
    }

    public void doSubscribe() {
        mChannel.subscribe(new MMXChannel.OnFinishedListener<String>() {
            public void onSuccess(String s) {
                prepare();
                Toast.makeText(getActivity(), "Subscribed successfully", Toast.LENGTH_LONG).show();
            }

            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Toast.makeText(getActivity(), "Unable to subscribe: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void doUnsubscribe() {
        mChannel.unsubscribe(new MMXChannel.OnFinishedListener<Boolean>() {
            public void onSuccess(Boolean result) {
                if (result) {
                    prepare();
                    Toast.makeText(getActivity(), "Unsubscribed successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Could not unsubscribe.", Toast.LENGTH_LONG).show();
                }
            }

            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Toast.makeText(getActivity(), "Exception caught: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void doDelete() {
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        mChannel.delete(new MMXChannel.OnFinishedListener<Void>() {
                            public void onSuccess(Void aVoid) {
                                getActivity().finish();
                            }

                            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                                Toast.makeText(getActivity(), getString(R.string.error_unable_to_delete_channel) + failureCode + ", " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
                dialog.dismiss();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dlg_delete_title)
                .setPositiveButton(R.string.ok, clickListener)
                .setNegativeButton(R.string.cancel, clickListener);
        builder.show();
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

    public void goBackToChannels() {
        ((MainActivity) getActivity()).gotoChannels();
    }

}