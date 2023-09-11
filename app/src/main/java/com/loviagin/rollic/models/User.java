package com.loviagin.rollic.models;

import com.loviagin.rollic.workers.NicknameGenerator;
import com.yandex.metrica.impl.ob.L;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class User {
    private String username, f_name, avatarUrl, bio, email;
    private List<String> posts;
    private List<String> paidSubscriptions;
    private List<String> videoposts;
    private List<String> subscribers;
    private List<String> subscriptions;
    private List<String> deviceTokens;
    private List<Map<String, String>> messages;
    private boolean isPaid;

    public User(String username, String f_name, String avatarUrl, String bio, String email, List<String> posts, List<String> subscribers,
                List<String> subscriptions, List<String> videoposts, List<String> deviceTokens, List<Map<String, String>> messages, boolean isPaid,
                List<String> paidSubscriptions) {
        this.username = username;
        this.f_name = f_name;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.email = email;
        this.posts = posts;
        this.subscribers = subscribers;
        this.subscriptions = subscriptions;
        this.videoposts = videoposts;
        this.deviceTokens = deviceTokens;
        this.messages = messages;
        this.isPaid = isPaid;
        this.paidSubscriptions = paidSubscriptions;
    }

    public User() {
    }

    public User(String username, String f_name, String avatarUrl, String email) {
        this.username = username;
        this.f_name = f_name;
        this.avatarUrl = avatarUrl;
        this.email = email;
    }

    public User(String email) {
        this.email = email;
        this.username = NicknameGenerator.generateRandomNickname();
        this.posts = new LinkedList<>();
        this.videoposts = new LinkedList<>();
        this.subscribers = new ArrayList<>();
        this.subscriptions = new ArrayList<>();
        this.deviceTokens = new ArrayList<>();
        this.messages = new LinkedList<>();
        this.paidSubscriptions = new LinkedList<>();
        this.isPaid = false;
    }

    public List<String> getPaidSubscriptions() {
        return paidSubscriptions;
    }

    public void setPaidSubscriptions(List<String> paidSubscriptions) {
        this.paidSubscriptions = paidSubscriptions;
    }

    public List<Map<String, String>> getMessages() {
        return messages;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public void setMessages(List<Map<String, String>> messages) {
        this.messages = messages;
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
