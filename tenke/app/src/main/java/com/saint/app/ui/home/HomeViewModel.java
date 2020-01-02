package com.saint.app.ui.home;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.Observer;

import com.tenke.lib_common.mvvm.BaseViewModel;
import com.tenke.voice.VoiceResult;
import com.tenke.voice.action.ActionExcecutor;
import com.tenke.voice.asr.baidu.control.BaiduASRManager;
import com.tenke.voice.asr.common.ASRState;

import io.reactivex.disposables.Disposable;

public class HomeViewModel extends BaseViewModel {

    public ObservableField<String>  url = new ObservableField<>();
    public ObservableField<String>  voiceText = new ObservableField<>();
    public ObservableField<Boolean>  recording = new ObservableField<>();


    final Observer<ASRState> mASRStateObserver = new Observer<ASRState>() {
        @Override
        public void onChanged(ASRState asrState) {
            switch (asrState){
                case LISTEN_START:
                    recording.set(true);
                    break;
                case LISTEN_END:
                    recording.set(false);
                    break;
            }
        }
    };

    public HomeViewModel(@NonNull Application application) {
        super(application);
        url.set("file:///android_asset/demo.html");
        BaiduASRManager.getInstance().listen().subscribe(new io.reactivex.Observer<VoiceResult>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(VoiceResult voiceResult) {
                voiceText.set(voiceResult.getAsrResult());
                ActionExcecutor.getInstance().excecute(voiceResult);
            }

            @Override
            public void onError(Throwable e) {
                voiceText.set(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void startRecord(){
        BaiduASRManager.getInstance().startASRSession();
    }

    public void stopRecord(){
        BaiduASRManager.getInstance().stopASRSession();
    }

}
