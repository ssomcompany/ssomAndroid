package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant.API;
import com.ssomcompany.ssomclient.network.api.model.ChatRoomItem;
import com.ssomcompany.ssomclient.network.model.SsomGetRequest;
import com.ssomcompany.ssomclient.network.model.SsomPutRequest;

import java.util.ArrayList;

public class UpdateChattingRoom {

    public static class Request extends SsomPutRequest {
        public Request(String chatRoomId, long lastAccessTime) {
            super(API.CHAT_ROOM + "/" + chatRoomId + "/" + lastAccessTime);
        }
    }

    public static class Response extends ToStringHelperClass {
    }
}
