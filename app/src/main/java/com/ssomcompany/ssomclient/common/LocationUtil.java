package com.ssomcompany.ssomclient.common;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ssomcompany.ssomclient.post.PostContent;

/**
 * Created by kshgizmo on 2015. 10. 12..
 */
public class LocationUtil {
    // Logging
    private static final String TAG = "LocationUtil";
    private static String provide;

    // location settings
    private static Location myLocation;
    private static LocationManager locationManager;
    private static LocationResult locationResult;
    private static boolean isGpsEnabled;
    private static boolean isNetEnabled;

    // 위치 정보 업데이트 거리 10미터
    private static final long MIN_DISTANCE_UPDATES = 3;

    // 위치 정보 업데이트 시간 1/1000
    private static final long MIN_TIME_UPDATES = 1000 * 10;

    public static boolean getMyLocation(Context context, LocationResult result) {
        locationResult = result;
        if(locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        try {
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            // permission denied
        }

        try {
            isNetEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            // permission denied
        }

        if(!isGpsEnabled && !isNetEnabled)
            return false;

        if(isGpsEnabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_UPDATES,
                    MIN_DISTANCE_UPDATES, gpsLocationListener);

            provide = LocationManager.GPS_PROVIDER;
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_UPDATES,
                    MIN_DISTANCE_UPDATES, networkLocationListener);

            provide = LocationManager.NETWORK_PROVIDER;
        }

        Log.i(TAG, "provider : " + provide);

        return true;
    }

    public static Location getLocation(Context context) {
        if (myLocation != null) {
            Log.i(TAG, "myLocation not null");
            return myLocation;
        } else {
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, true);
            try {
                myLocation = locationManager.getLastKnownLocation(bestProvider);
            }catch (SecurityException e){
                Toast.makeText(context,"위치 정보를 가져올수 없습니다.("+e.getLocalizedMessage()+")", Toast.LENGTH_SHORT).show();
            }
            Log.i(TAG, "myLocation null, new location : " + myLocation);
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

    public static void stopLocationUpdates() {
        if(locationManager != null) {
            if(gpsLocationListener != null) locationManager.removeUpdates(gpsLocationListener);
            if(networkLocationListener != null) locationManager.removeUpdates(networkLocationListener);
        }
    }

    // Location Listener for gps
    private static LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            locationResult.getLocationCallback(location);
            myLocation = location;
//            myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    // Location Listener for Network
    private static LocationListener networkLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            locationResult.getLocationCallback(location);
            myLocation = location;
//            myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public static abstract class LocationResult {
        public abstract void getLocationCallback(Location location);
    }
}
