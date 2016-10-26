package com.example.nyashcore.referee;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {
    @GET("/api-referee/{userId}/get-my-matches")
    Response getMatchList(@Path("userID") String userId);
    @POST("/api-referee/{userId}/set-info")
    Response sendAction(@Path("userID") String userId);
}
