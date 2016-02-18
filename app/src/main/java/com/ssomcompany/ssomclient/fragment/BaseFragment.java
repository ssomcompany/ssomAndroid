package com.ssomcompany.ssomclient.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.ssomcompany.ssomclient.post.PostContent;
import com.ssomcompany.ssomclient.post.PostDataChangeInterface;

import java.util.Map;

/**
 * Created by AaronMac on 2016. 2. 16..
 */
public abstract class BaseFragment extends Fragment implements PostDataChangeInterface {
    private static final String TAG = "BaseFragment";

    public Map<String, PostContent.PostItem> postItemMap;

    abstract void setPostItemMap();

    public BaseFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        Log.i(TAG, "onAttach()");
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume()");
        super.onResume();

        PostContent.init(getActivity(), this);
    }

    @Override
    public void onPostItemChanged() {
        setPostItemMap();
    }
}
