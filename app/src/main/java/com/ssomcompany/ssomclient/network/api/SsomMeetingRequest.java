package com.ssomcompany.ssomclient.network.api;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.SsomDeleteRequest;
import com.ssomcompany.ssomclient.network.model.SsomEntityRequest;
import com.ssomcompany.ssomclient.network.model.SsomPostRequest;
import com.ssomcompany.ssomclient.network.model.SsomPutRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SsomMeetingRequest {

    public static class PutRequest extends SsomPutRequest {
        int chatroomId;

        public PutRequest() {
            super(NetworkConstant.API.MEETING_REQUEST);
        }

        public int getChatroomId() {
            return chatroomId;
        }

        public PutRequest setChatroomId(int chatroomId) {
            this.chatroomId = chatroomId;
            return this;
        }
    }

    public static class DeleteRequest extends SsomEntityRequest {
        int chatroomId;

        public DeleteRequest() {
            this(NetworkConstant.API.MEETING_REQUEST);
        }

        DeleteRequest(String url) {
            super(url);
        }

        @Override
        public int getMethod() {
            return NetworkConstant.Method.DELETE;
        }

        public int getChatroomId() {
            return chatroomId;
        }

        public DeleteRequest setChatroomId(int chatroomId) {
            this.chatroomId = chatroomId;
            return this;
        }
    }

    public static class PostRequest extends SsomPostRequest {
        int chatroomId;

        public PostRequest() {
            super(NetworkConstant.API.MEETING_REQUEST);
        }

        public int getChatroomId() {
            return chatroomId;
        }

        public PostRequest setChatroomId(int chatroomId) {
            this.chatroomId = chatroomId;
            return this;
        }
    }

    public static class Response extends ToStringHelperClass {
    }
}
