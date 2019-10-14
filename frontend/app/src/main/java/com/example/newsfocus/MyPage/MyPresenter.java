package com.example.newsfocus.MyPage;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ListView;

import com.google.gson.JsonObject;

import java.io.File;

public class MyPresenter implements IMyPresenter{
    private MyMode mode;
    private IMyView view;

    public MyPresenter(IMyView v) {
        view = v;
        mode = new MyMode(this);
    }

    @Override
    public void initAdapter(ListView listView, Context c) {
        mode.initAdapter(listView, c);
    }

    @Override
    public void autoLogin(Context c) {
        mode.autoLogin(c);
    }

    @Override
    public void setLogin(JsonObject r) {
        view.autoLogin(r);
    }

    @Override
    public void uploadImage(File file, String img_path) {
        mode.upload(file, img_path);
    }

    @Override
    public void uploadSuccess(Bitmap bitmap) {
        view.updateImage(bitmap);
    }

    @Override
    public void downImageFromURL(String url) {
        mode.downImageFromURL(url);
    }

    @Override
    public void getImageSuccess(Bitmap bitmap) {
        view.setImage(bitmap);
    }

    @Override
    public void showMsg(int i) {
        view.showMsg(i);
    }
}
