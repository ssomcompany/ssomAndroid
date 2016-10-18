package com.ssomcompany.ssomclient.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.LocationTracker;
import com.ssomcompany.ssomclient.common.RoundImage;
import com.ssomcompany.ssomclient.common.SsomPreferences;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.control.SsomPermission;
import com.ssomcompany.ssomclient.control.ViewListener;
import com.ssomcompany.ssomclient.fragment.DetailFragment;
import com.ssomcompany.ssomclient.fragment.FilterFragment;
import com.ssomcompany.ssomclient.fragment.NavigationDrawerFragment;
import com.ssomcompany.ssomclient.fragment.SsomListFragment;
import com.ssomcompany.ssomclient.network.APICaller;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.api.GetSsomList;
import com.ssomcompany.ssomclient.network.api.SsomPostDelete;
import com.ssomcompany.ssomclient.network.api.model.SsomItem;
import com.ssomcompany.ssomclient.network.model.SsomResponse;
import com.ssomcompany.ssomclient.push.MessageCountCheck;
import com.ssomcompany.ssomclient.push.MessageManager;
import com.ssomcompany.ssomclient.widget.SsomActionBarView;
import com.ssomcompany.ssomclient.widget.dialog.CommonDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends BaseActivity
        implements ViewListener.NavigationDrawerCallbacks,
        ViewListener.OnPostItemInteractionListener, ViewListener.OnDetailFragmentInteractionListener,
        OnMapReadyCallback, ViewListener.OnFilterFragmentInteractionListener, MessageCountCheck {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_SSOM_WRITE = 1;
    private static final int REQUEST_SSOM_LOGIN = 2;
    private static final int REQUEST_CHECK_LOCATION_PERMISSION = 3;
    private static final int REQUEST_CHECK_DETAIL_LOCATION_PERMISSION = 4;
    private static final int REQUEST_PROFILE_ACTIVITY = 5;

    private static final String MAP_VIEW = "map";
    private static final String LIST_VIEW = "list";
    private static boolean canFinish;
    private static Toast toast = null;

    private ViewListener.OnTabChangedListener mTabListener;
    private ArrayList<SsomItem> ITEM_LIST = new ArrayList<>();
    private HashMap<Marker, String> mIdMap = new HashMap<>();

    private SsomActionBarView ssomActionBar;
    private DrawerLayout drawer;

    /**
     * The fragment's Tabs
     */
    private TextView giveTv;
    private TextView takeTv;
    private ImageView giveBtmBar;
    private ImageView takeBtmBar;

    /**
     * The filters resources
     */
    private SsomPreferences filterPref;

    /**
     * layout write resources
     */
    private ImageView mBtnMapMyLocation;

    /**
     * toolbar resources
     */
    private TextView mapBtn;
    private TextView listBtn;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private GoogleMap mMap;
    private String selectedView;
    private String selectedTab;
    private FragmentManager fragmentManager;

    // fragment instance 저장
    private FilterFragment filterFragment;
    private SupportMapFragment mapFragment;
    private SsomListFragment ssomListFragment;

    private Location myLocation;
    private ImageView btnWrite;

    private SsomItem myPost;
    private String myPostId;
    private String myPostSsomType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectedView = MAP_VIEW;
        selectedTab = CommonConst.SSOM;
        filterPref = new SsomPreferences(this, SsomPreferences.FILTER_PREF);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // 슬라이드 열기 막기
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer, drawer);

        //set up the toolbar
        initToolbar();
        initLayoutWrite();

        checkLocationServiceEnabled();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mNavigationDrawerFragment.syncState();
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // 다이어로그같은것을 띄워서 사용자에게 해당 권한이 필요한 이유에 대해 설명합니다
                // 해당 설명이 끝난뒤 requestPermissions()함수를 호출하여 권한허가를 요청해야 합니다
                makeDialogForRequestLocationPermission();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        deniedPermissions.toArray(new String[deniedPermissions.size()]), REQUEST_CHECK_DETAIL_LOCATION_PERMISSION);
            }
        }
    };

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
                        UiUtils.makeToastMessage(getApplicationContext(), "위치권한이 없어 앱을 사용하실 수 없습니다.");
                        finish();
                    }
                });
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

    private void requestSsomList(String ageFilter, String countFilter, final boolean needFilterToast) {
        APICaller.getSsomList(getUserId(), ageFilter, countFilter, new NetworkManager.NetworkListener<SsomResponse<GetSsomList.Response>>() {

            @Override
            public void onResponse(SsomResponse<GetSsomList.Response> response) {
                Log.i(TAG, "response : " + response.isSuccess());
                if (response.isSuccess()) {
                    GetSsomList.Response data = response.getData();
                    ITEM_LIST.clear();
                    if (data != null && data.getSsomList() != null && data.getSsomList().size() > 0) {
                        ITEM_LIST = data.getSsomList();
                    } else {
                        Log.i(TAG, "data is null !! nothing to show");
                    }
                    ssomDataChangedListener();

                    if(needFilterToast) Toast.makeText(getApplicationContext(),
                            getString(R.string.filter_apply_complete), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Response error with code " + response.getResultCode() + ", message : " + response.getMessage(),
                            response.getError());
                    showErrorMessage();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // message count 얻어오기
        if(getSession() != null && !TextUtils.isEmpty(getUserId()))
            MessageManager.getInstance().getMessageCount(getToken());

//        if(locationTracker != null && locationTracker.chkCanGetLocation()) {
//            locationTracker.startLocationUpdates(gpsLocationListener, networkLocationListener);
//        }
    }

    private void initLayoutWrite(){
        // Tab control
        giveTv = (TextView) findViewById(R.id.tab_give_tv);
        takeTv = (TextView) findViewById(R.id.tab_take_tv);
        giveBtmBar = (ImageView) findViewById(R.id.tab_give_bottom_bar);
        takeBtmBar = (ImageView) findViewById(R.id.tab_take_bottom_bar);

        // Set tab click listener
        giveTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (CommonConst.SSOM.equals(selectedTab)) return;

                selectedTab = CommonConst.SSOM;
                giveTv.setTextAppearance(getApplicationContext(), R.style.ssom_font_16_green_blue);
                giveBtmBar.setVisibility(View.VISIBLE);
                takeTv.setTextAppearance(getApplicationContext(), R.style.ssom_font_16_gray_warm);
                takeBtmBar.setVisibility(View.GONE);

                if(ssomListFragment != null) ssomListFragment.setPostItemClickListener(null);
                requestSsomList(null, null, false);
            }
        });

        takeTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (CommonConst.SSOA.equals(selectedTab)) return;

                selectedTab = CommonConst.SSOA;
                takeTv.setTextAppearance(getApplicationContext(), R.style.ssom_font_16_red_pink);
                takeBtmBar.setVisibility(View.VISIBLE);
                giveTv.setTextAppearance(getApplicationContext(), R.style.ssom_font_16_gray_warm);
                giveBtmBar.setVisibility(View.GONE);

                if(ssomListFragment != null) ssomListFragment.setPostItemClickListener(null);
                requestSsomList(null, null, false);
            }
        });

        btnWrite = (ImageView) findViewById(R.id.btn_write);
        setSsomWriteButtonImage();

        final Context context = this;
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(getSession().getString(SsomPreferences.PREF_SESSION_TOKEN, ""))) {
                    requestLogin();
                    return;
                }

                if(!TextUtils.isEmpty(myPostId)) {
                    startDetailFragment(getMyPostTypeItems(myPostSsomType), myPostId);
                } else {
                    Intent i = new Intent();
                    i.setClass(context, SsomWriteActivity.class);
                    startActivityForResult(i, REQUEST_SSOM_WRITE);
                }
            }
        });

        View filterImgLayout = findViewById(R.id.filter_img);
        filterImgLayout.setOnClickListener(filterClickListener);

        // TODO online user count setting
