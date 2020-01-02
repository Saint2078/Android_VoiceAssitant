package com.tenke.app;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

import com.orhanobut.logger.Logger;
import com.tenke.lib_common.ApplicationContextLink;
import com.tenke.lib_common.DataManager.DataManager;
import com.tenke.voice.asr.baidu.control.BaiduASRManager;
import com.tenke.voice.asr.common.ASREvent;
import com.tenke.voice.asr.common.ASREventReceiver;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AsrMap implements ASREventReceiver {
    private static final String MAP_KEY = "Voice Map";
    public static final String HISTORY_KEY = "history";

    private HashMap<String,String> mHashMap;
    private boolean openMap = true;

    private AsrMap(){
        BaiduASRManager.getInstance().registerListener("asr.partial",this);
        mHashMap = (HashMap<String, String>) DataManager.getInstance().get(MAP_KEY);
        if(mHashMap ==null){
            mHashMap =new HashMap<>();
            DataManager.getInstance().put(MAP_KEY,mHashMap);
        }
    }

    @Override
    public void onEvent(ASREvent asrEvent){
        try {
            JSONObject jsonObject  = new JSONObject(asrEvent.getParams());
            String result = jsonObject.getString("best_result");
            String command = null;
            if(openMap && mHashMap.containsKey(result)){
                command = mHashMap.get(result);
            }
            List<String> list = (List<String>) DataManager.getInstance().get(HISTORY_KEY);
            if(list == null){
                list = new ArrayList<>();
                DataManager.getInstance().put(HISTORY_KEY,list);
            }
            list.add(command);
            sendCommand(command);
        }catch (Exception e){
            Logger.e("",e);
        }
    }

    public void setOpenMap(boolean openMap) {
        this.openMap = openMap;
    }

    private void sendCommand(String command){
        final String keyAndAction = "COMMAND";
        Intent intent = new Intent(keyAndAction);
        intent.putExtra(keyAndAction,command);
        ApplicationContextLink.LinkToApplication().sendBroadcast(intent);
    }

    public void addPairs(String origin,String result){
        mHashMap.put(origin,result);
    }

    public void registerBroadCast(BroadcastReceiver broadcastReceiver){
        final String keyAndAction = "COMMAND";
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(keyAndAction);
        ApplicationContextLink.LinkToApplication().registerReceiver(broadcastReceiver,intentFilter);
    }

    public static AsrMap getInstance(){
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final AsrMap INSTANCE = new AsrMap();
    }
}
