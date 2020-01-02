package com.tenke.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.tenke.voice.asr.baidu.control.BaiduASRManager;
import com.tenke.voice.asr.common.ASREvent;
import com.tenke.voice.tts.TTSManager;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    TextView mTextView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_container);
        mTextView = findViewById(R.id.text);
        mTextView.bringToFront();

        BaiduASRManager.getInstance().registerListener("wp.data", asrEvent -> {
            mTextView.append(asrEvent.getParams());
            TTSManager.getInstance().speak("成功唤醒，请下达下一步指令");
            BaiduASRManager.getInstance().startASRSession();
        });
        BaiduASRManager.getInstance().registerListener("asr.partial", this::addContent);

//        FloatWindow.with(getApplicationContext())
//                .setView(R.layout.float_voice)
//                .setWidth(1080)
//                .setHeight(400)
//                .setY(1880)
//                .setTag("voice")
//                .build();
//        mTextView = FloatWindow.get("voice").getView().findViewById(R.id.voice_text);
//        mScrollView = FloatWindow.get("voice").getView().findViewById(R.id.textScrollView);
//        mScrollView.fullScroll(View.FOCUS_DOWN);
    }

    private void addContent(ASREvent asrEvent){
        try {
            JSONObject jsonObject = new JSONObject(asrEvent.getParams());
            Logger.d(jsonObject.toString());
            mTextView.append("\n"+jsonObject.getString("best_result"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
