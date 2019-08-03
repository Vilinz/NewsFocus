package com.example.newsfocus;

import com.google.gson.JsonObject;

import java.util.List;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.FormUrlEncoded;

public interface MyService {
    @GET("/news/list/offset={offset}&count={count}")
    Observable<JsonObject> getNewHead(@Path("offset") int offset, @Path("count") int count);
}
