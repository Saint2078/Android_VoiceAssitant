package com.tenke.library_wechat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WechatService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
