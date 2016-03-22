package com.ssomcompany.ssomclient.common;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.network.api.model.SsomItem;

public class LocationTracker {
    // Logging
    private static final String TAG = LocationTracker.class.getSimpleName();

    // location settings
    private static LocationTracker locationTracker;
    private static Location myLocation;
    private static LocationManager locationManager;
    private static LocationResult locationResult;
    private static boolean isGpsEnabled;
    private static boolean isNetEnabled;
    private static boolean canGetLocation;

    // 위치 정보 업데이트 거리 (미터)
    private static final long MIN_DISTANCE_UPDATES = 3;

    // 위치 정보 업데이트 시간 (1/1000)
    private static final long MIN_TIME_UPDATES = 1000;

    private LocationTracker() {
        super();
        if(locationManager == null) {
            locationManager = (LocationManager) BaseApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
        }

        getMyLocation();
    }

    public static synchronized LocationTracker getInstance() {
        if(locationTracker == null) {
            locationTracker = new LocationTracker();
        }

        return locationTracker;
    }

    private void getMyLocation() {
        String provide;

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

        canGetLocation = !(!isGpsEnabled && !isNetEnabled);
        Log.d(TAG, "getMyLocation() : " + canGetLocation);

        if(!canGetLocation) return;

        if(isNetEnabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_UPDATES,
                    MIN_DISTANCE_UPDATES, networkLocationListener);

            provide = LocationManager.NETWORK_PROVIDER;
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_UPDATES,
                    MIN_DISTANCE_UPDATES, gpsLocationListener);

            provide = LocationManager.GPS_PROVIDER;
        }

        Log.i(TAG, "provider : " + provide);
    }

    public Location getLocation() {
        myLocation = getLastBestLocation();

        Log.i(TAG, "myLocation null, new location : " + myLocation);

        // 위치정보를 가져올 수 없는 경우 기본을 홍대입구 역으로 셋팅
        if(myLocation == null) {
            myLocation = new Location("initial provider");
            myLocation.setLatitude(37.55595);
            myLocation.setLongitude(126.9230138);
        }
        return myLocation;
    }

    public void stopLocationUpdates() {
        if(locationManager != null) {
            if(isGpsEnabled) locationManager.removeUpdates(gpsLocationListener);
            if(isNetEnabled) locationManager.removeUpdates(networkLocationListener);
        }
    }

    public void setLocationResult(LocationResult result) {
        locationResult = result;
    }

    // Location Listener for gps
    private static LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "gpsLocationListener : " + location);
            myLocation = location;
            locationResult.getLocationCallback(location);
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
            Log.d(TAG, "networkLocationListener : " + location);
            myLocation = location;
            locationResult.getLocationCallback(location);
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

    /**
     * @return the last know best location
     */
    private Location getLastBestLocation() {
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        } else {
            return locationNet;
        }
    }

    public boolean chkCanGetLocation() {
        return canGetLocation;
    }

    public static abstract class LocationResult {
        public abstract void getLocationCallback(Location location);
    }
}
