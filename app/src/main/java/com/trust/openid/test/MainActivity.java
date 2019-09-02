package com.trust.openid.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.trust.openid.sso.client.TrustSSO;

public class MainActivity extends AppCompatActivity {
    Button boton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boton = findViewById(R.id.web);

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle header = new Bundle();
                header.putString("Autentia-Client-Id", "@!3011.6F0A.B190.8457!0001!294E.B0CD!0008!145D.F522.FFC3.439E");
                TrustSSO trustSSO = new TrustSSO.TrustSSOBuilder(MainActivity.this)
                        .setAcrKey("acr_values")
                        .setAcrValues("autoidentify")
                        .setScopes("openid+uma_protection+profile+profile.r+profile.w+address+audit.r+audit.w")
                        .setRedirectUri("trust.enrollment.app://auth.id")
                        .setBaseUrl("https://api.autentia.id/oxauth/restv1/")
                        .setClientID("@!3011.6F0A.B190.8457!0001!294E.B0CD!0008!145D.F522.FFC3.439E")
                        .setClientSecret("P2qr7PbPR3QxMMRIJwxqWO81")
                        .setMethodAuthorize("authorize")
                        .setMethodToken("token")
                        .setGrantType("refresh_token")
                        .setHeaders(new Bundle[]{header})
                        .build();

                trustSSO.authorizationRequest();
            }
        });
    }



}
