package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.activity.BaseActivity;
import com.ssomcompany.ssomclient.adapter.DrawerMenuAdapter;
import com.ssomcompany.ssomclient.common.SsomPreferences;
import com.ssomcompany.ssomclient.control.ViewListener;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.widget.CircularNetworkImageView;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {
    private static final String TAG = NavigationDrawerFragment.class.getSimpleName();

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private ViewListener.NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private View mFragmentContainerView;

//    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    // session 으로 email 영역 컨트롤
    private SsomPreferences session;

    /**
     * Left menu list 구성
     */
    private CircularNetworkImageView imgToday;
    private TextView tvLoginOrLogout;
    private TextView tvEmailOrDescription;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
//            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        session = ((BaseActivity) getActivity()).getSession();

        // Select either the default item (0) or the last selected item.
//        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        FrameLayout todayPhoto = (FrameLayout) view.findViewById(R.id.today_photo);
        imgToday = (CircularNetworkImageView) view.findViewById(R.id.img_today);
        tvLoginOrLogout = (TextView) view.findViewById(R.id.tv_login_or_logout);
        tvEmailOrDescription = (TextView) view.findViewById(R.id.tv_email_or_description);
        TextView tvSsomHomepage = (TextView) view.findViewById(R.id.tv_ssom_homepage);
        TextView tvMakeHeart = (TextView) view.findViewById(R.id.tv_make_heart);

        todayPhoto.setOnClickListener(menuItemClickListener);
        tvLoginOrLogout.setOnClickListener(menuItemClickListener);
        tvSsomHomepage.setOnClickListener(menuItemClickListener);
        tvMakeHeart.setOnClickListener(menuItemClickListener);

        // email 영역 셋팅
        setLoginEmailLayout();

        ListView mDrawerListView = (ListView) view.findViewById(R.id.lv_drawer_menu);
        // dummy view for header divider
        mDrawerListView.addHeaderView(new View(getActivity()));
        mDrawerListView.setAdapter(new DrawerMenuAdapter(getActivity()));
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "position : " + position);
                selectItem(position);
            }
        });
//        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

        return view;
    }

    /**
     * login 여부 체크 후 email 영역 셋팅
     */
    public void setLoginEmailLayout() {
        // user logged in
        if(session != null && !TextUtils.isEmpty(session.getString(SsomPreferences.PREF_SESSION_TOKEN, ""))
                && !TextUtils.isEmpty(session.getString(SsomPreferences.PREF_SESSION_EMAIL, ""))) {
            tvLoginOrLogout.setText(R.string.logout);
            tvEmailOrDescription.setText(session.getString(SsomPreferences.PREF_SESSION_EMAIL, ""));
        } else { // otherwise
            tvLoginOrLogout.setText(R.string.login_needed);
            tvEmailOrDescription.setText(R.string.login_required);
        }
    }

    public void setTodayImage() {
        Log.d(TAG, "today image : " + session.getString(SsomPreferences.PREF_SESSION_TODAY_IMAGE_URL, ""));

        if(TextUtils.isEmpty(session.getString(SsomPreferences.PREF_SESSION_TODAY_IMAGE_URL, ""))) {
            imgToday.setImageResource(0);
            return;
        }

        if(NetworkManager.getInstance().getBitmapFromCache(session.getString(SsomPreferences.PREF_SESSION_TODAY_IMAGE_URL, "")) == null) {
            ImageRequest imageRequest = new ImageRequest(session.getString(SsomPreferences.PREF_SESSION_TODAY_IMAGE_URL, ""), new Response.Listener<Bitmap>() {

                @Override
                public void onResponse(Bitmap bitmap) {
                    NetworkManager.getInstance().addBitmapToCache(session.getString(SsomPreferences.PREF_SESSION_TODAY_IMAGE_URL, ""), bitmap);
                    imgToday.setLocalImageBitmap(bitmap);
                }
            }, 800  // max width
                    , 600  // max height
                    , ImageView.ScaleType.CENTER  // scale type
                    , Bitmap.Config.RGB_565  // decode config
                    , new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                }
            });
            Volley.newRequestQueue(getActivity()).add(imageRequest);
        } else {
            imgToday.setLocalImageBitmap(NetworkManager.getInstance().getBitmapFromCache(session.getString(SsomPreferences.PREF_SESSION_TODAY_IMAGE_URL, "")));
        }
    }

    public boolean isDrawerOpen() {
        Log.d(TAG, "isDrawerOpen()");
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                mDrawerToggle.syncState();
                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                mDrawerToggle.syncState();
                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                setTodayImage();
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void syncState() {
        mDrawerToggle.syncState();
    }

    private void selectItem(int position) {
//        mCurrentSelectedPosition = position;
//        if (mDrawerListView != null) {
//            mDrawerListView.setItemChecked(position, true);
//        }
        if (position != R.id.today_photo && mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    private View.OnClickListener menuItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectItem(v.getId());
        }
    };

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                mCallbacks = (ViewListener.NavigationDrawerCallbacks) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallbacks = (ViewListener.NavigationDrawerCallbacks) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            return;
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }
}
