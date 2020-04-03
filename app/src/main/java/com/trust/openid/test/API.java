package com.trust.openid.test;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface API {
    @GET("userinfo")
    Call<JsonElement> getUserInfo(@Query("access_token") String accessToken);

}
