package com.ssomcompany.ssomclient.activity;

import android.Manifest;
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
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.onesignal.shortcutbadger.ShortcutBadger;
import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.BitmapWorkerTask;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.FilterType;
import com.ssomcompany.ssomclient.common.LocationTracker;
import com.ssomcompany.ssomclient.common.RoundImage;
import com.ssomcompany.ssomclient.common.SsomPreferences;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.control.InAppBillingHelper;
import com.ssomcompany.ssomclient.control.SsomPermission;
import com.ssomcompany.ssomclient.control.ViewListener;
import com.ssomcompany.ssomclient.fragment.ChatRoomTabFragment;
import com.ssomcompany.ssomclient.fragment.DetailFragment;
import com.ssomcompany.ssomclient.fragment.FilterFragment;
import com.ssomcompany.ssomclient.fragment.HeartStoreTabFragment;
import com.ssomcompany.ssomclient.fragment.NavigationDrawerFragment;
import com.ssomcompany.ssomclient.fragment.SsomListTabFragment;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.RetrofitManager;
import com.ssomcompany.ssomclient.network.api.ChatService;
import com.ssomcompany.ssomclient.network.api.SsomPostService;
import com.ssomcompany.ssomclient.network.api.UserService;
import com.ssomcompany.ssomclient.network.model.ChatRoomCreateResult;
import com.ssomcompany.ssomclient.network.model.ChatRoomItem;
import com.ssomcompany.ssomclient.network.model.ChatUnreadCountResult;
import com.ssomcompany.ssomclient.network.model.ProfileResult;
import com.ssomcompany.ssomclient.network.model.SsomItem;
import com.ssomcompany.ssomclient.network.model.UserCountResult;
import com.ssomcompany.ssomclient.push.MessageCountCheck;
import com.ssomcompany.ssomclient.push.MessageManager;
import com.ssomcompany.ssomclient.widget.SsomActionBarView;
import com.ssomcompany.ssomclient.widget.dialog.CommonDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends BaseActivity
        implements ViewListener.NavigationDrawerCallbacks,
        ViewListener.OnPostItemInteractionListener, ViewListener.OnDetailFragmentInteractionListener,
        OnMapReadyCallback, ViewListener.OnFilterFragmentInteractionListener,
        ViewListener.OnChatRoomListLoadingFinished, MessageCountCheck {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int BOTTOM_MAP = 0;
    private static final int BOTTOM_LIST = 1;
    private static final int BOTTOM_STORE = 2;
    private static final int BOTTOM_CHAT = 3;


    private static final int REQUEST_SSOM_WRITE = 100;
    private static final int REQUEST_CHECK_LOCATION_PERMISSION = 101;
    private static final int REQUEST_CHECK_DETAIL_LOCATION_PERMISSION = 102;
    private static final int REQUEST_PROFILE_ACTIVITY = 103;

    private static boolean canFinish;
    private static Toast toast = null;

    private ViewListener.OnTabChangedListener mTabListener;
    private ArrayList<SsomItem> ITEM_LIST = new ArrayList<>();
    private HashMap<Marker, String> mIdMap = new HashMap<>();

    private SsomActionBarView ssomActionBar;
    private DrawerLayout drawer;

    /**
     * The filters resources
     */
    private SsomPreferences filterPref;

    /**
     * layout write resources
     */
    private ImageView mBtnMapMyLocation;
    private ImageView mapRefresh;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private GoogleMap mMap;
    private FragmentManager fragmentManager;

    // pager 관리
    private ViewPager mainPager;
    private MainPagerAdapter mainAdapter;
    private TabLayout bottomTab;
    private TextView msgCount;

    // fragment instance 저장
    private FilterFragment filterFragment;
    private SupportMapFragment mapFragment;

    private Location myLocation;
    private ImageView btnWrite;

    private SsomItem myPost;
    private String myPostId;
    private String myPostSsomType;

    // shadow 객체 관리
    private View topShadow;
    private View bottomShadow;

    private boolean isFromNoti;
    private int userCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        filterPref = new SsomPreferences(this, SsomPreferences.FILTER_PREF);

        if(getIntent() != null && getIntent().getExtras() != null) {
            isFromNoti = getIntent().getBooleanExtra(CommonConst.Intent.IS_FROM_NOTI, false);
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // 슬라이드 열기 막기
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer, drawer);

        // set pager instance
        mainPager = (ViewPager) findViewById(R.id.main_pager);
        mainAdapter = new MainPagerAdapter(getSupportFragmentManager());
        bottomTab = (TabLayout) findViewById(R.id.bottom_tab);

        // set shadow instance
        topShadow = findViewById(R.id.topShadow);
        bottomShadow = findViewById(R.id.bottomShadow);

        if(bottomTab != null) {
            bottomTab.addTab(bottomTab.newTab().setIcon(R.drawable.foot_icon_map_on));
            bottomTab.addTab(bottomTab.newTab().setIcon(R.drawable.foot_icon_list_off));
            bottomTab.addTab(bottomTab.newTab().setIcon(R.drawable.foot_icon_heart_off));
            bottomTab.addTab(bottomTab.newTab().setIcon(R.drawable.foot_icon_chat_off));
            bottomTab.setTabGravity(TabLayout.GRAVITY_FILL);

            for (int i = 0; i < bottomTab.getTabCount(); i++) {
                TabLayout.Tab tab = bottomTab.getTabAt(i);
                if (tab != null) {
                    tab.setCustomView(R.layout.view_bottom_tab);
                    if(i == 3) {
                        msgCount = (TextView) tab.getCustomView().findViewById(R.id.msg_count);
                    }
                }
            }
        }

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

    public void requestSsomList(final boolean needFilterToast) {
        String typeFilter = filterPref.getString(SsomPreferences.PREF_FILTER_TYPE, "");
        Map<String, String> params = new HashMap<>();
        params.put("userId", getUserId());
        if(!typeFilter.contains(",")) {
            params.put("ssomTypeFilter", filterPref.getString(SsomPreferences.PREF_FILTER_TYPE, ""));
        }
        params.put("ageFilter", filterPref.getString(SsomPreferences.PREF_FILTER_AGE, ""));
        params.put("countFilter", filterPref.getString(SsomPreferences.PREF_FILTER_PEOPLE, ""));
        params.put("lat", String.valueOf(locationTracker.getLocation().getLatitude()));
        params.put("lng", String.valueOf(locationTracker.getLocation().getLongitude()));

        RetrofitManager.getInstance().create(SsomPostService.class)
                .requestSsomList(params)
                .enqueue(new Callback<ArrayList<SsomItem>>() {
                    @Override
                    public void onResponse(Call<ArrayList<SsomItem>> call, Response<ArrayList<SsomItem>> response) {
                        Log.i(TAG, "response : " + response.isSuccessful());
                        ArrayList<SsomItem> items = response.body();
                        if (response.isSuccessful() && items != null) {
                            ITEM_LIST.clear();
                            if (items.size() > 0) {
                                ITEM_LIST = items;
                            } else {
                                Log.i(TAG, "data is null !! nothing to show");
                            }
                            ssomDataChangedListener();

                            if(needFilterToast) {
                                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.filter_apply_complete), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, 0, Util.convertDpToPixel(50f));
                                toast.show();
                            }
                        } else {
                            showErrorMessage();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<SsomItem>> call, Throwable t) {
                        Log.e(TAG, "Response error with ", t);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // message count 얻어오기
        if(getSession() != null && !TextUtils.isEmpty(getUserId())) {
            MessageManager.getInstance().getMessageCount();
            MessageManager.getInstance().getHeartCount();
        }

//        if(locationTracker != null && locationTracker.chkCanGetLocation()) {
//            locationTracker.startLocationUpdates(gpsLocationListener, networkLocationListener);
//        }
    }

    private void initLayoutWrite(){
        btnWrite = (ImageView) findViewById(R.id.btn_write);
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(myPostId)) {
                    startDetailFragment(ITEM_LIST, myPostId);
                } else {
                    Intent i = new Intent();
                    i.setClass(MainActivity.this, SsomWriteActivity.class);
                    startActivityForResult(i, REQUEST_SSOM_WRITE);
                }
            }
        });

        if(!TextUtils.isEmpty(getUserId())) {
            // user profile 정보를 셋팅한다
            RetrofitManager.getInstance().create(UserService.class)
                    .getUserProfile(getUserId())
                    .enqueue(new Callback<ProfileResult>() {
                        @Override
                        public void onResponse(Call<ProfileResult> call, Response<ProfileResult> response) {
                            if(response.isSuccessful() && response.body() != null) {
                                getSession().put(SsomPreferences.PREF_SESSION_TODAY_IMAGE_URL, response.body().getProfileImgUrl());
                            } else {
                                showErrorMessage();
                            }
                            setSsomWriteButtonImage(false);
                        }

                        @Override
                        public void onFailure(Call<ProfileResult> call, Throwable t) {

                        }
                    });
        }
    }

    private void setSsomWriteButtonImage(final boolean isRefresh) {
        if(!TextUtils.isEmpty(getUserId())) {
            RetrofitManager.getInstance().create(SsomPostService.class)
                    .requestGetMyPost()
                    .enqueue(new Callback<SsomItem>() {
                        @Override
                        public void onResponse(Call<SsomItem> call, Response<SsomItem> response) {
                            if (response.isSuccessful()) {
                                Log.d(TAG, "data : " + response.body());
                                SsomItem data = response.body();
                                if(data != null && !TextUtils.isEmpty(data.getPostId())) {
                                    myPost = data;
                                    myPostId = myPost.getPostId();
                                    myPostSsomType = myPost.getSsomType();
                                    btnWrite.setImageResource(R.drawable.my_btn);
                                    if(TextUtils.isEmpty(getTodayImageUrl())) getSession().put(SsomPreferences.PREF_SESSION_TODAY_IMAGE_URL, myPost.getImageUrl());
                                } else {
                                    myPostId = "";
                                    btnWrite.setImageResource(R.drawable.main_write_btn);
                                }
                            } else {
                                Log.d(TAG, "get my post is failed");
                                myPostId = "";
                                btnWrite.setImageResource(R.drawable.main_write_btn);
                                showErrorMessage();
                            }
                            if(isRefresh) requestSsomList(false);
                        }

                        @Override
                        public void onFailure(Call<SsomItem> call, Throwable t) {

                        }
                    });
        } else {
            myPostId = "";
            btnWrite.setImageResource(R.drawable.main_write_btn);
        }
    }

    private void ssomDataChangedListener() {
        initMarker();
        mTabListener.onTabChangedAction(ITEM_LIST);
    }

    private View.OnClickListener filterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(filterFragment == null) filterFragment = new FilterFragment();
            replaceFragment(R.id.whole_container, filterFragment, CommonConst.FILTER_FRAG, true);
        }
    };

    private void initToolbar() {
        fragmentManager = getSupportFragmentManager();
        ssomActionBar = (SsomActionBarView) findViewById(R.id.ssom_toolbar);
        ssomActionBar.setSsomBarTitleLayoutGravity(RelativeLayout.CENTER_IN_PARENT);
        ssomActionBar.setOnLeftNaviBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });
        ssomActionBar.setOnSsomFilterClickListener(filterClickListener);
        setFilterDrawable();
        getUserCount(true);

        // 최초 앱 실행 시 unread 메시지 카운트를 맞추기 위해 한번 호출함
        RetrofitManager.getInstance().create(ChatService.class)
                .getChatUnreadCount()
                .enqueue(new Callback<ChatUnreadCountResult>() {
                    @Override
                    public void onResponse(Call<ChatUnreadCountResult> call, Response<ChatUnreadCountResult> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            int unreadCount = response.body().getUnreadCount();
                            getSession().put(SsomPreferences.PREF_SESSION_UNREAD_COUNT, unreadCount);
                            if(unreadCount != 0) {
                                ShortcutBadger.applyCount(MainActivity.this, unreadCount); //for 1.1.4+
                            } else {
                                ShortcutBadger.removeCount(MainActivity.this);
                            }
                        } else {
                            showErrorMessage();
                        }
                    }

                    @Override
                    public void onFailure(Call<ChatUnreadCountResult> call, Throwable t) {

                    }
                });

        mBtnMapMyLocation = (ImageView) findViewById(R.id.map_current_location);
        mapRefresh = (ImageView) findViewById(R.id.map_refresh);
    }

    private void getUserCount(final boolean isInit) {
        RetrofitManager.getInstance().create(UserService.class)
                .getUserCount()
                .enqueue(new Callback<UserCountResult>() {
                    @Override
                    public void onResponse(Call<UserCountResult> call, Response<UserCountResult> response) {
                        if(response.isSuccessful()) {
                            userCount = response.body().getUserCount();
                        } else {
                            userCount = userCount == 0 ? 100 : userCount;
                        }

                        if(isInit) {
                            ssomActionBar.setSsomBarTitleText(getString(R.string.current_user_count, userCount));
                            ssomActionBar.setSsomBarTitleDrawable(R.drawable.icon_ssom_map, Util.convertDpToPixel(2f));
                            ssomActionBar.setSsomBarTitleStyle(R.style.ssom_font_12_gray_warm);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserCountResult> call, Throwable t) {

                    }
                });
    }

    private void updateToolbarToMain() {
        getUserCount(false);
        ssomActionBar.setSsomBarTitleText(getString(R.string.current_user_count, userCount));
        ssomActionBar.setSsomBarTitleDrawable(R.drawable.icon_ssom_map, Util.convertDpToPixel(2f));
        ssomActionBar.setSsomBarTitleStyle(R.style.ssom_font_12_gray_warm);
    }

    private void updateToolbarToChatList() {
        ssomActionBar.setSsomBarTitleText(String.format(getString(R.string.chat_list_title), getUnreadCount()));
        ssomActionBar.setSsomBarTitleDrawable(0, 0);
        ssomActionBar.setSsomBarTitleStyle(R.style.ssom_font_14_custom_4d4d4d_single);
    }

    @Override
    protected void receivedPushMessage(Intent intent) {
        super.receivedPushMessage(intent);

        if(ssomActionBar == null) {
            Log.d(TAG, "ssomActionBar is null");
            return;
        }

        if(mainPager == null || mainAdapter == null) {
            Log.d(TAG, "main pager / adapter is null");
            return;
        }

        if(MessageManager.BROADCAST_MESSAGE_RECEIVED_PUSH.equalsIgnoreCase(intent.getAction())) {
            // badge count 올림
            setChattingCount(getUnreadCount());

            // chatting list 업데이트
            ChatRoomTabFragment chatRoomFragment = (ChatRoomTabFragment) mainAdapter.getItem(BOTTOM_CHAT);
            ArrayList<ChatRoomItem> roomList = chatRoomFragment.getChatRoomList();
            String chatRoomId = intent.getStringExtra(CommonConst.Intent.CHAT_ROOM_ID);
            int roomIndex = -1;

            for (int i = 0; i < roomList.size(); i++) {
                if (!TextUtils.isEmpty(chatRoomId) && chatRoomId.equals(roomList.get(i).getId())) {
                    roomIndex = i;
                    break;
                }
            }

            ChatRoomItem tempItem;
            if(roomIndex < 0) {   // 방이 없는거임 처음 생성 된 방
                tempItem = new ChatRoomItem();
                tempItem.setId(chatRoomId);
                tempItem.setOwnerId(getUserId());
                tempItem.setParticipantId(intent.getStringExtra(CommonConst.Intent.FROM_USER_ID));
                tempItem.setLastTimestamp(intent.getLongExtra(CommonConst.Intent.TIMESTAMP, 0));
                tempItem.setLastMsg(intent.getStringExtra(CommonConst.Intent.MESSAGE));
                tempItem.setUnreadCount(1);
                if (!TextUtils.isEmpty(intent.getStringExtra(CommonConst.Intent.STATUS)))
                    tempItem.setStatus(intent.getStringExtra(CommonConst.Intent.STATUS));
            } else {
                tempItem = roomList.get(roomIndex);
                tempItem.setLastMsg(intent.getStringExtra(CommonConst.Intent.MESSAGE));
                tempItem.setUnreadCount(tempItem.getUnreadCount() + 1);
                if (!TextUtils.isEmpty(intent.getStringExtra(CommonConst.Intent.STATUS)))
                    tempItem.setStatus(intent.getStringExtra(CommonConst.Intent.STATUS));
                roomList.remove(roomIndex);
            }
            roomList.add(0, tempItem);
            chatRoomFragment.setChatRoomListAndNotify(roomList);

            if(mainPager.getCurrentItem() == BOTTOM_CHAT) {
                updateToolbarToChatList();
            }
        } else if(MessageManager.BROADCAST_MESSAGE_OPENED_PUSH.equalsIgnoreCase(intent.getAction())) {
            mainPager.setCurrentItem(BOTTOM_CHAT);
            if(intent.getExtras() != null) {
                startChattingActivity(((ChatRoomTabFragment) mainAdapter.getItem(BOTTOM_CHAT))
                        .getChatRoomItem(intent.getStringExtra(CommonConst.Intent.CHAT_ROOM_ID)));
            }
        }
    }

    @Override
    protected void setMessageCount(String msgCount) {
        super.setMessageCount(msgCount);

        if(ssomActionBar == null) {
            Log.d(TAG, "ssomActionBar is null");
            return;
        }

        if(msgCount != null && !msgCount.isEmpty()) {
            msgCount = msgCount.contains("+") ? "99" : msgCount;
            getSession().put(SsomPreferences.PREF_SESSION_UNREAD_COUNT, Integer.parseInt(msgCount));
            ShortcutBadger.applyCount(this, Integer.parseInt(msgCount)); //for 1.1.4+
        } else {
            getSession().put(SsomPreferences.PREF_SESSION_UNREAD_COUNT, 0);
            msgCount = "0";
            ShortcutBadger.removeCount(this);
        }
        setChattingCount(Integer.parseInt(msgCount));
    }

    private void setChattingCount(int count) {
        msgCount.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
        msgCount.setText(String.valueOf(count));
    }

    private void startMapFragment(){
        locationTracker.startLocationUpdates(gpsLocationListener, networkLocationListener);

        mainPager.setAdapter(mainAdapter);
        mainPager.setOffscreenPageLimit(4);
        mainPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(bottomTab) {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                // request call for chat room list
                if(position == BOTTOM_CHAT) {
                    ((ChatRoomTabFragment) mainAdapter.getItem(position)).notifyChatRoomAdapter();
                }
            }
        });
        bottomTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mBtnMapMyLocation.setVisibility(View.INVISIBLE);
                mapRefresh.setVisibility(View.INVISIBLE);
                btnWrite.setVisibility(View.INVISIBLE);
                ssomActionBar.setSsomFilterVisibility(false);
                mainPager.setCurrentItem(tab.getPosition());

                topShadow.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Util.convertDpToPixel(5f)));
                RelativeLayout.LayoutParams bottomParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Util.convertDpToPixel(5f));
                bottomParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                bottomShadow.setLayoutParams(bottomParams);
