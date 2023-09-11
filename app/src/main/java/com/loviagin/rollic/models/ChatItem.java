package com.loviagin.rollic.models;

public class ChatItem {
    private String cUser, name, avatarUrl, message;
    private int time;

    public ChatItem(String cUser, String name, String avatarUrl, String message, int time) {
        this.name = name;
        this.cUser = cUser;
        this.avatarUrl = avatarUrl;
        this.message = message;
        this.time = time;
    }

    public ChatItem() {
    }

    public String getcUser() {
        return cUser;
    }

    public void setcUser(String cUser) {
        this.cUser = cUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
