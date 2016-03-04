package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.LocationUtil;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.common.VolleyUtil;
import com.ssomcompany.ssomclient.common.SsomContent;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDetailFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "DetailFragment";

    private static final String POST_ID = "postId";
    private String postId;

    private static DetailFragment detailFragment;
    private OnDetailFragmentInteractionListener mListener;

    ViewPager mViewPager;
    PagerAdapter mPagerAdapter;

    ImageView imgHeart;
    ImageView imgClose;

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

        imgHeart = (ImageView) view.findViewById(R.id.img_heart);
        imgClose = (ImageView) view.findViewById(R.id.img_close);

        imgHeart.setBackgroundResource("ssom".equals(postItemMap.get(postId).ssom)?R.drawable.icon_heart_green:R.drawable.icon_heart_red);
        imgHeart.setOnClickListener(this);
        imgClose.setOnClickListener(this);

        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mPagerAdapter = new DetailPagerAdapter(inflater);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageMargin(Math.round(Util.convertDpToPixel(16.5f, getActivity())));
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setClipToPadding(false);

        // set page position to selected item
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(getCurrentPosition(postId), false);
            }
        });

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
        mViewPager = null;
        mPagerAdapter = null;
        mListener = null;
    }

    @Override
    void setPostItems() {
        Log.i(TAG, "setPostItem called : " + getActivity().toString());
    }

    @Override
    public void onClick(View v) {
        if(v == imgHeart) {
            // TODO - heart action 정의 후 기능부여
        } else {
            onAdapterButtonPressed(false);
        }
    }

    private class DetailPagerAdapter extends PagerAdapter implements View.OnClickListener {
        LayoutInflater inflater;
        ImageLoader mImageLoader;

        public DetailPagerAdapter(LayoutInflater inflater) {
            super();
            this.inflater = inflater;
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

            SsomContent.PostItem item = postItemList.get(position);
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

            ((ViewPager)container).addView(mView);
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
            Log.d(TAG, "destroy views at : " + collection.getChildAt(position));
            ((ViewPager)collection).removeView((View) view);
            Log.d(TAG, "item count : " + collection.getChildCount());
        }

        @Override
        public int getCount() {
            return postItemList.size();
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

        //
//        @Override
//        public float getPageWidth(int position) {
//            return 0.95f;
//        }
    }
}
