package com.example.newsfocus.NewsDetail;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.newsfocus.Classes.Comments;
import com.example.newsfocus.R;
import com.example.newsfocus.Service.ServiceInstance;
import com.example.newsfocus.Service.ServiceInstanceWithToken;
import com.example.newsfocus.tools.StringUtils;
import com.example.newsfocus.tools.TimeUtils;
import com.google.gson.JsonObject;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class NewDetailMode implements INewDetailMode{
    private List<Comments> list;
    private CommentListAdapter commentListAdapter;
    private String group_id;

    private String token = null;
    private String username = null;

    private String author;
    private String time;
    private String title;
    private String comments;
    private NewDetailPresenter np;

    public NewDetailMode(NewDetailPresenter n) {
        np = n;
    }

    @Override
    public void initUsername(Context c, CommentListView listView) {
        SharedPreferences sp = c.getSharedPreferences("token", Context.MODE_PRIVATE);
        if(sp.contains("token")) {
            String timeStrip = TimeUtils.getTimeStrip();

            Log.i("timeStrip", timeStrip);
            token = sp.getString("token", null);
            username = sp.getString("username",null);
        }
        list = new ArrayList<Comments>();
        commentListAdapter = new CommentListAdapter(c, list);
        listView.setAdapter(commentListAdapter);
    }

    @Override
    public void setNewContent(String g, String a, String t, String ti, String c) {
        group_id  = g;
        author = a;
        time = t;
        title = ti;
        comments = c;
    }

    @Override
    public void getDetail() {
        CompositeDisposable mCompositeDisposable = new CompositeDisposable();
        DisposableObserver<JsonObject> disposableObserver_login = new DisposableObserver<JsonObject>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onNext(JsonObject r) {
                String html = r.getAsJsonObject("data").get("content").getAsString().replace("\\", "");

                String javascript ="function findImg(){"+
                        "var objs = document.getElementsByTagName('img');"+
                        "for(var i=0;i<objs.length;i++){"+
                        "objs[i].onclick=function(){" +
                        "window.connect.showImg(this.src)" +
                        "}"+
                        "}}"+
                        "function setWidth() {" +
                        "var objs = document.getElementsByTagName('img');" +
                        "for(var i=0;i<objs.length;i++)" +
                        "{" +
                        "var img = objs[i];" +
                        "img.style.width = '100%';img.style.height = 'auto';" +
                        "}}";
                //拼接成一个完成的 HTML，
                html = html.substring(0, 12) + "<script> " + javascript + " </script>" + html.substring(12);

                Log.i("html", html);

                ArrayList<String> urls = StringUtils.returnImageUrlsFromHtml(html);

                np.setHtml(html, urls);
            }
            @Override
            public void onError(Throwable e) {
                np.showMsg(R.string.network_error);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        };

        ServiceInstance.getInstance().getDetail(group_id).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver_login);
        mCompositeDisposable.add(disposableObserver_login);
    }

    @Override
    public void setHeader() {
        np.updateHeader(title, time, author);
    }

    @Override
    public void sendComment(final String text) {
        CompositeDisposable mCompositeDisposable = new CompositeDisposable();
        DisposableObserver<JsonObject> disposableObserver_sendComment = new DisposableObserver<JsonObject>() {
            @Override
            public void onNext(JsonObject r) {
                np.showMsg(R.string.comment_success);
                int id = r.get("data").getAsInt();
                commentListAdapter.addItem(new Comments(id, username, group_id, 0, TimeUtils.getTimeStrip(), text, R.drawable.star_before));
            }
            @Override
            public void onError(Throwable e) {
                np.showMsg(R.string.comment_fail);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        };

        if(username != null && token != null) {
            String timeStrip = TimeUtils.getTimeStrip();
            ServiceInstanceWithToken.getInstanceWithToken(token).sendComment(username, group_id, timeStrip, text).subscribeOn(Schedulers.newThread()).
                    observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver_sendComment);
            mCompositeDisposable.add(disposableObserver_sendComment);
        } else {
            np.showMsg(R.string.login_first);
        }
    }

    @Override
    public void getCommentByNewId() {
        CompositeDisposable mCompositeDisposable = new CompositeDisposable();
        DisposableObserver<JsonObject> disposableObserver_sendComment = new DisposableObserver<JsonObject>() {
            @Override
            public void onNext(JsonObject r) {
                getStarByUser(r);
            }
            @Override
            public void onError(Throwable e) {
                np.showMsg(R.string.network_error);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        };
        ServiceInstance.getInstance().getCommentByNewsID(group_id).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver_sendComment);
        mCompositeDisposable.add(disposableObserver_sendComment);
    }

    public void getStarByUser(final JsonObject obs) {
        CompositeDisposable mCompositeDisposable = new CompositeDisposable();
        DisposableObserver<JsonObject> disposableObserver_sendComment = new DisposableObserver<JsonObject>() {
            @Override
            public void onNext(JsonObject r) {
                int count = r.getAsJsonArray("data").size();
                int count_comment = obs.getAsJsonArray("comments").size();
                for(int j = 0; j < count_comment; j++) {
                    JsonObject ob = obs.getAsJsonArray("comments").get(j).getAsJsonObject();
                    int tag = 0;
                    for(int i = 0; i < count; i++) {
                        if((ob.get("commentID")+ "").equals(r.getAsJsonArray("data").get(i).getAsJsonObject().get("commentID").getAsString())) {
                            commentListAdapter.addItem(new Comments(ob.get("commentID").getAsInt(), ob.get("userID").getAsString(), ob.get("newsID").getAsString(),
                                    ob.get("stars").getAsInt(), ob.get("time").getAsString(), ob.get("content").getAsString(), R.drawable.star_after));
                            tag = 1;
                            break;
                        }
                    }
                    if(tag == 0) {
                        commentListAdapter.addItem(new Comments(ob.get("commentID").getAsInt(), ob.get("userID").getAsString(), ob.get("newsID").getAsString(),
                                ob.get("stars").getAsInt(), ob.get("time").getAsString(), ob.get("content").getAsString(), R.drawable.star_before));
                    }
                }
            }
            @Override
            public void onError(Throwable e) {
                np.showMsg(R.string.network_error);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        };
        if(username == null) {
            int count = obs.getAsJsonArray("comments").size();
            for(int i = 0; i < count; i++) {
                JsonObject ob = obs.getAsJsonArray("comments").get(i).getAsJsonObject();
                commentListAdapter.addItem(new Comments(ob.get("commentID").getAsInt(), ob.get("userID").getAsString(), ob.get("newsID").getAsString(),
                        ob.get("stars").getAsInt(), ob.get("time").getAsString(), ob.get("content").getAsString(), R.drawable.star_before));
            }
        } else {
            ServiceInstanceWithToken.getInstanceWithToken(token).getStarList(username).subscribeOn(Schedulers.newThread()).
                    observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver_sendComment);
            mCompositeDisposable.add(disposableObserver_sendComment);
        }
    }
}
