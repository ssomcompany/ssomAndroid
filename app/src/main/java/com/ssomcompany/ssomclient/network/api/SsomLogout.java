package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.SsomPostRequest;

public class SsomLogout {

    public static class Request extends SsomPostRequest {
        public Request() {
            super(NetworkConstant.API.SSOM_LOGOUT);
        }
    }
    
    public static class Response extends ToStringHelperClass {
    }
}
