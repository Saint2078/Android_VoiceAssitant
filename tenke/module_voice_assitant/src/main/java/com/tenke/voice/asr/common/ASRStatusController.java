package com.tenke.voice.asr.common;

import androidx.lifecycle.MutableLiveData;

public class ASRStatusController {

    private ASRState mASRState = ASRState.IDLE;
    public final MutableLiveData<ASRState> mASRStateMutableLiveData = new MutableLiveData<>();

    private ASRStatusController(){
        mASRStateMutableLiveData.setValue(mASRState);
    }

    public static ASRStatusController getInstance(){
        return Holder.INSTANCE;
    }

    private static final class Holder{
        private static final ASRStatusController INSTANCE = new ASRStatusController();
    }

    public void setState(ASRState state){
        mASRState = state;
        mASRStateMutableLiveData.setValue(state);
    }

    public ASRState getASRStatus(){
        return mASRState;
    }
}
