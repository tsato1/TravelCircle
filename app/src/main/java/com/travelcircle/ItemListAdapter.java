//package com.travelcircle;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//
///**
// * Created by T on 2015/09/26.
// */
//public class ItemListAdapter extends BaseAdapter {
//
//    Context context;
//    LayoutInflater layoutInflater = null;
//    ArrayList<MyProfile> userList;
//
//    public ItemListAdapter(Context context) {
//        this.context = context;
//        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }
//
//    public void setUserList(ArrayList<MyProfile> userList) {
//        this.userList = userList;
//    }
//
//    @Override
//    public int getCount() {
//        return userList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return userList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return userList.get(position).getId(); // id doesn't have meaning
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        convertView = layoutInflater.inflate(R.layout.row_list_item, parent ,false);
//
//        ((TextView)convertView.findViewById(R.id.txv_name)).setText(userList.get(position).getUserName());
//
//        return convertView;
//    }
//
//
//}
