package com.ssomcompany.ssomclient.common;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.network.api.model.SsomItem;

public class LocationUtil {
    // Logging
    private static final String TAG = LocationUtil.class.getSimpleName();

    public static String getDistanceString(SsomItem item){
        Location myLocation = LocationTracker.getInstance().getLocation();

        Log.i(TAG, "myLocation : " + myLocation);

        if(myLocation!=null) {
            float[] results = new float[1];
            Location.distanceBetween(item.getLatitude(), item.getLongitude(), myLocation.getLatitude(), myLocation.getLongitude(), results);
            float distance = results[0];
            if(distance > 1000){
                int km = (int) (distance/1000);
                return km+"km";
            }else{
                int m = (int) distance;
                return m+"m";
            }
        }
        return "";
    }
}
