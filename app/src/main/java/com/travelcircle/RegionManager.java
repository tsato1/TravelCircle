package com.travelcircle;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by T on 2015/12/19.
 */
public class RegionManager {
    private static final LatLng TOKYO = new LatLng(35.681382, 139.766084);
    private static final LatLng HAKATA = new LatLng(33.590002, 130.42062199999998);
    private static final LatLng US = new LatLng(37.09024, -95.712891);

    public static LatLng getLatLng(String country) {
        if (country.equalsIgnoreCase("us")) {
            return US;
        } else if (country.equalsIgnoreCase("tokyo")) {
            return TOKYO;
        }


        return null;
    }
}
