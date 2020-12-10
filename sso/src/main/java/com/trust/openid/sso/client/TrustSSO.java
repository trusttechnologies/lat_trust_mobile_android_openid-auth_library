package com.trust.openid.sso.client;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.util.Base64;

import androidx.browser.customtabs.CustomTabsIntent;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.orhanobut.hawk.Hawk;
import com.trust.openid.sso.TrustLoggerSSO;
import com.trust.openid.sso.model.AuthorizeSSO;
import com.trust.openid.sso.network.RestClientSSO;
import com.trust.openid.sso.network.res.RestClientUser;
import com.trust.openid.sso.network.res.TokenResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.trust.openid.sso.client.ConstantsSSO.AUTHORIZATIONSSO;
import static com.trust.openid.sso.client.ConstantsSSO.BASE_URL_SSO;
import static com.trust.openid.sso.client.ConstantsSSO.CLIENT_ID;
import static com.trust.openid.sso.client.ConstantsSSO.CODE;
import static com.trust.openid.sso.client.ConstantsSSO.REDIRECT_URI;
import static com.trust.openid.sso.client.ConstantsSSO.REFRESH_TOKEN;
import static com.trust.openid.sso.client.ConstantsSSO.RESPONSE_TYPE;
import static com.trust.openid.sso.client.ConstantsSSO.SCOPE;
import static com.trust.openid.sso.client.ConstantsSSO.SESSION_ID;
import static com.trust.openid.sso.client.ConstantsSSO.SESSION_STATE;
import static com.trust.openid.sso.client.ConstantsSSO.TIME_OUT;
import static com.trust.openid.sso.client.ConstantsSSO.TOKENRESPONSESSO;

public class TrustSSO {
    private String clientID;
    private String clientSecret;
    private String scopes;
    private HashMap<String, String> headers;
    private String acrValues;
    private String acrKey;
    private String redirecUri;
    private String baseURL;
    private String grantType;
    private String methodToken;
    private String methodAuthorize;
    private Context context;
    private String responseType;
    private String session_id;
    private String session_state;
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    private static TrustSSO instance = new TrustSSO();

    private TrustSSO() {
    }

    public static String getBundleId(Context context) {

        return context.getPackageName();
    }

    public String getSession_id() {
        return session_id;
    }

    private void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getSession_state() {
        return session_state;
    }

