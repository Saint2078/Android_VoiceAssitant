package com.tenke.lib_common;


import android.media.AudioManager;

import com.orhanobut.logger.Logger;

import java.util.concurrent.TimeUnit;

import io.reactivex.schedulers.Schedulers;

public class DuckingManager implements Runnable {
    private static final String TAG = "DuckingManager";

    private static final int DUCKING_DURATION = 200;
    private static final int DUCKING_INTERVALS = 50;
    private static final int DUCKING_COUNT = DUCKING_DURATION / DUCKING_INTERVALS;

    private static final float duckingLevel = 0.75f;
    private int originalVolume = 0;
    private int currentStep = -1;

    private AudioManager mAudioManager;
    private int streamType;
    private boolean ducking;


    public DuckingManager(AudioManager audioManager, int type) {
        mAudioManager = audioManager;
        streamType = type;
    }

    public synchronized void start() {
        if (currentStep == -1) {
            originalVolume = mAudioManager.getStreamVolume(streamType);
            Logger.t(TAG).d("set originalVolume = " + originalVolume);
            Schedulers.io().scheduleDirect(this);
        }
        currentStep = 1;
        ducking = true;
    }

    public synchronized void resume() {
        if (currentStep > 0) {
            ducking = false;
            currentStep = DUCKING_COUNT - 1;
            Schedulers.io().scheduleDirect(this);
        }
    }

    @Override
    public synchronized void run() {
        int volume = (int) (originalVolume * (1 - duckingLevel * currentStep / DUCKING_COUNT));
        mAudioManager.setStreamVolume(streamType, volume, 0);
        Logger.t(TAG).d("set volume = " + volume + " currentStep = " + currentStep + " direction = " + ducking);

        if (currentStep > 0 && currentStep < DUCKING_COUNT) {
            Schedulers.io().scheduleDirect(this, DUCKING_INTERVALS, TimeUnit.MILLISECONDS);
        }
        currentStep += (ducking ? 1 : -1);
    }
}

