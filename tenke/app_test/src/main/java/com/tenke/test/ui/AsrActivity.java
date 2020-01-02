package com.tenke.test.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.tenke.test.R;
import com.tenke.voice.VoiceResult;
import com.tenke.voice.asr.baidu.control.BaiduASRManager;
import com.tenke.voice.asr.common.ASREvent;

import org.json.JSONObject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


public class AsrActivity extends AppCompatActivity {
    TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asr);
        mTextView=findViewById(R.id.result);

        BaiduASRManager.getInstance().startWakeUpSession();
        Logger.t("ASRDEBUG").d("BaiduASRManager.getInstance().startASRSession()");
        BaiduASRManager.getInstance().startASRSession();
        BaiduASRManager.getInstance().listen()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VoiceResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(VoiceResult voiceResult) {
                        mTextView.append(voiceResult.toString());
                        mTextView.append("\n");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e("",e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onStop() {
        BaiduASRManager.getInstance().stopASRSession();
        super.onStop();
    }
}
