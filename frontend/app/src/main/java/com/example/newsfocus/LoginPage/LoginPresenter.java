package com.example.newsfocus.LoginPage;

import com.google.gson.JsonObject;

public class LoginPresenter implements ILoginPresenter{
    ILoginView loginView;
    LoginMode loginMode;
    public LoginPresenter(ILoginView l) {
        loginView = l;
        loginMode = new LoginMode(this);
    }
    @Override
    public void login(String u, String p) {
        loginMode.login(u, p);
    }

    @Override
    public void loginSuccess(JsonObject r) {
        loginView.setLogin(r);
    }

    @Override
    public void showMsg(int s) {
        loginView.showMsg(s);
    }
}
