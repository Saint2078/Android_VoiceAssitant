package com.tenke.test.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.tenke.call.CallManager;
import com.tenke.test.R;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.asr:
                startActivity(new Intent(this,AsrActivity.class));
                break;
            case R.id.tts:
                startActivity(new Intent(this,TTSActivity.class));
                break;
            case R.id.call:
                CallManager.getInstance().callByName("老妈");
                break;
        }
    }
}
