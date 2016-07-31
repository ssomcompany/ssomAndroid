package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.SsomPostRequest;

public class SsomRegisterUser {

    public static class Request extends SsomPostRequest {
        private String email;
        private String password;

        public Request() {
            super(NetworkConstant.API.SSOM_REGISTER_USER);
        }

        public String getEmail() {
            return email;
        }

        public Request setEmail(String email) {
            this.email = email;
            return this;
        }

        public String getPassword() {
            return password;
        }

        public Request setPassword(String password) {
            this.password = password;
            return this;
        }
    }
    
    public static class Response extends ToStringHelperClass {}
}