//        ((TextView) findViewById(R.id.tv_online_user)).setText();
    }

    private void setSsomWriteButtonImage() {
        if(!TextUtils.isEmpty(getToken())) {
            APICaller.ssomExistMyPost(getToken(), new NetworkManager.NetworkListener<SsomResponse<SsomItem>>() {

                @Override
                public void onResponse(SsomResponse<SsomItem> response) {
                    if (response.isSuccess()) {
                        Log.d(TAG, "data : " + response.getData());
                        if(response.getData() != null && !TextUtils.isEmpty(response.getData().getPostId())) {
                            myPost = response.getData();
                            myPostId = myPost.getPostId();
                            myPostSsomType = myPost.getSsomType();
                            btnWrite.setImageResource(R.drawable.my_btn);
                            if(TextUtils.isEmpty(getTodayImageUrl())) getSession().put(SsomPreferences.PREF_SESSION_TODAY_IMAGE_URL, myPost.getImageUrl());
                        } else {
                            myPostId = "";
                            btnWrite.setImageResource(R.drawable.btn_write);
                        }
                    } else {
                        Log.d(TAG, "get my post is failed");
                        myPostId = "";
                        btnWrite.setImageResource(R.drawable.btn_write);
                    }
                }
            });
        } else {
            myPostId = "";
            btnWrite.setImageResource(R.drawable.btn_write);
        }
    }

    private void ssomDataChangedListener() {
        if(MAP_VIEW.equals(selectedView)){
            initMarker();
        } else {
            mTabListener.onTabChangedAction(getCurrentPostItems());
        }
    }

    private View.OnClickListener filterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(filterFragment == null) filterFragment = new FilterFragment();
            replaceFragment(R.id.top_container, filterFragment, CommonConst.FILTER_FRAG, true);
        }
    };

    private void initToolbar() {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        fragmentManager = getSupportFragmentManager();

        ssomActionBar = (SsomActionBarView) tb.findViewById(R.id.ssom_action_bar);
        ssomActionBar.setHeartCount(0);
        ssomActionBar.setHeartRefillTime("--:--");
        ssomActionBar.setOnLeftNaviBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });
        ssomActionBar.setOnHeartBtnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO heart button action
            }
        });
        ssomActionBar.setOnChattingBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(getSession().getString(SsomPreferences.PREF_SESSION_TOKEN, ""))) {
                    requestLogin();
                    return;
                }

                Intent intent = new Intent(MainActivity.this, SsomChattingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        mapBtn = (TextView) tb.findViewById(R.id.toggle_s_map);
        listBtn = (TextView) tb.findViewById(R.id.toggle_s_list);
        View toggleView = tb.findViewById(R.id.toggle_bg);
        mBtnMapMyLocation = (ImageView) findViewById(R.id.map_current_location);

        toggleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedView.equals(MAP_VIEW)) {
                    startListFragment();
                } else {
                    startMapFragment();
                }

                setToggleButtonUI();
            }
        });
    }

    @Override
    protected void receivedPushMessage(Intent intent) {
        super.receivedPushMessage(intent);

        if(ssomActionBar == null) {
            Log.d(TAG, "ssomActionBar is null");
            return;
        }

        if(ssomActionBar.getChatCount() == 0) {
            ssomActionBar.setChatIconOnOff(true);
        }

        ssomActionBar.setChatCount(String.valueOf(ssomActionBar.getChatCount() + 1));
    }

    @Override
    protected void setMessageCount(String msgCount) {
        super.setMessageCount(msgCount);

        if(ssomActionBar == null) {
            Log.d(TAG, "ssomActionBar is null");
            return;
        }

        if(msgCount != null && !msgCount.isEmpty()) {
            ssomActionBar.setChatIconOnOff(true);
            ssomActionBar.setChatCount(msgCount);
        } else {
            ssomActionBar.setChatIconOnOff(false);
            ssomActionBar.setChatCount("0");
        }
    }

    private void setToggleButtonUI() {
        mapBtn.setTextAppearance(getApplicationContext(),
                MAP_VIEW.equals(selectedView)? R.style.ssom_font_12_white_single : R.style.ssom_font_12_grayish_brown_single);
        mapBtn.setBackgroundResource(MAP_VIEW.equals(selectedView) ? R.drawable.bg_main_toggle_on : 0);
        listBtn.setTextAppearance(getApplicationContext(),
                LIST_VIEW.equals(selectedView) ? R.style.ssom_font_12_white_single : R.style.ssom_font_12_grayish_brown_single);
        listBtn.setBackgroundResource(LIST_VIEW.equals(selectedView) ? R.drawable.bg_main_toggle_on : 0);
    }

    private void startMapFragment(){
        locationTracker.startLocationUpdates(gpsLocationListener, networkLocationListener);
        selectedView = MAP_VIEW;
        if(mapFragment == null) mapFragment = SupportMapFragment.newInstance();
        fragmentManager.beginTransaction().
                replace(R.id.container, mapFragment).commitAllowingStateLoss();
        mapFragment.getMapAsync(this);
    }

    private void startListFragment() {
        selectedView = LIST_VIEW;
        mBtnMapMyLocation.setVisibility(View.INVISIBLE);
        if(ssomListFragment == null) ssomListFragment = new SsomListFragment();
        ssomListFragment.setSsomListData(getCurrentPostItems());
        fragmentManager.beginTransaction().
                replace(R.id.container, ssomListFragment, CommonConst.SSOM_LIST_FRAG).commit();
    }

    private void startDetailFragment(ArrayList<SsomItem> ssomList, String postId) {
        DetailFragment fragment = DetailFragment.newInstance(postId);
        if(ssomList.size() == 0) ssomList.add(myPost);
        fragment.setSsomListData(ssomList);
        replaceFragment(R.id.whole_container, fragment, CommonConst.DETAIL_FRAG, true);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        switch (position) {
            case R.id.today_photo:
                // gallery 실행
                Intent i = new Intent(MainActivity.this, SsomTodayProfileActivity.class);
                startActivityForResult(i, REQUEST_PROFILE_ACTIVITY);
                break;
            case R.id.tv_login_or_logout:
                if(getSession() != null && !TextUtils.isEmpty(getSession().getString(SsomPreferences.PREF_SESSION_TOKEN, ""))
                        && !TextUtils.isEmpty(getSession().getString(SsomPreferences.PREF_SESSION_EMAIL, ""))) {
                    UiUtils.makeCommonDialog(MainActivity.this, CommonDialog.DIALOG_STYLE_ALERT_BUTTON, R.string.dialog_notice, 0,
                            R.string.dialog_logout_message, 0, R.string.dialog_okay, R.string.dialog_cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setSessionInfo("", "", "", "");
                                    mNavigationDrawerFragment.setLoginEmailLayout();
                                    mNavigationDrawerFragment.setTodayImage();
                                    setSsomWriteButtonImage();
                                }
                            }, null);
                } else {
                    startLoginActivity();
                }
                break;
            case R.id.tv_ssom_homepage:
                Intent homepageIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.myssom.com"));
                startActivity(homepageIntent);
                break;
            case R.id.tv_make_heart:
                break;
            /**
             *  list menu item click event
             *  1 : 개인정보 , 2 : 이용약관 , 3 : 문의하기
             */
            case 1:
                Intent privacyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ssomcompany.wixsite.com/ssominfo"));
                startActivity(privacyIntent);
                break;
            case 2:
                Intent policyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ssomcompany.wixsite.com/termsandconditions"));
                startActivity(policyIntent);
                break;
            case 3:
                // TODO action to internal web view
                break;
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return !mNavigationDrawerFragment.isDrawerOpen() || super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

