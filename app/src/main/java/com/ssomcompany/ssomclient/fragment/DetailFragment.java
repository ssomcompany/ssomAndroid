package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.activity.MainActivity;
import com.ssomcompany.ssomclient.common.CategoryUtil;
import com.ssomcompany.ssomclient.common.LocationUtil;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.common.VolleyUtil;
import com.ssomcompany.ssomclient.post.PostContent;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDetailFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "postId";

    private String postId;

    private OnDetailFragmentInteractionListener mListener;


    public static DetailFragment newInstance(String postId) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, postId);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PostContent.PostItem item  = PostContent.ITEM_MAP.get(postId);
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        //content
        TextView content = (TextView) view.findViewById(R.id.detail_content);
        content.setText(item.content);

        //image
        final ImageView fullPhoto = (ImageView) view.findViewById(R.id.full_photo);
        ImageRequest imageRequest = new ImageRequest(item.getImage(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                fullPhoto.setImageDrawable(Util.getCircleBitmap(bitmap,428));
            }
        },144, 256, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888
                , new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        VolleyUtil.getInstance(getActivity().getApplicationContext()).getRequestQueue().add(imageRequest);
        //category
        ImageView categoryIcon = (ImageView) view.findViewById(R.id.full_category);
        TextView categoryText = (TextView) view.findViewById(R.id.full_text_category);
        categoryIcon.setImageResource(CategoryUtil.getCategoryIconId(item.category));
        categoryText.setText("\n" + CategoryUtil.getCategoryDescription(item.category));

        //age
        TextView ageTextView  = (TextView) view.findViewById(R.id.full_text_age);
        ageTextView.setText(item.minAge+"~"+item.maxAge);
        //userCount
        TextView userCountTextView = (TextView) view.findViewById(R.id.full_text_user_count);
        userCountTextView.setText("" + item.userCount);

        ImageView fullPhotoSt = (ImageView) view.findViewById(R.id.full_photo_st_b);
        if("ssom".equals(item.ssom)){
            fullPhotoSt.setImageResource(R.drawable.full_photo_st_b);
        }else{
            fullPhotoSt.setImageResource(R.drawable.full_photo_st_r);
        }
        //distance
        TextView distanceText = (TextView) view.findViewById(R.id.full_text_distance);
        distanceText.setText("\n\n"+LocationUtil.getDistanceString(item));

        //time
        TextView timeText = (TextView) view.findViewById(R.id.full_detail_time);
        timeText.setText("He says - "+Util.getTimeText(Long.valueOf(item.postId)));

        ImageView btnClose = (ImageView) view.findViewById(R.id.close_detail_btn);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseButtonPressed(null);
            }
        });
        view.setClickable(true);
        return view;
    }

    public void onCloseButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onDeatilFragmentInteraction(uri);
        }
    }

    private MainActivity activity;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity  = (MainActivity) activity;
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
        activity.setWriteBtn(true);
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
        public void onDeatilFragmentInteraction(Uri uri);
    }

}