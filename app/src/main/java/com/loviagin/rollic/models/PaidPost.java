package com.loviagin.rollic.models;

import java.util.List;

public class PaidPost {
    private String name;
    private List<String> posts;

    public PaidPost(String name, List<String> posts) {
        this.name = name;
        this.posts = posts;
    }

    public PaidPost() {
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
