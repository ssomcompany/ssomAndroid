package com.ssomcompany.ssomclient.common;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;
import com.ssomcompany.ssomclient.post.PostContent;

/**
 * Created by kshgizmo on 2015. 10. 12..
 */
public class LocationUtil {
    private static Location myLocation;
    private LocationManager locationManager;
    private LocationResult locationResult;
    private boolean isGpsEnabled;
    private boolean isNetEnabled;
    private String provide;

    public static void setMyLocation(Location location) {
        myLocation = location;
    }

    public boolean getMyLocation(Context context, LocationResult result) {
        locationResult = result;
        if(locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsLocationListener);
        }

        return true;

//        if (myLocation != null) {
//            return myLocation;
//        } else {
//            LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//            Criteria criteria = new Criteria();
//            String bestProvider = mLocationManager.getBestProvider(criteria, true);
//            try {
//                myLocation = mLocationManager.getLastKnownLocation(bestProvider);
//            }catch (SecurityException e){
//                Toast.makeText(context,"위치 정보를 가져올수 없습니다.("+e.getLocalizedMessage()+")",Toast.LENGTH_SHORT).show();
//            }
//            return myLocation;
//        }
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

    // Location Listener
    private LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
//            timer1.cancel();
//            locationResult.gotLocation(location);

            try {

                locationManager.removeUpdates(this);
//                locationManager.removeUpdates(locationListenerNetwork);

            } catch (SecurityException e) {
//                Log.e("PERMISSION_EXCEPTION", "PERMISSION_NOT_GRANTED");
            }
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

}
