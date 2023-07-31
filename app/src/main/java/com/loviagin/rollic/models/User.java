package com.loviagin.rollic.models;

import com.loviagin.rollic.workers.NicknameGenerator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class User {
    private String username, f_name, avatarUrl, bio, email;
    private List<String> posts;
    private List<String> videoposts;
    private List<String> subscribers;
    private List<String> subscriptions;
    private List<String> deviceTokens;

    public User(String username, String f_name, String avatarUrl, String bio, String email, List<String> posts, List<String> subscribers, List<String> subscriptions, List<String> videoposts, List<String> deviceTokens) {
        this.username = username;
        this.f_name = f_name;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.email = email;
        this.posts = posts;
        this.subscribers = subscribers;
        this.subscriptions = subscriptions;
        this.videoposts = videoposts;
    }

    public User() {
    }

    public User(String email) {
        this.email = email;
        this.username = NicknameGenerator.generateRandomNickname();
        this.posts = new LinkedList<>();
        this.videoposts = new LinkedList<>();
        this.subscribers = new ArrayList<>();
        this.subscriptions = new ArrayList<>();
        this.deviceTokens = new ArrayList<>();
    }

    public List<String> getDeviceTokens() {
        return deviceTokens;
    }

    public void setDeviceTokens(List<String> deviceTokens) {
        this.deviceTokens = deviceTokens;
    }

    public List<String> getVideoposts() {
        return videoposts;
    }

    public void setVideoposts(List<String> videoposts) {
        this.videoposts = videoposts;
    }

    public List<String> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<String> subscribers) {
        this.subscribers = subscribers;
    }

    public List<String> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<String> subscriptions) {
        this.subscriptions = subscriptions;
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

    public List<String> getPosts() {
        return posts;
    }

    public void setPosts(List<String> posts) {
        this.posts = posts;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", f_name='" + f_name + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", bio='" + bio + '\'' +
                ", email='" + email + '\'' +
                ", posts=" + posts +
                ", videoposts=" + videoposts +
                ", subscribers=" + subscribers +
                ", subscriptions=" + subscriptions +
                ", deviceTokens=" + deviceTokens +
                '}';
    }
}
