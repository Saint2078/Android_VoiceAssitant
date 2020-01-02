package com.tenke.lib_common;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class ApplicationContextLink {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public static void init(Application application){
        mContext = application.getApplicationContext();
    };

    public static Context LinkToApplication(){
        return mContext;
    }
}

