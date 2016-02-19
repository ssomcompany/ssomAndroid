package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.activity.MainActivity;
import com.ssomcompany.ssomclient.common.LocationUtil;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.common.VolleyUtil;
import com.ssomcompany.ssomclient.post.PostContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDetailFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends BaseFragment {
    private static final String TAG = "DetailFragment";

    private static final String POST_ID = "postId";
    private String postId;

    private static DetailFragment detailFragment;
    private OnDetailFragmentInteractionListener mListener;

    ViewPager mViewPager;
    PagerAdapter mPagerAdapter;

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnDetailFragmentInteractionListener {
        void onDetailFragmentInteraction(boolean isApply);
    }

    public DetailFragment() { super(); }

    public static DetailFragment newInstance(String postId) {
        if(detailFragment == null) {
            detailFragment = new DetailFragment();
        }

        detailFragment.postId = postId;

//        Bundle args = new Bundle();
//        args.putString(POST_ID, postId);
//        args.putParcelable("map", items);
//        detailFragment.setArguments(args);

        return detailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            postId = getArguments().getString(POST_ID);
            Log.i(TAG, "postId : " + postId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mPagerAdapter = new DetailPagerAdapter(inflater, postItemList);
//        mViewPager.setCurrentItem(item);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageMargin(Math.round(Util.convertDpToPixel(16.5f, getActivity())));
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setClipToPadding(false);

        return view;
    }

    public void onAdapterButtonPressed(boolean apply) {
        if (mListener != null) {
            mListener.onDetailFragmentInteraction(apply);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnDetailFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDetailFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    void setPostItems() {
        Log.i(TAG, "setPostItem called : " + getActivity().toString());
    }

    private class DetailPagerAdapter extends PagerAdapter implements View.OnClickListener {
        LayoutInflater inflater;
        ImageLoader mImageLoader;
        ArrayList<PostContent.PostItem> pagerItems;

        public DetailPagerAdapter(LayoutInflater inflater, ArrayList<PostContent.PostItem> items) {
            this.inflater = inflater;
            this.pagerItems = items;
            this.mImageLoader = VolleyUtil.getInstance(getActivity()).getImageLoader();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View mView = inflater.inflate(R.layout.detail_pager_adapter, null);

            NetworkImageView profileImg = (NetworkImageView) mView.findViewById(R.id.profile_img);
            LinearLayout centerLine = (LinearLayout) mView.findViewById(R.id.center_line_layout);
            TextView tvCategory = (TextView) mView.findViewById(R.id.tv_category);
            TextView tvDistance = (TextView) mView.findViewById(R.id.tv_distance);
            TextView tvAgePeople = (TextView) mView.findViewById(R.id.tv_age_people);
            TextView tvContent = (TextView) mView.findViewById(R.id.tv_content);
            TextView btnCancel = (TextView) mView.findViewById(R.id.btn_cancel);
            LinearLayout btnApply = (LinearLayout) mView.findViewById(R.id.btn_apply);

            PostContent.PostItem item = pagerItems.get(position);
            // item setting
            profileImg.setImageUrl(item.getImage(), mImageLoader);
            centerLine.setBackgroundResource("ssom".equals(item.ssom) ? R.drawable.bg_detail_center_green : R.drawable.bg_detail_center_red);
            tvCategory.setText("ssom".equals(item.ssom) ? R.string.title_tab_give : R.string.title_tab_take);
            tvDistance.setText( String.format( getResources().getString(R.string.detail_distance), LocationUtil.getDistanceString(item) ) );
            tvAgePeople.setText( String.format( getResources().getString(R.string.detail_age_people), Util.convertAgeRange(item.minAge), item.userCount) );
            tvContent.setText(item.content);
            btnApply.setBackgroundResource("ssom".equals(item.ssom) ? R.drawable.btn_write_apply_ssom : R.drawable.btn_write_apply_ssoa);

            // btn setting
            btnCancel.setOnClickListener(this);
            btnApply.setOnClickListener(this);

            container.addView(mView, position);
            return mView;
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();

            if(id == R.id.btn_cancel) {
                onAdapterButtonPressed(false);
            } else {
                onAdapterButtonPressed(true);
            }
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            //must be overridden else throws exception as not overridden.
            Log.d(TAG, "destroy views at : " + collection.getChildCount());
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return pagerItems.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public float getPageWidth(int position) {
            return 0.925f;
        }
    }
}
