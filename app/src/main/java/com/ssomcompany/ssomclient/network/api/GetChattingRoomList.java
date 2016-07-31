package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant.API;
import com.ssomcompany.ssomclient.network.api.model.ChatRoomItem;
import com.ssomcompany.ssomclient.network.model.SsomGetRequest;

import java.util.ArrayList;

public class GetChattingRoomList {

    public static class Request extends SsomGetRequest {
        public Request() {
            super(API.CHAT_ROOM_LIST);
        }
    }

    public static class Response extends ToStringHelperClass {
        private ArrayList<ChatRoomItem> list;

        public ArrayList<ChatRoomItem> getChattingRoomList() {
            return list;
        }

        public void setChattingRoomList(ArrayList<ChatRoomItem> list) {
            this.list = list;
        }
    }
}
