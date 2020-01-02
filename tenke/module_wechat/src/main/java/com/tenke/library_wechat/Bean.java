package com.tenke.library_wechat;

import com.google.gson.Gson;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * Created by xhpan on 28/09/2017.
 */

public class Bean implements Serializable {
    private String timestamp;
    private long localstamp;

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Object fromJson(String string, Type c) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(string, c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return toJson();
    }

    public void tagTime() {
        localstamp = System.currentTimeMillis();
    }

    public void tagTime(long timestamps) {
        this.localstamp = timestamps;
    }

    public long getTimestamps() {
        return localstamp;
    }

    public boolean isOld(long interval) {
        return (System.currentTimeMillis() - localstamp > interval);
    }

}
