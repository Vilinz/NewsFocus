package com.example.newsfocus.NewsDetail;

import java.util.ArrayList;

public interface INewDetailView {
    void setHtml(String html, ArrayList<String> urls);
    void updateHeader(String title, String time, String author);
    void showMsg(int i);
}