//        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostItemClick(ArrayList<SsomItem> ssomList, int position) {
        Log.i(TAG, "onPostItemClick() : " + position);

        startDetailFragment(ssomList, ssomList.get(position).getPostId());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        // init start my location
        moveToMyLocation(true);
        setMapUiSetting();

        // current position settings
        mBtnMapMyLocation.setVisibility(View.VISIBLE);
        mBtnMapMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationTracker == null || !LocationTracker.getInstance().chkCanGetLocation()) {
                    showActivateGPSPopup(true);
                    return;
                }

                moveToMyLocation(false);
            }
        });
        requestSsomList(null, null, false);

        // 마커 클릭 리스너
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            public boolean onMarkerClick(Marker marker) {
                String postId = mIdMap.get(marker);

                // my position click 시 이벤트 없음
                if (postId == null || "".equals(postId)) return false;

                Log.i(TAG, "[마커 클릭 이벤트] latitude ="
                        + marker.getPosition().latitude + ", longitude ="
                        + marker.getPosition().longitude);

                startDetailFragment(getCurrentPostItems(), postId);
                return false;
            }
        });

        // TODO - 마커가 겹쳤을 경우 처리
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return new View(getApplicationContext());
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
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

    private void moveToMyLocation(boolean init) {
        if(myLocation == null) {
            myLocation = locationTracker.getLocation();
        }
        LatLng myPosition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        if(init) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 15));
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 15));
        }
    }

    // Location Listener for gps
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

