package com.ssomcompany.ssomclient.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.BitmapWorkerTask;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.LocationTracker;
import com.ssomcompany.ssomclient.common.RoundImage;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.control.SsomPermission;
import com.ssomcompany.ssomclient.control.ViewListener;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.api.model.ChatRoomItem;
import com.ssomcompany.ssomclient.widget.SsomActionBarView;
import com.ssomcompany.ssomclient.widget.dialog.CommonDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SsomMapActivity extends BaseActivity implements OnMapReadyCallback {
    private static final String TAG = SsomMapActivity.class.getSimpleName();
    private static final int REQUEST_CHECK_LOCATION_PERMISSION = 1;
    private static final int REQUEST_CHECK_DETAIL_LOCATION_PERMISSION = 2;

    private GoogleMap mMap;
    private Location myLocation;
    private SupportMapFragment mapFragment;

    private ChatRoomItem roomItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if(getIntent() != null && getIntent().getExtras() != null) {
            roomItem = (ChatRoomItem) getIntent().getSerializableExtra(CommonConst.Intent.EXTRA_ROOM_ITEM);
        }

        SsomActionBarView actionBarView = (SsomActionBarView) findViewById(R.id.ssom_action_bar);
        actionBarView.setCurrentMode(SsomActionBarView.SSOM_MAP);
        actionBarView.setSsomBarTitleLayoutGravity(Gravity.CENTER_VERTICAL);
        actionBarView.setSsomBarTitleText("채팅으로 돌아가기");
        actionBarView.setSsomBarTitleStyle(R.style.ssom_font_16_custom_4d4d4d_single);
        actionBarView.setHeartLayoutVisibility(false);
        actionBarView.setChatLayoutVisibility(false);
        actionBarView.setOnLeftNaviBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        checkLocationServiceEnabled();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "map Ready to use..");
        this.mMap = googleMap;

        setMapUiSetting();

        // marker 생성
        addMarker(roomItem);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.moveCamera(cu);
    }

    private void checkLocationServiceEnabled() {
        locationTracker = LocationTracker.getInstance();
        if(locationTracker.chkCanGetLocation()) {
            continueProcess();
        } else {
            showActivateGPSPopup(false);
        }
    }

    private void continueProcess() {
        SsomPermission.getInstance()
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .setOnPermissionListener(mPermissionListener)
                .checkPermission();
    }

    private ViewListener.OnPermissionListener mPermissionListener = new ViewListener.OnPermissionListener() {
        @Override
        public void onPermissionGranted() {
            startMapFragment();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Log.d(TAG, "denied permission size : " + deniedPermissions.size());

            // 이 권한을 필요한 이유를 설명해야하는가?
            if (ActivityCompat.shouldShowRequestPermissionRationale(SsomMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // 다이어로그같은것을 띄워서 사용자에게 해당 권한이 필요한 이유에 대해 설명합니다
                // 해당 설명이 끝난뒤 requestPermissions()함수를 호출하여 권한허가를 요청해야 합니다
                makeDialogForRequestLocationPermission();
            } else {
                ActivityCompat.requestPermissions(SsomMapActivity.this,
                        deniedPermissions.toArray(new String[deniedPermissions.size()]), REQUEST_CHECK_DETAIL_LOCATION_PERMISSION);
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CHECK_DETAIL_LOCATION_PERMISSION) {
            Map<String, Integer> permissionMap = new HashMap<>();

            for (int i = 0 ; i < permissions.length ; i++) {
                permissionMap.put(permissions[i], grantResults[i]);
            }

            // 거절을 클릭 한 경우에 해당함
            if(grantResults.length > 0 &&
                    permissionMap.get(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                makeDialogForRequestLocationPermission();
            } else {
                startMapFragment();
            }
        }
    }

    private void makeDialogForRequestLocationPermission() {
        UiUtils.makeCommonDialog(this, CommonDialog.DIALOG_STYLE_ALERT_BUTTON, R.string.dialog_notice, 0,
                R.string.dialog_explain_location_permission_message, R.style.ssom_font_16_custom_666666,
                R.string.dialog_move, R.string.dialog_close,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent();
                        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        i.addCategory(Intent.CATEGORY_DEFAULT);
                        i.setData(Uri.parse("package:" + getPackageName()));
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivityForResult(i, REQUEST_CHECK_DETAIL_LOCATION_PERMISSION);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UiUtils.makeToastMessage(getApplicationContext(), "위치권한이 없어 기능을 사용하실 수 없어요 T.T");
                        finish();
                    }
                });
    }

    private void showActivateGPSPopup(final boolean isBtnMyLocation) {
        // GPS OFF 일때 Dialog 표시
        AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
        gsDialog.setTitle("위치 서비스 설정");
        gsDialog.setMessage("무선 네트워크 사용, GPS 위성 사용을 모두 체크하셔야 정확한 위치 서비스가 가능합니다.\n위치 서비스 기능을 설정하시겠습니까?");
        gsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // GPS설정 화면으로 이동
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(intent, REQUEST_CHECK_LOCATION_PERMISSION);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!isBtnMyLocation) continueProcess();
            }
        }).create().show();
    }

    @SuppressWarnings("MissingPermission")
    private void setMapUiSetting() {
        // default my location marker disabled
        mMap.setMyLocationEnabled(true);

        // 내 위치 버튼 설정
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        // 지도 회전시키기 설정
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        // 마커 선택 시 툴바 설정
        mMap.getUiSettings().setMapToolbarEnabled(false);

        // 맵 기울이기 설정
        mMap.getUiSettings().setTiltGesturesEnabled(false);

        // 나침반 설정
        mMap.getUiSettings().setCompassEnabled(false);
    }

    private void startMapFragment(){
        locationTracker.startLocationUpdates(gpsLocationListener, networkLocationListener);
        if(mapFragment == null) mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().
                replace(R.id.container, mapFragment).commitAllowingStateLoss();
        mapFragment.getMapAsync(this);
    }

    private LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "gpsLocationListener : " + location);
            myLocation = location;
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
    private LocationListener networkLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "networkLocationListener : " + location);
            myLocation = location;
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

    private ArrayList<Marker> markers;
    private Marker marker;
    private void addMarker(final ChatRoomItem item) {
        markers = new ArrayList<>();
        for(int i = 0 ; i < 2 ; i++) {
            final boolean isOwnerItem = i == 0;
            final boolean iAmOwner = getUserId().equals(item.getOwnerId());
            final String thumbnailImageUrl = (isOwnerItem ? item.getOwnerImageUrl() : item.getParticipantImageUrl()) + "?thumbnail=200";
            final double lat = iAmOwner ? item.getLatitude() : myLocation.getLatitude();
            final double lon = iAmOwner ? item.getLongitude() : myLocation.getLongitude();

            if (NetworkManager.getInstance().hasBitmapInCache(thumbnailImageUrl)) {
                if (NetworkManager.getInstance().hasBitmapFromMemoryCache(thumbnailImageUrl)) {
                    // get bitmap from memory cache
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lon)).draggable(false)
                            .icon(BitmapDescriptorFactory.fromBitmap(getMarkerImage(item.getStatus(), isOwnerItem ? item.getSsomType() :
                                            CommonConst.SSOM.equals(item.getSsomType()) ? CommonConst.SSOA : CommonConst.SSOM,
                                    NetworkManager.getInstance().getBitmapFromMemoryCache(thumbnailImageUrl)))));
                } else {
                    // get bitmap from disk cache
                    BitmapWorkerTask diskCacheTask = new BitmapWorkerTask() {
                        @Override
                        protected void onPostExecute(Bitmap result) {
                            super.onPostExecute(result);
                            if (result != null) {
                                // Add final bitmap to caches
                                NetworkManager.getInstance().addBitmapToCache(thumbnailImageUrl, result);

                                marker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(lat, lon)).draggable(false)
                                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerImage(item.getStatus(), isOwnerItem ? item.getSsomType() :
                                                CommonConst.SSOM.equals(item.getSsomType()) ? CommonConst.SSOA : CommonConst.SSOM, result))));
                            }
                        }
                    };

                    diskCacheTask.execute(thumbnailImageUrl);
                }
            } else {
                ImageRequest imageRequest = new ImageRequest(thumbnailImageUrl, new Response.Listener<Bitmap>() {

                    @Override
                    public void onResponse(Bitmap bitmap) {
                        NetworkManager.getInstance().addBitmapToCache(thumbnailImageUrl, bitmap);
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lon)).draggable(false)
                                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerImage(item.getStatus(), isOwnerItem ? item.getSsomType() :
                                        CommonConst.SSOM.equals(item.getSsomType()) ? CommonConst.SSOA : CommonConst.SSOM, bitmap))));
                    }
                }, 0  // max width
                        , 0  // max height
                        , ImageView.ScaleType.CENTER  // scale type
                        , Bitmap.Config.RGB_565  // decode config
                        , new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
                NetworkManager.getInstance().getRequestQueue().add(imageRequest);
            }

            markers.add(marker);
        }
    }

    private Bitmap getMarkerImage(String status, String ssomType , Bitmap imageBitmap){
        Bitmap mergedBitmap = null;
        try {
            mergedBitmap = Bitmap.createBitmap(Util.convertDpToPixel(49),
                    Util.convertDpToPixel(57), Bitmap.Config.ARGB_4444);
            Canvas c = new Canvas(mergedBitmap);
            Bitmap iconBitmap = BitmapFactory.decodeResource(getResources(), CommonConst.SSOM.equals(ssomType) ?
                    R.drawable.icon_map_st_g : R.drawable.icon_map_st_r);

            Bitmap iconIng = null;
            // ing image
            if(CommonConst.Chatting.MEETING_APPROVE.equals(status)) {
                iconIng = BitmapFactory.decodeResource(getResources(), CommonConst.SSOM.equals(ssomType) ?
                        R.drawable.ssom_ing_green_big : R.drawable.ssom_ing_red_big);
            }

            Drawable iconDrawable = new BitmapDrawable(getResources(), iconBitmap);
            Drawable imageDrawable = new RoundImage(Util.cropCenterBitmap(imageBitmap));
            Drawable ingDrawable = null;
            if(iconIng != null) ingDrawable = new BitmapDrawable(getResources(), iconIng);

            iconDrawable.setBounds(0, 0,
                    Util.convertDpToPixel(49), Util.convertDpToPixel(57));
            imageDrawable.setBounds(Util.convertDpToPixel(2), Util.convertDpToPixel(2),
                    Util.convertDpToPixel(47), Util.convertDpToPixel(47));
            iconDrawable.draw(c);
            imageDrawable.draw(c);
            if(ingDrawable != null) {
                ingDrawable.setBounds(Util.convertDpToPixel(2), Util.convertDpToPixel(2),
                        Util.convertDpToPixel(47), Util.convertDpToPixel(47));
                ingDrawable.draw(c);
            }

        } catch (Exception e) {
            Log.i(TAG, "Get Marker image finished by exception..!");
        }

        return mergedBitmap;
    }
}
