package com.example.newsfocus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {
    private TextView toRegisterView;
    private Button loginButton;
    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toRegisterView = findViewById(R.id.to_register);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);

        toRegisterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompositeDisposable mCompositeDisposable = new CompositeDisposable();
                DisposableObserver<JsonObject> disposableObserver_login = new DisposableObserver<JsonObject>() {
                    @Override
                    public void onNext(JsonObject r) {
                        Log.i("Login", r.toString());
                        String result = r.get("message").getAsString();
                        if(result.equals("success")) {
                            Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        //Toast.makeText(GithubApi.this, R.string.network_error, Toast.LENGTH_LONG).show();
                    }
                };
                ServiceInstance.getInstance().login(username.getText().toString(), password.getText().toString()).subscribeOn(Schedulers.newThread()).
                        observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver_login);
                mCompositeDisposable.add(disposableObserver_login);
            }
        });
    }
}
