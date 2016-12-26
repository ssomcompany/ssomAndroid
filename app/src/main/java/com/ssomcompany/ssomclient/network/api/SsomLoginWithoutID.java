package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.SsomPostRequest;

public class SsomLoginWithoutID {

    public static class Request extends SsomPostRequest {
        private String playerId;

        public Request() {
            super(NetworkConstant.API.SSOM_LOGIN_WITHOUT_ID);
        }

        public String getPlayerId() {
            return playerId;
        }

        public Request setPlayerId(String playerId) {
            this.playerId = playerId;
            return this;
        }
    }
    
    public static class Response extends ToStringHelperClass {
        String token;
        String userId;
        String profileImgUrl;
        int hearts;

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

        public int getHearts() {
            return hearts;
        }

        public void setHearts(int hearts) {
            this.hearts = hearts;
        }
    }
}
