package com.loviagin.rollic.models;

import com.loviagin.rollic.workers.NicknameGenerator;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username, f_name, avatarUrl, bio, email;
    private List<Post> posts;

    public User(String email, String username, String f_name, String avatarUrl, String bio, List<Post> posts) {
        this.username = username;
        this.f_name = f_name;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.posts = posts;
        this.email = email;
    }

    public User() {
    }

    public User(String email) {
        this.email = email;
        this.username = NicknameGenerator.generateRandomNickname();
        this.posts = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
