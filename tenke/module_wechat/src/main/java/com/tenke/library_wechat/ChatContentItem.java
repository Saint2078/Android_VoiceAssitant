package com.tenke.library_wechat;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;


public class ChatContentItem implements Parcelable {

    private String chatId = "";

    private String userId = "";

    private String userNickName = "";

    private String groupId = "";

    private String avatarUri = "";

    private String msgId = "";

    private String msgText = "";

    private String msgType = "";

    private boolean isMyMessage;

    private String msgResUrl = "";

    private String msgResUrl2 = "";

    private String time = "";

    private ChatMessageLocationVo locationVo = new ChatMessageLocationVo();

    public ChatContentItem() {

    }

    protected ChatContentItem(Parcel in) {
        chatId = in.readString();
        userId = in.readString();
        userNickName = in.readString();
        groupId = in.readString();
        avatarUri = in.readString();
        msgId = in.readString();
        msgText = in.readString();
        msgType = in.readString();
        isMyMessage = in.readByte() != 0;
        msgResUrl = in.readString();
        msgResUrl2 = in.readString();
        time = in.readString();
    }

    public static final Creator<ChatContentItem> CREATOR = new Creator<ChatContentItem>() {
        @Override
        public ChatContentItem createFromParcel(Parcel in) {
            return new ChatContentItem(in);
        }

        @Override
        public ChatContentItem[] newArray(int size) {
            return new ChatContentItem[size];
        }
    };

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public boolean isMyMessage() {
        return isMyMessage;
    }

    public void setMyMessage(boolean myMessage) {
        isMyMessage = myMessage;
    }

    public String getMsgResUrl() {
        return msgResUrl;
    }

    public void setMsgResUrl(String msgResUrl) {
        this.msgResUrl = msgResUrl;
    }

    public String getMsgResUrl2() {
        return msgResUrl2;
    }

    public void setMsgResUrl2(String msgResUrl2) {
        this.msgResUrl2 = msgResUrl2;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ChatMessageLocationVo getLocationVo() {
        return locationVo;
    }

    public void setLocationVo(ChatMessageLocationVo locationVo) {
        this.locationVo = locationVo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chatId);
        dest.writeString(userId);
        dest.writeString(userNickName);
        dest.writeString(groupId);
        dest.writeString(avatarUri);
        dest.writeString(msgId);
        dest.writeString(msgText);
        dest.writeString(msgType);
        dest.writeByte((byte) (isMyMessage ? 1 : 0));
        dest.writeString(msgResUrl);
        dest.writeString(msgResUrl2);
        dest.writeString(time);
    }
    public static String getMessageString(ChatContentItem item, Context context) {
        if(context == null){
            return "";
        }
        String contentMsg = "";
        if (item != null) {
            switch (item.getMsgType()) {
                case ProtocolConstants.VALUE_TYPE_TEXT:
                    contentMsg = item.getMsgText();
                    break;
                case ProtocolConstants.VALUE_TYPE_VIDEO:
                    contentMsg = "视频";
                    break;
                case ProtocolConstants.VALUE_TYPE_VOICE:
                    contentMsg = "语音";
                    break;
                case ProtocolConstants.VALUE_TYPE_LOCATION:
                    contentMsg = "位置";
                    break;
                case ProtocolConstants.VALUE_TYPE_IMAGE:
                    contentMsg = "图片";
                    break;
            }
            if (!ProtocolConstants.VALUE_TYPE_TEXT.equals(item.getMsgType())) {
                contentMsg = "[" + contentMsg + "]";
            }
        }
        if(TextUtils.isEmpty(contentMsg)){
            contentMsg = "";
        }
        return contentMsg;
    }
}
