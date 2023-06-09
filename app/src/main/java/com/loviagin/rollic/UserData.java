package com.loviagin.rollic;

import com.loviagin.rollic.models.Post;

import java.util.LinkedList;
import java.util.List;

public class UserData {
    public static String email, username, uid, name, urlAvatar, bio;
    public static List<String> posts;
    public static List<String> subscribers;
    public static List<String> subscriptions;
    public static List<Post> usrPosts = new LinkedList<>();
    public static List<Post> dynPosts = new LinkedList<>();

    public static void remove() {
        email = "";
        username = "";
        uid = "";
        name = "";
        urlAvatar = "";
        bio = "";
        posts = null;
        subscribers = null;
        subscriptions = null;
        usrPosts = new LinkedList<>();
        dynPosts = new LinkedList<>();
    }
}
