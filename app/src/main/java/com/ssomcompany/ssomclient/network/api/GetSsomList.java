package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant.API;
import com.ssomcompany.ssomclient.network.api.model.SsomItem;
import com.ssomcompany.ssomclient.network.model.SsomGetRequest;

import java.util.ArrayList;

public class GetSsomList {

    public static class Request extends SsomGetRequest {
        public Request() {
            super(API.SSOM.SSOM_LIST);
        }
    }

    public static class Response extends ToStringHelperClass {
        private ArrayList<SsomItem> list;

        public ArrayList<SsomItem> getSsomList() {
            return list;
        }

        public void setSsomList(ArrayList<SsomItem> list) {
            this.list = list;
        }
    }
}
