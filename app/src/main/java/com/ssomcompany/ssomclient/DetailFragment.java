package com.ssomcompany.ssomclient;

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
import com.ssomcompany.ssomclient.common.CategoryUtil;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.common.VolleyUtil;
import com.ssomcompany.ssomclient.post.PostContent;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "postId";

    private String postId;

    private OnFragmentInteractionListener mListener;


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
        VolleyUtil.getInstance(getContext()).getRequestQueue().add(imageRequest);
        //category
        ImageView categoryIcon = (ImageView) view.findViewById(R.id.full_category);
        TextView categoryText = (TextView) view.findViewById(R.id.full_text_category);
        categoryIcon.setImageResource(CategoryUtil.getCategoryIconId(item.category));
        categoryText.setText(CategoryUtil.getCategoryDescription(item.category));

        //age

        //userCount

        ImageView btnClose = (ImageView) view.findViewById(R.id.close_detail_btn);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(null);
            }
        });
        view.setClickable(true);
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
