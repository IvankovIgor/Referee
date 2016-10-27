package com.example.nyashcore.referee;

import com.example.nyashcore.referee.content.MatchList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {
    @GET("api-referee/4189816/get-my-matches")
//    Call<MatchList.Match> getMatchList(@Path("userID") String userId);
    Call<MatchList.Match> getMatchList();
    @POST("api-referee/{userId}/set-info")
    Response sendAction(@Path("userID") String userId);
}
