package com.v.eventbus;

import android.app.Application;

public class MyApplication extends Application {
    private static MyApplication mInstance;

    public static MyApplication getAppInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
}
