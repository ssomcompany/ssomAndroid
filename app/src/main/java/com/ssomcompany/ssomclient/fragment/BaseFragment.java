package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.ssomcompany.ssomclient.activity.MainActivity;
import com.ssomcompany.ssomclient.post.PostContent;
import com.ssomcompany.ssomclient.post.PostDataChangeInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by AaronMac on 2016. 2. 16..
 */
public abstract class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";

    public Map<String, PostContent.PostItem> postItemMap;
    public ArrayList<PostContent.PostItem> postItemList;

    abstract void setPostItems();

    public BaseFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        Log.i(TAG, "onAttach()");
        super.onAttach(activity);

        // Map, List setting
        postItemMap = ((MainActivity) activity).getCurrentPostMap();
        postItemList = ((MainActivity) activity).getCurrentPostItems();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume()");
        super.onResume();
    }

    @Override
    public void onDetach() {
        Log.i(TAG, "onDetach()");
        super.onDetach();

        if(postItemMap != null) postItemMap = null;
        if(postItemList != null) postItemList = null;
    }
}
