package com.example.newsfocus.Service;

import com.example.newsfocus.Service.MyService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceInstanceWithToken {
    public static MyService myservice;
    private ServiceInstanceWithToken() {

    }

    public static MyService getInstanceWithToken(final String token) {
        if(myservice == null) {
            synchronized (MyService.class) {
                if(myservice == null) {
                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    builder.addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Interceptor.Chain chain) throws IOException {
                            Request original = chain.request();

                            // Request customization: add request headers
                            Request.Builder requestBuilder = original.newBuilder()
                                    .addHeader("Authorization", "Bearer " + token);

                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }
                    });


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

