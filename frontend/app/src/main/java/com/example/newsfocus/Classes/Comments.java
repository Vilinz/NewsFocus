package com.example.newsfocus.Classes;

public class Comments {
    private int commentId;
    private String userID;
    private String newsID;
    private int stars;
    private String time;
    private  String comment;

    public Comments(int commentId, String userID, String newsID, int stars, String time, String comment) {
        this.commentId = commentId;
        this.newsID = newsID;
        this.userID = userID;
        this.stars = stars;
        this.time = time;
        this.comment = comment;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getNewsID() {
        return newsID;
    }

    public void setNewsID(String newsID) {
        this.newsID = newsID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
