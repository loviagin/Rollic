package com.loviagin.rollic.models;

import com.loviagin.rollic.UserData;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Video {
    private String uid, description, tags, uidAuthor, authorAvatarUrl, authorNickname, videoUrl, captureUrl;
    private List<String> likes;
    private List<String> comments;

    public Video(String uid, String description, String tags, String uidAuthor, String authorAvatarUrl, String authorNickname,
                 String videoUrl, List<String> likes, List<String> comments, String captureUrl) {
        this.uid = uid;
        this.description = description;
        this.tags = tags;
        this.uidAuthor = uidAuthor;
        this.authorAvatarUrl = authorAvatarUrl;
        this.authorNickname = authorNickname;
        this.videoUrl = videoUrl;
        this.likes = likes;
        this.comments = comments;
        this.captureUrl = captureUrl;
    }

    public Video() {
    }

    public Video(String description, String tags, String videoUrl, String captureUrl) {
        this.description = description;
        this.tags = tags;
        this.videoUrl = videoUrl;
        this.uidAuthor = UserData.uid;
        this.authorAvatarUrl = UserData.urlAvatar;
        this.authorNickname = UserData.username;
        this.likes = new ArrayList<>();
        this.captureUrl = captureUrl;
        this.comments = new LinkedList<>();
    }

    public String getCaptureUrl() {
        return captureUrl;
    }

    public void setCaptureUrl(String captureUrl) {
        this.captureUrl = captureUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getUidAuthor() {
        return uidAuthor;
    }

    public void setUidAuthor(String uidAuthor) {
        this.uidAuthor = uidAuthor;
    }

    public String getAuthorAvatarUrl() {
        return authorAvatarUrl;
    }

    public void setAuthorAvatarUrl(String authorAvatarUrl) {
        this.authorAvatarUrl = authorAvatarUrl;
    }

    public String getAuthorNickname() {
        return authorNickname;
    }

    public void setAuthorNickname(String authorNickname) {
        this.authorNickname = authorNickname;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Video{" +
                "uid='" + uid + '\'' +
                ", description='" + description + '\'' +
                ", tags='" + tags + '\'' +
                ", uidAuthor='" + uidAuthor + '\'' +
                ", authorAvatarUrl='" + authorAvatarUrl + '\'' +
                ", authorNickname='" + authorNickname + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", likes=" + likes +
                ", comments=" + comments +
                '}';
    }
}
