package com.example.newsfocus.HotPage;

import android.content.Context;
import android.widget.ListView;

import com.example.newsfocus.Classes.News;

public class HotPresenter implements IHotPresenter {
    private HotMode mode;
    private IHotView view;

    public HotPresenter(IHotView v) {
        view = v;
        mode = new HotMode(this);
    }

    @Override
    public void initAdapter(Context c, ListView listView) {
        mode.initAdapter(c, listView);
    }

    @Override
    public News getNewByPosition(int position) {
        return mode.getNewByPosition(position);
    }

    @Override
    public void loadMoreData() {
        mode.loadMoreData();
    }

    @Override
    public void loadMoreDataSuccess() {
        view.setListView();
    }

    @Override
    public void initData() {
        mode.initData();
    }

    @Override
    public void showMsg(int s) {
        view.showMsg(s);
    }
}
