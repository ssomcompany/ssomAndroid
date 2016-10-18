package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.SsomGetRequest;

/**
 * Created by AaronMac on 2016. 10. 16..
 */

public class GetApplicationVersion {

    public static class Request extends SsomGetRequest {
        public Request() {
            super(NetworkConstant.API.APP_VERSION);
        }
    }

    public static class Response extends ToStringHelperClass {
        private String version;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
