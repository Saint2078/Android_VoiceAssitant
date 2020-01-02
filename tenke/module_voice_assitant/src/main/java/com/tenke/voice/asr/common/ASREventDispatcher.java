package com.tenke.voice.asr.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ASREventDispatcher {
    private static final String TAG = "ASREventDispatcher";
    private HashMap<String, List<ASREventReceiver>> maps = new HashMap<>();

    private static class Holder{
        private static final ASREventDispatcher INSTANCE = new ASREventDispatcher();
    }

    private ASREventDispatcher(){

    }

    public static ASREventDispatcher getInstance(){
        return Holder.INSTANCE;
    }

    public void registerReceiver(String type,ASREventReceiver receiver){
        if(!maps.containsKey(type)){
            maps.put(type,new ArrayList<ASREventReceiver>());
        }
        maps.get(type).add(receiver);
    }
    public void registerReceiver(String[] types,ASREventReceiver receiver){
        for (String type : types){
            if(!maps.containsKey(type)){
                maps.put(type,new ArrayList<ASREventReceiver>());
            }
            maps.get(type).add(receiver);
        }
    }


    public void dispatchEvent(ASREvent asrEvent){
        if(maps.containsKey(null)){
            for(ASREventReceiver receiver : Objects.requireNonNull(maps.get(null))){
                receiver.onEvent(asrEvent);
            }
        }

        if (!maps.containsKey(asrEvent.getName())){
            return;
        }

        List<ASREventReceiver> receivers = maps.get(asrEvent.getName());
        for(ASREventReceiver receiver : Objects.requireNonNull(receivers)){
            receiver.onEvent(asrEvent);
        }
    }
}
