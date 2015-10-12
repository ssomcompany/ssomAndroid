package com.ssomcompany.ssomclient.common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.widget.Toast;

import com.ssomcompany.ssomclient.post.PostContent;

/**
 * Created by kshgizmo on 2015. 10. 12..
 */
public class LocationUtil {
    static public Location myLocation;

    public static Location getMyLocation(Context context) {
        if (myLocation != null) {
            return myLocation;
        } else {
            LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = mLocationManager.getBestProvider(criteria, false);
            try {
                myLocation = mLocationManager.getLastKnownLocation(bestProvider);
            }catch (SecurityException e){
                Toast.makeText(context,"위치 정보를 가져올수 없습니다.("+e.getLocalizedMessage()+")",Toast.LENGTH_SHORT).show();
            }
            return myLocation;
        }
    }

    public static String getDistanceString(PostContent.PostItem item){
        if(myLocation!=null) {
            float[] results = new float[1];
            Location.distanceBetween(item.lat, item.lng, myLocation.getLatitude(), myLocation.getLongitude(), results);
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
