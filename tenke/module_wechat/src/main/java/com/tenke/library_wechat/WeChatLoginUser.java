package com.tenke.library_wechat;

import android.text.TextUtils;

import com.google.gson.GsonBuilder;

public class WeChatLoginUser {

    private WeChatUserBean mWeChatUserBean;

    private long id = 1;
    private long uin;
    private String wechatUser;

    public WeChatLoginUser(long id, long uin, String wechatUser) {
        this.id = id;
        this.uin = uin;
        this.wechatUser = wechatUser;
    }

    public WeChatLoginUser(long uin) {
        this.uin = uin;
    }

    public WeChatLoginUser() {
    }


    public WeChatUserBean getWechatUserBean() {
        if (!TextUtils.isEmpty(wechatUser) && mWeChatUserBean == null) {
            mWeChatUserBean = new GsonBuilder().create().fromJson(wechatUser, WeChatUserBean.class);
        }
        return mWeChatUserBean;
    }

    public void setWeChatUserBean(WeChatUserBean weChatUserBean) {
        mWeChatUserBean = weChatUserBean;
        wechatUser = mWeChatUserBean.toJson();
    }

    public long getUin() {
        return this.uin;
    }

    public void setUin(long uin) {
        this.uin = uin;
    }

    public String getWechatUser() {
        return this.wechatUser;
    }

    public void setWechatUser(String wechatUser) {
        this.wechatUser = wechatUser;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
