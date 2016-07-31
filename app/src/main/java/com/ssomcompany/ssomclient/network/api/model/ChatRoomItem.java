package com.ssomcompany.ssomclient.network.api.model;

public class ChatRoomItem extends SsomItem {
    public enum InfoType {
        none,  // info 가 없음
        sent,  // 내가 만남을 요청함
        received,  // 상대가 만남을 요청함
        success  // 쏨이 성사됨
    }

    private int id;
    private String ownerId;
    private String participantId;
    private long createdTimestamp;
    private String lastMessage;
    private long lastMessageTime;
    private InfoType infoType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public InfoType getInfoType() {
        return infoType;
    }

    public void setInfoType(InfoType infoType) {
        this.infoType = infoType;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
