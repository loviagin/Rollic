package com.loviagin.rollic.models;

public class Comment {
    private String usrName, usrNickname, urlAvatar, text, pstUid, usrUid;

    public Comment(String usrName, String usrNickname, String urlAvatar, String text, String pstUid, String usrUid) {
        this.usrName = usrName;
        this.usrNickname = usrNickname;
        this.urlAvatar = urlAvatar;
        this.text = text;
        this.pstUid = pstUid;
        this.usrUid = usrUid;
    }

    public Comment() {
    }

    public String getUsrName() {
        return usrName;
    }

    public String getUsrUid() {
        return usrUid;
    }

    public void setUsrUid(String usrUid) {
        this.usrUid = usrUid;
    }

    public void setUsrName(String usrName) {
        this.usrName = usrName;
    }

    public String getUsrNickname() {
        return usrNickname;
    }

    public void setUsrNickname(String usrNickname) {
        this.usrNickname = usrNickname;
    }

    public String getUrlAvatar() {
        return urlAvatar;
    }

    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPstUid() {
        return pstUid;
    }

    public void setPstUid(String pstUid) {
        this.pstUid = pstUid;
    }
}
