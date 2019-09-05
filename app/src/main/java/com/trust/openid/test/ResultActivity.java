package com.trust.openid.test;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.trust.openid.sso.TrustLoggerSSO;
import com.trust.openid.sso.client.TrustSSO;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        final TrustSSO trustSSO = TrustSSO.getInstance();


        trustSSO.getToken(getIntent(), new TrustSSO.TrustAuthListener() {
            @Override
            public void onSucces(String accessToken) {
                TrustLoggerSSO.d("accessToken: " + accessToken);
                TrustLoggerSSO.d("session id: " + trustSSO.getSession_id());
                TrustLoggerSSO.d("session state: " + trustSSO.getSession_state());
            }

            @Override
            public void onError(String error) {
                TrustLoggerSSO.d("onError: " + error);
            }
        });


    }

}
