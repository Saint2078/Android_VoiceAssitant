package com.tenke.library_wechat;

public class WechatBaseResponseBean extends Bean {

    private int Ret;
    private String ErrMsg;

    public boolean isSuccess() {
        return Ret == 0;
    }

    public int getRet() {
        return Ret;
    }

    public void setRet(int Ret) {
        this.Ret = Ret;
    }

    public String getErrMsg() {
        return ErrMsg;
    }

    public void setErrMsg(String ErrMsg) {
        this.ErrMsg = ErrMsg;
    }
}
