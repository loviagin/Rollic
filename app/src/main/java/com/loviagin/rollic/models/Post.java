package com.loviagin.rollic.models;

import java.util.List;

public class Post {
    private String uid, title, description, tags, uidAuthor, authorName, authorAvatarUrl, authorNickname;
    private int commentsCount, repostCount;
    private List<String> likes;
    private List<String> imagesUrls;


    //post add
    public Post(String uid, String title, String description, String tags, String uidAuthor, String authorName, String authorAvatarUrl, String authorNickname) {
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.uidAuthor = uidAuthor;
        this.authorName = authorName;
        this.authorAvatarUrl = authorAvatarUrl;
        this.authorNickname = authorNickname;
        this.uid = uid;
    }

    public Post(String title, String description, String tags, String uidAuthor, String authorName, String authorAvatarUrl, String authorNickname, List<String> imagesUrls, List<String> likes, int commentsCount, int repostCount) {
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.uidAuthor = uidAuthor;
        this.authorName = authorName;
        this.authorAvatarUrl = authorAvatarUrl;
        this.authorNickname = authorNickname;
        this.imagesUrls = imagesUrls;
        this.repostCount = repostCount;
        this.likes = likes;
        this.commentsCount = commentsCount;
    }

    public Post() {
    }

    public Post(String uid, String tags, String uidAuthor, String authorName, String authorAvatarUrl, String authorNickname, List<String> imagesUrls) {
        this.tags = tags;
        this.uidAuthor = uidAuthor;
        this.authorName = authorName;
        this.authorAvatarUrl = authorAvatarUrl;
        this.imagesUrls = imagesUrls;
        this.authorNickname = authorNickname;
        this.uid = uid;
    }

    public void addLike(String uid){
        likes.add(uid);
    }

    public void deleteLike(String uid){
        likes.remove(uid);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getRepostCount() {
        return repostCount;
    }

    public void setRepostCount(int repostCount) {
        this.repostCount = repostCount;
    }


    public String getAuthorNickname() {
        return authorNickname;
    }

    public void setAuthorNickname(String authorNickname) {
        this.authorNickname = authorNickname;
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

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorAvatarUrl() {
        return authorAvatarUrl;
    }

    public void setAuthorAvatarUrl(String authorAvatarUrl) {
        this.authorAvatarUrl = authorAvatarUrl;
    }

    public List<String> getImagesUrls() {
        return imagesUrls;
    }

    public void setImagesUrls(List<String> imagesUrls) {
        this.imagesUrls = imagesUrls;
    }

    @Override
    public String toString() {
        return "Post{" +
                "uid='" + uid + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", tags='" + tags + '\'' +
                ", uidAuthor='" + uidAuthor + '\'' +
                ", authorName='" + authorName + '\'' +
                ", authorAvatarUrl='" + authorAvatarUrl + '\'' +
                ", authorNickname='" + authorNickname + '\'' +
                ", commentsCount=" + commentsCount +
                ", repostCount=" + repostCount +
                ", likes=" + likes +
                ", imagesUrls=" + imagesUrls +
                '}';
    }
}
