package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant.API;
import com.ssomcompany.ssomclient.network.model.SsomGetRequest;

public class SsomChatUnreadCount {

    public static class Request extends SsomGetRequest {
        public Request() {
            super(API.CHAT_TOTAL_UNREAD_COUNT);
        }
    }

    public static class Response extends ToStringHelperClass {
        private int unreadCount;

        public int getUnreadCount() {
            return unreadCount;
        }

        public void setUnreadCount(int unreadCount) {
            this.unreadCount = unreadCount;
        }
    }
}
