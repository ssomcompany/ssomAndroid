package com.ssomcompany.ssomclient.network.model;

import com.ssomcompany.ssomclient.network.NetworkConstant;

public class SsomDeleteRequest extends SsomURLRequest {

    public SsomDeleteRequest(String url) {
        super(url);
    }

    @Override
    public int getMethod() {
        return NetworkConstant.Method.DELETE;
    }
}
