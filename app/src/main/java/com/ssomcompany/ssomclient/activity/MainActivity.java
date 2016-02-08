package com.ssomcompany.ssomclient.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.ssomcompany.ssomclient.common.LocationUtil;
import com.ssomcompany.ssomclient.common.RoundImage;
import com.ssomcompany.ssomclient.common.SsomPreferences;
import com.ssomcompany.ssomclient.common.VolleyUtil;
import com.ssomcompany.ssomclient.fragment.DetailFragment;
import com.ssomcompany.ssomclient.fragment.FilterFragment;
import com.ssomcompany.ssomclient.fragment.NavigationDrawerFragment;
import com.ssomcompany.ssomclient.fragment.SsomListFragment;
import com.ssomcompany.ssomclient.post.PostContent;
import com.ssomcompany.ssomclient.post.PostDataChangeInterface;
import com.ssomcompany.ssomclient.post.WriteActivity;
import com.ssomcompany.ssomclient.push.PushManageService;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        SsomListFragment.OnPostItemInteractionListener, DetailFragment.OnDetailFragmentInteractionListener,
        OnMapReadyCallback, FilterFragment.OnFilterFragmentInteractionListener,
        PostDataChangeInterface {

    private static final String TAG_MAP = "MainActivity_MAP";
    private static final String TAG_LIST = "MainActivity_LIST";

    private static final String MAP_VIEW = "map";
    private static final String LIST_VIEW = "list";

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
    private View filter;
    private View filterImgLayout;
    private TextView filterTv;
    private SsomPreferences filterPref;

    /**
     * layout write resources
     */
    private ImageView mBtnMapMyLocation;
    private ImageView btn_write;

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
    private boolean initMarker;
    private FragmentManager fragmentManager;

    // current marker
    Marker currentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectedView = MAP_VIEW;
        PostContent.init(this, this);
        filterPref = new SsomPreferences(this, SsomPreferences.FILTER_PREF);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        //set up the toolbar
        initToolbar();
        initLayoutWrite();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        startMapFragment();
        startService(new Intent(this, PushManageService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        initFilterView();
    }

    private void initFilterView() {
        filterTv = (TextView) findViewById(R.id.filter_txt_age_n_count);
        filterTv.setText(String.format(getResources().getString(R.string.filter_age_n_count),
                filterPref.getInt(SsomPreferences.PREF_FILTER_AGE, 20) + "대 초",
                filterPref.getInt(SsomPreferences.PREF_FILTER_PEOPLE, 1)));

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
                giveTv.setTextAppearance(getApplicationContext(), R.style.ssom_font_16_greenblue);
                giveBtmBar.setVisibility(View.VISIBLE);
                takeTv.setTextAppearance(getApplicationContext(), R.style.ssom_font_16_greywarm);
                takeBtmBar.setVisibility(View.GONE);
            }
        });

        takeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeTv.setTextAppearance(getApplicationContext(), R.style.ssom_font_16_redpink);
                takeBtmBar.setVisibility(View.VISIBLE);
                giveTv.setTextAppearance(getApplicationContext(), R.style.ssom_font_16_greywarm);
                giveBtmBar.setVisibility(View.GONE);
            }
        });

        btn_write = (ImageView) findViewById(R.id.btn_write);
        final Context context = this;
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(context, WriteActivity.class);
                startActivity(i);
            }
        });
        filter = findViewById(R.id.filter_txt_layout);
        filterImgLayout = findViewById(R.id.filter_img_layout);

        // listener register
        filter.setOnClickListener(filterClickListener);
        filterImgLayout.setOnClickListener(filterClickListener);
    }

    private View.OnClickListener filterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FilterFragment filterFragment = FilterFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.container, filterFragment, "filter_fragment")
                    .addToBackStack(null)
                    .commit();
            setWriteBtn(false);
        }
    };

    private void initToolbar() {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        fragmentManager = getSupportFragmentManager();
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawers();
        ImageView lnbMenu = (ImageView) tb.findViewById(R.id.lnb_menu_btn);
        lnbMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
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
            }
        });
    }

    private void startMapFragment(){
        selectedView = MAP_VIEW;
        initMarker = false;
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        fragmentManager.beginTransaction().
                replace(R.id.container, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);
        mapBtn.setVisibility(View.VISIBLE);
        listBtn.setVisibility(View.INVISIBLE);
    }

    private void startListFragment() {
        selectedView = LIST_VIEW;
        mapBtn.setVisibility(View.INVISIBLE);
        listBtn.setVisibility(View.VISIBLE);
        mBtnMapMyLocation.setVisibility(View.INVISIBLE);

        fragmentManager.beginTransaction().
                replace(R.id.container, SsomListFragment.newInstance("1", "2"))
                .commit();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostItemClick(String id) {
        setWriteBtn(false);
        Fragment fragment = DetailFragment.newInstance(PostContent.ITEM_MAP.get(id).postId);
        fragmentManager.beginTransaction().add(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    // layout visibility setting
    public void setWriteBtn(boolean on){
        if(on){
            btn_write.setVisibility(View.VISIBLE);
            filter.setVisibility(View.VISIBLE);
            filterImgLayout.setVisibility(View.VISIBLE);
        }else{
            btn_write.setVisibility(View.INVISIBLE);
            filter.setVisibility(View.INVISIBLE);
            filterImgLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDeatilFragmentInteraction(Uri uri) {
        onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        mMap.setMyLocationEnabled(false);
        // Marshmallow
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//        } else {
//            // Show rationale and request permission.
//        }

        // init start my location
        initMyLocation();

        // current position settings
        mBtnMapMyLocation.setVisibility(View.VISIBLE);
        mBtnMapMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!LocationUtil.getMyLocation(getApplicationContext(), locationResult)) {
                    showActivateGPSPopup();
                    return;
                }

                Location currentLo = LocationUtil.getLocation(getApplicationContext());
                LatLng currentPosition = new LatLng(currentLo.getLatitude(), currentLo.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 13));
                currentMarker.setPosition(currentPosition);
            }
        });
        initMarker();
    }

    private void initMyLocation() {
        LatLng initPosition;
        if(LocationUtil.getMyLocation(this, locationResult)) {
            Location initLo = LocationUtil.getLocation(getApplicationContext());
            initPosition = new LatLng(initLo!=null?initLo.getLatitude():37.55595, initLo!=null?initLo.getLongitude():126.9230138);
        } else {
            // 위치정보를 가져올 수 없는 경우 기본을 홍대입구 역으로 셋팅
            initPosition = new LatLng(37.55595, 126.9230138);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initPosition, 13));
        currentMarker = mMap.addMarker(new MarkerOptions()
                .position(initPosition)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

    private LocationUtil.LocationResult locationResult = new LocationUtil.LocationResult() {
        @Override
        public void getLocationCallback(Location location) {
            Log.i(TAG_MAP, "lat : " + location.getLatitude() + ", lon : " + location.getLongitude());

            LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

            // currentPosition 위치로 카메라 중심을 옮기고 화면 줌을 조정한다. 줌범위는 2~21, 숫자클수록 확대
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 13));

            if(currentMarker == null) {  // 마커 추가
                currentMarker = mMap.addMarker(new MarkerOptions()
                        .position(currentPosition)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            } else {  // 마커 갱신
                currentMarker.setPosition(currentPosition);
            }

        }
    };

    private void showActivateGPSPopup() {
        // TODO - GPS on dialog popup
    }

    private void initMarker() {
        if(PostContent.ITEMS.size()>0 && !initMarker){
            initMarker = true;
            for ( PostContent.PostItem item: PostContent.ITEMS) {
                addMarker(item);
            }
        }
    }

    private void addMarker(final PostContent.PostItem item) {
        ImageRequest imageRequest = new ImageRequest(item.getImage(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(item.lat,item.lng))
                        .title(item.content).draggable(false).icon(getMarkerImage(item.ssom, bitmap)));
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
        VolleyUtil.getInstance(getApplicationContext()).getRequestQueue().add(imageRequest);
    }

    private BitmapDescriptor getMarkerImage(String ssom , Bitmap imageBitmap){
        //TODO make BitmapDescriptor with profile image

        Bitmap mergedBitmap = null;
        try {

            mergedBitmap = Bitmap.createBitmap(188, 237, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(mergedBitmap);
            Resources res = getResources();
            Bitmap iconBitmap =  null;
            if("ssom".equals(ssom)){
                iconBitmap = BitmapFactory.decodeResource(res, R.drawable.icon_buy_black);
            }else{
                iconBitmap = BitmapFactory.decodeResource(res, R.drawable.icon_sell_red);
            }

            Drawable iconDrawable = new BitmapDrawable(iconBitmap);
            Drawable imageDrawable = new RoundImage(imageBitmap);

            iconDrawable.setBounds(0, 0, 188, 237);
            imageDrawable.setBounds(18, 18, 170, 170);
            imageDrawable.draw(c);
            iconDrawable.draw(c);

        } catch (Exception e) {
        }
        return BitmapDescriptorFactory.fromBitmap(mergedBitmap);
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
        Log.i(TAG_MAP, "filter interaction : " + isApply);
        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag("filter_fragment")).commit();
        if(isApply) initFilterView();
        setWriteBtn(true);
    }

    @Override
    public void onPostItemChanged() {
        if(MAP_VIEW.equals(selectedView)){
            initMarker();
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
        LocationUtil.stopLocationUpdates();
    }

}
