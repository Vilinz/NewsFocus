package com.example.newsfocus.RegisterPage;

public class RegisterPresenter implements IRegisterPresenter {
    RegisterMode mode;
    IRegisterView view;

    public RegisterPresenter(IRegisterView v) {
        view = v;
        mode = new RegisterMode(this);
    }

    @Override
    public void register(String u, String p, String t) {
        mode.register(u, p, t);
    }

    @Override
    public void setRegister() {
        view.registerSuccess();
    }

    @Override
    public void showMsg(int i) {
        view.showMsg(i);
    }
}