    private void setSession_state(String session_state) {
        this.session_state = session_state;
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

    public static boolean getTokenState() {
        return Hawk.contains(TOKENRESPONSESSO);
    }

    public static void clearDataSession() {
        Hawk.delete(TOKENRESPONSESSO);
        Hawk.delete(AUTHORIZATIONSSO);
        Hawk.delete(TIME_OUT);
        Hawk.delete(REFRESH_TOKEN);
        Hawk.delete(BASE_URL_SSO);
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

    public void authorizationRequestWithState() {
        try {
            Uri uri = getUriAuthorizeWithState();
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
            customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (this.headers != null) {
                TrustLoggerSSO.d(headers.get("Autentia-Client-Id").toString());
                customTabsIntent.intent.putExtra(Browser.EXTRA_HEADERS, headers);
            }
            TrustLoggerSSO.d("launchCustomTab: " + uri.toString());

            customTabsIntent.launchUrl(this.context, uri);
        } catch (Exception ex) {
            TrustLoggerSSO.d("Launch Custom Tab: " + ex.getMessage());
        }

    }

    private void refreshToken(final TrustAuthListener listener) {
        try {
            final Long time_past = Long.parseLong(Hawk.get(TIME_OUT).toString());
            final Long time_now = System.currentTimeMillis() / 1000;
            AuthorizeSSO authorizeSSO = new AuthorizeSSO();
            TokenResponse tokenResponse = new TokenResponse();
            if (Hawk.contains(TOKENRESPONSESSO)) {
                JsonElement jsonElement = new Gson().toJsonTree(Hawk.get(TOKENRESPONSESSO));
                tokenResponse = new Gson().fromJson(jsonElement, TokenResponse.class);
                TrustLoggerSSO.d(tokenResponse.getRefresh_token());
            }
            if (Hawk.contains(AUTHORIZATIONSSO)) {
                JsonElement jsonElement = new Gson().toJsonTree(Hawk.get(AUTHORIZATIONSSO));
                authorizeSSO = new Gson().fromJson(jsonElement, AuthorizeSSO.class);
                TrustLoggerSSO.d(authorizeSSO.getClient_id());
            }
            if ((time_now - time_past) > tokenResponse.getExpires_in()) {
                RestClientSSO.get().refreshToken(
                        REFRESH_TOKEN,
                        authorizeSSO.getClient_id(),
                        authorizeSSO.getClient_secret(),
                        tokenResponse.getRefresh_token()).enqueue(new Callback<TokenResponse>() {
                    @Override
                    public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                        if (response != null && response.body() != null) {
                            TrustLoggerSSO.d(response.body().getAccess_token());
                            if (response.isSuccessful()) {
                                Hawk.put(TIME_OUT, System.currentTimeMillis() / 1000);
                                Hawk.put(TOKENRESPONSESSO, response.body());
                                listener.onSucces(response.body().getAccess_token());
                            } else {
                                listener.onError("refresh token expired");
                            }
                        } else {
                            listener.onError("refresh token expired");

                        }

                    }

                    @Override
                    public void onFailure(Call<TokenResponse> call, Throwable t) {
                        listener.onError(t.getMessage());
                    }
                });
            } else {
                listener.onSucces(tokenResponse.getAccess_token());
            }
        } catch (Exception ex) {
            listener.onError(ex.getMessage());
        }


    }

    private Uri getUriAuthorize() {

        Uri uri = Uri.parse(this.baseURL)
                .buildUpon()
                .appendPath(this.methodAuthorize)
                .appendQueryParameter(SCOPE, this.scopes)
                .appendQueryParameter(RESPONSE_TYPE, this.responseType)
                .appendQueryParameter(REDIRECT_URI, this.redirecUri)
                .appendQueryParameter(CLIENT_ID, this.clientID)
                .appendQueryParameter(this.acrKey, this.acrValues)
                .build();


        TrustLoggerSSO.d("getUriAuthorize: " + this.acrValues);

        return uri;
    }

    private Uri getUriAuthorizeWithState() {

        Uri uri = Uri.parse(this.baseURL)
                .buildUpon()
                .appendPath(this.methodAuthorize)
                .appendQueryParameter(SCOPE, this.scopes)
                .appendQueryParameter(RESPONSE_TYPE, this.responseType)
                .appendQueryParameter(REDIRECT_URI, this.redirecUri)
                .appendQueryParameter(CLIENT_ID, this.clientID)
                .appendQueryParameter(this.acrKey, this.acrValues)
                .appendQueryParameter("state", this.state).build();


        TrustLoggerSSO.d("getUriAuthorize: " + this.acrValues);

        return uri;
    }

    public void getToken(Intent intent, TrustSSOListener authListener) {
        try {
           /* if (Hawk.contains(TIME_OUT)) {

                refreshToken(authListener);
                return;
            }*/
            // refreshToken(authListener);
            Uri data = getUriFromIntent(intent);
            if (data == null) {
                throw new Exception("Get Token: data from intent cannot be null");
            }
            AuthorizeSSO authorizeSSO = getAuthorizeSSOWithState(data);
            getTokenFromApi(authorizeSSO, authListener);

        } catch (Exception ex) {
            TrustLoggerSSO.d("Get Token: " + ex.getMessage());
        }

    }

    private void getTokenFromApi(AuthorizeSSO authorizeSSO, TrustSSOListener listener) {
        validateAuthorizeSSO(authorizeSSO);
        if (this.headers != null) {
            getTokenWithCustomHeaders(authorizeSSO, listener);
        } else {
            getTokenDefault(authorizeSSO);
        }
    }



    private void getTokenDefault(AuthorizeSSO authorizeSSO) {
        //llamada para obtener access token con 0 cabeceras

    }

    private void getTokenWithCustomHeaders(AuthorizeSSO authorizeSSO, final TrustSSOListener listener) {
        String authorization = getAuthorization(authorizeSSO);
        RestClientSSO.get().token(
                "Basic " + authorization, //base64(client_id:client_secret)
                this.headers, //header: Autentia-Client-Id= client_id
                authorizeSSO.getGrantType(),
                authorizeSSO.getScope(),
                authorizeSSO.getCode(),
                authorizeSSO.getRedirect_uri())
                .enqueue(new Callback<TokenResponse>() {
                    @Override
                    public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                        if (response != null && response.body() != null) {
                            Hawk.put(TOKENRESPONSESSO, response.body());
                            Hawk.put(TIME_OUT, System.currentTimeMillis() / 1000);
                          //  listener.getUser(response.body().getAccess_token());
                            getUserInfo(response.body().getAccess_token(), listener);
                        } else {
                        }

                    }

                    @Override
                    public void onFailure(Call<TokenResponse> call, Throwable t) {
                       // listener.onError("onFailure: " + t.getMessage());
                    }
                });
    }

