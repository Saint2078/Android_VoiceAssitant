package com.tenke.library_wechat;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.Strings;

import java.util.ArrayList;

public class ChatData implements Parcelable {

    private String chatId;

    private String chatName;

    private String avatarUrl;

    private ArrayList<ChatContentItem> chatAllItems = new ArrayList<>();

    public ChatData() {
    }

    public ChatData(Parcel in) {
        chatId = in.readString();
        chatName = in.readString();
        avatarUrl = in.readString();
        chatAllItems = in.createTypedArrayList(ChatContentItem.CREATOR);
    }

    public static final Creator<ChatData> CREATOR = new Creator<ChatData>() {
        @Override
        public ChatData createFromParcel(Parcel in) {
            return new ChatData(in);
        }

        @Override
        public ChatData[] newArray(int size) {
            return new ChatData[size];
        }
    };

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    @NonNull
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(@Nullable String avatarUrl) {
        this.avatarUrl = Strings.nullToEmpty(avatarUrl);
    }

    public ArrayList<ChatContentItem> getChatAllItems() {
        return chatAllItems;
    }

    public void setChatAllItems(ArrayList<ChatContentItem> chatAllItems) {
        this.chatAllItems = chatAllItems;
    }

    public void addChatContentItem(ChatContentItem item) {
        this.chatAllItems.add(item);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chatId);
        dest.writeString(chatName);
        dest.writeString(avatarUrl);
        dest.writeTypedList(chatAllItems);
    }
}
