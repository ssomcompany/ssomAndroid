package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.ssomcompany.ssomclient.activity.MainActivity;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.network.api.model.SsomItem;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by AaronMac on 2016. 2. 16..
 */
public abstract class BaseFragment extends Fragment {
    public Map<String, SsomItem> postItemMap;
    public ArrayList<SsomItem> postItemList;

    abstract void setPostItems();

    public BaseFragment() {
        super();
    }

    public int getCurrentPosition(String postId) {
        int position = 0;
        if(postItemList == null) return position;

        for(int i=0 ; i<postItemList.size() ; i++) {
            if(postId.equals(postItemList.get(i).getPostId())) position = i;
        }

        Log.i(CommonConst.Tag.BASE_FRAGMENT, "getCurrentPosition() : " + position);
        return position;
    }

    @Override
    public void onAttach(Activity activity) {
        Log.i(CommonConst.Tag.BASE_FRAGMENT, "onAttach()");
        super.onAttach(activity);

        // Map, List setting
        postItemMap = ((MainActivity) activity).getCurrentPostMap();
        postItemList = ((MainActivity) activity).getCurrentPostItems();
    }

    @Override
    public void onResume() {
        Log.i(CommonConst.Tag.BASE_FRAGMENT, "onResume()");
        super.onResume();

        if(getActivity() instanceof MainActivity) {
            Log.i(CommonConst.Tag.BASE_FRAGMENT, "Reset list and map.");
            postItemMap = ((MainActivity) getActivity()).getCurrentPostMap();
            postItemList = ((MainActivity) getActivity()).getCurrentPostItems();
        }
    }

    @Override
    public void onDetach() {
        Log.i(CommonConst.Tag.BASE_FRAGMENT, "onDetach()");
        super.onDetach();

        if(postItemMap != null) postItemMap = null;
        if(postItemList != null) postItemList = null;
    }
}
