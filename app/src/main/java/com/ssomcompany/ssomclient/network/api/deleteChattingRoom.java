package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.SsomDeleteRequest;

public class DeleteChattingRoom {

    public static class Request extends SsomDeleteRequest {

        public Request(String chatroomId) {
            super(NetworkConstant.API.CHAT_ROOM + "/" + chatroomId);
        }
    }

    public static class Response extends ToStringHelperClass {
    }
}
