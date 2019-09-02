package com.trust.openid.sso.client;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.util.Base64;
import android.util.Log;

import androidx.browser.customtabs.CustomTabsIntent;

import com.orhanobut.hawk.Hawk;
import com.trust.openid.sso.ConstantsSSO;
import com.trust.openid.sso.model.Authorize;
import com.trust.openid.sso.network.RestClientSSO;
import com.trust.openid.sso.network.res.TokenResponse;

import javax.security.auth.login.LoginException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrustSSO {
    private static final String TAG = TrustSSO.class.getSimpleName();
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


    private TrustSSO() {
    }

    //todo method authorization
    public void authorizationRequest() {
        String url = this.baseURL + this.methodAuthorize
                + "?scope=" + this.scopes
                + "&response_type=code&redirect_uri=" + this.redirecUri
                + "&client_id=" + this.clientID
                + "&acr_values=" + this.acrValues;


        Log.i(TAG, "authorizationRequest: " + url);
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        Bundle headers = new Bundle();
        headers.putString("Autentia-Client-Id", this.clientID);
        customTabsIntent.intent.putExtra(Browser.EXTRA_HEADERS, headers);
        customTabsIntent.launchUrl(this.context, Uri.parse(url));
        /*
         * https://api.autentia.id/oidc/authorize
         * ?scope={{scope}}
         * &response_type={{response_type}}
         * &redirect_uri ={{redirect_uri}}
         * &client_id={{client_id}}
         * &acr_values={{acr_values}}
         * &state={{state}}'
         * */
    }


    public static void retrieveUriResponse(Uri data) {
        Log.i(TAG, "retrieveUriResponse: " + data);

        Authorize authorize = new Authorize();
        authorize.setCode(data.getQueryParameter("code"));
        authorize.setGrantType("authorization_code");
        String a = data.getQueryParameter("scope");
        String b = a.replaceAll(" ", "+");
        authorize.setScope("openid uma_protection profile profile.r profile.w address audit.r audit.w");
        //authorize.setScope(data.getQueryParameter("scope"));
        getToken(authorize);
    }

    private static void getToken(Authorize authorize) {
        try {
            String base = "@!3011.6F0A.B190.8457!0001!294E.B0CD!0008!145D.F522.FFC3.439E:P2qr7PbPR3QxMMRIJwxqWO81";
            byte[] data = base.getBytes("UTF-8");
            String base64 = Base64.encodeToString(data, Base64.DEFAULT);
            RestClientSSO.get().token("Basic QCEzMDExLjZGMEEuQjE5MC44NDU3ITAwMDEhMjk0RS5CMENEITAwMDghMTQ1RC5GNTIyLkZGQzMuNDM5RTpQMnFyN1BiUFIzUXhNTVJJSnd4cVdPODE=",
                    "@!3011.6F0A.B190.8457!0001!294E.B0CD!0008!145D.F522.FFC3.439E",
                    authorize.getGrantType(),
                    authorize.getScope(),
                    authorize.getCode()).enqueue(new Callback<TokenResponse>() {
                @Override
                public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                    Log.i(TAG, "onResponse: " + response.code());
                }

                @Override
                public void onFailure(Call<TokenResponse> call, Throwable t) {
                    Log.i(TAG, "getToken: " + t.getMessage());
                }
            });

          /*  RestClientSSO.get().token2("Basic QCEzMDExLjZGMEEuQjE5MC44NDU3ITAwMDEhMjk0RS5CMENEITAwMDghMTQ1RC5GNTIyLkZGQzMuNDM5RTpQMnFyN1BiUFIzUXhNTVJJSnd4cVdPODE=", "@!3011.6F0A.B190.8457!0001!294E.B0CD!0008!145D.F522.FFC3.439E", authorize).enqueue(new Callback<TokenResponse>() {
                @Override
                public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                    Log.i(TAG, "onResponse: " + response.code());

                }

                @Override
                public void onFailure(Call<TokenResponse> call, Throwable t) {
                    Log.i(TAG, "onFailure:  " + t.getMessage());
                }
            });*/
        } catch (Exception ex) {
            Log.i(TAG, "getToken: " + ex.getMessage());
        }

    }


    //todo method token
    //todo method refresh
    private Bundle[] getHeaders() {
        return this.headers;
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
        private Bundle[] headers;
        private String acrValues;
        private String acrKey;
        private String redirectUri;
        private String baseURL;
        private String grantType;
        private String methodToken;
        private String methodAuthorize;

        public TrustSSOBuilder(Context context) {
            this.context = context;
            Hawk.init(context).build();
        }

        public TrustSSOBuilder setClientID(String clientID) {
            if (clientID != null && !clientID.equals("")) {
                this.clientID = clientID;
            } else {
                //todo log error
            }
            return this;
        }

        public TrustSSOBuilder setClientSecret(String clientSecret) {
            if (clientSecret != null && !clientSecret.equals("")) {
                this.clientSecret = clientSecret;
            } else {
                //todo log error
            }
            return this;
        }

        public TrustSSOBuilder setScopes(String scopes) {
            if (scopes != null && !scopes.equals("")) {
                this.scopes = scopes;
            } else {
                //todo log error
            }
            return this;
        }

        public TrustSSOBuilder setHeaders(Bundle[] headers) {
            if (headers != null && headers.length > 0) {
                this.headers = headers;
            } else {
                //todo log error
            }
            return this;
        }//arreglo

        public TrustSSOBuilder setAcrValues(String acrValues) {
            if (acrValues != null && !acrValues.equals("")) {
                this.acrValues = acrValues;
            } else {
                //todo log error
            }
            return this;
        }

        public TrustSSOBuilder setAcrKey(String acrKey) {
            if (acrKey != null && !acrKey.equals("")) {
                this.acrKey = acrKey;
            } else {
                //todo log error
            }
            return this;
        }

        public TrustSSOBuilder setRedirectUri(String redirectUri) {
            if (redirectUri != null && !redirectUri.equals("")) {
                this.redirectUri = redirectUri;
            } else {
                //todo log error
            }
            return this;
        }

        public TrustSSOBuilder setBaseUrl(String baseURL) {
            if (baseURL != null && !baseURL.equals("")) {
                this.baseURL = baseURL;
                Hawk.put(ConstantsSSO.BASE_URL_SSO, baseURL);
            } else {
                //todo log error
            }
            return this;
        }

        public TrustSSOBuilder setGrantType(String grantType) {
            if (grantType != null && !grantType.equals("")) {
                this.grantType = grantType;
            } else {
                //todo log error
            }
            return this;
        }

        public TrustSSOBuilder setMethodToken(String methodToken) {
            if (methodToken != null && !methodToken.equals("")) {
                this.methodToken = methodToken;
            } else {
                //todo log error
            }
            return this;
        }

        public TrustSSOBuilder setMethodAuthorize(String methodAuthorize) {
            if (methodAuthorize != null && !methodAuthorize.equals("")) {
                this.methodAuthorize = methodAuthorize;
            } else {
                //todo log error
            }
            return this;
        }

        public TrustSSO build() {
            TrustSSO trustSSO = new TrustSSO();
            trustSSO.clientID = this.clientID;
            trustSSO.clientSecret = this.clientSecret;
            trustSSO.acrKey = this.acrKey;
            trustSSO.acrValues = this.acrValues;
            trustSSO.baseURL = this.baseURL;
            trustSSO.grantType = this.grantType;
            trustSSO.headers = this.headers;
            trustSSO.methodAuthorize = this.methodAuthorize;
            trustSSO.methodToken = this.methodToken;
            trustSSO.redirecUri = this.redirectUri;
            trustSSO.scopes = this.scopes;
            trustSSO.context = this.context;
            return trustSSO;
        }
    }
}
