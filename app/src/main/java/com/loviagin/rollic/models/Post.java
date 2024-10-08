package com.loviagin.rollic.models;

import java.util.List;

public class Post {
    private String uid, title, description, tags, uidAuthor, authorName, authorAvatarUrl, authorNickname;
    private int repostCount;
    private List<String> likes;
    private List<String> imagesUrls;
    private List<String> comments;
    private boolean paid;

    //post add
    public Post(String uid, String title, String description, String tags, String uidAuthor, String authorName, String authorAvatarUrl, String authorNickname,
                boolean paid) {
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.uidAuthor = uidAuthor;
        this.authorName = authorName;
        this.authorAvatarUrl = authorAvatarUrl;
        this.authorNickname = authorNickname;
        this.uid = uid;
        this.paid = paid;
    }

    public Post(String title, String description, String tags, String uidAuthor, String authorName, String authorAvatarUrl, String authorNickname,
                List<String> imagesUrls, List<String> likes, List<String> comments, int repostCount, boolean paid) {
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
        this.comments = comments;
        this.paid = paid;
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

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
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
                ", repostCount=" + repostCount +
                ", likes=" + likes +
                ", imagesUrls=" + imagesUrls +
                '}';
    }
}
