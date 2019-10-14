package com.example.newsfocus.LoginPage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsfocus.MainActivity;
import com.example.newsfocus.R;
import com.example.newsfocus.RegisterPage.RegisterActivity;
import com.example.newsfocus.Service.ServiceInstance;
import com.google.gson.JsonObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity implements ILoginView {
    private TextView toRegisterView;
    private Button loginButton;
    private EditText username;
    private EditText password;
    private LoginPresenter lp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        lp = new LoginPresenter(this);

        toRegisterView = findViewById(R.id.to_register);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);

        Intent intent = getIntent();
        if(intent != null) {
            username.setText(intent.getStringExtra("username"));
        }

        toRegisterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toRegister();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void login() {
        lp.login(username.getText().toString(), password.getText().toString());
    }

    @Override
    public void setLogin(JsonObject r) {
        SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        //步骤2： 实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //步骤3：将获取过来的值放入文件
        editor.putString("username", username.getText().toString());
        editor.putString("password", password.getText().toString());
        editor.putString("token",r.get("token").getAsString());
        editor.putString("avatar", "MTc2MjI0NjU3MTIwMDE4MDIxMDU=");
        editor.commit();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("username", username.getText().toString());
        startActivity(intent);
    }

    @Override
    public void toRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void showMsg(int s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}
