package com.tenke.library_wechat;

import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.orhanobut.logger.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import okhttp3.HttpUrl;

public class WeChatRepository {
    private static final String TAG = WeChatRepository.class.getSimpleName();

    private static final String WECHAT_GROUP_USER_PREFIX = "@@";

    private WeChatLocalDataSource mWeChatLocalDataSource;
    private CopyOnWriteArrayList<ChatData> mChatDataList = new CopyOnWriteArrayList<>();

    private Map<String, String> mUserNameNickNameMap = new HashMap<>();
    private Multimap<String, String> mNickNameUserNameMap = HashMultimap.create();

    private Map<String, Bean> mContactsDetailInfoMap = new HashMap<>();  //WeChatContactVo && WeChatUserBean

    private WeChatLoginUser mWeChatLoginUser;
    private WeChatAuthenticationBean mWeChatAuthenticationBean;
    private boolean loginStatus = false;

    public WeChatRepository(SharedPreferences sharedPreferences) {
        mWeChatLocalDataSource = new WeChatLocalDataSource(sharedPreferences);
    }

    public void saveAuthBean(WeChatAuthenticationBean weChatAuthenticationBean) {
        mWeChatAuthenticationBean = weChatAuthenticationBean;
        mWeChatLocalDataSource.saveAuthBean(weChatAuthenticationBean);
    }

    public WeChatAuthenticationBean getAuthBean() {
        if (mWeChatAuthenticationBean == null) {
            mWeChatAuthenticationBean = mWeChatLocalDataSource.getAuthBean();
        }
        return mWeChatAuthenticationBean;
    }

    public void saveWeChatLoginUser(WeChatLoginUser wechatUserBean) {
        loginStatus = true;
        mWeChatLoginUser = wechatUserBean;
    }

    public WeChatLoginUser getWeChatLoginUser() {
        return mWeChatLoginUser;
    }

    public synchronized void clearWeChatData() {
        Logger.t(TAG).d("clearWeChatData");
        loginStatus = false;
        mWeChatAuthenticationBean = null;
        mWeChatLoginUser = null;
        mUserNameNickNameMap.clear();
        mNickNameUserNameMap.clear();
        mContactsDetailInfoMap.clear();
        mChatDataList.clear();
        mWeChatLocalDataSource.deleteAuthBean();
    }

    public synchronized void saveWeChatContacts(final List<WeChatUserBean> userBeans, boolean isFromConversation) {
        for (final WeChatUserBean userBeen : userBeans) {
            mContactsDetailInfoMap.put(userBeen.getUserName(), userBeen);
            mUserNameNickNameMap.put(userBeen.getUserName(),
                    TextUtils.isEmpty(userBeen.getRemarkName()) ? userBeen.getNickName() : userBeen.getRemarkName());

            if (Strings.isNullOrEmpty(userBeen.getRemarkName())) {
                if (!Strings.isNullOrEmpty(userBeen.getNickName())) {
                    mNickNameUserNameMap.put(userBeen.getNickName(), userBeen.getUserName());
                }
            } else {
                mNickNameUserNameMap.put(userBeen.getRemarkName(), userBeen.getUserName());
            }
        }
    }

    public synchronized void saveWeChatConversationContacts(List<WeChatContactVo> weChatContactVos) {
        for (final WeChatContactVo contactVo : weChatContactVos) {
            if (contactVo.getUserName().contains(WECHAT_GROUP_USER_PREFIX)) {
                mContactsDetailInfoMap.put(contactVo.getUserName(), contactVo);
                mUserNameNickNameMap.put(contactVo.getUserName(),
                        TextUtils.isEmpty(contactVo.getRemarkName()) ? contactVo.getNickName() : contactVo.getRemarkName());

                if (Strings.isNullOrEmpty(contactVo.getRemarkName())) {
                    if (!Strings.isNullOrEmpty(contactVo.getNickName())) {
                        mNickNameUserNameMap.put(contactVo.getNickName(), contactVo.getUserName());
                    }
                } else {
                    mNickNameUserNameMap.put(contactVo.getRemarkName(), contactVo.getUserName());
                }
                saveWeChatContacts(contactVo.getMemberList(), true);
            }
        }
    }

    /**
     * Get contact information of the specified user.
     *
     * @param userName
     * @return Using Bean here cause we have two kind of data type return from WeChat json.
     * WeChatContactVo for users we load from your wechat conversations
     * WeChatUserBean for users we load from your wechat contacts
     */
    public synchronized Bean getContactBean(String userName) {
        return mContactsDetailInfoMap.get(userName);
    }

