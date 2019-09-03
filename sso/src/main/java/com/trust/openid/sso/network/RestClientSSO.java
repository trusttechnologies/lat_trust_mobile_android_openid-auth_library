package com.trust.openid.sso.network;

import com.orhanobut.hawk.Hawk;
import com.trust.openid.sso.client.ConstantsSSO;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClientSSO {

    private static final int CONNECT_TIMEOUT = 30 * 4;
    private static final int WRITE_TIMEOUT = 30 * 4;
    private static final int READ_TIMEOUT = 30 * 4;
    private static APISSO api;

    static {
        setup();
    }

    /**
     * Class constructor
     */
    private RestClientSSO() {
    }

    /**
     * Obtain the access of all API methods
     *
     * @return API reference
     */
    public static APISSO get() {
        return api;
    }

    /**
     * Default setup for OkHttp and Retrofit
     */
    private static void setup() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Hawk.contains(ConstantsSSO.BASE_URL_SSO) ? Hawk.get(ConstantsSSO.BASE_URL_SSO).toString() : "") // PROD O TEST
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        api = retrofit.create(APISSO.class);
    }
}
