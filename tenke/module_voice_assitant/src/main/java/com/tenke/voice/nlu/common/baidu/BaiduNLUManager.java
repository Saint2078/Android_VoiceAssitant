package com.tenke.voice.nlu.common.baidu;

public class BaiduNLUManager {

    private BaiduNLUManager(){}

    public static BaiduNLUManager getInstance(){
        return Holder.INSTANCE;
    }

    private static final class Holder{
        private static final BaiduNLUManager INSTANCE = new BaiduNLUManager();
    }

}
