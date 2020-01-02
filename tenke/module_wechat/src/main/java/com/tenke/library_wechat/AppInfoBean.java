package com.tenke.library_wechat;

import java.io.Serializable;

public class AppInfoBean implements Serializable {

    private String AppID;
    private int Type;

    public String getAppID() {
        return AppID;
    }

    public void setAppID(String AppID) {
        this.AppID = AppID;
    }

    public int getType() {
        return Type;
    }

    public void setType(int Type) {
        this.Type = Type;
    }

}