//    public Map<String, SsomItem> getCurrentPostMap() {
//        return CommonConst.SSOM.equals(selectedTab)? Util.convertAllMapToSsomMap(ITEM_MAP) : Util.convertAllMapToSsoaMap(ITEM_MAP);
//    }

    private ArrayList<SsomItem> getMyPostTypeItems(String myPostSsomType) {
        return CommonConst.SSOM.equals(myPostSsomType)? Util.convertAllListToSsomList(ITEM_LIST) : Util.convertAllListToSsoaList(ITEM_LIST);
    }

    public ArrayList<SsomItem> getCurrentPostItems() {
        return CommonConst.SSOM.equals(selectedTab)? Util.convertAllListToSsomList(ITEM_LIST) : Util.convertAllListToSsoaList(ITEM_LIST);
    }

    private void initMarker() {
        if(mMap != null) {
            mMap.clear();
            NetworkManager.getInstance().getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    Log.d(TAG, "all requests canceled");
                    return true;
                }
            });
        }
        mIdMap.clear();
        ArrayList<SsomItem> items = getCurrentPostItems();
        if(items != null && items.size() > 0){
            boolean isLastItem;
            for (int i=0 ; i<items.size() ; i++) {
                isLastItem = (i == items.size() - 1);
                addMarker(items.get(i), isLastItem);
            }
        }
    }

    private void addMarker(final SsomItem item, final boolean isLastItem) {
        if(NetworkManager.getInstance().getBitmapFromCache(item.getImageUrl()) != null) {
            mIdMap.put(mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(item.getLatitude(), item.getLongitude())).draggable(false)
                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerImage(item.getSsomType(),
                            NetworkManager.getInstance().getBitmapFromCache(item.getImageUrl()))))), item.getPostId());

