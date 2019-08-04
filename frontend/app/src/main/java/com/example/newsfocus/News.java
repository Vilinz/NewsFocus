package com.example.newsfocus;

import java.util.List;

public class News {
    private String group_id;
    private String title;
    private String author;
    private String time;
    private List<String> image_info;
    private String comments;

    public News(String group_id, String title, String author, String time, List<String> image_info, String comments) {
        this.group_id = group_id;
        this.title = title;
        this.author = author;
        this.time = time;
        this.image_info = image_info;
        this.comments = comments;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<String> getImage_info() {
        return image_info;
    }

    public void setImage_info(List<String> image_info) {
        this.image_info = image_info;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
