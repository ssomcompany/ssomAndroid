
package com.ssomcompany.ssomclient.network.model;

import com.ssomcompany.ssomclient.network.NetworkConstant;

public class SsomPostRequest extends SsomEntityRequest {

    public SsomPostRequest(String url) {
        super(url);
    }

    @Override
    public int getMethod() {
        return NetworkConstant.Method.POST;
    }

}
