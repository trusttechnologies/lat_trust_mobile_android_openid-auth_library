package com.trust.openid.sso;

import android.util.Log;

public class TrustLoggerSSO {


    private static final String TAG = "[TrustLoggerSSO]";

    /**
     * log of the SDK, TAG is TrustLogger
     *
     * @param message
     */
    public static void d(String message) {
        Log.d(TAG, message);
    }

    /**
     * log of the SDK, TAG is TrustLogger
     *
     * @param message
     */
    public static void i(String message) {
        Log.i(TAG, message);
    }

    /**
     * log of the SDK, TAG is TrustLogger
     *
     * @param message
     */
    public static void e(final String message, Throwable e) {
        if (e != null)
            Log.e(TAG, message, e);
        else
            Log.e(TAG, message);
    }
}
