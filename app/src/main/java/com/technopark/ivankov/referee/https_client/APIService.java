package com.technopark.ivankov.referee.https_client;

import com.technopark.ivankov.referee.content.ActionList;
import com.technopark.ivankov.referee.content.MatchList;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import static android.R.attr.action;

interface APIService {
    @GET("api-referee/{idUser}/get-my-matches")
    Call<List<MatchList.Match>> getMatches(@Path("idUser") String idUser);
//    @GET("api-referee/{idMatch}/{idAction}/{event}/set-info")
//    Call<ResponseBody> postAction(@Path("idMatch") String idMatch, @Path("idAction") long idAction, @Path("event") int event);
//    @POST("api-referee/set-info")
//    Call<ResponseBody> postAction(@Field("idAction") long idAction, @Field("idMatch") String idMatch,
//                                  @Field("idTeam") String idTeam, @Field("idPlayer") String idPlayer,
//                                  @Field("minute") int minute, @Field("event") int event);
    @POST("api-referee/set-info")
    Call<ResponseBody> postAction(@Body ActionList.Action action);
}