package com.loviagin.rollic.models;
import static com.loviagin.rollic.UserData.uid;

import java.time.Instant;
import java.util.Objects;

public class Message {

    private String text, cUid, mediaUrl;
    private long time;

    public Message(String text, String cUid, long time, String mediaUrl) {
        this.text = text;
        this.cUid = cUid;
        this.time = time;
        this.mediaUrl = mediaUrl;
    }

    public Message() {
    }

    public Message(String text, String cUid, long time) {
        this.text = text;
        this.cUid = cUid;
        this.time = time;
    }

    public Message(String mediaUrl) {
        this.mediaUrl = mediaUrl;
        this.cUid = uid;
        Instant currentTime = Instant.now();
        this.time = currentTime.toEpochMilli();
    }

    public Message(String text, String cUid) {
        this.text = text;
        this.cUid = cUid;
        Instant currentTime = Instant.now();
        this.time = currentTime.toEpochMilli();
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getcUid() {
        return cUid;
    }

    public void setcUid(String cUid) {
        this.cUid = cUid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(text, message.text) && Objects.equals(cUid, message.cUid) && Objects.equals(time, message.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, cUid, time);
    }
}

