package com.example.newsfocus.LoginPage;

import com.google.gson.JsonObject;

public interface ILoginView {
    void setLogin(JsonObject r);
    void toRegister();
    void showMsg(int s);
}
