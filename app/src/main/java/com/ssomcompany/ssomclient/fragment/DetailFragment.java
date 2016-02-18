package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.activity.MainActivity;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.common.VolleyUtil;
import com.ssomcompany.ssomclient.post.PostContent;

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

    public DetailFragment() { super(); }

    public static DetailFragment newInstance(String postId, Map<String, PostContent.PostItem> items) {
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
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            postId = getArguments().getString(POST_ID);
            Log.i(TAG, "postId : " + postId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PostContent.PostItem item  = postItemMap.get(postId);
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mPagerAdapter = new DetailPagerAdapter(inflater);
//        mViewPager.setCurrentItem(item);
        mViewPager.setAdapter(mPagerAdapter);

        return view;
    }

    public void onCloseButtonPressed() {
        if (mListener != null) {
            mListener.onDeatilFragmentInteraction(false);
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
    void setPostItemMap() {
        this.postItemMap = ((MainActivity) getActivity()).getCurrentPostItems();
        mPagerAdapter.notifyDataSetChanged();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDetailFragmentInteractionListener {
        public void onDeatilFragmentInteraction(boolean isApply);
    }

    private class DetailPagerAdapter extends PagerAdapter {
        LayoutInflater inflater;
        ImageLoader mImageLoader;

        public DetailPagerAdapter(LayoutInflater inflater) {
            this.inflater = inflater;
            this.mImageLoader = VolleyUtil.getInstance(getActivity()).getImageLoader();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = inflater.inflate(R.layout.detail_pager_adapter, null);

            NetworkImageView profileImg = (NetworkImageView) view.findViewById(R.id.profile_img);
            LinearLayout centerLine = (LinearLayout) view.findViewById(R.id.center_line_layout);
            TextView tvCategory = (TextView) view.findViewById(R.id.tv_category);
            TextView tvDistance = (TextView) view.findViewById(R.id.tv_distance);
            TextView tvAgePeople = (TextView) view.findViewById(R.id.tv_age_people);
            TextView tvContent = (TextView) view.findViewById(R.id.tv_content);
            TextView btnCancel = (TextView) view.findViewById(R.id.btn_cancel);
            LinearLayout btnApply = (LinearLayout) view.findViewById(R.id.btn_apply);

            PostContent.PostItem item = postItemMap.get(position);

            profileImg.setImageUrl(item.getImage(), mImageLoader);

            return view;
        }

        @Override
        public int getCount() {
            return postItemMap.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
