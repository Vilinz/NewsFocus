package com.example.newsfocus.MyPage;

import android.content.Context;
import android.widget.ListView;

import java.io.File;

public interface IMyMode {
    void initAdapter(ListView listView, Context c);

    void autoLogin(Context c);

    void upload(File file, final String img_path);

    void downImageFromURL(final String url);
}
