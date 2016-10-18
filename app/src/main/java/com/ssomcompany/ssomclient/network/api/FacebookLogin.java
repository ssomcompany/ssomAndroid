package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.SsomPostRequest;

public class FacebookLogin {

    public static class Request extends SsomPostRequest {
        public Request() {
            super(NetworkConstant.API.FACE_BOOK_LOGIN);
        }
    }
    
    public static class Response extends ToStringHelperClass {
        String token;
        String userId;
        String profileImgUrl;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getProfileImgUrl() {
            return profileImgUrl;
        }

        public void setProfileImgUrl(String profileImgUrl) {
            this.profileImgUrl = profileImgUrl;
        }
    }
}
