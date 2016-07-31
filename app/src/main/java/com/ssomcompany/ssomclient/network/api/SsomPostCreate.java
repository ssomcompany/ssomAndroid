package com.ssomcompany.ssomclient.network.api;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.model.SsomPostRequest;

public class SsomPostCreate {

    public static class Request extends SsomPostRequest {
        private String postId;
        private String userId;
        private String content;
        private String imageUrl;
        private int minAge;
        private int userCount;
        private String ssomType;
        private double latitude;
        private double longitude;

        public Request() {
            super(NetworkConstant.API.SSOM_POST);
        }

        public String getPostId() {
            return postId;
        }

        public Request setPostId(String postId) {
            this.postId = postId;
            return this;
        }

        public String getUserId() {
            return userId;
        }

        public Request setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public String getContent() {
            return content;
        }

        public Request setContent(String content) {
            this.content = content;
            return this;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public Request setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public int getMinAge() {
            return minAge;
        }

        public Request setMinAge(int minAge) {
            this.minAge = minAge;
            return this;
        }

        public int getUserCount() {
            return userCount;
        }

        public Request setUserCount(int userCount) {
            this.userCount = userCount;
            return this;
        }

        public String getSsomType() {
            return ssomType;
        }

        public Request setSsomType(String ssomType) {
            this.ssomType = ssomType;
            return this;
        }

        public double getLatitude() {
            return latitude;
        }

        public Request setLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public double getLongitude() {
            return longitude;
        }

        public Request setLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }
    }
    
    public static class Response extends ToStringHelperClass {

    }
}
