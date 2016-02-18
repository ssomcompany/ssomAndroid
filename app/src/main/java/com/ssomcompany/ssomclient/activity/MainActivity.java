package com.ssomcompany.ssomclient.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.common.VolleyUtil;
import com.ssomcompany.ssomclient.fragment.DetailFragment;
import com.ssomcompany.ssomclient.fragment.FilterFragment;
import com.ssomcompany.ssomclient.fragment.NavigationDrawerFragment;
import com.ssomcompany.ssomclient.fragment.SsomListFragment;
import com.ssomcompany.ssomclient.post.PostContent;
import com.ssomcompany.ssomclient.post.PostDataChangeInterface;
import com.ssomcompany.ssomclient.post.WriteActivity;
import com.ssomcompany.ssomclient.push.PushManageService;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        SsomListFragment.OnPostItemInteractionListener, DetailFragment.OnDetailFragmentInteractionListener,
        OnMapReadyCallback, FilterFragment.OnFilterFragmentInteractionListener,
        PostDataChangeInterface {
    private static final String FILTER_FRAG = "filter_fragment";
    private static final String DETAIL_FRAG = "detail_fragment";

    private static final String TAG_MAP = "MainActivity_MAP";
    private static final String TAG_LIST = "MainActivity_LIST";

    private static final String MAP_VIEW = "map";
    private static final String LIST_VIEW = "list";

    private static final String SSOM = "ssom";
    private static final String SSOA = "ssoa";

    private HashMap<Marker, String> mIdMap = new HashMap<>();

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
    private String selectedTab;
    private FragmentManager fragmentManager;

    // current marker
    Marker currentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectedView = MAP_VIEW;
        selectedTab = SSOM;
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
        String filterAge = "";
        String filterPeople = "";
        int age = filterPref.getInt(SsomPreferences.PREF_FILTER_AGE, 20);
        int people = filterPref.getInt(SsomPreferences.PREF_FILTER_PEOPLE, 1);

        switch(age) {
            case 20:
                filterAge = getResources().getString(R.string.filter_age_20_early);
                break;
            case 25:
                filterAge = getResources().getString(R.string.filter_age_20_middle);
                break;
            case 29:
                filterAge = getResources().getString(R.string.filter_age_20_late);
                break;
            case 30:
                filterAge = getResources().getString(R.string.filter_age_30_all);
                break;
        }

        switch(people) {
            case 1:
                filterPeople = getResources().getString(R.string.filter_people_1);
                break;
            case 2:
                filterPeople = getResources().getString(R.string.filter_people_2);
                break;
            case 3:
                filterPeople = getResources().getString(R.string.filter_people_3);
                break;
            case 4:
                filterPeople = getResources().getString(R.string.filter_people_4_n_over);
                break;
        }
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
                if (SSOM.equals(selectedTab)) return;

                selectedTab = SSOM;
                giveTv.setTextAppearance(getApplicationContext(), R.style.ssom_font_16_greenblue);
                giveBtmBar.setVisibility(View.VISIBLE);
                takeTv.setTextAppearance(getApplicationContext(), R.style.ssom_font_16_greywarm);
                takeBtmBar.setVisibility(View.GONE);
            }
        });

        takeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SSOA.equals(selectedTab)) return;

                selectedTab = SSOA;
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
        filterImgLayout = findViewById(R.id.filter_img);

        // listener register
        filter.setOnClickListener(filterClickListener);
        filterImgLayout.setOnClickListener(filterClickListener);
    }

    private View.OnClickListener filterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FilterFragment filterFragment = FilterFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.top_container, filterFragment, FILTER_FRAG)
                    .addToBackStack(null).commit();
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
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        fragmentManager.beginTransaction().
                replace(R.id.container, mapFragment).commit();
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
                replace(R.id.container, SsomListFragment.newInstance(getCurrentPostItems())).commit();
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
        Fragment fragment = DetailFragment.newInstance(id, getCurrentPostItems());
        fragmentManager.beginTransaction().add(R.id.whole_container, fragment, DETAIL_FRAG)
                .addToBackStack(null).commit();
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
                if (!LocationUtil.getMyLocation(getApplicationContext(), locationResult)) {
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

        // 마커 클릭 리스너
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            public boolean onMarkerClick(Marker marker) {
                String postId = mIdMap.get(marker);

                Log.i(TAG_MAP, "[마커 클릭 이벤트] latitude ="
                        + marker.getPosition().latitude + ", longitude ="
                        + marker.getPosition().longitude);

                Fragment fragment = DetailFragment.newInstance(postId, getCurrentPostItems());
                fragmentManager.beginTransaction().add(R.id.whole_container, fragment, DETAIL_FRAG)
                        .addToBackStack(null).commit();
                return false;
            }
        });
    }

    private void initMyLocation() {
        LocationUtil.getMyLocation(this, locationResult);

        Location initLo = LocationUtil.getLocation(getApplicationContext());
        LatLng initPosition = new LatLng(initLo.getLatitude(), initLo.getLongitude());

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

    public Map<String, PostContent.PostItem> getCurrentPostItems() {
        return SSOM.equals(selectedTab)?PostContent.ITEM_GIVE:PostContent.ITEM_TAKE;
    }

    private void showActivateGPSPopup() {
        // TODO - GPS on dialog popup
    }

    private void initMarker() {
        Map<String, PostContent.PostItem> items = getCurrentPostItems();
        if(items != null && items.size()>0){
            for (Map.Entry<String, PostContent.PostItem> item : items.entrySet()) {
                addMarker(item.getValue());
            }
        }
    }

    private void addMarker(final PostContent.PostItem item) {
        ImageRequest imageRequest = new ImageRequest(item.getImage(), new Response.Listener<Bitmap>() {
            Marker marker;

            @Override
            public void onResponse(Bitmap bitmap) {
                marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(item.lat, item.lng))
                        .title(item.content).draggable(false).icon(getMarkerImage(item.ssom, bitmap)));

                mIdMap.put(marker, item.postId);
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
        Bitmap mergedBitmap = null;
        try {
            mergedBitmap = Bitmap.createBitmap((int) Util.convertDpToPixel(49, this),
                    (int) Util.convertDpToPixel(57, this), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(mergedBitmap);
            Bitmap iconBitmap =  null;
            if("ssom".equals(ssom)){
                iconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_map_st_g);
            }else{
                iconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_map_st_r);
            }

            Drawable iconDrawable = new BitmapDrawable(iconBitmap);
            Drawable imageDrawable = new RoundImage(imageBitmap);

            iconDrawable.setBounds(0, 0,
                    (int) Util.convertDpToPixel(49, this), (int) Util.convertDpToPixel(57, this));
            imageDrawable.setBounds((int) Util.convertDpToPixel(2, this), (int) Util.convertDpToPixel(2, this),
                    (int) Util.convertDpToPixel(47, this), (int) Util.convertDpToPixel(47, this));
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
        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(FILTER_FRAG)).commit();
        if(isApply) initFilterView();
    }

    @Override
    public void onDeatilFragmentInteraction(boolean isApply) {
        Log.i(TAG_MAP, "detail interaction : " + isApply);
        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(DETAIL_FRAG)).commit();

        // TODO - if true, go to chatting activity
        if(isApply) {

        }
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
