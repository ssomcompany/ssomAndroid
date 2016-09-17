package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.SsomDeleteRequest;
import com.ssomcompany.ssomclient.network.model.SsomPostRequest;

import java.util.Locale;

public class SsomPostDelete {

    public static class Request extends SsomDeleteRequest {
        public Request(String postId) {
            super(String.format(Locale.getDefault(), NetworkConstant.API.SSOM_POST_DELETE, postId));
        }
    }
    
    public static class Response extends ToStringHelperClass {
    }
}
