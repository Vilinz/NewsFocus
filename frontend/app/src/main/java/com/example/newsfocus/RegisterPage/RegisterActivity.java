package com.example.newsfocus.RegisterPage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsfocus.LoginPage.LoginActivity;
import com.example.newsfocus.R;
import com.example.newsfocus.Service.ServiceInstance;
import com.google.gson.JsonObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class RegisterActivity extends AppCompatActivity {
    private Button registerButton;
    private TextView toLoginView;

    private EditText username;
    private EditText password;
    private EditText telephone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerButton = findViewById(R.id.register);
        toLoginView = findViewById(R.id.to_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        telephone = findViewById(R.id.telephone);

        Intent intent = getIntent();
        Intent intentReturn = new Intent();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompositeDisposable mCompositeDisposable = new CompositeDisposable();
                DisposableObserver<JsonObject> disposableObserver_login = new DisposableObserver<JsonObject>() {
                    @Override
                    public void onNext(JsonObject r) {
                        String result = r.get("message").getAsString();
                        if(result.equals("success")) {
                            Toast.makeText(getApplicationContext(), R.string.register_success, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
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
                ServiceInstance.getInstance().register(username.getText().toString(), password.getText().toString(), telephone.getText().toString()).subscribeOn(Schedulers.newThread()).
                        observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver_login);
                mCompositeDisposable.add(disposableObserver_login);
            }
        });

        toLoginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
