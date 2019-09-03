package com.trust.openid.test;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.trust.openid.sso.TrustLoggerSSO;
import com.trust.openid.sso.client.TrustSSO;

public class ResultActivity extends AppCompatActivity {

    private static final String TAG = ResultActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        TrustSSO trustSSO = TrustSSO.getInstance();
        /*
        todo
        *trustSSO.setAcrValues("acr_value_customizado");
        * hice un singleton para poder obtener la instancia desde cualquie
        * parte de la aplicacion, te disponibilize un metodo para settear un acr_value
        * para cuando tengas que diferencias entre login o cambio de contrasena
        *
        * */
        trustSSO.getToken(getIntent(), new TrustSSO.TrustAuthListener() {
            @Override
            public void onSucces(String accessToken) {
                TrustLoggerSSO.d("accessToken: " + accessToken);
            }

            @Override
            public void onError(String error) {
                TrustLoggerSSO.d("onError: " + error);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + requestCode + " Resquest code: " + requestCode);
    }
}
