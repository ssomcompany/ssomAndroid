package com.ssomcompany.ssomclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.ssomcompany.ssomclient.common.VolleyUtil;
import com.ssomcompany.ssomclient.post.PostContent;
import com.ssomcompany.ssomclient.post.RoundImage;
import com.ssomcompany.ssomclient.push.PushManageService;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        SsomListFragment.OnPostItemInteractionListener, DetailFragment.OnFragmentInteractionListener,
        OnMapReadyCallback,GoogleMap.OnMyLocationChangeListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private String selectedView = "map";
    private ImageView mBtnMapMyLocation;
    private ImageView btn_write;
    private TextView mapBtn;
    private TextView listBtn;
    private FragmentManager fragmentManager;
    private Location pastLocation;
    private ImageView bgList;
    private View filter;
    private int postItemIndexForMapFragment =0; // TODO: 2015. 10. 6. temporary variable for putting marker on random location

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    private void initLayoutWrite(){
        bgList = (ImageView) findViewById(R.id.bg_list_bot);
        btn_write = (ImageView) findViewById(R.id.btn_write);
        filter = findViewById(R.id.filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"start filter",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initToolbar() {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        fragmentManager = getSupportFragmentManager();
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        final Context context = this;

        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(context, WriteActivity.class);
                startActivity(i);
            }
        });
        mBtnMapMyLocation = (ImageView) findViewById(R.id.map_current_location);

        toggleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedView.equals("map")) {
                    startListFragment();
                } else {
                    startMapFragment();
                }
            }
        });
    }
    private void startMapFragment(){
        selectedView = "map";
        postItemIndexForMapFragment = 0;
        SupportMapFragment mapFragment = new SupportMapFragment();
        PostContent.init(this, null);
        bgList.setVisibility(View.INVISIBLE);
        fragmentManager.beginTransaction().
                replace(R.id.container, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);
        mapBtn.setVisibility(View.VISIBLE);
        listBtn.setVisibility(View.INVISIBLE);
        isFirstTimeChangeLocation = true;
    }
    private void startListFragment() {
        mapBtn.setVisibility(View.INVISIBLE);
        listBtn.setVisibility(View.VISIBLE);
        selectedView = "list";
        bgList.setVisibility(View.VISIBLE);
        mBtnMapMyLocation.setVisibility(View.INVISIBLE);
        Fragment fragment = SsomListFragment.newInstance("1", "2");
        fragmentManager.beginTransaction().
                replace(R.id.container, fragment)
                .commit();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
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
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().
                add(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }
    public void setWriteBtn(boolean on){
        if(on){
            btn_write.setVisibility(View.VISIBLE);
            bgList.setVisibility(View.VISIBLE);
            filter.setVisibility(View.VISIBLE);
        }else{
            btn_write.setVisibility(View.INVISIBLE);
            bgList.setVisibility(View.INVISIBLE);
            filter.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onFragmentInteraction(Uri uri) {
        onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        mMap.setMyLocationEnabled(true);
        mBtnMapMyLocation.setVisibility(View.VISIBLE);
        if(pastLocation!=null){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(pastLocation.getLatitude(), pastLocation.getLongitude())));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
        }
        mMap.setOnMyLocationChangeListener(this);
//        initLocationUsingLocationManager();
        final Context context = this;
        mBtnMapMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Location location = mMap.getMyLocation();
                if (location != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
                } else {
                    Toast.makeText(context, "my location didn`t set yet. wait a minute ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addMarker(latLng);
            }
        });
        

    }

    private void addMarker(final LatLng location) {
        if(PostContent.ITEMS.size() <= postItemIndexForMapFragment){
            Toast.makeText(getApplicationContext(),"no more post item",Toast.LENGTH_SHORT).show();
            return ;
        }
        final PostContent.PostItem item = PostContent.ITEMS.get(postItemIndexForMapFragment++);
        ImageRequest imageRequest = new ImageRequest(item.getImage(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(item.content).draggable(false).icon(getMarkerImage(item.ssom, bitmap)));
            }
        },144, 256, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888
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
    private boolean isFirstTimeChangeLocation;
    @Override
    public void onMyLocationChange(Location location) {
        if(isFirstTimeChangeLocation && pastLocation==null){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
            isFirstTimeChangeLocation = false;
            pastLocation = location;
        }
    }


//    private void initLocationUsingLocationManager() {
//        mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
////        String bestProvider = mLocationManager.getBestProvider(criteria,false);
//        String bestProvider = LocationManager.NETWORK_PROVIDER;
//        Location location = mLocationManager.getLastKnownLocation(bestProvider);
//        Toast.makeText(this,location.getLatitude()+"/"+location.getLongitude(),Toast.LENGTH_SHORT).show();
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
//
//
//    }

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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
