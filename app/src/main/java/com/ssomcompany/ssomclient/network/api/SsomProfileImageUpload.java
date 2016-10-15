package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.SsomPutRequest;
import com.ssomcompany.ssomclient.network.model.SsomResponse;

/**
 * Created by AaronMac on 2016. 10. 15..
 */

public class SsomProfileImageUpload {

    public static class Request extends SsomPutRequest {
        String profileImgUrl;

        public Request() {
            super(NetworkConstant.API.PROFILE_IMAGE);
        }

        public String getProfileImgUrl() {
            return profileImgUrl;
        }

        public Request setProfileImgUrl(String profileImgUrl) {
            this.profileImgUrl = profileImgUrl;
            return this;
        }
    }

    public static class Response extends SsomResponse {
    }
}
