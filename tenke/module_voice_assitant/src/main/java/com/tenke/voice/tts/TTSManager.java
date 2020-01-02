package com.tenke.voice.tts;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import com.tenke.lib_common.ApplicationContextLink;
import java.util.HashMap;
import java.util.Locale;

public class TTSManager extends UtteranceProgressListener {
    private static final String TAG = "TTSManager";
    private static final String PRIVACY = "天科尊重每个人的隐私，" +
            "您所有的原始数据均不会存在网络上传的行为，" +
            "所有的的原始数据处理都会在您家庭中的终端中处理，" +
            "没有任何人有权拿到您的原始数据，包括天科！";

    public static final String UTTERANCEID = "Tenke Demo";
    HashMap<String, String> params;

    TextToSpeech mTextToSpeech = new TextToSpeech(ApplicationContextLink.LinkToApplication(), new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS){
                int supported =mTextToSpeech.setLanguage(Locale.US);
            }
        }
    });


    private TTSManager(){
        params = new HashMap<>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,UTTERANCEID);
    }

    @Override
    public void onStart(String utteranceId) {
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onDone(String utteranceId) {

    }

    @Override
    public void onError(String utteranceId) {

    }

    public void speak(String content){
        mTextToSpeech.speak(content,TextToSpeech.QUEUE_ADD,params);
    }

    public void sayPrivacy(){
        mTextToSpeech.shutdown();
        speak(PRIVACY);
    }


    public static TTSManager getInstance(){
        return Holder.INSTANCE;
    }

    private static final class Holder{
        private static final TTSManager INSTANCE = new TTSManager();
    }

}