//                findViewById(R.id.topMapShadow).setVisibility(View.GONE);
//                findViewById(R.id.topDropShadow).setVisibility(View.VISIBLE);

                switch (tab.getPosition()) {
                    case BOTTOM_MAP:
                        mBtnMapMyLocation.setVisibility(View.VISIBLE);
                        mapRefresh.setVisibility(View.VISIBLE);
                        btnWrite.setVisibility(View.VISIBLE);
                        ssomActionBar.setSsomFilterVisibility(true);
                        topShadow.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                        bottomParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                        bottomShadow.setLayoutParams(bottomParams);
//                        findViewById(R.id.topMapShadow).setVisibility(View.VISIBLE);
//                        findViewById(R.id.topDropShadow).setVisibility(View.GONE);
                        updateToolbarToMain();
                        tab.setIcon(R.drawable.foot_icon_map_on);
                        break;
                    case BOTTOM_LIST:
                        btnWrite.setVisibility(View.VISIBLE);
                        ssomActionBar.setSsomFilterVisibility(true);
                        updateToolbarToMain();
                        tab.setIcon(R.drawable.foot_icon_list_on);
                        break;
                    case BOTTOM_STORE:
                        updateToolbarToMain();
                        tab.setIcon(R.drawable.foot_icon_heart_on);
                        break;
                    case BOTTOM_CHAT:
                        updateToolbarToChatList();
                        tab.setIcon(R.drawable.foot_icon_chat_on);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case BOTTOM_MAP:
                        tab.setIcon(R.drawable.foot_icon_map_off);
                        break;
                    case BOTTOM_LIST:
                        tab.setIcon(R.drawable.foot_icon_list_off);
                        break;
                    case BOTTOM_STORE:
                        tab.setIcon(R.drawable.foot_icon_heart_off);
                        break;
                    case BOTTOM_CHAT:
                        tab.setIcon(R.drawable.foot_icon_chat_off);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        if(isFromNoti) {
            mainPager.setCurrentItem(BOTTOM_CHAT);
            isFromNoti = false;
        }
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
                Intent i = new Intent(MainActivity.this, SsomTodayProfileActivity.class);
                startActivityForResult(i, REQUEST_PROFILE_ACTIVITY);
                break;
            case R.id.tv_ssom_homepage:
                Intent homepageIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(NetworkConstant.WEB_PAGE));
                startActivity(homepageIntent);
                break;
            case R.id.tv_make_heart:
                mainPager.setCurrentItem(2);
                break;
            /**
             *  list menu item click event
             *  1 : 개인정보 , 2 : 이용약관 , 3 : 회원탈퇴
             */
            case 1:
                Intent privacyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(NetworkConstant.WEB_PRIVACY));
                startActivity(privacyIntent);
                break;
            case 2:
                Intent policyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(NetworkConstant.WEB_POLICY));
                startActivity(policyIntent);
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
        mapRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSsomList(false);
            }
        });
        requestSsomList(false);

        // 마커 클릭 리스너
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            public boolean onMarkerClick(Marker marker) {
                String postId = mIdMap.get(marker);

                // my position click 시 이벤트 없음
                if (postId == null || "".equals(postId)) return false;

                Log.i(TAG, "[마커 클릭 이벤트] latitude ="
                        + marker.getPosition().latitude + ", longitude ="
                        + marker.getPosition().longitude);

                startDetailFragment(ITEM_LIST, postId);
                return false;
            }
        });

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
        myLocation = locationTracker.getLocation();
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

    private void initMarker() {
        if(mMap != null) {
            mMap.clear();
            Glide.with(this).onDestroy();
        }
        mIdMap.clear();
        if(ITEM_LIST != null && ITEM_LIST.size() > 0){
            boolean isLastItem;
            for (int i=0 ; i<ITEM_LIST.size() ; i++) {
                isLastItem = (i == ITEM_LIST.size() - 1);
                addMarker(ITEM_LIST.get(i), isLastItem);
            }
        }
    }

    private void addMarker(final SsomItem item, final boolean isLastItem) {
        new BitmapWorkerTask(this){
            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null) {
                    // Add final bitmap to caches
                    mIdMap.put(mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(item.getLatitude(), item.getLongitude())).draggable(false)
                            .icon(BitmapDescriptorFactory.fromBitmap(getMarkerImage(item, bitmap)))), item.getPostId());
                }
            }
        }.execute(item.getThumbnailImageUrl());
    }

    private Bitmap getMarkerImage(SsomItem ssom , Bitmap imageBitmap){
        Bitmap mergedBitmap = null;
        try {
            mergedBitmap = Bitmap.createBitmap(Util.convertDpToPixel(49),
                    Util.convertDpToPixel(57), Bitmap.Config.ARGB_4444);
            Canvas c = new Canvas(mergedBitmap);
            Bitmap iconBitmap = BitmapFactory.decodeResource(getResources(), CommonConst.SSOM.equals(ssom.getSsomType()) ?
                    R.drawable.icon_map_st_g : R.drawable.icon_map_st_r);

            Bitmap iconIng = null;
            // ing image
            if(CommonConst.Chatting.MEETING_APPROVE.equals(ssom.getStatus())) {
                iconIng = BitmapFactory.decodeResource(getResources(), CommonConst.SSOM.equals(ssom.getSsomType()) ?
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
                ingDrawable.setBounds(Util.convertDpToPixel(1), Util.convertDpToPixel(1),
                        Util.convertDpToPixel(48), Util.convertDpToPixel(48));
                ingDrawable.draw(c);
            }

        } catch (Exception e) {
            Log.i(TAG, "Get Marker image finished by exception..!");
        }

        return mergedBitmap;
    }

    private void startChattingActivity(ChatRoomItem chatRoomItem) {
        if(chatRoomItem == null) return;
        Intent chattingIntent = new Intent(this, SsomChattingActivity.class);
        chattingIntent.putExtra(CommonConst.Intent.CHAT_ROOM_ITEM, chatRoomItem);
        startActivity(chattingIntent);
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
            requestSsomList(true);
            setFilterDrawable();
        }

        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(CommonConst.FILTER_FRAG)).commit();
        fragmentManager.popBackStack();
    }

    private void setFilterDrawable() {
        ArrayList<String> typeFilter = filterPref.getStringArray(SsomPreferences.PREF_FILTER_TYPE, new ArrayList<String>());
        int resId;
        switch (typeFilter.size()) {
            case 1:
                if(typeFilter.contains(FilterType.ssom.getValue())) {
                    resId = R.drawable.top_icon_green;
                } else {
                    resId = R.drawable.top_icon_red;
                }
                break;
            case 2:
                resId = R.drawable.top_icon_greenred;
                break;
            default:
                resId = R.drawable.top_icon_greenred;
                break;
        }
        ssomActionBar.setSsomFilterDrawable(resId);
    }

    @Override
    public void onDetailFragmentInteraction(boolean isApply, final SsomItem ssomItem) {
        Log.i(TAG, "detail interaction : " + isApply);

        if(isApply) {
            if(ssomItem == null) return;

            if(!TextUtils.isEmpty(getUserId()) && getUserId().equals(ssomItem.getUserId())) {
                UiUtils.makeCommonDialog(this, CommonDialog.DIALOG_STYLE_ALERT_BUTTON, R.string.dialog_notice, 0,
                        R.string.detail_my_post_delete, R.style.ssom_font_16_custom_666666,
                        R.string.dialog_delete, R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 삭제 진행
                                RetrofitManager.getInstance().create(SsomPostService.class)
                                        .deletePost(ssomItem.getPostId())
                                        .enqueue(new Callback<Void>() {
                                            @Override
                                            public void onResponse(Call<Void> call, Response<Void> response) {
                                                if(response.isSuccessful()) {
                                                    Log.d(TAG, "delete success : " + response);
                                                    fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(CommonConst.DETAIL_FRAG)).commit();
                                                    fragmentManager.popBackStack();
                                                    requestSsomList(false);
                                                    myPostId = "";
                                                    btnWrite.setImageResource(R.drawable.main_write_btn);
                                                } else {
                                                    showErrorMessage();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Void> call, Throwable t) {

                                            }
                                        });
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                return;
            }

            if(TextUtils.isEmpty(ssomItem.getChatroomId())
                    && getHeartCount() == 0) {
                UiUtils.makeCommonDialog(this, CommonDialog.DIALOG_STYLE_ALERT_BUTTON, R.string.dialog_notice, 0,
                        R.string.heart_not_enough_go_to_store, R.style.ssom_font_16_custom_666666,
                        R.string.dialog_move, R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mainPager.setCurrentItem(2);
                                fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(CommonConst.DETAIL_FRAG)).commit();
                                fragmentManager.popBackStack();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                return;
            }

            if(CommonConst.Chatting.MEETING_APPROVE.equals(ssomItem.getStatus()) && TextUtils.isEmpty(ssomItem.getChatroomId())) {
                UiUtils.makeToastMessage(this, "상대방이 이미 만남을 진행 중 입니다.");
                return;
            }

            // 채팅 중인 상대이므로 채팅방으로 이동시키기
            if(!TextUtils.isEmpty(ssomItem.getChatroomId()) && !CommonConst.Chatting.MEETING_OUT.equalsIgnoreCase(ssomItem.getStatus())) {
                // 기존에 방이 있으므로 그쪽으로 이동시킴
                startChattingActivity(((ChatRoomTabFragment) mainAdapter.getItem(BOTTOM_CHAT)).getChatRoomItem(ssomItem.getChatroomId()));
            } else {
                RetrofitManager.getInstance().create(ChatService.class)
                        .createChatRoom(ssomItem.getPostId(), locationTracker.getLocation().getLatitude(), locationTracker.getLocation().getLongitude())
                        .enqueue(new Callback<ChatRoomCreateResult>() {
                            @Override
                            public void onResponse(Call<ChatRoomCreateResult> call, Response<ChatRoomCreateResult> response) {
                                ChatRoomCreateResult data = response.body();
                                if (response.isSuccessful() && data != null) {
                                    ChatRoomItem chatRoomItem = new ChatRoomItem();
                                    chatRoomItem.setId(data.getChatroomId());
                                    chatRoomItem.setOwnerId(getUserId());
                                    chatRoomItem.setOwnerImageUrl(getTodayImageUrl());
                                    chatRoomItem.setParticipantId(ssomItem.getUserId());
                                    chatRoomItem.setParticipantImageUrl(ssomItem.getImageUrl());
                                    chatRoomItem.setSsomType(ssomItem.getSsomType());
                                    chatRoomItem.setUserCount(ssomItem.getUserCount());
                                    chatRoomItem.setMinAge(ssomItem.getMinAge());
                                    chatRoomItem.setLongitude(ssomItem.getLongitude());
                                    chatRoomItem.setLatitude(ssomItem.getLatitude());
                                    chatRoomItem.setPostId(ssomItem.getPostId());
                                    chatRoomItem.setCreatedTimestamp(data.getCreatedTimestamp());

                                    // create room heart 소진
                                    Intent intent = new Intent();
                                    intent.setAction(MessageManager.BROADCAST_HEART_COUNT_CHANGE);
                                    intent.putExtra(MessageManager.EXTRA_KEY_HEART_COUNT, getHeartCount() - 1);
                                    LocalBroadcastManager.getInstance(BaseApplication.getInstance()).sendBroadcast(intent);

                                    if(getSession().getLong(SsomPreferences.PREF_SESSION_HEART_REFILL_TIME, 0) == 0) {
                                        getSession().put(SsomPreferences.PREF_SESSION_HEART_REFILL_TIME, System.currentTimeMillis());
                                    }
                                    startChattingActivity(chatRoomItem);
                                } else if(response.code() == 428) {
                                    Log.d(TAG, "ssom 진행 중, 더이상 쏨타기 못함");
                                    showToastMessageShort(R.string.ssom_progress_message);
                                } else {
                                    showErrorMessage();
                                }
                            }

                            @Override
                            public void onFailure(Call<ChatRoomCreateResult> call, Throwable t) {

                            }
                        });
            }
        }

        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(CommonConst.DETAIL_FRAG)).commit();
        fragmentManager.popBackStack();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult called : " + requestCode + ", resultCode : " + resultCode);

        switch (requestCode) {
            case REQUEST_PROFILE_ACTIVITY :
                mNavigationDrawerFragment.setTodayImage();
                break;
            case REQUEST_SSOM_WRITE :
                if(resultCode == RESULT_OK) {
                    setSsomWriteButtonImage(true);
                    mNavigationDrawerFragment.setTodayImage();
                    dismissProgressDialog();
                }
                break;
            case REQUEST_CHECK_LOCATION_PERMISSION:
            case REQUEST_CHECK_DETAIL_LOCATION_PERMISSION:
                continueProcess();
                break;
            case InAppBillingHelper.REQUEST_CODE:
                mainAdapter.getItem(BOTTOM_STORE).onActivityResult(requestCode, resultCode, data);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(mNavigationDrawerFragment.isDrawerOpen()) {
            drawer.closeDrawers();
            return;
        }

        if(fragmentManager.findFragmentById(R.id.whole_container) != null) {
            super.onBackPressed();
            return;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(locationTracker != null) locationTracker.stopLocationUpdates();
    }

    @Override
    public void onFinishLoadingRoomList(ArrayList<ChatRoomItem> chatRoomList) {
        int unreadCount = 0;
        for(ChatRoomItem roomItem : chatRoomList) {
            unreadCount += roomItem.getUnreadCount();
        }
        setChattingCount(unreadCount);
        getSession().put(SsomPreferences.PREF_SESSION_UNREAD_COUNT, unreadCount);
        if(unreadCount != 0) {
            ShortcutBadger.applyCount(MainActivity.this, unreadCount); //for 1.1.4+
        } else {
            ShortcutBadger.removeCount(MainActivity.this);
        }
        if(mainPager.getCurrentItem() == BOTTOM_CHAT) updateToolbarToChatList();
    }

    private class MainPagerAdapter extends FragmentPagerAdapter {
        Fragment[] tabFragments;

        MainPagerAdapter(FragmentManager fm) {
            super(fm);

            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(MainActivity.this);
            tabFragments = new Fragment[]{
                    mapFragment,
                    new SsomListTabFragment().setSsomListData(ITEM_LIST),
                    new HeartStoreTabFragment(),
                    new ChatRoomTabFragment()
            };
        }

        @Override
        public Fragment getItem(int position) {
            return tabFragments[position];
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
