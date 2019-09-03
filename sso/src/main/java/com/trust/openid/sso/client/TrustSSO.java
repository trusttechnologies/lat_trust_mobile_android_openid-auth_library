package com.trust.openid.sso.client;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.util.Base64;
import android.util.Log;

import androidx.browser.customtabs.CustomTabsIntent;

import com.orhanobut.hawk.Hawk;
import com.trust.openid.sso.TrustLoggerSSO;
import com.trust.openid.sso.model.AuthorizeSSO;
import com.trust.openid.sso.network.APISSO;
import com.trust.openid.sso.network.RestClientSSO;
import com.trust.openid.sso.network.res.TokenResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.trust.openid.sso.client.ConstantsSSO.ACR_VALUE;
import static com.trust.openid.sso.client.ConstantsSSO.CLIENT_ID;
import static com.trust.openid.sso.client.ConstantsSSO.CODE;
import static com.trust.openid.sso.client.ConstantsSSO.REDIRECT_URI;
import static com.trust.openid.sso.client.ConstantsSSO.RESPONSE_TYPE;
import static com.trust.openid.sso.client.ConstantsSSO.SCOPE;

public class TrustSSO {
    private String clientID;
    private String clientSecret;
    private String scopes;
    private Bundle[] headers;
    private String acrValues;
    private String acrKey;
    private String redirecUri;
    private String baseURL;
    private String grantType;
    private String methodToken;
    private String methodAuthorize;
    private Context context;
    private String responseType;

    private static TrustSSO instance = new TrustSSO();

    private TrustSSO() {
    }

    public void setAcrValues(String acrValues) {
        this.acrValues = acrValues;
    }

    public String getAcrValues() {
        return acrValues;
    }

    public static TrustSSO getInstance() {
        return instance;

    }

