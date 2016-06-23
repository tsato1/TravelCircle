package com.travelcircle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
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
    private Toolbar mToolbar;

    private ChannelsManager mChannelsManager = null;
    private Handler mSearchHandler = new Handler();
    private ListView mListView = null;
    private SearchView mSearchFilter = null;
    private String mSearchWord = "";
    private ChannelHeaderListAdapter mAdapter = null;

    public static PageChannelsFragment newInstance(Context context) {
        PageChannelsFragment fragment = new PageChannelsFragment();
        Bundle args = new Bundle();
        args.putInt(context.getString(R.string.section_number), 3);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_page_channels, container, false);

        if (mSearchFilter == null) {
            createSearchView();
        }

        mChannelsManager = ChannelsManager.getInstance(getActivity());
        mAdapter = new ChannelHeaderListAdapter(getActivity(),
                mChannelsManager.getSubscribedChannels(mSearchWord),
                mChannelsManager.getOtherChannels(mSearchWord));
        mListView = (ListView) _view.findViewById(R.id.channels_list);
        mListView.setOnItemClickListener(mOnItemClickListener);
        MMX.registerListener(mListener);

        return _view;
    }

    private void createSearchView() {
        mToolbar = ((MainActivity) getActivity()).getToolbar();
        mToolbar.inflateMenu(R.menu.menu_search);

        mSearchFilter = (SearchView) mToolbar.getMenu().findItem(R.id.search_view).getActionView();

        // 虫眼鏡アイコンを最初表示するかの設定
        mSearchFilter.setIconifiedByDefault(true);

        // Submitボタンを表示するかどうか
        mSearchFilter.setSubmitButtonEnabled(false);

        if (!mSearchWord.equals("")) {
            // TextView.setTextみたいなもの
            mSearchFilter.setQuery(mSearchWord, false);
        } else {
            String queryHint = this.getResources().getString(R.string.search_menu_query_hint_text);
            // placeholderみたいなもの
            mSearchFilter.setQueryHint(queryHint);
        }

        mSearchFilter.setOnQueryTextListener(this.onQueryTextListener);
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String channelName = (String) view.getTag();
            if (channelName != null) {
                ((MainActivity) getActivity()).gotoChatroom(channelName);
            }
        }
    };

    private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String searchWord) {
            // SubmitボタンorEnterKeyを押されたら呼び出されるメソッド
            setSearchWord(searchWord);
            resetSearchFilter();
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            // 入力される度に呼び出される
            return false;
        }
    };

    public void resetSearchFilter() {
        // 虫眼鏡アイコンを隠す
        mSearchFilter.setIconified(false);
        // SearchViewを隠す
        mSearchFilter.onActionViewCollapsed();
        // Focusを外す
        mSearchFilter.clearFocus();
    }

    private void setSearchWord(String searchWord) {
        //updateView(); instead of below
        mListView.setAdapter(mAdapter);
        mListView.setTextFilterEnabled(true);
    }

    public void onDestroy() {
        MMX.unregisterListener(mListener);
        super.onDestroy();
        mToolbar.getMenu().clear();
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
                //Toast.makeText(getActivity(), "Exception: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("ChannesFragment", "Exception: " + throwable.getMessage());
                updateView();
            }
        });
    }

    private void updateView() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                mAdapter = new ChannelHeaderListAdapter(getActivity(),
                        mChannelsManager.getSubscribedChannels(mSearchWord),
                        mChannelsManager.getOtherChannels(mSearchWord));
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

}
