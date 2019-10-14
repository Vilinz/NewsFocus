package com.example.newsfocus.RegisterPage;
import com.example.newsfocus.NewsDetail.INewDetailMode;
import com.example.newsfocus.R;
import com.example.newsfocus.Service.ServiceInstance;
import com.google.gson.JsonObject;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class RegisterMode implements IRegisterMode {
    RegisterPresenter rp;

    public RegisterMode(RegisterPresenter r) {
        rp = r;
    }

    @Override
    public void register(final String username, String password, String phone) {
        CompositeDisposable mCompositeDisposable = new CompositeDisposable();
        DisposableObserver<JsonObject> disposableObserver = new DisposableObserver<JsonObject>() {
            @Override
            public void onNext(JsonObject r) {
                String result = r.get("message").getAsString();
                if(result.equals("success")) {
                    rp.setRegister();
                } else {
                    rp.showMsg(R.string.register_fail);
                }
            }
            @Override
            public void onError(Throwable e) {
                rp.showMsg(R.string.network_error);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        };
        ServiceInstance.getInstance().register(username, password, phone).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
    }
}
