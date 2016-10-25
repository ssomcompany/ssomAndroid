package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.SsomGetRequest;

/**
 * Created by AaronMac on 2016. 10. 16..
 */

public class GetHeartCount {

    public static class Request extends SsomGetRequest {
        public Request() {
            super(NetworkConstant.API.USER_HEART);
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
