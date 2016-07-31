package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.SsomPostRequest;

public class SsomLogin {

    public static class Request extends SsomPostRequest {
        public Request() {
            super(NetworkConstant.API.SSOM_LOGIN);
        }
    }
    
    public static class Response extends ToStringHelperClass {
        String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
