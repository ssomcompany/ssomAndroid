package com.ssomcompany.ssomclient.network.api.model;

public class ChatRoomItem extends SsomItem {
    public enum InfoType {
        none,  // info 가 없음
        sent,  // 내가 만남을 요청함
        received,  // 상대가 만남을 요청함
        success  // 쏨이 성사됨
    }

    private String lastMessage;
    private long lastMessageTime;
    private InfoType infoType;

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
}
