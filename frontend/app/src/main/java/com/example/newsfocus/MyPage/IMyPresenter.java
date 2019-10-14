package com.example.newsfocus.MyPage;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ListView;

import com.google.gson.JsonObject;

import java.io.File;

public interface IMyPresenter {
    void initAdapter(ListView listView, Context c);

    void autoLogin(Context c);

    void setLogin(JsonObject r);

    void uploadImage(File file, String img_path);

    void uploadSuccess(Bitmap bitmap);

    void downImageFromURL(String url);

    void getImageSuccess(Bitmap bitmap);

    void showMsg(int i);
}
