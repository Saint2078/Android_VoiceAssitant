package com.saint.app;

import android.app.Application;

import com.tenke.lib_common.ApplicationContextLink;
import com.tenke.lib_common.LoggerUtil;
import com.tenke.voice.asr.baidu.control.BaiduASRManager;

public class TenkeApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationContextLink.init(this);
        LoggerUtil.init("saintApp",true,false);

        BaiduASRManager.getInstance().startWakeUpSession();
    }
}
