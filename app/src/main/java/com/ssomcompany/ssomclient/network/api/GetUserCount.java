package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.SsomGetRequest;
import com.ssomcompany.ssomclient.network.model.SsomPostRequest;

/**
 * Created by AaronMac on 2016. 10. 16..
 */

public class GetUserCount {

    public static class Request extends SsomGetRequest {
        public Request() {
            super(NetworkConstant.API.USER_COUNT);
        }
    }

    public static class Response extends ToStringHelperClass {
        private int userCount;

        public int getUserCount() {
            return userCount;
        }

        public void setUserCount(int userCount) {
            this.userCount = userCount;
        }
    }
}
