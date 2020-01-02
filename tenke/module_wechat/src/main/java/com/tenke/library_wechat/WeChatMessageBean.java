package com.tenke.library_wechat;


import java.util.ArrayList;
import java.util.List;

public class WeChatMessageBean extends Bean {

    private WechatBaseResponseBean BaseResponse;
    private int AddMsgCount;
    private int ModContactCount;
    private int DelContactCount;
    private int ModChatRoomMemberCount;
    private ProfileBean Profile;
    private int ContinueFlag;
    private WeChatSyncKeyBean SyncKey;
    private String SKey;
    private WeChatSyncKeyBean SyncCheckKey;
    private ArrayList<WeChatMessage> AddMsgList;
    private ArrayList<WeChatUserBean> ModContactList;
    private ArrayList<WeChatUserBean> DelContactList;
    private ArrayList<WeChatUserBean> ModChatRoomMemberList;

    public WechatBaseResponseBean getBaseResponse() {
        return BaseResponse;
    }

    public void setBaseResponse(WechatBaseResponseBean BaseResponse) {
        this.BaseResponse = BaseResponse;
    }

    public int getAddMsgCount() {
        return AddMsgCount;
    }

    public void setAddMsgCount(int AddMsgCount) {
        this.AddMsgCount = AddMsgCount;
    }

    public int getModContactCount() {
        return ModContactCount;
    }

    public void setModContactCount(int ModContactCount) {
        this.ModContactCount = ModContactCount;
    }

    public int getDelContactCount() {
        return DelContactCount;
    }

    public void setDelContactCount(int DelContactCount) {
        this.DelContactCount = DelContactCount;
    }

    public int getModChatRoomMemberCount() {
        return ModChatRoomMemberCount;
    }

    public void setModChatRoomMemberCount(int ModChatRoomMemberCount) {
        this.ModChatRoomMemberCount = ModChatRoomMemberCount;
    }

    public ProfileBean getProfile() {
        return Profile;
    }

    public void setProfile(ProfileBean Profile) {
        this.Profile = Profile;
    }

    public int getContinueFlag() {
        return ContinueFlag;
    }

    public void setContinueFlag(int ContinueFlag) {
        this.ContinueFlag = ContinueFlag;
    }

    public WeChatSyncKeyBean getSyncKey() {
        return SyncKey;
    }

    public void setSyncKey(WeChatSyncKeyBean SyncKey) {
        this.SyncKey = SyncKey;
    }

    public String getSKey() {
        return SKey;
    }

    public void setSKey(String SKey) {
        this.SKey = SKey;
    }

    public WeChatSyncKeyBean getSyncCheckKey() {
        return SyncCheckKey;
    }

    public void setSyncCheckKey(WeChatSyncKeyBean SyncCheckKey) {
        this.SyncCheckKey = SyncCheckKey;
    }

    public List<WeChatMessage> getAddMsgList() {
        return AddMsgList;
    }

    public void setAddMsgList(ArrayList<WeChatMessage> AddMsgList) {
        this.AddMsgList = AddMsgList;
    }

    public List<WeChatUserBean> getModContactList() {
        return ModContactList;
    }

    public void setModContactList(ArrayList<WeChatUserBean> ModContactList) {
        this.ModContactList = ModContactList;
    }

    public List<WeChatUserBean> getDelContactList() {
        return DelContactList;
    }

    public void setDelContactList(ArrayList<WeChatUserBean> DelContactList) {
        this.DelContactList = DelContactList;
    }

    public List<WeChatUserBean> getModChatRoomMemberList() {
        return ModChatRoomMemberList;
    }

    public void setModChatRoomMemberList(ArrayList<WeChatUserBean> ModChatRoomMemberList) {
        this.ModChatRoomMemberList = ModChatRoomMemberList;
    }
}
