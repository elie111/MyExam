package com.example.myexam;




import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitAPI {


    @GET("/GetMeetingsStat/{time}")
    Call<int[]> getMeetingsStat(@Path("time") int time);

    @GET("/GetParentsStat/{time}")
    Call<int[]> getParentsStat(@Path("time") int time);

    @GET("/GetActiveKidsStat/{time}")
    Call<int[]> getKidsStat(@Path("time") int time);

    @GET("/GetCategoriesInfo/{time}")
    Call<HashMap<String, Integer>> getKidsInCategories(@Path("time") int time);
    @GET("/GetCategoriesTrend/{time}")
    Call<HashMap<String, HashMap<Integer, Integer>>> getTrend(@Path("time") int time);

}