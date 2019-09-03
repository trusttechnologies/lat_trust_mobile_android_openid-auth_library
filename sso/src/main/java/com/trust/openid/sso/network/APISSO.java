package com.trust.openid.sso.network;

import com.trust.openid.sso.model.AuthorizeSSO;
import com.trust.openid.sso.network.res.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface APISSO {

    @FormUrlEncoded
    @POST("token")
    Call<TokenResponse> token(@Header("Authorization") String bearer,
                              @Header("Autentia-Client-Id") String aci,
                              @Field("grant_type") String code,
                              @Field("scope") String scope,
                              @Field("code") String grant_type,
                              @Field("redirect_uri") String redirectUri);

    @POST("token")
    Call<TokenResponse> token2(@Header("Authorization") String bearer,
                               @Header("Autentia-Client-Id") String aci,
                               @Body AuthorizeSSO authorize);
}