    public synchronized String getNickOrRemarkNameByUserName(String userName) {
        return mUserNameNickNameMap.get(userName);
    }

    public synchronized Collection<String> getUserNameByNickOrRemarkName(String nickName) {
        return new HashSet<>(mNickNameUserNameMap.get(nickName));
    }

    public synchronized Set<String> getNickNameList() {
        return new HashSet<>(mNickNameUserNameMap.keySet());
    }

    public boolean isLogin() {
        return loginStatus;
    }

    public boolean isAutoPlayWeChatMessage() {
        return mWeChatLocalDataSource.isAutoPlayWeChatMessage();
    }

    public void setAutoPlayWeChatMessage(boolean autoPlayWeChatMessage) {
        mWeChatLocalDataSource.setAutoPlayWeChatMessage(autoPlayWeChatMessage);
    }

    public synchronized ChatData saveNewMessage(ChatContentItem contentItem) {
        String chatId = contentItem.getChatId();
        if (chatId == null) return new ChatData();
        for (ChatData chatData : mChatDataList) {
            if (chatId.equals(chatData.getChatId())) {
                chatData.addChatContentItem(contentItem);
                mChatDataList.remove(chatData);
                mChatDataList.add(0, chatData);
                return chatData;
            }
        }

        ChatData newData = new ChatData();
        newData.setChatId(contentItem.getChatId());
        newData.setChatName(getNickOrRemarkNameByUserName(contentItem.getChatId()));
        newData.setAvatarUrl(getAvatarUrlByUserName(contentItem.getChatId()));
        newData.addChatContentItem(contentItem);
        mChatDataList.add(0, newData);
        return newData;
    }

    public synchronized void saveNewChat(ChatData chatData) {
        if (chatData == null) return;
        mChatDataList.addIfAbsent(chatData);
    }

    @NonNull
    public synchronized List<ChatData> getChatDataList() {
        return mChatDataList;
    }

    /**
     * Get full url string from BaseUrl and relative avatar path
     *
     * @param avatarPath relative avatar path return from WeChatUserBean or WeChatContactVo
     * @return Full url string or "" otherwise
     */
    @NonNull
    public String getAvatarUrl(@Nullable String avatarPath) {
        String avatarUrl = null;
        if (!Strings.isNullOrEmpty(avatarPath)) {
            try {
                HttpUrl httpUrl = HttpUrl.parse(mWeChatAuthenticationBean.getBaseUrl());
                if (httpUrl != null) {
                    avatarUrl = httpUrl.scheme() + "://" + httpUrl.host() + avatarPath;
                }
            } catch (Throwable e) {
                Logger.t(TAG).d(e.toString());
            }
        }
        return Strings.nullToEmpty(avatarUrl);
    }

    /**
     * Get full url string with userName
     *
     * @param userName user id returned from wechat server
     * @return Full url string or "" otherwise
     */
    @NonNull
    public String getAvatarUrlByUserName(@Nullable String userName) {
        if (Strings.isNullOrEmpty(userName)) return "";
        Bean bean = getContactBean(userName);
        String avatarUrl = "";
        if (bean instanceof WeChatUserBean) {
            avatarUrl = ((WeChatUserBean) bean).getHeadImgUrl();
        } else if (bean instanceof WeChatContactVo) {
            avatarUrl = ((WeChatContactVo) bean).getHeadImgUrl();
        }
        if (Strings.isNullOrEmpty(avatarUrl)) {
            avatarUrl = getGroupNonFriendUserAvatar(userName);
        }
        return getAvatarUrl(avatarUrl);
    }

    /**
     * Get group message user's avatar who is not my friend.
     * Cause wechat server don't return detail data of those people.
     *
     * @param userName
     * @return
     */
    private String getGroupNonFriendUserAvatar(String userName) {
        String relativeUrl;
        if (userName.contains(WECHAT_GROUP_USER_PREFIX)) {
            relativeUrl = "/cgi-bin/mmwebwx-bin/webwxgetheadimg?";
        } else {
            relativeUrl = "/cgi-bin/mmwebwx-bin/webwxgeticon?";
        }
        relativeUrl += "username=" + userName + "&skey=" + mWeChatAuthenticationBean.getSkey();
        return relativeUrl;
    }
}
