package com.trust.openid.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.orhanobut.hawk.Hawk;
import com.trust.openid.sso.TrustLoggerSSO;
import com.trust.openid.sso.client.TrustSSO;
import com.trust.openid.sso.client.TrustSSOListener;

import javax.security.auth.login.LoginException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ITrustResultActivity extends AppCompatActivity implements TrustSSOListener  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_trust_result);
        final TrustSSO trustSSO = TrustSSO.getInstance();


        Log.i("TrustLoggerSSO", "onCreate: " + new Gson().toJson(Hawk.get("token.response.sso")));
        Log.i("TrustLoggerSSO", "onCreate: " + trustSSO.getAcrValues());
        Log.i("TrustLoggerSSO", "onCreate: " + new Gson().toJson(getIntent().getData().toString()));
        trustSSO.getToken(getIntent() ,this);
    }


    private void getUserInfo(String accessToken  ) {
        RestClientTrust.setup().getUserInfo(accessToken).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                TrustLoggerSSO.d(response.body().toString());
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                TrustLoggerSSO.d(t.getMessage());

            }
        });
    }


    @Override
    public void getUser(Object user) {
        Log.e(getClass().getSimpleName(), new Gson().toJson(user));
    }
}
