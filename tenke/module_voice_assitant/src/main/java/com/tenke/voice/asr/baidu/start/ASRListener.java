package com.tenke.voice.asr.baidu.start;

import com.baidu.speech.EventListener;
import com.orhanobut.logger.Logger;
import com.tenke.voice.asr.common.ASREvent;
import com.tenke.voice.asr.common.ASREventDispatcher;

public class ASRListener implements EventListener {
    private static final String TAG = "ASRListener";

    private ASRListener() {
    }

    public static ASRListener getInstance() {
        return HOLDER.INSTANCE;
    }

    private static final class HOLDER {
        private static final ASRListener INSTANCE = new ASRListener();
    }

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        Logger.t("ASRDEBUG").d("name = " +name);
        Logger.t("ASRDEBUG").d("params = " +params);
        if(data !=null) {
            Logger.t("ASRDEBUG").d("data = " + new String(data));
        }
        Logger.t("ASRDEBUG").d("offset = "+offset );
        Logger.t("ASRDEBUG").d("length = "+ length);

        Logger.t(TAG).d("dispatchEvent: " + name + " | " + params);

        ASREvent asrEvent = new ASREvent(name,params,data,offset,length);
        ASREventDispatcher.getInstance().dispatchEvent(asrEvent);
    }
}
