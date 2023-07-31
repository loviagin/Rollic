package com.loviagin.rollic.models;

import java.time.Instant;

public class Notification {
    private String title, description, avatarUrl, url;
    private boolean read;
    private long time;

    public Notification(String title, String description, String avatarUrl, String url, boolean read, long time) {
        this.title = title;
        this.description = description;
        this.avatarUrl = avatarUrl;
        this.url = url;
        this.read = read;
        this.time = time;
    }

    public Notification(String title, String description, String avatarUrl, String url) {
        this.title = title;
        this.description = description;
        this.avatarUrl = avatarUrl;
        this.url = url;
        this.read = false;
        Instant currentTime = Instant.now();
        this.time = currentTime.toEpochMilli();
    }

    public Notification() {
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
