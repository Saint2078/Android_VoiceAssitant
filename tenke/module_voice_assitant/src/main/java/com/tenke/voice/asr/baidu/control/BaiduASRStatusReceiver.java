package com.tenke.voice.asr.baidu.control;

import com.tenke.voice.asr.common.ASREvent;
import com.tenke.voice.asr.common.ASREventReceiver;
import com.tenke.voice.asr.common.ASRState;
import com.tenke.voice.asr.common.ASRStatusController;

public class BaiduASRStatusReceiver implements ASREventReceiver {

    private BaiduASRStatusReceiver(){ }

    public static BaiduASRStatusReceiver getInstance(){
        return Holder.INSTANCE;
    }

    @Override
    public void onEvent(ASREvent asrEvent) {
        if("wp.data".equals(asrEvent.getName())){
            ASRStatusController.getInstance().setState(ASRState.WAKEUP);
        }else if("wp.exit".equals(asrEvent.getName())){
            ASRStatusController.getInstance().setState(ASRState.IDLE);
        }else if("wp.error".equals(asrEvent.getName())){
            ASRStatusController.getInstance().setState(ASRState.ERROR);
        }else if("wp.ready".equals(asrEvent.getName())){
            ASRStatusController.getInstance().setState(ASRState.IDLE);
        }else if("asr.ready".equals(asrEvent.getName())){
            ASRStatusController.getInstance().setState(ASRState.LISTEN_START);
        }else if("asr.end".equals(asrEvent.getName())){
            ASRStatusController.getInstance().setState(ASRState.LISTEN_END);
        }
    }

    private static final class Holder{
        private static final BaiduASRStatusReceiver INSTANCE = new BaiduASRStatusReceiver();
    }
}
