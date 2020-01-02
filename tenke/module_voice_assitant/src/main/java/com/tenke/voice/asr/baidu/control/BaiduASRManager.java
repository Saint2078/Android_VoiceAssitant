package com.tenke.voice.asr.baidu.control;

import android.media.AudioManager;
import android.media.MediaPlayer;

import androidx.lifecycle.MutableLiveData;

import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.google.gson.Gson;
import com.tenke.lib_common.ApplicationContextLink;
import com.tenke.voice.R;
import com.tenke.voice.VoiceResult;
import com.tenke.voice.asr.algorithm.Corrector;
import com.tenke.voice.asr.common.ASREvent;
import com.tenke.voice.asr.common.ASREventDispatcher;
import com.tenke.voice.asr.common.ASREventReceiver;
import com.tenke.voice.asr.common.ASRState;
import com.tenke.voice.asr.common.ASRStatusController;
import com.tenke.voice.asr.baidu.start.ASRListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class BaiduASRManager {

    private final EventManager mWakeUpManager;
    private final EventManager mAsr;



    private BaiduASRManager(){
        mWakeUpManager = EventManagerFactory.create(ApplicationContextLink.LinkToApplication(),"wp");
        mWakeUpManager.registerListener(ASRListener.getInstance());

        HashMap<String, Object> params = new HashMap<>();
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        mWakeUpManager.send(SpeechConstant.WAKEUP_START, new JSONObject(params).toString(), null, 0, 0);

        mAsr = EventManagerFactory.create(ApplicationContextLink.LinkToApplication(), "asr");
        mAsr.registerListener(ASRListener.getInstance());
        //loadOfflineEngine();

        ASREventDispatcher.getInstance()
                .registerReceiver(new String[]{"wp.data", "wp.exit", "wp.error", "wp.ready", "asr.ready", "asr.end"},BaiduASRStatusReceiver.getInstance());
    }

    public static BaiduASRManager getInstance(){
        return Holder.INSTANCE;
    }

    private static final class Holder{
        private static final BaiduASRManager INSTANCE = new BaiduASRManager();
    }

    public Observable<VoiceResult> listen(){
       return Observable.create(new ObservableOnSubscribe<VoiceResult>() {
           @Override
           public void subscribe(final ObservableEmitter<VoiceResult> emitter) throws Exception {
               ASREventDispatcher.getInstance().registerReceiver("asr.partial", new ASREventReceiver() {
                   @Override
                   public void onEvent(ASREvent asrEvent) {
                       if("asr.partial".equals(asrEvent.getName()) && asrEvent.getParams().contains("nlu_result")){
                           String data = new String(asrEvent.getData());
                           Gson gson = new Gson();
                           VoiceResult.ASRBean asrBean = gson.fromJson(data,VoiceResult.ASRBean.class);
                           VoiceResult voiceResult = new VoiceResult(asrBean);
                           voiceResult = Corrector.getInstance().excute(voiceResult);
                           emitter.onNext(voiceResult);
                       }
                   }
               });
           }
       });
    }

    public void startASRSession() {
        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                MediaPlayer mediaPlayer = MediaPlayer.create(ApplicationContextLink.LinkToApplication(), R.raw.bdspeech_recognition_start);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setLooping(false);
                mediaPlayer.start();
            }
        });

        String json = new JSONObject(getParams()).toString();
        mAsr.send(SpeechConstant.ASR_START, json, null, 0, 0);
    }

    public void stopASRSession() {
        mAsr.send(SpeechConstant.ASR_STOP, "{}", null, 0, 0);
    }

    public void cancelASRSession() {
        mAsr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
    }

    public void destroyASR() {
        if (mAsr != null) mAsr.unregisterListener(ASRListener.getInstance());
    }

    public void startWakeUpSession() {
        HashMap<String, Object> params = new HashMap<>();
        params.put(SpeechConstant.APP_ID, "15432712");
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        mWakeUpManager.send(SpeechConstant.WAKEUP_START, new JSONObject(params).toString(), null, 0, 0);
    }

    public void stopWakeUpSession() {
        mWakeUpManager.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0);
    }
    public void destoryWakeUp() {

    }

    private Map<String, Object> getParams() {
        Map<String, Object> params = new HashMap<>();
//        params.put(SpeechConstant.SOUND_START, R.raw.bdspeech_recognition_start);
//        params.put(SpeechConstant.SOUND_END, R.raw.bdspeech_speech_end);
//        params.put(SpeechConstant.SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
        params.put(SpeechConstant.SOUND_ERROR, R.raw.bdspeech_recognition_error);
        params.put(SpeechConstant.SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);
        params.put("pid", "15363");
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0);
        params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
        params.put(SpeechConstant.DECODER, 0);
        params.put(SpeechConstant.ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH, "assets:///baidu_speech_grammar.bsg");
        params.put(SpeechConstant.NLU, "enable");
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
//        if (!mVoiceServiceProvider.isSessionStarted()) {
//            params.put(SpeechConstant.AUDIO_MILLS, System.currentTimeMillis() - BACK_TRACK_IN_MILLISECOND);
//        }
        return params;
    }

    private void loadOfflineEngine() {
        Map<String, Object> params = new HashMap<>();
        params.put(SpeechConstant.DECODER, 2);
        params.put(SpeechConstant.ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH, "assets:///baidu_speech_grammar.bsg");
        String json = new JSONObject(params).toString();
        mAsr.send(SpeechConstant.ASR_KWS_LOAD_ENGINE, json, null, 0, 0);
    }

    public void registerListener(String type, ASREventReceiver receiver){
        ASREventDispatcher.getInstance().registerReceiver(type,receiver);
    }

    public ASRState getState(){
        return ASRStatusController.getInstance().getASRStatus();
    }
}

