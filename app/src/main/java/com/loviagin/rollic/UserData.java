package com.loviagin.rollic;

import com.loviagin.rollic.models.Post;

import java.util.LinkedList;
import java.util.List;

public class UserData {
    public static String email, username, uid, name, urlAvatar;
    public static List<String> posts;
    public static List<String> subscribers;
    public static List<String> subscriptions;
    public static List<Post> usrPosts = new LinkedList<>();
}
