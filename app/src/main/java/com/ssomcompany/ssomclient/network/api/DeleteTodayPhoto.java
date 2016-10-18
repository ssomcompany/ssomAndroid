package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.SsomDeleteRequest;

/**
 * Created by AaronMac on 2016. 10. 17..
 */

public class DeleteTodayPhoto {

    public static class Request extends SsomDeleteRequest {
        public Request() {
            super(NetworkConstant.API.PROFILE_IMAGE);
        }
    }

    public static class Response extends ToStringHelperClass {
    }
}
