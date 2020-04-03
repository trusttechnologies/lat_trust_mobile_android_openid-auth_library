package com.trust.openid.sso.network;

import com.trust.openid.sso.network.res.TokenResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface APISSO {

    @FormUrlEncoded
    @POST("token")
    Call<TokenResponse> token(@Header("Authorization") String bearer,
                              @HeaderMap HashMap<String, String> headers,
                              @Field("grant_type") String code,
                              @Field("scope") String scope,
                              @Field("code") String grant_type,
                              @Field("redirect_uri") String redirect_uri

    );

    @FormUrlEncoded
    @POST("token")
    Call<TokenResponse> token(
            @Field("grant_type") String code,
            @Field("scope") String scope,
            @Field("code") String grant_type,
            @Field("redirect_uri") String redirect_uri

    );

    @FormUrlEncoded
    @POST("token")
    Call<TokenResponse> refreshToken(
            @Field("grant_type") String grant_type,
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("refresh_token") String refresh_token

    );
}
