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

    public static class Response extends ToStringHelperClass {
        private String postId;
        private String ssomType;

        public String getPostId() {
            return postId;
        }

        public void setPostId(String postId) {
            this.postId = postId;
        }

        public String getSsomType() {
            return ssomType;
        }

        public void setSsomType(String ssomType) {
            this.ssomType = ssomType;
        }
    }
}
