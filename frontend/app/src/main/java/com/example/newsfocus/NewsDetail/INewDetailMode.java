package com.example.newsfocus.NewsDetail;

import android.content.Context;

public interface INewDetailMode {
    void initUsername(Context c, CommentListView listView);

    void setNewContent(String g, String a, String t, String ti, String c);

    void getDetail();

    void setHeader();

    void sendComment(final String text);

    void getCommentByNewId();
}
