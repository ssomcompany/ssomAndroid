package com.ssomcompany.ssomclient.network.api.model;

public class ChatRoomItem extends SsomItem {
    private String id;
    private String ownerId;
    private String participantId;
    private String lastMsg;
    private String lastMsgType;
    private long lastTimestamp;
    private int unreadCount;
    private String requestId;
    private String ownerImageUrl;
    private String participantImageUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getLastMsgType() {
        return lastMsgType;
    }

    public void setLastMsgType(String lastMsgType) {
        this.lastMsgType = lastMsgType;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getOwnerImageUrl() {
        return ownerImageUrl;
    }

    public void setOwnerImageUrl(String ownerImageUrl) {
        this.ownerImageUrl = ownerImageUrl;
    }

    public String getParticipantImageUrl() {
        return participantImageUrl;
    }

    public void setParticipantImageUrl(String participantImageUrl) {
        this.participantImageUrl = participantImageUrl;
    }
}
