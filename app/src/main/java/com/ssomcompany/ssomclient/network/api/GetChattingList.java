package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant.API;
import com.ssomcompany.ssomclient.network.api.model.ChatRoomItem;
import com.ssomcompany.ssomclient.network.api.model.ChattingItem;
import com.ssomcompany.ssomclient.network.api.model.SsomItem;
import com.ssomcompany.ssomclient.network.model.SsomGetRequest;

import java.util.ArrayList;
import java.util.Locale;

public class GetChattingList {

    public static class Request extends SsomGetRequest {
        public Request(int roomId) {
            super(String.format(Locale.getDefault(), API.CHAT_LIST, roomId));
        }
    }

    public static class Response extends ToStringHelperClass {
        private ArrayList<ChattingItem> list;

        public ArrayList<ChattingItem> getChattingList() {
            return list;
        }

        public void setChattingList(ArrayList<ChattingItem> list) {
            this.list = list;
        }
    }
}
