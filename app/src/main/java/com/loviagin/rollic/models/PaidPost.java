package com.loviagin.rollic.models;

import java.util.List;

public class PaidPost {
    private String name, urlAvatar;
    private List<String> posts;

    public PaidPost(String name, List<String> posts, String urlAvatar) {
        this.name = name;
        this.posts = posts;
        this.urlAvatar = urlAvatar;
    }

    public PaidPost() {
    }

    public String getUrlAvatar() {
        return urlAvatar;
    }

    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPosts() {
        return posts;
    }

    public void setPosts(List<String> posts) {
        this.posts = posts;
    }
}