//            if (isLastItem) {
//                Log.d(TAG, "last item created!");
//                dismissProgressDialog();
//            }
        } else {
            ImageRequest imageRequest = new ImageRequest(item.getImageUrl(), new Response.Listener<Bitmap>() {
                Marker marker;

                @Override
                public void onResponse(Bitmap bitmap) {
                    NetworkManager.getInstance().addBitmapToCache(item.getImageUrl(), bitmap);
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(item.getLatitude(), item.getLongitude())).draggable(false)
                            .icon(BitmapDescriptorFactory.fromBitmap(getMarkerImage(item.getSsomType(), bitmap))));

                    mIdMap.put(marker, item.getPostId());
//                    if (isLastItem) {
//                        Log.d(TAG, "last item created!");
//                        dismissProgressDialog();
//                    }
                }
            }, 480  // max width
                    , 320  // max height
                    , ImageView.ScaleType.CENTER  // scale type
                    , Bitmap.Config.RGB_565  // decode config
                    , new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });
            NetworkManager.getInstance().getRequestQueue().add(imageRequest);
        }
    }

    private Bitmap getMarkerImage(String ssom , Bitmap imageBitmap){
        Bitmap mergedBitmap = null;
        try {
            mergedBitmap = Bitmap.createBitmap(Util.convertDpToPixel(49),
                    Util.convertDpToPixel(57), Bitmap.Config.ARGB_4444);
            Canvas c = new Canvas(mergedBitmap);
            Bitmap iconBitmap;
            if(CommonConst.SSOM.equals(ssom)){
                iconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_map_st_g);
            }else{
                iconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_map_st_r);
            }

            Drawable iconDrawable = new BitmapDrawable(getResources(), iconBitmap);
            Drawable imageDrawable = new RoundImage(Bitmap.createScaledBitmap(imageBitmap, Util.convertDpToPixel(47),
                    Util.convertDpToPixel(47), false));

            iconDrawable.setBounds(0, 0,
                    Util.convertDpToPixel(49), Util.convertDpToPixel(57));
            imageDrawable.setBounds(Util.convertDpToPixel(2), Util.convertDpToPixel(2),
                    Util.convertDpToPixel(47), Util.convertDpToPixel(47));
            imageDrawable.draw(c);
            iconDrawable.draw(c);
        } catch (Exception e) {
            Log.i(TAG, "Get Marker image finished by exception..!");
        }

        return mergedBitmap;
    }

    private void startLoginActivity() {
        startActivityForResult(new Intent(this, SsomLoginBaseActivity.class), REQUEST_SSOM_LOGIN);
    }

    private void startChattingActivity(SsomItem ssomItem, boolean ssomRequest) {
        Intent chattingIntent = new Intent(this, SsomChattingActivity.class);
        chattingIntent.putExtra(CommonConst.Intent.SSOM_ITEM, ssomItem);
        startActivity(chattingIntent);
    }

    private void requestLogin() {
        CommonDialog dialog = CommonDialog.getInstance(CommonDialog.DIALOG_STYLE_ALERT_BUTTON);
        dialog.setTitle(getString(R.string.dialog_notice));
        dialog.setTitleStyle(R.style.ssom_font_20_grayish_brown_bold);
        dialog.setMessage(getString(R.string.dialog_require_login));
        dialog.setPositiveButton(getString(R.string.dialog_okay), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "Move to LoginActivity");
                startLoginActivity();
            }
        });
        dialog.setNegativeButton(getString(R.string.dialog_close),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
        dialog.setAutoDismissEnable(true);
        dialog.show(getFragmentManager(), null);
    }

