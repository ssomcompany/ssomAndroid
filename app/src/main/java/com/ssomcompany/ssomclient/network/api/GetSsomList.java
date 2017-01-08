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
        String userId;
        String ssomTypeFilter;
        String ageFilter;
        String countFilter;
        double lat;
        double lng;

        public Request() {
            super(API.SSOM_LIST);
        }

        public String getUserId() {
            return userId;
        }

        public Request setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public String getSsomTypeFilter() {
            return ssomTypeFilter;
        }

        public Request setSsomTypeFilter(String ssomTypeFilter) {
            this.ssomTypeFilter = ssomTypeFilter;
            return this;
        }

        public String getAgeFilter() {
            return ageFilter;
        }

        public Request setAgeFilter(String ageFilter) {
            this.ageFilter = ageFilter;
            return this;
        }

        public String getCountFilter() {
            return countFilter;
        }

        public Request setCountFilter(String countFilter) {
            this.countFilter = countFilter;
            return this;
        }

        public double getLat() {
            return lat;
        }

        public Request setLat(double lat) {
            this.lat = lat;
            return this;
        }

        public double getLng() {
            return lng;
        }

        public Request setLng(double lng) {
            this.lng = lng;
            return this;
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
