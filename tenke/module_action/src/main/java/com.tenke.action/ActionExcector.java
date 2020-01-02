package com.tenke.action;

import android.speech.tts.Voice;


public class ActionExcector {

    private ActionExcector(){ }

    public static ActionExcector getInstance(){
        return Holder.INSTANCE;
    }

    private static final class Holder{
        private static final ActionExcector INSTANCE = new ActionExcector();
    }

    public void excector(){

    }

//    private Intents getIntent(){
//
//    }
}
