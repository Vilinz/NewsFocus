package com.example.newsfocus.MyPage;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.newsfocus.Classes.SampleClass;
import com.example.newsfocus.R;
import com.example.newsfocus.Service.ServiceInstanceWithToken;
import com.example.newsfocus.tools.BitmapUtils;
import com.example.newsfocus.tools.HttpUtils;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.content.Context.MODE_PRIVATE;

public class MyMode implements IMyMode{
    private List<SampleClass> list;
    private SampleListAdapter sampleListAdapter;

    private String username = null;
    private String telephone;
    private String avatar;

    public String baseUrl = "http://47.102.84.27:3000/image/avatar/";
    private String token;
    private MyPresenter mp;

    public MyMode(MyPresenter m) {
        mp = m;
    }

    @Override
    public void initAdapter(ListView listView, Context c) {
        list = new ArrayList<SampleClass>();
        list.add(new SampleClass("设置", R.drawable.ic_action_setting));
        list.add(new SampleClass("退出登录",R.drawable.ic_action_logout));
        sampleListAdapter = new SampleListAdapter(list, c);
        listView.setAdapter(sampleListAdapter);
    }

    @Override
    public void autoLogin(Context c) {
        try {
            SharedPreferences sp = c.getSharedPreferences("token", MODE_PRIVATE);
            if(sp.contains("token")) {
                token = sp.getString("token", null);
                username = sp.getString("username", null);
                avatar = sp.getString("avatar", null);
                Log.i("token2", token);
                CompositeDisposable mCompositeDisposable = new CompositeDisposable();
                DisposableObserver<JsonObject> disposableObserver_login = new DisposableObserver<JsonObject>() {
                    @Override
                    public void onNext(JsonObject r) {
                        username = r.getAsJsonObject("username").get("username").getAsString();
                        telephone = r.getAsJsonObject("username").get("telephone").getAsString();
                        if(!(r.getAsJsonObject("username").get("avatar") + "").equals("null")) {
                            avatar = r.getAsJsonObject("username").get("avatar").getAsString();
                        }
                        mp.setLogin(r);
                    }
                    @Override
                    public void onError(Throwable e) {
                        mp.showMsg(R.string.login_again);
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                    }
                };
                ServiceInstanceWithToken.getInstanceWithToken(token).getUserInfo(username).subscribeOn(Schedulers.newThread()).
                        observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver_login);
                mCompositeDisposable.add(disposableObserver_login);
            }
        } catch (Exception e) {
            Log.i("pppppppppppppp", "ppppppppppppp");
        }
    }

    @Override
    public void upload(File file, final String img_path) {
        RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)//表单类型
                .addFormDataPart("file", "headImage.png", imageBody)
                .addFormDataPart("username", username);
        List<MultipartBody.Part> parts = builder.build().parts();

        CompositeDisposable mCompositeDisposable = new CompositeDisposable();
        DisposableObserver<JsonObject> disposableObserver_login = new DisposableObserver<JsonObject>() {
            @Override
            public void onNext(JsonObject r) {
                Bitmap newBitmap = BitmapUtils.getSmallBitmap(img_path);
                mp.uploadSuccess(newBitmap);
            }
            @Override
            public void onError(Throwable e) {
                mp.showMsg(R.string.upload_fail);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        };
        ServiceInstanceWithToken.getInstanceWithToken(token).uploadImages(parts).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver_login);
        mCompositeDisposable.add(disposableObserver_login);
    }

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            // headImage.setImageBitmap((Bitmap) msg.obj)
            mp.getImageSuccess((Bitmap) msg.obj);
        }
    };

    @Override
    public void downImageFromURL(final String url) {
        new Thread() {
            public void run() {
                Bitmap bitmap = HttpUtils.getBitmapFromUrl(url);
                Message message = Message.obtain();
                message.obj = bitmap;
                mHandler.sendMessage(message);
            }
        }.start();
    }
}
