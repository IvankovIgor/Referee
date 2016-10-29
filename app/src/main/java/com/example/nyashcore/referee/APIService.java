package com.example.nyashcore.referee;

import com.example.nyashcore.referee.content.ActionList;
import com.example.nyashcore.referee.content.MatchList;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {
    @GET("api-referee/{idUser}/get-my-matches")
    Call<List<MatchList.Match>> getMatches(@Path("idUser") String idUser);
    @GET("api-referee/{idMatch}/{idAction}/{event}/set-info")
    Call<ResponseBody> postAction(@Path("idMatch") String idMatch, @Path("idAction") long idAction, @Path("event") int event);
}