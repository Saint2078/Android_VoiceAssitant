package com.tenke.library_wechat;

import android.content.SharedPreferences;

public class WeChatLocalDataSource {

    private static final String PERSISTENT_DIRECTORY_WECHAT_USER_PATH = Constants.STORAGE_PATH + "/wechat/user/";
    private static final String PERSISTENT_FILE_NAME_WECHATAUTHBEAN = "WeChatAuthBean";
    private static final String SP_KEY_AUTO_PLAY_MSG = "isAutoPlayWeChatMessage";

    private SharedPreferences mSharedPreferences;

    public WeChatLocalDataSource(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    public void saveAuthBean(WeChatAuthenticationBean authBean) {
        StorageUtil.saveToCache(authBean, PERSISTENT_FILE_NAME_WECHATAUTHBEAN, PERSISTENT_DIRECTORY_WECHAT_USER_PATH);
    }

    public WeChatAuthenticationBean getAuthBean() {
        return (WeChatAuthenticationBean) StorageUtil.readFromCache(PERSISTENT_FILE_NAME_WECHATAUTHBEAN,
                PERSISTENT_DIRECTORY_WECHAT_USER_PATH);
    }

    public boolean deleteAuthBean() {
        return StorageUtil.removeFromCache(PERSISTENT_FILE_NAME_WECHATAUTHBEAN, PERSISTENT_DIRECTORY_WECHAT_USER_PATH);
    }

    public boolean isAutoPlayWeChatMessage() {
        return mSharedPreferences.getBoolean(SP_KEY_AUTO_PLAY_MSG, true);
    }

    public void setAutoPlayWeChatMessage(boolean autoPlayWeChatMessage) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(SP_KEY_AUTO_PLAY_MSG, autoPlayWeChatMessage);
        editor.apply();
    }
}