    public String getClientID() {
        return clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void authorizationRequest() {
        try {
            Uri uri = getUriAuthorize();
            TrustLoggerSSO.d("authorizationRequest: " + uri.toString());
            launchCustomTab(uri);
        } catch (Exception ex) {
            TrustLoggerSSO.d("Authorization Request: " + ex.getMessage());
        }

    }

    private void launchCustomTab(Uri uri) {
        try {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            if (this.headers != null && this.headers.length > 0) {
                customTabsIntent.intent.putExtra(Browser.EXTRA_HEADERS, headers);
            }
            customTabsIntent.launchUrl(this.context, uri);
        } catch (Exception ex) {
            TrustLoggerSSO.d("Launch Custom Tab: " + ex.getMessage());
        }

    }

    private Uri getUriAuthorize() {
        return Uri.parse(this.baseURL)
                .buildUpon()
                .appendPath(this.methodAuthorize)
                .appendQueryParameter(SCOPE, this.scopes)
                .appendQueryParameter(RESPONSE_TYPE, this.responseType)
                .appendQueryParameter(REDIRECT_URI, this.redirecUri)
                .appendQueryParameter(CLIENT_ID, this.clientID)
                .appendQueryParameter(ACR_VALUE, this.acrValues).build();
    }

    public void getToken(Intent intent, TrustAuthListener authListener) {
        try {
            Uri data = getUriFromIntent(intent);
            if (data == null) {
                throw new Exception("Get Token: data from intent cannot be null");
            }
            AuthorizeSSO authorizeSSO = getAuthorizeSSO(data);
            getTokenFromApi(authorizeSSO, authListener);
        } catch (Exception ex) {
            TrustLoggerSSO.d("Get Token: " + ex.getMessage());
        }

    }

    private void getTokenFromApi(AuthorizeSSO authorizeSSO, TrustAuthListener listener) {
        validateAuthorizeSSO(authorizeSSO);
        if (this.headers != null || this.headers.length > 0) {
            getTokenWithCustomHeaders(authorizeSSO, listener);
        } else {
            getTokenDefault(authorizeSSO);
        }
    }

    private void getTokenDefault(AuthorizeSSO authorizeSSO) {
        //llamada para obtener access token con 0 cabeceras

    }

    private void getTokenWithCustomHeaders(AuthorizeSSO authorizeSSO, final TrustAuthListener listener) {
        //todo
        //llamada para obtener access token con 1 o mas cabeceras
        //quiza para pruebas debas harcodear esta parte, aqui se hace
        //la llamada a /token para obtener access_token, este es el
        //metodo que retornaba un code400 o un code500
        RestClientSSO.get().token(
                "Basic QCEzMDExLjZGMEEuQjE5MC44NDU3ITAwMDEhMjk0RS5CMENEITAwMDghMTQ1RC5GNTIyLkZGQzMuNDM5RTpQMnFyN1BiUFIzUXhNTVJJSnd4cVdPODE=", //base64(client_id:client_secret)
                "@!3011.6F0A.B190.8457!0001!294E.B0CD!0008!145D.F522.FFC3.439E", //header: Autentia-Client-Id= client_id
                authorizeSSO.getGrantType(),
                authorizeSSO.getScope(),
                authorizeSSO.getCode())
                .enqueue(new Callback<TokenResponse>() {
                    @Override
                    public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                        if (response.code() != 400 && response.code() != 500 && response.body() != null) {
                            listener.onSucces(response.body().getAccess_token());
                        } else {
                            listener.onError(response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<TokenResponse> call, Throwable t) {
                        listener.onError(t.getMessage());
                    }
                });
    }


    private void validateAuthorizeSSO(AuthorizeSSO authorizeSSO) {
        try {
            if (authorizeSSO == null) {
                throw new Exception("Validate AuthorizeSSO: authorizeSSO cannot be null");
            }
            if (authorizeSSO.getCode() == null || authorizeSSO.getCode().equals("")) {
                throw new Exception("Validate AuthorizeSSO: code cannot be null or empty");
            }
            if (authorizeSSO.getGrantType() == null || authorizeSSO.getGrantType().equals("")) {
                throw new Exception("Validate AuthorizeSSO: grant type cannot be null or empty");
            }
            if (authorizeSSO.getScope() == null || authorizeSSO.getScope().equals("")) {
                throw new Exception("Validate AuthorizeSSO: scope cannot be null or empty");
            }
        } catch (Exception ex) {
            TrustLoggerSSO.d("Validate AuthorizeSSO: " + ex.getMessage());
        }

    }

    private AuthorizeSSO getAuthorizeSSO(Uri data) {
        AuthorizeSSO authorize = new AuthorizeSSO();
        if (data == null) {
            TrustLoggerSSO.d("Get AuthorizeSSO: data uri cannot be null");
            return new AuthorizeSSO();
        }
        if (data.getQueryParameter(CODE) == null) {
            TrustLoggerSSO.d("Get AuthorizeSSO: code cannot be null");
            return new AuthorizeSSO();
        }
        if (data.getQueryParameter(SCOPE) == null) {
            TrustLoggerSSO.d("Get AuthorizeSSO: scope cannot be null");
            return new AuthorizeSSO();
        }
        if (this.grantType == null || this.grantType.equals("")) {
            TrustLoggerSSO.d("Get AuthorizeSSO: grant type cannot be null");
            return new AuthorizeSSO();
        }
        authorize.setCode(data.getQueryParameter(CODE));
        authorize.setGrantType(this.grantType);
        authorize.setScope(data.getQueryParameter(SCOPE));
        return authorize;
    }

    private Uri getUriFromIntent(Intent intent) {
        Uri data = null;
        if (intent != null && intent.getData() != null) {
            data = intent.getData();
        } else {
            TrustLoggerSSO.d("Get Uri From Intent: intent cannot be null");
        }
        TrustLoggerSSO.d(data.toString());
        return data;
    }


    //todo method token
    //todo method refresh
    //todo method logout
    public interface TrustAuthListener {

        void onSucces(String accessToken);

        void onError(String error);
    }


    public static class TrustSSOBuilder {
        private Context context;
        private String clientID;
        private String clientSecret;
        private String scopes;
        private Bundle[] headers;
        private String acrValues;
        private String acrKey;
        private String redirectUri;
        private String baseURL;
        private String grantType;
        private String methodToken;
        private String methodAuthorize;
        private String responseType;

        public TrustSSOBuilder(Context context) {
            this.context = context;
            Hawk.init(context).build();
        }

        public TrustSSOBuilder setClientID(String clientID) {
            if (clientID != null && !clientID.equals("")) {
                this.clientID = clientID;
            } else {
                TrustLoggerSSO.d("Client Id cannot be null or empty");
            }
            return this;
        }

        public TrustSSOBuilder setClientSecret(String clientSecret) {
            if (clientSecret != null && !clientSecret.equals("")) {
                this.clientSecret = clientSecret;
            } else {
                TrustLoggerSSO.d("Client Secret cannot be null or empty");
            }
            return this;
        }

        public TrustSSOBuilder setScopes(String scopes) {
            if (scopes != null && !scopes.equals("")) {
                this.scopes = scopes;
            } else {
                TrustLoggerSSO.d("scopes cannot be null or empty");
            }
            return this;
        }

        public TrustSSOBuilder setHeaders(Bundle[] headers) {
            if (headers != null && headers.length > 0) {
                this.headers = headers;
            } else {
                TrustLoggerSSO.d("Headers cannot be null or empty");
            }
            return this;
        }//arreglo

        public TrustSSOBuilder setAcrValues(String acrValues) {
            if (acrValues != null && !acrValues.equals("")) {
                this.acrValues = acrValues;
            } else {
                TrustLoggerSSO.d("Acr Values Secret cannot be null or empty");
            }
            return this;
        }

        public TrustSSOBuilder setAcrKey(String acrKey) {
            if (acrKey != null && !acrKey.equals("")) {
                this.acrKey = acrKey;
            } else {
                TrustLoggerSSO.d("Acr Key cannot be null or empty");
            }
            return this;
        }

        public TrustSSOBuilder setRedirectUri(String redirectUri) {
            if (redirectUri != null && !redirectUri.equals("")) {
                this.redirectUri = redirectUri;
            } else {
                TrustLoggerSSO.d("Redirect URI cannot be null or empty");
            }
            return this;
        }

        public TrustSSOBuilder setBaseUrl(String baseURL) {
            if (baseURL != null && !baseURL.equals("")) {
                this.baseURL = baseURL;
                Hawk.put(ConstantsSSO.BASE_URL_SSO, baseURL);
            } else {
                TrustLoggerSSO.d("Base URL  cannot be null or empty");
            }
            return this;
        }

        public TrustSSOBuilder setGrantType(String grantType) {
            if (grantType != null && !grantType.equals("")) {
                this.grantType = grantType;
            } else {
                TrustLoggerSSO.d("Grant Type cannot be null or empty");
            }
            return this;
        }

        public TrustSSOBuilder setMethodToken(String methodToken) {
            if (methodToken != null && !methodToken.equals("")) {
                this.methodToken = methodToken;
            } else {
                TrustLoggerSSO.d("Method Token cannot be null or empty");
            }
            return this;
        }

        public TrustSSOBuilder setMethodAuthorize(String methodAuthorize) {
            if (methodAuthorize != null && !methodAuthorize.equals("")) {
                this.methodAuthorize = methodAuthorize;
            } else {
                TrustLoggerSSO.d("Method Authorize cannot be null or empty");
            }
            return this;
        }

        public TrustSSOBuilder setResponseType(String responseType) {
            if (responseType != null && !responseType.equals("")) {
                this.responseType = responseType;
            } else {
                TrustLoggerSSO.d("Response Type cannot be null or empty");

            }
            return this;
        }

        public TrustSSO build() {
            try {
                instance.clientID = this.clientID;//
                instance.clientSecret = this.clientSecret;//
                instance.acrKey = this.acrKey;
                instance.acrValues = this.acrValues;
                instance.baseURL = this.baseURL;//
                instance.grantType = this.grantType;
                instance.headers = this.headers;
                instance.methodAuthorize = this.methodAuthorize;//
                instance.methodToken = this.methodToken;//
                instance.redirecUri = this.redirectUri;//
                instance.scopes = this.scopes;//
                instance.responseType = this.responseType;
                instance.context = this.context;//
                return instance;

            } catch (Exception ex) {
                return new TrustSSO();
            }

        }
    }
}
