package com.example.newsfocus.Service;

import com.google.gson.JsonObject;

import java.util.List;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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

    @FormUrlEncoded
    @POST("/comment")
    Observable<JsonObject> sendComment(@Field("userID") String userID, @Field("newsID")String newsID, @Field("time") String time, @Field("content") String content);

    @GET("/comment/username={username}")
    Observable<JsonObject> getComment(@Path("username") String username);

    @GET("/comment/newsID={newsID}")
    Observable<JsonObject> getCommentByNewsID(@Path("newsID") String newsID);

    @Multipart
    @POST("/user/avatar")
    Observable<JsonObject> uploadImages(@Part List<MultipartBody.Part> partList);

    @GET("/image/avatar/{filename}")
    Observable<JsonObject> getAvatar(@Path("filename") String filename);

    @GET("/user/info/{username}")
    Observable<JsonObject> getUserInfo(@Path("username") String username);

}
