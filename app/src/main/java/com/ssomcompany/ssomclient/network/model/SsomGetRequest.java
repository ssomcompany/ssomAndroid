package com.ssomcompany.ssomclient.network.model;

import com.ssomcompany.ssomclient.network.NetworkConstant;

public class SsomGetRequest extends SsomURLRequest {

    public SsomGetRequest(String url) {
        super(url);
    }

    @Override
    public int getMethod() {
        return NetworkConstant.Method.GET;
    }
}
