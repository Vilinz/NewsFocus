package com.example.newsfocus.LoginPage;

import com.google.gson.JsonObject;

public interface ILoginPresenter {

    void login(String u, String p);

    void loginSuccess(JsonObject r);

    void showMsg(int s);
}
