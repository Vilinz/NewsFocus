package com.example.newsfocus.Service;

import com.google.gson.JsonObject;

import java.util.List;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.FormUrlEncoded;

public interface MyService {
    @GET("/news/list/offset={offset}&count={count}")
    Observable<JsonObject> getNewHead(@Path("offset") int offset, @Path("count") int count);

    @GET("/news/content/id={id}")
    Observable<JsonObject> getDetail(@Path("id") String id);

    @FormUrlEncoded
    @POST("/user/verification")
    Observable<JsonObject> verification(@Field("token")String token);

    @FormUrlEncoded
    @POST("/user/signup")
    Observable<JsonObject> register(@Field("username")String username, @Field("password") String password, @Field("telephone") String telephone);

    @FormUrlEncoded
    @POST("/user/login")
    Observable<JsonObject> login(@Field("username")String username, @Field("password") String password);


}
