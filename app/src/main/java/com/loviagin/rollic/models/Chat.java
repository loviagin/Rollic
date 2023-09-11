package com.loviagin.rollic.models;

import java.util.List;

public class Chat {
    private List<String> uids;
    //    private long lastMessageTime;
    private List<Message> messages;

    public Chat(List<String> uids, List<Message> messages) {
        this.uids = uids;
        this.messages = messages;
//        Instant currentTime = Instant.now();
//        this.lastMessageTime = currentTime.toEpochMilli();
    }

    public Chat() {
    }

    public List<String> getUids() {
        return uids;
    }

    public void setUids(List<String> uids) {
        this.uids = uids;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}

