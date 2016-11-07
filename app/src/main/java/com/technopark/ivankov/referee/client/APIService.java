package com.technopark.ivankov.referee.client;

import com.google.gson.JsonObject;
import com.technopark.ivankov.referee.content.Action;
import com.technopark.ivankov.referee.content.MatchList;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

interface APIService {
    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("api-referee/get-my-matches")
    Call<List<MatchList.Match>> getMatches(@Body JsonObject user);

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("api-referee/set-info")
    Call<ResponseBody> postAction(@Body Action action);
}