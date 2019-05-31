package com.eldarja.ping.helpers;

import android.app.Application;
import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * Registered in manifest
 */
public class WeakRefApp extends Application {
    private static WeakReference<Context> context;

    public static Context getContext() { return context.get(); }

    @Override
    public void onCreate() {
        super.onCreate();
        context = new WeakReference<Context>(getApplicationContext());
    }
}