    private void getUserInfo(String accessToken , final TrustSSOListener listener ) {
        RestClientUser.setup().getUserInfo(accessToken).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                listener.getUser(response.body());

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                TrustLoggerSSO.d(t.getMessage());

            }
        });
    }

    private String getAuthorization(AuthorizeSSO authorizeSSO) {
        byte[] data;
        StringBuilder authorization = new StringBuilder();
        authorization.append(authorizeSSO.getClient_id());
        authorization.append(":");
        authorization.append(authorizeSSO.getClient_secret());
        try {
            data = authorization.toString().getBytes("UTF-8");
            String base64 = Base64.encodeToString(data, Base64.DEFAULT);
            TrustLoggerSSO.i(base64);
            return base64.replaceAll(System.getProperty("line.separator", ""), "");
        } catch (Exception ex) {
            TrustLoggerSSO.d(ex.getMessage());
            return null;
        }
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

    private AuthorizeSSO getAuthorizeSSOWithState(Uri data) {
        TrustLoggerSSO.d(data.toString());
        AuthorizeSSO authorizeSSO = new AuthorizeSSO();
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
      /*  if (data.getQueryParameter(SESSION_ID) == null) {
            TrustLoggerSSO.d("Get AuthorizeSSO: session id cannot be null");
            return new AuthorizeSSO();
        }
        if (data.getQueryParameter(SESSION_STATE) == null) {
            TrustLoggerSSO.d("Get AuthorizeSSO: session state cannot be null");
            return new AuthorizeSSO();
        }*/
        if (this.grantType == null || this.grantType.equals("")) {
            TrustLoggerSSO.d("Get AuthorizeSSO: grant type cannot be null or empty");
            return new AuthorizeSSO();
        }
        if (this.redirecUri == null || this.redirecUri.equals("")) {
            TrustLoggerSSO.d("Get AuthorizeSSO: redirect uri cannot be null or empty");
            return new AuthorizeSSO();
        }
        if (this.clientSecret == null || this.clientSecret.equals("")) {
            TrustLoggerSSO.d("Get AuthorizeSSO: client secret cannot be null or empty");
            return new AuthorizeSSO();
        }
        if (this.clientID == null || this.clientID.equals("")) {
            TrustLoggerSSO.d("Get AuthorizeSSO: client id cannot be null or empty");
            return new AuthorizeSSO();
        }
        authorizeSSO.setCode(data.getQueryParameter(CODE));
        authorizeSSO.setGrantType(this.grantType);
        authorizeSSO.setScope(data.getQueryParameter(SCOPE));
        authorizeSSO.setRedirect_uri(this.redirecUri);
        authorizeSSO.setClient_id(this.clientID);
        authorizeSSO.setClient_secret(this.clientSecret);
        // authorizeSSO.setSession_id(data.getQueryParameter(SESSION_ID));
        //authorizeSSO.setSession_state(data.getQueryParameter(SESSION_STATE));
        TrustSSO trustSSO = getInstance();
        trustSSO.setSession_id(data.getQueryParameter(SESSION_ID));
        trustSSO.setSession_state(data.getQueryParameter(SESSION_STATE));
        Hawk.put(AUTHORIZATIONSSO, authorizeSSO);
        return authorizeSSO;
    }


    private AuthorizeSSO getAuthorizeSSO(Uri data) {
        TrustLoggerSSO.d(data.toString());
        AuthorizeSSO authorizeSSO = new AuthorizeSSO();
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
        if (data.getQueryParameter(SESSION_ID) == null) {
            TrustLoggerSSO.d("Get AuthorizeSSO: session id cannot be null");
            return new AuthorizeSSO();
        }
        if (data.getQueryParameter(SESSION_STATE) == null) {
            TrustLoggerSSO.d("Get AuthorizeSSO: session state cannot be null");
            return new AuthorizeSSO();
        }
        if (this.grantType == null || this.grantType.equals("")) {
            TrustLoggerSSO.d("Get AuthorizeSSO: grant type cannot be null or empty");
            return new AuthorizeSSO();
        }
        if (this.redirecUri == null || this.redirecUri.equals("")) {
            TrustLoggerSSO.d("Get AuthorizeSSO: redirect uri cannot be null or empty");
            return new AuthorizeSSO();
        }
        if (this.clientSecret == null || this.clientSecret.equals("")) {
            TrustLoggerSSO.d("Get AuthorizeSSO: client secret cannot be null or empty");
            return new AuthorizeSSO();
        }
        if (this.clientID == null || this.clientID.equals("")) {
            TrustLoggerSSO.d("Get AuthorizeSSO: client id cannot be null or empty");
            return new AuthorizeSSO();
        }
        authorizeSSO.setCode(data.getQueryParameter(CODE));
        authorizeSSO.setGrantType(this.grantType);
        authorizeSSO.setScope(data.getQueryParameter(SCOPE));
        authorizeSSO.setRedirect_uri(this.redirecUri);
        authorizeSSO.setClient_id(this.clientID);
        authorizeSSO.setClient_secret(this.clientSecret);
        authorizeSSO.setSession_id(data.getQueryParameter(SESSION_ID));
        authorizeSSO.setSession_state(data.getQueryParameter(SESSION_STATE));
        TrustSSO trustSSO = getInstance();
        trustSSO.setSession_id(data.getQueryParameter(SESSION_ID));
        trustSSO.setSession_state(data.getQueryParameter(SESSION_STATE));
        Hawk.put(AUTHORIZATIONSSO, authorizeSSO);
        return authorizeSSO;
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


    public interface TrustAuthListener {

        void onSucces(String accessToken);

        void onError(String error);
    }


    public static class TrustSSOBuilder {
        private Context context;
        private String clientID;
        private String clientSecret;
        private String scopes;
        private HashMap<String, String> headers;
        private String acrValues;
        private String acrKey;
        private String redirectUri;
        private String baseURL;
        private String grantType;
        private String methodToken;
        private String methodAuthorize;
        private String responseType;
        private String state;

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

        public TrustSSOBuilder setState(String state) {
            if (state != null && !state.equals("")) {
                this.state = state;
            } else {
                TrustLoggerSSO.d("state cannot be null or empty");
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

        public TrustSSOBuilder setHeaders(HashMap<String, String> headers) {
            if (headers != null) {
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
                TrustLoggerSSO.d("Acr Values cannot be null or empty");
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
                Hawk.put(BASE_URL_SSO, baseURL);
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
                instance.state = this.state;
                return instance;

            } catch (Exception ex) {
                return new TrustSSO();
            }

        }
    }
}
