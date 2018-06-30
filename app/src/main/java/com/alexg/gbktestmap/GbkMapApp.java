package com.alexg.gbktestmap;

import android.app.Application;
import android.content.Context;

public class GbkMapApp extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        GbkMapApp.sContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return GbkMapApp.sContext;
    }

}
