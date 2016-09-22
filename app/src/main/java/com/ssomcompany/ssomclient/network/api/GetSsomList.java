package com.ssomcompany.ssomclient.network.api;

import android.text.TextUtils;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant.API;
import com.ssomcompany.ssomclient.network.api.model.SsomItem;
import com.ssomcompany.ssomclient.network.model.SsomGetRequest;

import java.util.ArrayList;
import java.util.Locale;

public class GetSsomList {

    public static class Request extends SsomGetRequest {
        public Request(double lat, double lng, String userId) {
            super(String.format(Locale.getDefault(), API.SSOM_LIST, lat, lng) + (TextUtils.isEmpty(userId) ? "" : ("&userId=" + userId)));
        }

        public Request(double lat, double lng, String userId, int age, int count) {
            super(String.format(Locale.getDefault(), API.SSOM_LIST, lat, lng) +
                    (TextUtils.isEmpty(userId) ? "" : ("&userId=" + userId) + "&ageFilter=" + age + "&countFilter=" + count));
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
