package com.ssomcompany.ssomclient.common;

public class CommonConst {

    // fragment
    public static final String FILTER_FRAG = "filter_fragment";
    public static final String DETAIL_FRAG = "detail_fragment";
    public static final String SSOM_LIST_FRAG = "ssom_list_fragment";
    public static final String CHAT_LIST_FRAG = "chat_list_fragment";
    public static final String CHATTING_FRAG = "chatting_fragment";
    public static final String LOGIN_FRAGMENT = "login_fragment";
    public static final String LOGIN_REGIST_FRAGMENT = "login_regist_fragment";

    // ssom string
    public static final String SSOM = "ssom";
    public static final String SSOA = "ssoseyo";

    // ssom purchase
    public static final String HEART_2 = "hearts_2";
    public static final String HEART_8 = "hearts_8";
    public static final String HEART_17 = "hearts_17";
    public static final String HEART_28 = "hearts_28";

    // image file size
    public static final int LIMIT_IMAGE_SIZE = 1024 * 1024 * 10;

    public class Chatting {
        public static final String NORMAL = "NORMAL";
        public static final String SYSTEM = "SYSTEM";
        // request string
        public static final String MEETING_REQUEST = "request";
        public static final String MEETING_APPROVE = "approve";
        public static final String MEETING_CANCEL = "cancel";
        public static final String MEETING_COMPLETE = "complete";
        public static final String MEETING_OUT = "out";
    }

    public class Intent {
        public static final String POST_ID = "post_id";
        public static final String USER_ID = "user_id";
        public static final String IMAGE_URL = "image_url";
        public static final String SSOM_TYPE = "ssom_type";
        public static final String SSOM_ITEM = "ssom_item";
        public static final String FILE_ID = "fileId";

        // for push message
        public static final String FROM_USER_ID = "fromUserId";
        public static final String TO_USER_ID = "toUserId";
        public static final String TIMESTAMP = "timestamp";
        public static final String CHAT_ROOM_ID = "chatroomId";
        public static final String STATUS = "status";
        public static final String MESSAGE = "message";

        public static final String IS_FROM_NOTI = "isFromNoti";
        public static final String EXTRA_ROOM_ITEM = "roomItem";
    }
}
