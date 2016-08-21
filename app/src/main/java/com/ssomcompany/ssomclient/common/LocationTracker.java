package com.ssomcompany.ssomclient.common;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.network.api.model.SsomItem;

public class LocationTracker {
    // Logging
    private static final String TAG = LocationTracker.class.getSimpleName();

    // location settings
    private static LocationTracker locationTracker;
    private static LocationManager locationManager;
    private static boolean isGpsEnabled;
    private static boolean isNetEnabled;
    private static boolean canGetLocation;

    private LocationListener gpsLocationListener;
    private LocationListener networkLocationListener;

    // 위치 정보 업데이트 거리 (미터)
    private static final long MIN_DISTANCE_UPDATES = 100;

    // 위치 정보 업데이트 시간 (1/1000)
    private static final long MIN_TIME_UPDATES = 30000;

    private LocationTracker() {
        super();

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(BaseApplication.getInstance(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(BaseApplication.getInstance(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

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
    }

    public Location getLocation() {
        Location myLocation = getLastBestLocation();

        // 위치정보를 가져올 수 없는 경우 기본을 홍대입구 역으로 셋팅
        if(myLocation == null) {
            myLocation = new Location("initial provider");
            myLocation.setLatitude(37.55595);
            myLocation.setLongitude(126.9230138);
        }
        return myLocation;
    }

    public void startLocationUpdates(LocationListener gpsListener, LocationListener netListener) {
        gpsLocationListener = gpsListener;
        networkLocationListener = netListener;

        if(locationManager != null) {
            if(isGpsEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_UPDATES,
                        MIN_DISTANCE_UPDATES, gpsListener);
            }

            if(isNetEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_UPDATES,
                        MIN_DISTANCE_UPDATES, netListener);
            }
        }
    }

    public void stopLocationUpdates() {
        if(locationManager != null) {
            if(isGpsEnabled) locationManager.removeUpdates(gpsLocationListener);
            if(isNetEnabled) locationManager.removeUpdates(networkLocationListener);
        }
    }

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

    public String getDistanceString(double targetLatitude, double targetLongitude){
        Location myLocation = getLocation();

        Log.i(TAG, "myLocation : " + myLocation);

        if(myLocation != null) {
            float[] results = new float[1];
            Location.distanceBetween(targetLatitude, targetLongitude, myLocation.getLatitude(), myLocation.getLongitude(), results);
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

    public boolean chkCanGetLocation() {
        return canGetLocation;
    }

    public boolean chkGpsEnabled() {
        return isGpsEnabled;
    }

    public boolean chkNetEnabled() {
        return isNetEnabled;
    }
}
