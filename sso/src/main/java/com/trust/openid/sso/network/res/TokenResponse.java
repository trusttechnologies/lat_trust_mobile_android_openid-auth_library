package com.trust.openid.sso.network.res;

public class TokenResponse {
    private String refresh_token;
    private String token_type;
    private String access_token;
    private String scope;
    private String id_token;
    private int expires_in;

    public TokenResponse() {
    }
}
