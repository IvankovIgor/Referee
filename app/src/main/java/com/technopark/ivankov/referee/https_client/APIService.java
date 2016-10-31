package com.technopark.ivankov.referee.https_client;

import com.technopark.ivankov.referee.content.MatchList;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

interface APIService {
    @GET("api-referee/{idUser}/get-my-matches")
    Call<List<MatchList.Match>> getMatches(@Path("idUser") String idUser);
    @GET("api-referee/{idMatch}/{idAction}/{event}/set-info")
    Call<ResponseBody> postAction(@Path("idMatch") String idMatch, @Path("idAction") long idAction, @Path("event") int event);
}