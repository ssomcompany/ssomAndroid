package com.ssomcompany.ssomclient.network;

public class NetworkConstant {

    public static final boolean isHttps = false;

    public static final String HTTP_SCHME = "http://";
    public static final String HTTPS_SCHME = "https://";
    public static final String HOST = "api.myssom.com";

    public static final String WEB_PAGE = "http://www.myssom.com";
    public static final String WEB_PRIVACY = "http://ssomcompany.wixsite.com/ssominfo";
    public static final String WEB_POLICY = "http://ssomcompany.wixsite.com/termsandconditions";


    public static class HeaderParam {
        public static final String AUTHORIZATION = "Authorization";
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
        public static final String SSOM_LOGIN_WITHOUT_ID = "/loginWithoutId";
        public static final String SSOM_LOGOUT = "/logout";
        public static final String SSOM_REGISTER_USER = "/users";
        public static final String CHAT_TOTAL_UNREAD_COUNT = "/chatroom/unreadcount";
        public static final String CHAT_ROOM = "/chatroom";
        public static final String SSOM_POST_DELETE = "/posts/{postId}";
        public static final String MEETING_REQUEST = "/request";
        public static final String PROFILE_IMAGE = "/users/profileImgUrl";

        // $1 : roomId, $2 : currentTimeStamp
        public static final String SEND_MESSAGE = "/chatroom/{roomId}/chats";
        // $1 : roomId
        public static final String CHAT_LIST = "/chatroom/{roomId}/chats";
        // 하트관련
        public static final String USER_HEART = "/users/hearts";
        // user profile
        public static final String USER_PROFILE = "/users/{userId}";
        // get app version
        public static final String APP_VERSION = "/version/android";
        // get user count
        public static final String USER_COUNT = "/users?status=connected&field=count";
    }
}
