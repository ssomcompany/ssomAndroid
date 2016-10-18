package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.SsomDeleteRequest;

public class DeleteChattingRoom {

    public static class Request extends SsomDeleteRequest {

        public Request(long chatroomId) {
            super(NetworkConstant.API.CREATE_ROOM + "/" + chatroomId);
        }
    }

    public static class Response extends ToStringHelperClass {
    }
}
