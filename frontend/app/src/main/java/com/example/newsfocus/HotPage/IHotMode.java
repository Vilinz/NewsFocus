package com.example.newsfocus.HotPage;

import android.content.Context;
import android.widget.ListView;

import com.example.newsfocus.Classes.News;

public interface IHotMode {
    void initAdapter(Context c, ListView listView);
    News getNewByPosition(int position);
    void loadMoreData();
    void initData();
}
