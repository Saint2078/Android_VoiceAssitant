package com.tenke.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tenke.lib_common.DataManager.DataManager;
import com.tenke.voice.tts.TTSManager;

import java.util.ArrayList;
import java.util.List;


/**
 * equal 纠正/你听错了：进入标注模式
 * equal 打开历史
 * equal 你怎么保护我的隐私 contain 隐私
 * 不演示nlp能力
 */
public class CommandReceiver extends BroadcastReceiver {
    private String lastCommand = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        String command = intent.getStringExtra("COMMAND");
        if(command.equals("纠正")
                || command.equals("你听错了")){
            List<String> list = (List<String>) DataManager.getInstance().get(AsrMap.HISTORY_KEY);
            if(list == null){
                list = new ArrayList<>();
                DataManager.getInstance().put(AsrMap.HISTORY_KEY,list);
            }
//            FloatWindowManager.getInstance().showCorrWindows(list.get(list.size()-1),"");
        }else if(command.equals("打开历史")){
//            FloatWindowManager.getInstance().showHistory();
        }else if(command.equals("你怎么保护我的隐私")
                || command.contains("隐私")){
            TTSManager.getInstance().sayPrivacy();
        }
    }

    private CommandReceiver(){
        AsrMap.getInstance().registerBroadCast(this);
    }

    public static CommandReceiver getInstance(){
        return Holder.INSTANCE;
    }

    private static final class Holder{
        private static final CommandReceiver INSTANCE = new CommandReceiver();
    }
}
