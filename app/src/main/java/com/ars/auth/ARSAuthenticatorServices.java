package com.ars.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class ARSAuthenticatorServices extends Service {

    private ARSAuthenticator authenticator;
    private static final Object mAuthenticatorLock = new Object();

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (mAuthenticatorLock) {
            if (authenticator == null) {
                authenticator = new ARSAuthenticator(getApplicationContext());
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
