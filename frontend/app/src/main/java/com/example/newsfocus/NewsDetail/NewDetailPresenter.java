package com.example.newsfocus.NewsDetail;

import android.content.Context;

import com.example.newsfocus.Classes.Comments;

import java.util.ArrayList;

public class NewDetailPresenter implements INewDetailPresenter {
    INewDetailView view;
    NewDetailMode mode;

    public NewDetailPresenter(INewDetailView v) {
        view = v;
        mode = new NewDetailMode(this);
    }

    @Override
    public void initUsername(Context c, CommentListView listView) {
        mode.initUsername(c, listView);
    }

    @Override
    public void setNewContent(String group_id, String author, String time, String title, String comments) {
        mode.setNewContent(group_id, author, time, title, comments);
    }

    @Override
    public void getDetail() {
        mode.getDetail();
    }

    @Override
    public void setHtml(String html, ArrayList<String> urls) {
        view.setHtml(html, urls);
    }

    @Override
    public void setHeader() {
        mode.setHeader();
    }

    @Override
    public void updateHeader(String title, String time, String author) {
        view.updateHeader(title, time, author);
    }

    @Override
    public void sendComment(String text) {
        mode.sendComment(text);
    }

    @Override
    public void getCommentByNewId() {
        mode.getCommentByNewId();
    }

    @Override
    public void showMsg(int i) {
        view.showMsg(i);
    }
}
