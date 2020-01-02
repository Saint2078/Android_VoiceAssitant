package com.tenke.voice.action;

import java.util.HashMap;
import java.util.List;

public interface  VoiceIntents {

    HashMap<String,Integer> intents = new HashMap<String,Integer>(){
        {
            intents.put("OPEN_APP",1);

        }
    };


}
