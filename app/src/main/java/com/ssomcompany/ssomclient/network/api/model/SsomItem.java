package com.ssomcompany.ssomclient.network.api.model;

import android.util.Log;

import com.ssomcompany.ssomclient.common.ToStringHelperClass;
import com.ssomcompany.ssomclient.common.Util;

import java.io.Serializable;

public class SsomItem extends ToStringHelperClass implements Serializable {
    private String postId;
    private String userId;
    private String content;
    private String imageUrl;
    private String category;
    private int minAge;
    private int maxAge;
    private int userCount;
    private String chatroomId;
    private String status;
    private String fromUserId;
    private long createdTimestamp;
    private String ssomType;
    private double latitude;
    private double longitude;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return Util.getDecodedString(content);
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public String getSsomType() {
        return ssomType;
    }

    public void setSsomType(String ssomType) {
        this.ssomType = ssomType;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public SsomItem setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
        return this;
    }

    public String getThumbnailImageUrl() {
        return getImageUrl() + "?thumbnail=200";
    }
}
