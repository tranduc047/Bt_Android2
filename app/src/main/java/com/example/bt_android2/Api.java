package com.example.bt_android2;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Api {
    @GET(".")
    Call<List<MessageModule>> getJsonData();
}
