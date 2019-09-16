package com.trust.openid.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.trust.openid.sso.TrustLoggerSSO;
import com.trust.openid.sso.client.TrustSSO;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    Button boton, boton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boton = findViewById(R.id.web);
        boton1 = findViewById(R.id.web2);
        TrustLoggerSSO.d(TrustSSO.getBundleId(this));
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Autentia-Client-Id", "@!3011.6F0A.B190.8457!0001!294E.B0CD!0008!D9CA.ABCD.F686.9C3C");

                TrustSSO trustSSO = new TrustSSO.TrustSSOBuilder(MainActivity.this)
                        .setAcrKey("acr_values")
                        .setAcrValues("login_autentiax")
                        .setScopes("address+email+openid+profile+uma_protection+mobile_phone+phone+document.r")
                        .setRedirectUri("identidad.digital://auth.id")
                        .setBaseUrl("https://api.autentia.id/oxauth/restv1/")
                        .setClientID("@!3011.6F0A.B190.8457!0001!294E.B0CD!0008!D9CA.ABCD.F686.9C3C")
                        .setClientSecret("Ds48vEADZb4c7a87LWGUJ4Kv")
                        .setMethodAuthorize("authorize")
                        .setMethodToken("token")
                        .setGrantType("authorization_code")
                        .setResponseType("code")
                        .setHeaders(headers)
                        .build();
                trustSSO.authorizationRequest();

            }
        });

        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Autentia-Client-Id", "@!3011.6F0A.B190.8457!0001!294E.B0CD!0008!D9CA.ABCD.F686.9C3C");

                TrustSSO trustSSO = new TrustSSO.TrustSSOBuilder(MainActivity.this)
                        .setAcrKey("acr_values")
                        .setAcrValues("update_password_autentiax")
                        .setScopes("address email openid profile uma_protection mobile_phone phone document.r")
                        .setRedirectUri("identidad.digital://auth.id")
                        .setBaseUrl("https://api.autentia.id/oxauth/restv1/")
                        .setClientID("@!3011.6F0A.B190.8457!0001!294E.B0CD!0008!D9CA.ABCD.F686.9C3C")
                        .setClientSecret("Ds48vEADZb4c7a87LWGUJ4Kv")
                        .setMethodAuthorize("authorize")
                        .setMethodToken("token")
                        .setGrantType("authorization_code")
                        .setResponseType("code")
                        .setHeaders(headers)
                        .build();
                trustSSO.authorizationRequest();
            }
        });



    }


}
