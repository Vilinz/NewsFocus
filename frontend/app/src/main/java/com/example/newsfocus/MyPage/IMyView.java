package com.example.newsfocus.MyPage;

import android.graphics.Bitmap;

import com.google.gson.JsonObject;

public interface IMyView {
    void autoLogin(JsonObject r);
    void updateImage(Bitmap bitmap);
    void setImage(Bitmap bitmap);
    void showMsg(int i);
}
