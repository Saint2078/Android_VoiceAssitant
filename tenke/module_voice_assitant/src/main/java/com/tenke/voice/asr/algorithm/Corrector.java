package com.tenke.voice.asr.algorithm;

import com.tenke.lib_common.DataManager.DataManager;
import com.tenke.voice.VoiceResult;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Corrector {
    private static final String MAP_KEY = "algo_map";
    private static final String LIST_KEY = "algo_list";
    private static final String BEST_RESULT = "best_result";

    private HashMap<String,String> algoMap = new HashMap<>();
    private LinkedList<String> history = new LinkedList<>();
    private boolean isEnable = true;

    private void init(){
        Object object = DataManager.getInstance().get(MAP_KEY);
        if(object instanceof HashMap){
            algoMap = (HashMap<String, String>) object;
        }

        Object list = DataManager.getInstance().get(LIST_KEY);
        if(list instanceof LinkedList){
            history = (LinkedList<String>) list;
        }
    }

    public VoiceResult excute(VoiceResult voiceResult){
        record(voiceResult);
        VoiceResult after = voiceResult;
        if (algoMap.keySet().contains(voiceResult.getAsrResult())){
            String  result = algoMap.get(voiceResult.getAsrResult());
            after = new VoiceResult(voiceResult,result);
        }
        return after;
    }

    private void record(VoiceResult voiceResult){
        if(history.size() > 500){
            history.remove(0);
        }
        history.add(voiceResult.getAsrResult());
        DataManager.getInstance().put(LIST_KEY,history);
    }

    public List<String> getRecords(){
        return history;
    }



    //单例
    private Corrector(){
        init();
    }
    private static final class Holder{
        private static final Corrector INSTANCE = new Corrector();
    }
    public static Corrector getInstance(){
        return Holder.INSTANCE;
    }


}
