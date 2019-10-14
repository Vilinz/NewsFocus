package com.example.newsfocus.HotPage;

import android.content.Context;
import android.util.Log;
import android.widget.ListView;

import com.example.newsfocus.Classes.News;
import com.example.newsfocus.R;
import com.example.newsfocus.Service.ServiceInstance;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class HotMode implements IHotMode {
    private HotPresenter hp;
    private List<News> mData = new ArrayList<News>();
    private ListAdapter listAdapter;
    private int currentOffset = 0;
    private int count = 10;

    public HotMode(HotPresenter h) {
        hp = h;
    }

    @Override
    public void initAdapter(Context c, ListView listView) {
        listAdapter = new ListAdapter(c, mData);
        listView.setAdapter(listAdapter);
    }

    @Override
    public News getNewByPosition(int position) {
        return mData.get(position);
    }

    @Override
    public void loadMoreData() {
        CompositeDisposable mCompositeDisposable = new CompositeDisposable();
        DisposableObserver<JsonObject> disposableObserver_login = new DisposableObserver<JsonObject>() {
            @Override
            public void onNext(JsonObject r) {
                int count = r.getAsJsonArray("data").size();
                Log.i("count", count + "");
                for(int i = 0; i < count; i++) {
                    JsonObject ob = r.getAsJsonArray("data").get(i).getAsJsonObject();
                    String image_infos = ob.get("image_infos").getAsString();
                    JsonParser jsonParser = new JsonParser();
                    JsonArray array = (JsonArray) jsonParser.parse(image_infos);
                    List<String> url = new ArrayList<String>();
                    for(int j = 0; j < array.size(); j++) {
                        JsonObject temp = array.get(j).getAsJsonObject();
                        String url_temp = temp.get("url_prefix").getAsString() + temp.get("web_uri").getAsString();
                        url.add(url_temp);
                    }
                    News n = new News(ob.get("group_id").getAsString(),
                            ob.get("title").getAsString(),
                            ob.get("author").getAsString(),
                            ob.get("time").getAsString(),
                            url,
                            ob.get("comments").getAsString());
                    mData.add(n);
                }
                hp.loadMoreDataSuccess();
            }
            @Override
            public void onError(Throwable e) {
                hp.showMsg(R.string.network_error);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        };
        ServiceInstance.getInstance().getNewHead(currentOffset, count).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver_login);
        currentOffset += count;
        mCompositeDisposable.add(disposableObserver_login);
    }

    @Override
    public void initData() {
        CompositeDisposable mCompositeDisposable = new CompositeDisposable();
        DisposableObserver<JsonObject> disposableObserver_login = new DisposableObserver<JsonObject>() {
            @Override
            public void onNext(JsonObject r) {
                int count = r.getAsJsonArray("data").size();
                Log.i("count", count + "");
                for(int i = 0; i < count; i++) {
                    JsonObject ob = r.getAsJsonArray("data").get(i).getAsJsonObject();
                    String image_infos = ob.get("image_infos").getAsString();
                    JsonParser jsonParser = new JsonParser();
                    JsonArray array = (JsonArray) jsonParser.parse(image_infos);
                    List<String> url = new ArrayList<String>();
                    for(int j = 0; j < array.size(); j++) {
                        JsonObject temp = array.get(j).getAsJsonObject();
                        String url_temp = temp.get("url_prefix").getAsString() + temp.get("web_uri").getAsString();
                        url.add(url_temp);
                    }
                    News n = new News(ob.get("group_id").getAsString(),
                            ob.get("title").getAsString(),
                            ob.get("author").getAsString(),
                            ob.get("time").getAsString(),
                            url,
                            ob.get("comments").getAsString());
                    // mData.add(n);
                    listAdapter.addItem(n);
                }
            }
            @Override
            public void onError(Throwable e) {
                hp.showMsg(R.string.network_error);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        };
        ServiceInstance.getInstance().getNewHead(currentOffset, count).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver_login);
        currentOffset += count;
        mCompositeDisposable.add(disposableObserver_login);
    }
}
