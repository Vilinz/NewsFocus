package com.example.newsfocus.Service;

import com.example.newsfocus.Service.MyService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceInstance {
    public static MyService myservice;
    private ServiceInstance() {

    }

    public static MyService getInstance() {
        if(myservice == null) {
            synchronized (MyService.class) {
                if(myservice == null) {
                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    builder.connectTimeout(3, TimeUnit.SECONDS);
                    builder.writeTimeout(3, TimeUnit.SECONDS);
                    builder.readTimeout(3, TimeUnit.SECONDS);

                    myservice = (MyService)new Retrofit.Builder().baseUrl("http://47.102.84.27:3000")
                            .client(builder.build())
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build()
                            .create(MyService.class);
                }
            }
        }
        return myservice;
    }
}
