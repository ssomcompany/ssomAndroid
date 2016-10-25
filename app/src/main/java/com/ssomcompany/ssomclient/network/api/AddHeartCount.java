package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.SsomGetRequest;

/**
 * Created by AaronMac on 2016. 10. 16..
 */

public class AddHeartCount {

    public static class Request extends SsomGetRequest {
        String count;
        String device;
        String token;

        public Request() {
            super(NetworkConstant.API.USER_HEART);
        }

        public String getCount() {
            return count;
        }

        public Request setCount(String count) {
            this.count = count;
            return this;
        }

        public String getDevice() {
            return device;
        }

        public Request setDevice(String device) {
            this.device = device;
            return this;
        }

        public String getToken() {
            return token;
        }

        public Request setToken(String token) {
            this.token = token;
            return this;
        }
    }

    public static class Response extends ToStringHelperClass {
        private int heartsCount;

        public int getHeartsCount() {
            return heartsCount;
        }

        public void setHeartsCount(int heartsCount) {
            this.heartsCount = heartsCount;
        }
    }
}
