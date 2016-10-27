package com.example.nyashcore.referee;

import com.example.nyashcore.referee.content.MatchList;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {
    @GET("api-referee/{userId}/get-my-matches")
    Call<List<MatchList.Match>> getMatchList(@Path("userId") String userId);
    @POST("api-referee/{userId}/set-info")
    Response sendAction(@Path("userID") String userId);
}
