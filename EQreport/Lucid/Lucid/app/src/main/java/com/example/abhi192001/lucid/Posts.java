package com.example.abhi192001.lucid;

public class Posts
{
    private String uid,time,date,postimage,description,profileimage,fullname;

    public Posts(){}

    public Posts(String uid, String time, String date, String postImage, String description, String profileimage, String fullname) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.postimage = postImage;
        this.description = description;
        this.profileimage = profileimage;
        this.fullname = fullname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostImage() {
        return postimage;
    }

    public void setPostImage(String postimage) {
        this.postimage = postimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
