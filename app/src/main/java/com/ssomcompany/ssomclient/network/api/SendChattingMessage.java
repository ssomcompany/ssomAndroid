package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.SsomPostRequest;

import java.util.Locale;

public class SendChattingMessage {

    public static class Request extends SsomPostRequest {
        String toUserId;
        String msg;

        public Request(int roomId, long lastMessageTime) {
            super(String.format(Locale.getDefault(), NetworkConstant.API.SEND_MESSAGE, roomId, lastMessageTime));
        }

        public String getToUserId() {
            return toUserId;
        }

        public Request setToUserId(String toUserId) {
            this.toUserId = toUserId;
            return this;
        }

        public String getMsg() {
            return msg;
        }

        public Request setMsg(String msg) {
            this.msg = msg;
            return this;
        }
    }
    
    public static class Response extends ToStringHelperClass {}
}