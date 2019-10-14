package com.example.newsfocus.NewsDetail;

import android.content.Context;

import java.util.ArrayList;

public interface INewDetailPresenter {
    void initUsername(Context c, CommentListView listView);

    void setNewContent(String group_id, String author, String time, String title, String comments);

    void getDetail();

    void setHtml(String html, ArrayList<String> urls);

    void setHeader();

    void updateHeader(String title, String time, String author);

    void sendComment(String text);

    void getCommentByNewId();

    void showMsg(int i);
}
