package com.tenke.demo;

import java.util.HashMap;

/**
 * 如何设置标注的意思呢？
 * 使用“我是说”吧，先不引入NLP，可能还是需要用到吧
 */
public class DemoCase {
    private static final HashMap<String,String> cases= new HashMap<>();
    //NLP模块的设计
    static {
        //日常：方便和快捷，展示技术能力，展示这种个人订制方案有多么大的市场
        //首先是展示技术，做一些非常简单的展示，展示这个技术的优越性
        //衣食住行的问题
        cases.put("明天周末，看看上海有什么好玩的","这个周末在上海环球金融中心有如海空间的展览，非常符合您的猎奇口味和感官享受，需要我安排吗");
        cases.put("给我订一张下午6点的票","收到，正在订购中,已完成");
        cases.put("明天早起，设置一个6点的闹钟","闹钟设置完成");
        cases.put("打给我的合伙人","请问是哪位合伙人呢");
        cases.put("区块链合伙人","正在拨打");
        cases.put("添加一个日程：明天晚上7点约了投资人","日程安排完毕，我会提前半小时通知您");
        cases.put("介绍方案","");
        //展示一下隐私
        cases.put("保护隐私","Tenke采用");
        //智能家居类
        cases.put("","");
        cases.put("检查冰箱","智能冰箱模块开启，检测中，");
        cases.put("让附近的市场送点过来吧","智能冰箱模块开启，检测中，");
        cases.put("进入节能模式","智能冰箱模块开启，检测中，");
        //金融模块
        cases.put("我睡觉的这段时间里股市怎么样","");
        cases.put("给我分析一下这支股票","");
        cases.put("昨天出租算力的收入怎么样","");
        //教育
        //额外的模块
        cases.put("介绍一下XXX","");
        cases.put("那麻烦你把预算也给投资人看一下吧","");
    }

    public static HashMap loadCase(){
        return cases;
    }
}
