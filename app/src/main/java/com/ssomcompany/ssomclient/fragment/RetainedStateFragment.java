package com.ssomcompany.ssomclient.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by AaronMac on 2017. 1. 2..
 */

public class RetainedStateFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
}
