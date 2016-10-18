package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.SsomDeleteRequest;
import com.ssomcompany.ssomclient.network.model.SsomEntityRequest;
import com.ssomcompany.ssomclient.network.model.SsomPostRequest;
import com.ssomcompany.ssomclient.network.model.SsomPutRequest;

public class SsomMeetingRequest {

    public static class PutRequest extends SsomPutRequest {
        long chatroomId;

        public PutRequest() {
            super(NetworkConstant.API.MEETING_REQUEST);
        }

        public long getChatroomId() {
            return chatroomId;
        }

        public PutRequest setChatroomId(long chatroomId) {
            this.chatroomId = chatroomId;
            return this;
        }
    }

    public static class DeleteRequest extends SsomDeleteRequest {
//        String chatroomId;

        public DeleteRequest(long chatroomId) {
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
        long chatroomId;

        public PostRequest() {
            super(NetworkConstant.API.MEETING_REQUEST);
        }

        public long getChatroomId() {
            return chatroomId;
        }

        public PostRequest setChatroomId(long chatroomId) {
            this.chatroomId = chatroomId;
            return this;
        }
    }

    public static class Response extends ToStringHelperClass {
    }
}
