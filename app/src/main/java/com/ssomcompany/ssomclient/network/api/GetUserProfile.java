package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.SsomGetRequest;

import java.util.Locale;

/**
 * Created by AaronMac on 2016. 10. 16..
 */

public class GetUserProfile {

    public static class Request extends SsomGetRequest {
        public Request(String userEmail) {
            super(String.format(Locale.getDefault(), NetworkConstant.API.USER_PROFILE, userEmail));
        }
    }

    public static class Response extends ToStringHelperClass {
        private int profileImgUrl;
        private int hearts;

        public int getHearts() {
            return hearts;
        }

        public void setHearts(int hearts) {
            this.hearts = hearts;
        }

        public int getProfileImgUrl() {
            return profileImgUrl;
        }

        public void setProfileImgUrl(int profileImgUrl) {
            this.profileImgUrl = profileImgUrl;
        }
    }
}
