package com.example.android.med_ai;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("api/login")
    Call<Response> storeUser(@Body User user);
}