//    @Override
//    public void onMyLocationChange(Location location) {
//        if(isFirstTimeChangeLocation && LocationUtil.getMyLocation(this)==null){
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
//            isFirstTimeChangeLocation = false;
//            LocationUtil.setMyLocation(location);
//        }else{
//            LocationUtil.setMyLocation(location);
//        }
//    }

    @Override
    public void onFilterFragmentInteraction(boolean isApply) {
        Log.i(TAG, "filter interaction : " + isApply);
        if(isApply) {
            requestSsomList(filterPref.getString(SsomPreferences.PREF_FILTER_AGE, ""),
                    filterPref.getString(SsomPreferences.PREF_FILTER_PEOPLE, ""), true);
        }

        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(CommonConst.FILTER_FRAG)).commit();
        fragmentManager.popBackStack();
    }

    @Override
    public void onDetailFragmentInteraction(boolean isApply, SsomItem ssomItem) {
        Log.i(TAG, "detail interaction : " + isApply);

        if(isApply) {
            // login check
            if(TextUtils.isEmpty(getSession().getString(SsomPreferences.PREF_SESSION_TOKEN, ""))) {
                requestLogin();
                return;
            }

            if(ssomItem != null && !TextUtils.isEmpty(getUserId()) && getUserId().equals(ssomItem.getUserId())) {
                APICaller.ssomPostDelete(getToken(), ssomItem.getPostId(),
                        new NetworkManager.NetworkListener<SsomResponse<SsomPostDelete.Response>>() {
                    @Override
                    public void onResponse(SsomResponse<SsomPostDelete.Response> response) {
                        if(response.isSuccess()) {
                            Log.d(TAG, "delete success : " + response);
                            fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(CommonConst.DETAIL_FRAG)).commit();
                            fragmentManager.popBackStack();
                            requestSsomList(null, null, false);
                            myPostId = "";
                            btnWrite.setImageResource(R.drawable.btn_write);
                        } else {
                            showErrorMessage();
                        }
                    }
                });
                return;
            }

            startChattingActivity(ssomItem, true);
        }

        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(CommonConst.DETAIL_FRAG)).commit();
        fragmentManager.popBackStack();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult called : " + requestCode + ", reusltCode : " + resultCode);

        switch (requestCode) {
            case REQUEST_PROFILE_ACTIVITY :
                mNavigationDrawerFragment.setTodayImage();
                break;
            case REQUEST_SSOM_WRITE :
                if(resultCode == RESULT_OK) {
                    requestSsomList(null, null, false);
                    mNavigationDrawerFragment.setTodayImage();
                    setSsomWriteButtonImage();
                    dismissProgressDialog();
                }
                break;
            case REQUEST_SSOM_LOGIN :
                if(resultCode == RESULT_OK) {
                    mNavigationDrawerFragment.setLoginEmailLayout();
                    mNavigationDrawerFragment.setTodayImage();
                    setSsomWriteButtonImage();
                }
                break;
            case REQUEST_CHECK_LOCATION_PERMISSION:
//                checkLocationServiceEnabled();
//                break;
            case REQUEST_CHECK_DETAIL_LOCATION_PERMISSION:
                continueProcess();
                break;
            default:
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(locationTracker != null) locationTracker.stopLocationUpdates();
    }

    @Override
    public void onBackPressed() {
        if(fragmentManager.findFragmentById(R.id.top_container) != null ||
                fragmentManager.findFragmentById(R.id.whole_container) != null) {
            super.onBackPressed();
            return;
        }

        if(mNavigationDrawerFragment.isDrawerOpen()) {
            drawer.closeDrawers();
        }

        if(canFinish) {
            if(toast != null) toast.cancel();
            super.onBackPressed();
        } else {
            toast = Toast.makeText(this, getString(R.string.app_finish), Toast.LENGTH_SHORT);
            toast.show();
            canFinish = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    canFinish = false;
                }
            }, 1500);
        }
    }

    public void setOnTabChangedListener(ViewListener.OnTabChangedListener mTabListener) {
        this.mTabListener = mTabListener;
    }

    private void replaceFragment(int containerId, Fragment fragment, String tagName, boolean isBackStack) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(containerId, fragment, tagName);
        if(isBackStack) ft.addToBackStack(null);
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mNavigationDrawerFragment.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        if(mapFragment != null) {
            mapFragment.onLowMemory();
        }
    }
}
