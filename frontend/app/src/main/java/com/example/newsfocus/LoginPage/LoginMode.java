package com.example.newsfocus.LoginPage;
import android.util.Log;

import com.example.newsfocus.R;
import com.example.newsfocus.Service.ServiceInstance;
import com.google.gson.JsonObject;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class LoginMode implements ILoginMode{
    private LoginPresenter lp;

    public LoginMode(LoginPresenter c) {
        lp = c;
    }

    @Override
    public void login(final String username, final String password) {
        CompositeDisposable mCompositeDisposable = new CompositeDisposable();
        DisposableObserver<JsonObject> disposableObserver = new DisposableObserver<JsonObject>() {
            @Override
            public void onNext(JsonObject r) {
                Log.i("Login", r.toString());
                String result = r.get("message").getAsString();
                if(result.equals("success")) {
                    Log.i("token1", r.get("token").getAsString());
                    lp.loginSuccess(r);
                }
            }
            @Override
            public void onError(Throwable e) {
                lp.showMsg(R.string.login_fail);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        };
        ServiceInstance.getInstance().login(username, password).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
    }
}
