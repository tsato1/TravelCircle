package com.travelcircle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import android.util.Log;
/**
 * Created by T on 2015/10/12.
 */
public class MapUserInfoAdapter implements GoogleMap.InfoWindowAdapter {
    private View mWindow;
    private Context _context;
    private LayoutInflater _layoutInflater;

    public MapUserInfoAdapter (Context context) {
        this._context = context;
        this._layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        mWindow = _layoutInflater.inflate(R.layout.map_info_window, null);
        render(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void render(Marker marker, View view) {
        ImageView imvBadge = (ImageView) view.findViewById(R.id.imv_badge);
        imvBadge.setImageResource(R.mipmap.ic_launcher);

        TextView txvTitle = (TextView) view.findViewById(R.id.txv_title);
        txvTitle.setText(marker.getTitle());

        TextView txvSnippet = (TextView) view.findViewById(R.id.txv_snippet);
        txvSnippet.setText(marker.getSnippet());
    }
}
