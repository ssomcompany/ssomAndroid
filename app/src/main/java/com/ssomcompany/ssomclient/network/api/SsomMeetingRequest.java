package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.SsomDeleteRequest;
import com.ssomcompany.ssomclient.network.model.SsomEntityRequest;
import com.ssomcompany.ssomclient.network.model.SsomPostRequest;
import com.ssomcompany.ssomclient.network.model.SsomPutRequest;

public class SsomMeetingRequest {

    public static class PutRequest extends SsomPutRequest {
        String chatroomId;

        public PutRequest() {
            super(NetworkConstant.API.MEETING_REQUEST);
        }

        public String getChatroomId() {
            return chatroomId;
        }

        public PutRequest setChatroomId(String chatroomId) {
            this.chatroomId = chatroomId;
            return this;
        }
    }

    public static class DeleteRequest extends SsomDeleteRequest {
//        String chatroomId;

        public DeleteRequest(String chatroomId) {
            super(NetworkConstant.API.MEETING_REQUEST + "/" + chatroomId);
        }

//        @Override
//        public int getMethod() {
//            return NetworkConstant.Method.DELETE;
//        }

//        public String getChatroomId() {
//            return chatroomId;
//        }
//
//        public DeleteRequest setChatroomId(String chatroomId) {
//            this.chatroomId = chatroomId;
//            return this;
//        }
    }

    public static class PostRequest extends SsomPostRequest {
        String chatroomId;

        public PostRequest() {
            super(NetworkConstant.API.MEETING_REQUEST);
        }

        public String getChatroomId() {
            return chatroomId;
        }

        public PostRequest setChatroomId(String chatroomId) {
            this.chatroomId = chatroomId;
            return this;
        }
    }

    public static class Response extends ToStringHelperClass {
    }
}
