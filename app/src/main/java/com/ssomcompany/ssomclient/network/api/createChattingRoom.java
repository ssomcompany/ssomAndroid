package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.SsomPostRequest;

public class CreateChattingRoom {

    public static class Request extends SsomPostRequest {
        String postId;

        public Request() {
            super(NetworkConstant.API.CREATE_ROOM);
        }

        public String getPostId() {
            return postId;
        }

        public Request setPostId(String postId) {
            this.postId = postId;
            return this;
        }
    }
    
    public static class Response extends ToStringHelperClass {}
}