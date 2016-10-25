package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.activity.SsomImageDetailActivity;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.LocationTracker;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.control.ViewListener;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.api.model.SsomItem;
import com.ssomcompany.ssomclient.widget.RoundedNetworkImageView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewListener.OnDetailFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = DetailFragment.class.getSimpleName();

    private String postId;
    private int currentPos;

    private static DetailFragment detailFragment;
    private ViewListener.OnDetailFragmentInteractionListener mListener;

    private ArrayList<SsomItem> ssomList;

    ViewPager mViewPager;
    PagerAdapter mPagerAdapter;

    ImageView imgHeart;
    ImageView imgClose;

    public DetailFragment() { super(); }

    public static DetailFragment newInstance(String postId) {
        if(detailFragment == null) {
            detailFragment = new DetailFragment();
        }

        detailFragment.postId = postId;

        return detailFragment;
    }

    public void setSsomListData(ArrayList<SsomItem> ssomList) {
        this.ssomList = ssomList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        currentPos = getCurrentPosition(postId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        imgHeart = (ImageView) view.findViewById(R.id.img_heart);
        imgClose = (ImageView) view.findViewById(R.id.img_close);

        imgHeart.setImageResource(CommonConst.SSOM.equals(ssomList.get(0).getSsomType()) ? R.drawable.icon_heart_green : R.drawable.icon_heart_red);
        imgHeart.setOnClickListener(this);
        imgClose.setOnClickListener(this);

        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mPagerAdapter = new DetailPagerAdapter(inflater);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageMargin(Math.round(Util.convertDpToPixel(16.5f)));
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setClipToPadding(false);

        // set page position to selected item
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(currentPos, false);
            }
        });

        return view;
    }

    public void onAdapterButtonPressed(boolean apply) {
        if (mListener != null) {
            if(apply) {
                mListener.onDetailFragmentInteraction(true, ssomList.get(currentPos));
            } else {
                mListener.onDetailFragmentInteraction(false, null);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (ViewListener.OnDetailFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnDetailFragmentInteractionListener");
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                mListener = (ViewListener.OnDetailFragmentInteractionListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnDetailFragmentInteractionListener");
            }
        }
    }

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            postId = ssomList.get(position).getPostId();
            currentPos = position;
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        if(mViewPager == null) {
            Log.d(TAG, "viewPager is null");
            return;
        }

        mViewPager.addOnPageChangeListener(mPageChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mViewPager == null) {
            Log.d(TAG, "viewPager is null");
            return;
        }

        mViewPager.removeOnPageChangeListener(mPageChangeListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mViewPager = null;
        mPagerAdapter = null;
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if(v == imgHeart) {
            // TODO - heart action 정의 후 기능부여
        } else {
            onAdapterButtonPressed(false);
        }
    }

    public int getCurrentPosition(String postId) {
        int position = 0;
        if(ssomList == null) return position;

        for(int i=0 ; i<ssomList.size() ; i++) {
            if(postId.equals(ssomList.get(i).getPostId())) {
                position = i;
                break;
            }
        }

        Log.i(TAG, "getCurrentPosition() : " + position);
        return position;
    }

    private class DetailPagerAdapter extends PagerAdapter implements View.OnClickListener {
        LayoutInflater inflater;
        ImageLoader mImageLoader;

        DetailPagerAdapter(LayoutInflater inflater) {
            super();
            this.inflater = inflater;
            this.mImageLoader = NetworkManager.getInstance().getImageLoader();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View mView = inflater.inflate(R.layout.detail_pager_adapter, container, false);

            RoundedNetworkImageView profileImg = (RoundedNetworkImageView) mView.findViewById(R.id.profile_img);
            LinearLayout centerLine = (LinearLayout) mView.findViewById(R.id.center_line_layout);
            TextView tvCategory = (TextView) mView.findViewById(R.id.tv_category);
            TextView tvDistance = (TextView) mView.findViewById(R.id.tv_distance);
            TextView tvAgePeople = (TextView) mView.findViewById(R.id.tv_age_people);
            TextView tvContent = (TextView) mView.findViewById(R.id.tv_content);
            TextView btnCancel = (TextView) mView.findViewById(R.id.btn_cancel);
            LinearLayout btnApply = (LinearLayout) mView.findViewById(R.id.btn_apply);

            SsomItem item = ssomList.get(position);
            // item setting
            profileImg.setImageUrl(item.getImageUrl(), mImageLoader);
            profileImg.setOnClickListener(this);
            centerLine.setBackgroundResource(CommonConst.SSOM.equals(item.getSsomType()) ? R.drawable.bg_detail_center_green : R.drawable.bg_detail_center_red);
            tvCategory.setText(CommonConst.SSOM.equals(item.getSsomType()) ? R.string.detail_category_ssom : R.string.detail_category_ssoa);
            tvDistance.setText( String.format(getResources().getString(R.string.detail_distance),
                    LocationTracker.getInstance().getDistanceString(item.getLatitude(), item.getLongitude())) );
            tvAgePeople.setText( String.format( getResources().getString(R.string.detail_age_people), Util.convertAgeRange(item.getMinAge()), item.getUserCount()) );
            tvContent.setText(item.getContent());
            btnApply.setBackgroundResource(CommonConst.SSOM.equals(item.getSsomType()) ? R.drawable.btn_write_apply_ssom : R.drawable.btn_write_apply_ssoa);
            if(!TextUtils.isEmpty(item.getUserId()) && item.getUserId().equals(getUserId())) {
                btnApply.getChildAt(0).setVisibility(View.GONE);
                ((TextView) btnApply.getChildAt(1)).setText(getResources().getString(R.string.dialog_delete));
            }

            // btn setting
            btnCancel.setOnClickListener(this);
            btnApply.setOnClickListener(this);

            container.addView(mView);
            return mView;
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();

            switch (id) {
                case R.id.btn_cancel:
                    onAdapterButtonPressed(false);
                    break;
                case R.id.btn_apply:
                    onAdapterButtonPressed(true);
                    break;
                case R.id.profile_img:
                    Intent intent = new Intent(getActivity(), SsomImageDetailActivity.class);
                    intent.putExtra(CommonConst.Intent.IMAGE_URL, ssomList.get(currentPos).getImageUrl());
                    startActivity(intent);
                    break;
            }
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            //must be overridden else throws exception as not overridden.
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return ssomList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        /* To make sure this pager's view correctly loaded, must override these four methods below */
        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(ViewGroup container) {}

        @Override
        public void finishUpdate(ViewGroup container) {}

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {}

        // viewpager page width setting left and right padding with this
//        @Override
//        public float getPageWidth(int position) {
//            return 0.95f;
//        }
    }
}
