package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.ssomcompany.ssomclient.activity.MainActivity;
import com.ssomcompany.ssomclient.common.SsomContent;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by AaronMac on 2016. 2. 16..
 */
public abstract class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";

    public Map<String, SsomContent.PostItem> postItemMap;
    public ArrayList<SsomContent.PostItem> postItemList;

    abstract void setPostItems();

    public BaseFragment() {
        super();
    }

    public int getCurrentPosition(String postId) {
        int position = 0;
        if(postItemList == null) return position;

        for(int i=0 ; i<postItemList.size() ; i++) {
            if(postId.equals(postItemList.get(i).postId)) position = i;
        }

        Log.i(TAG, "getCurrentPosition() : " + position);
        return position;
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
