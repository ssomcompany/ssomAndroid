package com.ssomcompany.ssomclient.network.model;

import com.ssomcompany.ssomclient.network.NetworkConstant;

public class SsomPutRequest extends SsomEntityRequest {

    public SsomPutRequest(String url) {
        super(url);
    }

    @Override
    public int getMethod() {
        return NetworkConstant.Method.PUT;
    }
}
