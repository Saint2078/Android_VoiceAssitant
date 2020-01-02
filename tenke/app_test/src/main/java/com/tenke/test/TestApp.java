package com.tenke.test;

import android.app.Application;

import com.tenke.lib_common.ApplicationContextLink;
import com.tenke.lib_common.LoggerUtil;
import com.tenke.voice.asr.baidu.control.BaiduASRManager;

public class TestApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationContextLink.init(this);
        LoggerUtil.init("TENKEtest",true,false);

    }
}
