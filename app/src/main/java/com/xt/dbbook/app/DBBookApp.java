package com.xt.dbbook.app;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.xt.dbbook.Config.EnvInfo;

/**
 * Created by xt on 2018/01/25.
 */

public class DBBookApp extends MultiDexApplication {
    public static Context appContext;
    private Activity currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;

        initEnv();

        initUserInfo();
    }

    private void initUserInfo() {
        DBBookManager.initUserInfo();
    }

    private void initEnv() {
        EnvInfo.checkEnvInfo();
    }

    public static Context getAppContext() {
        return appContext;
    }

    public void setCurrentActivity(Activity activity) {
        currentActivity = activity;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }
}
