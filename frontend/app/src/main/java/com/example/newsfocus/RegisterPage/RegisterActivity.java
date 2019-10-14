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
import com.example.newsfocus.tools.ValidateUtil;
import com.google.gson.JsonObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class RegisterActivity extends AppCompatActivity implements IRegisterView {
    private Button registerButton;
    private TextView toLoginView;

    private EditText username;
    private EditText password;
    private EditText passwordRepeat;
    private EditText telephone;
    private RegisterPresenter rp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rp = new RegisterPresenter(this);

        registerButton = findViewById(R.id.register);
        toLoginView = findViewById(R.id.to_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        passwordRepeat = findViewById(R.id.repeat_password);
        telephone = findViewById(R.id.telephone);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ValidateUtil.isAccount(username.getText().toString())) {
                    if(ValidateUtil.isPhone(telephone.getText().toString())) {
                        if(ValidateUtil.isPassword(password.getText().toString())) {
                            if(password.getText().toString().equals(passwordRepeat.getText().toString())) {
                                rp.register(username.getText().toString(), password.getText().toString(), telephone.getText().toString());
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.password_missmatch, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.password_re, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.phone_re, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.username_re, Toast.LENGTH_LONG).show();
                }
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

    @Override
    public void registerSuccess() {
        Toast.makeText(getApplicationContext(), R.string.register_success, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.putExtra("username", username.getText().toString());
        startActivity(intent);
    }

    @Override
    public void showMsg(int i) {
        Toast.makeText(getApplicationContext(), i, Toast.LENGTH_SHORT).show();
    }
}
