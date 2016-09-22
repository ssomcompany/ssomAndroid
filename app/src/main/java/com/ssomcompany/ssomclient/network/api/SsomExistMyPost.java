package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant.API;
import com.ssomcompany.ssomclient.network.model.SsomGetRequest;
import com.ssomcompany.ssomclient.network.model.SsomPostRequest;

public class SsomExistMyPost {

    public static class Request extends SsomGetRequest {
        public Request() {
            super(API.SSOM_MY_POST);
        }
    }
}
