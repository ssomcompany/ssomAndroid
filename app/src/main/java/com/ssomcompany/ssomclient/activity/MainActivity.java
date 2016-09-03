package com.ssomcompany.ssomclient.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
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
import com.ssomcompany.ssomclient.control.ViewListener;
import com.ssomcompany.ssomclient.fragment.DetailFragment;
import com.ssomcompany.ssomclient.fragment.FilterFragment;
import com.ssomcompany.ssomclient.fragment.NavigationDrawerFragment;
import com.ssomcompany.ssomclient.fragment.SsomListFragment;
import com.ssomcompany.ssomclient.network.APICaller;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.api.GetSsomList;
import com.ssomcompany.ssomclient.network.api.model.SsomItem;
import com.ssomcompany.ssomclient.network.model.SsomResponse;
import com.ssomcompany.ssomclient.push.MessageCountCheck;
import com.ssomcompany.ssomclient.push.PushManageService;
import com.ssomcompany.ssomclient.widget.SsomActionBarView;
import com.ssomcompany.ssomclient.widget.dialog.CommonDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends BaseActivity
        implements ViewListener.NavigationDrawerCallbacks,
        ViewListener.OnPostItemInteractionListener, ViewListener.OnDetailFragmentInteractionListener,
        OnMapReadyCallback, ViewListener.OnFilterFragmentInteractionListener, MessageCountCheck {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_SSOM_WRITE = 100;
    private static final int REQUEST_SSOM_LOGIN = 200;

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

    private Location myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectedView = MAP_VIEW;
        selectedTab = CommonConst.SSOM;
        filterPref = new SsomPreferences(this, SsomPreferences.FILTER_PREF);

        locationTracker = LocationTracker.getInstance();
        if(locationTracker.chkCanGetLocation()) {
            locationTracker.startLocationUpdates(gpsLocationListener, networkLocationListener);
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //set up the toolbar
        initToolbar();
        initLayoutWrite();

//        Log.i(TAG, "drawer open state : " + drawer.isDrawerOpen(Gravity.LEFT));
//        if(drawer.isDrawerOpen(Gravity.LEFT)) drawer.closeDrawers();

        startMapFragment();
        startService(new Intent(this, PushManageService.class));
    }

    private void requestSsomList() {
        APICaller.getSsomList(new NetworkManager.NetworkListener<SsomResponse<GetSsomList.Response>>() {

            @Override
            public void onResponse(SsomResponse<GetSsomList.Response> response) {
                Log.i(TAG, "response : " + response.isSuccess());
                if (response.isSuccess()) {
                    GetSsomList.Response data = response.getData();
                    if (data != null && data.getSsomList() != null && data.getSsomList().size() > 0) {
                        ITEM_LIST.clear();
//                        ITEM_MAP.clear();

                        ITEM_LIST = data.getSsomList();
//                        for(SsomItem item : data.getSsomList()) {
//                            ITEM_MAP.put(item.getPostId(), item);
//                        }

                        // ui change at last
                        ssomDataChangedListener();
                    } else {
                        // TODO reloading to use app
                        Log.i(TAG, "data is null !!");
                    }
                } else {
                    Log.e(TAG, "Response error with code " + response.getResultCode() + ", message : " + response.getMessage(),
                            response.getError());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initFilterView();

        if(locationTracker.chkCanGetLocation()) {
            locationTracker.startLocationUpdates(gpsLocationListener, networkLocationListener);
        }
    }

    private void initFilterView() {
        TextView filterTv = (TextView) findViewById(R.id.filter_txt_age_n_count);
        String filterAge;
        String filterPeople;
        int age = filterPref.getInt(SsomPreferences.PREF_FILTER_AGE, 20);
        int people = filterPref.getInt(SsomPreferences.PREF_FILTER_PEOPLE, 1);

        filterAge = Util.convertAgeRangeAtBackOneChar(age);
        filterPeople = Util.convertPeopleRange(people);
        filterTv.setText(String.format(getResources().getString(R.string.filter_age_n_count),
                filterAge, filterPeople));
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

                SsomListFragment.newInstance().setPostItemClickListener(null);
                requestSsomList();
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

                SsomListFragment.newInstance().setPostItemClickListener(null);
                requestSsomList();
            }
        });

        ImageView btn_write = (ImageView) findViewById(R.id.btn_write);
        final Context context = this;
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(getSession().getString(SsomPreferences.PREF_SESSION_TOKEN, ""))) {
                    requestLogin();
                    return;
                }

                Intent i = new Intent();
                i.setClass(context, SsomWriteActivity.class);
                startActivityForResult(i, REQUEST_SSOM_WRITE);
            }
        });

        View filter = findViewById(R.id.filter_txt_layout);
        View filterImgLayout = findViewById(R.id.filter_img);

        // listener register
        filter.setOnClickListener(filterClickListener);
        filterImgLayout.setOnClickListener(filterClickListener);
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
            FilterFragment filterFragment = FilterFragment.newInstance();
            fragmentManager.beginTransaction().replace(R.id.top_container, filterFragment, CommonConst.FILTER_FRAG)
                    .addToBackStack(null).commit();
        }
    };

    private void initToolbar() {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        fragmentManager = getSupportFragmentManager();

        ssomActionBar = (SsomActionBarView) tb.findViewById(R.id.ssom_action_bar);
        // TODO set message count by calling getMessageCount api
        ssomActionBar.setChatCount("0");
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
        mapBtn.setTextAppearance(this, MAP_VIEW.equals(selectedView)? R.style.ssom_font_12_white_single : R.style.ssom_font_12_grayish_brown_single);
        mapBtn.setBackgroundResource(MAP_VIEW.equals(selectedView) ? R.drawable.bg_main_toggle_on : 0);
        listBtn.setTextAppearance(this, LIST_VIEW.equals(selectedView) ? R.style.ssom_font_12_white_single : R.style.ssom_font_12_grayish_brown_single);
        listBtn.setBackgroundResource(LIST_VIEW.equals(selectedView) ? R.drawable.bg_main_toggle_on : 0);
    }

    private void startMapFragment(){
        selectedView = MAP_VIEW;
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        fragmentManager.beginTransaction().
                replace(R.id.container, mapFragment).commit();
        mapFragment.getMapAsync(this);
    }

    private void startListFragment() {
        selectedView = LIST_VIEW;
        mBtnMapMyLocation.setVisibility(View.INVISIBLE);
        SsomListFragment fragment = SsomListFragment.newInstance();
        fragment.setSsomListData(getCurrentPostItems());
        fragmentManager.beginTransaction().
                replace(R.id.container, fragment, CommonConst.SSOM_LIST_FRAG).commit();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        switch (position) {
            case R.id.tv_login:
                startLoginActivity();
                break;
            case R.id.tv_confirm_email:
                break;
            case R.id.tv_noti_setting:
                break;
            case R.id.tv_make_heart:
                break;
            case R.id.tv_logout:
                UiUtils.makeCommonDialog(MainActivity.this, CommonDialog.DIALOG_STYLE_ALERT_BUTTON, R.string.dialog_notice, 0,
                        R.string.dialog_logout_message, R.string.dialog_okay, R.string.dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setSessionInfo("", "");
                                mNavigationDrawerFragment.setLoginEmailLayout();
                            }
                        }, null);
                break;
            /**
             *  list menu item click event
             *  0 : 개인정보 , 1 : 이용약관 , 2 : 문의하기
             */
            case 0:
                break;
            case 1:
                break;
            case 2:
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

        DetailFragment fragment = DetailFragment.newInstance(ssomList.get(position).getPostId());
        fragment.setSsomListData(ssomList);
        fragmentManager.beginTransaction().replace(R.id.whole_container, fragment, CommonConst.DETAIL_FRAG)
                .addToBackStack(null).commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        // Marshmallow
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            return;
//        } else {
//            // Show rationale and request permission.
//        }

        // init start my location
        moveToMyLocation(true);
        setMapUiSetting();

        // current position settings
        mBtnMapMyLocation.setVisibility(View.VISIBLE);
        mBtnMapMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!locationTracker.chkCanGetLocation()) {
                    showActivateGPSPopup();
                    return;
                }

                moveToMyLocation(false);
            }
        });
        requestSsomList();

        // 마커 클릭 리스너
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            public boolean onMarkerClick(Marker marker) {
                String postId = mIdMap.get(marker);

                // my position click 시 이벤트 없음
                if (postId == null || "".equals(postId)) return false;

                Log.i(TAG, "[마커 클릭 이벤트] latitude ="
                        + marker.getPosition().latitude + ", longitude ="
                        + marker.getPosition().longitude);

                DetailFragment fragment = DetailFragment.newInstance(postId);
                fragment.setSsomListData(getCurrentPostItems());
                fragmentManager.beginTransaction().replace(R.id.whole_container, fragment, CommonConst.DETAIL_FRAG)
                        .addToBackStack(null).commit();
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

    public ArrayList<SsomItem> getCurrentPostItems() {
        return CommonConst.SSOM.equals(selectedTab)? Util.convertAllListToSsomList(ITEM_LIST) : Util.convertAllListToSsoaList(ITEM_LIST);
    }

    private void showActivateGPSPopup() {
        // TODO - GPS on dialog popup
    }

    private void initMarker() {
        ArrayList<SsomItem> items = getCurrentPostItems();
        if(items != null && items.size()>0){
            if(mMap != null) mMap.clear();
            mIdMap.clear();
            for (int i=0 ; i<items.size() ; i++) {
                boolean isLastItem = (i == items.size() - 1);
                addMarker(items.get(i), isLastItem);
            }
        }
    }

    private void addMarker(final SsomItem item, final boolean isLastItem) {
        ImageRequest imageRequest = new ImageRequest(item.getImageUrl(), new Response.Listener<Bitmap>() {
            Marker marker;

            @Override
            public void onResponse(Bitmap bitmap) {
                marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(item.getLatitude(), item.getLongitude()))
                        .title(item.getContent()).draggable(false).icon(getMarkerImage(item.getSsomType(), bitmap)));

                mIdMap.put(marker, item.getPostId());
            }
        }
        , 144  // max width
        , 256  // max height
        , ImageView.ScaleType.CENTER  // scale type
        , Bitmap.Config.ARGB_8888  // decode config
        , new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        NetworkManager.getInstance().getRequestQueue().add(imageRequest);
    }

    private BitmapDescriptor getMarkerImage(String ssom , Bitmap imageBitmap){
        Bitmap mergedBitmap = null;
        try {
            mergedBitmap = Bitmap.createBitmap(Util.convertDpToPixel(49),
                    Util.convertDpToPixel(57), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(mergedBitmap);
            Bitmap iconBitmap;
            if(CommonConst.SSOM.equals(ssom)){
                iconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_map_st_g);
            }else{
                iconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_map_st_r);
            }

            Drawable iconDrawable = new BitmapDrawable(getApplicationContext().getResources(), iconBitmap);
            Drawable imageDrawable = new RoundImage(imageBitmap);

            iconDrawable.setBounds(0, 0,
                    Util.convertDpToPixel(49), Util.convertDpToPixel(57));
            imageDrawable.setBounds(Util.convertDpToPixel(2), Util.convertDpToPixel(2),
                    Util.convertDpToPixel(47), Util.convertDpToPixel(47));
            imageDrawable.draw(c);
            iconDrawable.draw(c);
        } catch (Exception e) {
            Log.i(TAG, "Get Marker image finished by exception..!");
        }

        return BitmapDescriptorFactory.fromBitmap(mergedBitmap);
    }

    private void startLoginActivity() {
        startActivityForResult(new Intent(this, SsomLoginBaseActivity.class), REQUEST_SSOM_LOGIN);
    }

    private void startChattingActivity(String postId, String userId, boolean ssomRequest) {
        Intent chattingIntent = new Intent(this, SsomChattingActivity.class);
        chattingIntent.putExtra(CommonConst.Intent.POST_ID, postId);
        chattingIntent.putExtra(CommonConst.Intent.USER_ID, userId);
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
        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(CommonConst.FILTER_FRAG)).commit();
        fragmentManager.popBackStack();
        if(isApply) initFilterView();
    }

    @Override
    public void onDetailFragmentInteraction(boolean isApply, String postId, String userId) {
        Log.i(TAG, "detail interaction : " + isApply);

        if(isApply) {
            // login check
            if(TextUtils.isEmpty(getSession().getString(SsomPreferences.PREF_SESSION_TOKEN, ""))) {
                requestLogin();
                return;
            }

            startChattingActivity(postId, userId, true);
        }

        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(CommonConst.DETAIL_FRAG)).commit();
        fragmentManager.popBackStack();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SSOM_WRITE :
                    requestSsomList();
                    break;
                case REQUEST_SSOM_LOGIN :
                    mNavigationDrawerFragment.setLoginEmailLayout();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_main, container, false);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        locationTracker.stopLocationUpdates();
    }



    @Override
    public void onBackPressed() {
        if(fragmentManager.findFragmentById(R.id.top_container) != null ||
                fragmentManager.findFragmentById(R.id.whole_container) != null) {
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
            }, 2000);
        }
    }

    public void setOnTabChangedListener(ViewListener.OnTabChangedListener mTabListener) {
        this.mTabListener = mTabListener;
    }
}
