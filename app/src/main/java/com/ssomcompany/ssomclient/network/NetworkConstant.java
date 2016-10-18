package com.ssomcompany.ssomclient.network;

public class NetworkConstant {

    public static final boolean isHttps = false;

    public static final String HTTP_SCHME = "http://";
    public static final String HTTPS_SCHME = "https://";
    public static final String HOST = "52.78.137.209:3000";

    public static class HeaderParam {
        public static final String CACHE_CONTROL = "Cache-Control";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String ACCEPT = "Accept";
        public static final String APPLICATION_TYPE = "Application-Type";
        public static final String AUTHORIZATION = "Authorization";
        public static final String CONTENT_TEXT = "content-text";
        public static final String CONNECTION = "Connection";
        public static final String KEEP_ALIVE = "keep-alive";
    }

    public static class Method {
        public static final int GET = com.android.volley.Request.Method.GET;
        public static final int POST = com.android.volley.Request.Method.POST;
        public static final int PUT = com.android.volley.Request.Method.PUT;
        public static final int DELETE = com.android.volley.Request.Method.DELETE;
    }

    public static class NetworkErrorCode {
        // TODO error code 추가
    }

    // API start
    public static class API {
        public static final String SSOM_LIST = "/posts?";
        public static final String SSOM_POST = "/posts";
        public static final String SSOM_MY_POST = "/posts/mine";
        public static final String IMAGE_PATH = "/file/images/";
        public static final String IMAGE_FILE_UPLOAD = "/file/upload";
        public static final String SSOM_LOGIN = "/login";
        public static final String SSOM_REGISTER_USER = "/users";
        public static final String CHAT_ROOM_LIST = "/chatroom";
        public static final String CHAT_TOTAL_UNREAD_COUNT = "/chatroom/unreadcount";
        public static final String CREATE_ROOM = "/chatroom";
        public static final String SSOM_POST_DELETE = "/posts/%s";
        public static final String MEETING_REQUEST = "/request";
        public static final String PROFILE_IMAGE = "/users/profileImgUrl";

        // $1 : roomId, $2 : currentTimeStamp
        public static final String SEND_MESSAGE = "/chatroom/%d/chats?lastTimestamp=%d";
        // $1 : roomId
        public static final String CHAT_LIST = "/chatroom/%d/chats";

        // get app version
        public static final String APP_VERSION = "/version/android";
    }
}
