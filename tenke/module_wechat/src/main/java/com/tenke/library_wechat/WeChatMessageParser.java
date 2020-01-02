package com.tenke.library_wechat;

import android.os.Bundle;
import android.util.Log;

import com.google.common.base.Strings;
import com.orhanobut.logger.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;

class WeChatMessageParser {

    private static final String TAG = WeChatMessageParser.class.getSimpleName();

    private static final int WECHAT_MSG_TYPE_TEXT = 1;
    private static final int WECHAT_MSG_TYPE_VOICE = 34;
    private static final int WECHAT_MSG_TYPE_GIF = 47;
    private static final int WECHAT_MSG_TYPE_IMAGE = 3;
    private static final int WECHAT_MSG_TYPE_VIDEO = 43;
    private static final int WECHAT_MSG_TYPE_MINI_VIDEO = 62;
    private static final int WECHAT_MSG_TYPE_RED_PACKET = 10000;

    private static final int WECHAT_MSG_SUB_TYPE_PURE_TEXT = 0;
    private static final int WECHAT_MSG_SUB_TYPE_LOCATION = 48;

    private static XmlPullParser xpp;

    static {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            xpp = factory.newPullParser();
        } catch (Exception e) {
            Logger.t("WeChatMessageParser").e("", e);
        }
    }

    static void parseForVUI(WeChatRepository weChatRepository, WeChatAuthenticationBean weChatAuthenticationBean, WeChatMessage weChatMessage) {
        Logger.t(TAG).d("parseForVUI start");
        final Bundle bundle = new Bundle();
        String msgType = ProtocolConstants.VALUE_TYPE_TEXT;
        String userName = weChatMessage.getFromUserName();
        String userNickName = weChatRepository.getNickOrRemarkNameByUserName(userName);
        String groupNickName = "";
        String msgContent = weChatMessage.getContent();

        String msgRegex = RegexUtil.WECHAT_GROUP_TEXT_MESSAGE_FROM_USER_NAME;
        if (isLocationMessage(weChatMessage)) {
            msgRegex = RegexUtil.WECHAT_GROUP_LOCATION_MESSAGE_FROM_USER_NAME;
        }

        if (isGroupMessage(weChatMessage)) {
            Pattern pattern = Pattern.compile(msgRegex);
            Matcher match = pattern.matcher(msgContent);
            while (match.find()) {
                groupNickName = userNickName;
                userName = match.group(1);
                userNickName = weChatRepository.getNickOrRemarkNameByUserName(userName);
                msgContent = match.group(2);
            }
        }
        bundle.putString(ProtocolConstants.KEY_MESSAGE_CONTENT, msgContent);
        bundle.putString(ProtocolConstants.KEY_USER_NICKNAME, userNickName);
        bundle.putString(ProtocolConstants.KEY_GROUP_NICKNAME, groupNickName);
        bundle.putString(ProtocolConstants.KEY_MESSAGE_HEAD_URL, weChatRepository.getAvatarUrlByUserName(weChatMessage.getFromUserName()));

        if (isTextMessage(weChatMessage)
                || isRedPacket(weChatMessage)) {
            Logger.t(TAG).d("parseForVUI textMsg = " + msgContent);
            bundle.putString(ProtocolConstants.KEY_MESSAGE_TYPE, ProtocolConstants.VALUE_TYPE_TEXT);
            //VoiceProxy.getInstance().receiveWeChatMessage(bundle);
        } else if (isLocationMessage(weChatMessage)) {
            Logger.t(TAG).d("parseForVUI locationMsg = " + msgContent);
            bundle.putString(ProtocolConstants.KEY_MESSAGE_TYPE, ProtocolConstants.VALUE_TYPE_LOCATION);
            try {
                xpp.setInput(new StringReader(weChatMessage.getOriContent()));
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if ("location".equals(xpp.getName())) {
                            bundle.putString(ProtocolConstants.KEY_ADDRESS, xpp.getAttributeValue(null, "label"));
                            bundle.putString(ProtocolConstants.KEY_POI_NAME, xpp.getAttributeValue(null, "poiname"));
                            try {
                                bundle.putDouble(ProtocolConstants.KEY_LAT, Double.parseDouble(xpp.getAttributeValue(null, "x")));
                                bundle.putDouble(ProtocolConstants.KEY_LON, Double.parseDouble(xpp.getAttributeValue(null, "y")));
                            } catch (Exception e) {
                                Log.e("WeChatMessageParser", "", e);
                            }
                            break;
                        }
                    }
                    eventType = xpp.next();
                }
            } catch (Exception e) {
                Logger.t("WeChatMessageParser").e("", e);
            }
            //VoiceProxy.getInstance().receiveWeChatMessage(bundle);
        } else if (isVoiceMessage(weChatMessage)) {
            Logger.t(TAG).d("parseForVUI voiceMsg = " + msgContent);
            bundle.putString(ProtocolConstants.KEY_MESSAGE_TYPE, ProtocolConstants.VALUE_TYPE_VOICE);
            WeChatManager.getInstance().getVoiceMessage(weChatAuthenticationBean, weChatMessage.getMsgId())
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                    .subscribe(new io.reactivex.Observer<File>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(File file) {
                            bundle.putString(ProtocolConstants.KEY_VOICE_URL, file.getPath());
                            //VoiceProxy.getInstance().receiveWeChatMessage(bundle);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Logger.t(TAG).e("getVoiceMessage fail", e);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else if (isImageMessage(weChatMessage)) {
            Logger.t(TAG).d("parseForVUI imageMsg = " + msgContent);
            bundle.putString(ProtocolConstants.KEY_MESSAGE_TYPE, ProtocolConstants.VALUE_TYPE_IMAGE);
            WeChatManager.getInstance().getImageMessage(weChatAuthenticationBean, weChatMessage.getMsgId(), WeChatManager.WECHAT_MSG_IMAGE_TYPE_BIG)
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                    .subscribe(new io.reactivex.Observer<File>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(File file) {
                            bundle.putString(ProtocolConstants.KEY_IMAGE_URL, file.getPath());
                            //VoiceProxy.getInstance().receiveWeChatMessage(bundle);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Logger.t(TAG).e("getImageMessage fail", e);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else if (isVideoMessage(weChatMessage)) {
            Logger.t(TAG).d("parseForVUI radioMsg = " + msgContent);
            bundle.putString(ProtocolConstants.KEY_MESSAGE_TYPE, ProtocolConstants.VALUE_TYPE_VIDEO);
            WeChatManager.getInstance().getVideoMessage(weChatAuthenticationBean, weChatMessage.getMsgId())
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                    .subscribe(new io.reactivex.Observer<File>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(File file) {
                            bundle.putString(ProtocolConstants.KEY_VIDEO_URL, file.getPath());
                            //sVoiceProxy.getInstance().receiveWeChatMessage(bundle);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Logger.t(TAG).e("getVideoMessage fail", e);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Logger.t(TAG).d("parseForVUI msgType = " + msgType + " msgContent = " + msgContent);
        }
        Logger.t(TAG).d("parseForVUI end");
    }

    static io.reactivex.Observable<ChatContentItem> parseForGUI(final WeChatRepository weChatRepository,
                                                                final WeChatAuthenticationBean weChatAuthenticationBean,
                                                                final WeChatMessage weChatMessage) {

        return io.reactivex.Observable.create(new ObservableOnSubscribe<ChatContentItem>() {
            @Override
            public void subscribe(final ObservableEmitter<ChatContentItem> emitter) {
                if (weChatMessage.getFromUserName().equals(weChatMessage.getToUserName()))
                    return;

                final ChatContentItem chatContentItem = new ChatContentItem();

                String msgType = ProtocolConstants.VALUE_TYPE_TEXT;
                String userName = weChatMessage.getFromUserName();
                String userNickName = weChatRepository.getNickOrRemarkNameByUserName(userName);
                String groupId = "";
                String groupNickName = "";
                String msgContent = weChatMessage.getContent();

                String msgRegex = RegexUtil.WECHAT_GROUP_TEXT_MESSAGE_FROM_USER_NAME;
                if (isLocationMessage(weChatMessage)) {
                    msgRegex = RegexUtil.WECHAT_GROUP_LOCATION_MESSAGE_FROM_USER_NAME;
                }
                if (isGroupMessage(weChatMessage)) {
                    Pattern pattern = Pattern.compile(msgRegex);
                    Matcher match = pattern.matcher(msgContent);
                    while (match.find()) {
                        groupId = userName;
                        groupNickName = userNickName;
                        userName = match.group(1);
                        userNickName = weChatRepository.getNickOrRemarkNameByUserName(userName);
                        msgContent = match.group(2);
                    }
                }

                boolean isMyMessage = userName.equals(WeChatManager.getInstance().getLoginUserName());
                chatContentItem.setUserId(userName);
                chatContentItem.setUserNickName(userNickName);
                chatContentItem.setMsgText(msgContent);
                chatContentItem.setGroupId(groupId);
                chatContentItem.setMyMessage(isMyMessage);
                chatContentItem.setAvatarUri(weChatRepository.getAvatarUrlByUserName(userName));
                chatContentItem.setMyMessage(isMyMessage);
                String chatId;
                if (!Strings.isNullOrEmpty(groupId)) {
                    chatId = groupId;
                } else {
                    if (isMyMessage) {
                        chatId = weChatMessage.getToUserName();
                    } else {
                        chatId = userName;
                    }
                }
                chatContentItem.setChatId(chatId);

                if (isTextMessage(weChatMessage)
                        || isRedPacket(weChatMessage)) {
                    Logger.t(TAG).d("parseForGUI textMsg = " + msgContent);
                    chatContentItem.setMsgType(ProtocolConstants.VALUE_TYPE_TEXT);
                    emitter.onNext(chatContentItem);
                } else if (isLocationMessage(weChatMessage)) {
                    Logger.t(TAG).d("parseForGUI locationMsg = " + msgContent);
                    chatContentItem.setMsgType(ProtocolConstants.VALUE_TYPE_LOCATION);
                    try {
                        xpp.setInput(new StringReader(weChatMessage.getOriContent()));
                        int eventType = xpp.getEventType();
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            if (eventType == XmlPullParser.START_TAG) {
                                if ("location".equals(xpp.getName())) {
                                    chatContentItem.setLocationVo(new ChatMessageLocationVo(xpp.getAttributeValue(null, "poiname"),
                                            xpp.getAttributeValue(null, "label"),
                                            Double.parseDouble(xpp.getAttributeValue(null, "x")),
                                            Double.parseDouble(xpp.getAttributeValue(null, "y"))));
                                    break;
                                }
                            }
                            eventType = xpp.next();
                        }
                    } catch (Exception e) {
                        Log.e("WeChatMessageParser", "", e);
                    }

                    WeChatManager.getInstance().getLocationCover(weChatAuthenticationBean, weChatMessage.getMsgId())
                            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                            .subscribeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                            .subscribe(new io.reactivex.Observer<File>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(File file) {
                                    chatContentItem.setMsgResUrl(file.getPath());
                                    emitter.onNext(chatContentItem);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    emitter.tryOnError(e);
                                }

                                @Override
                                public void onComplete() {

                                }
                            });

                } else if (isVoiceMessage(weChatMessage)) {
                    Logger.t(TAG).d("parseForGUI voiceMsg = " + msgContent);
                    chatContentItem.setMsgType(ProtocolConstants.VALUE_TYPE_VOICE);
                    WeChatManager.getInstance().getVoiceMessage(weChatAuthenticationBean, weChatMessage.getMsgId())
                            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                            .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                            .subscribe(new io.reactivex.Observer<File>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(File file) {
                                    chatContentItem.setMsgResUrl(file.getPath());
                                    emitter.onNext(chatContentItem);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    emitter.tryOnError(e);
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                } else if (isImageMessage(weChatMessage)) {
                    Logger.t(TAG).d("parseForGUI imageMsg = " + msgContent);
                    chatContentItem.setMsgType(ProtocolConstants.VALUE_TYPE_IMAGE);
                    WeChatManager.getInstance().getImageMessage(weChatAuthenticationBean, weChatMessage.getMsgId(), WeChatManager.WECHAT_MSG_IMAGE_TYPE_BIG)
                            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                            .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                            .subscribe(new io.reactivex.Observer<File>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(File file) {
                                    chatContentItem.setMsgResUrl(file.getPath());
//                                    listener.parseDone(chatContentItem);
                                    emitter.onNext(chatContentItem);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    emitter.tryOnError(e);
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                } else if (isVideoMessage(weChatMessage)) {
                    Logger.t(TAG).d("parseForGUI radioMsg = " + msgContent);
                    chatContentItem.setMsgType(ProtocolConstants.VALUE_TYPE_VIDEO);
                    io.reactivex.Observable.zip(WeChatManager.getInstance().getImageMessage(weChatAuthenticationBean, weChatMessage.getMsgId(), WeChatManager.WECHAT_MSG_IMAGE_TYPE_SLAVE),
                            WeChatManager.getInstance().getVideoMessage(weChatAuthenticationBean, weChatMessage.getMsgId()),
                            new BiFunction<File, File, String[]>() {
                                @Override
                                public String[] apply(File file, File file2) {
                                    String[] urls = new String[2];
                                    urls[0] = file.getPath();
                                    urls[1] = file2.getPath();
                                    return urls;
                                }
                            })
                            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                            .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                            .subscribe(new io.reactivex.Observer<String[]>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(String[] strings) {
                                    chatContentItem.setMsgResUrl(strings[0]);
                                    chatContentItem.setMsgResUrl2(strings[1]);
//                                    listener.parseDone(chatContentItem);
                                    emitter.onNext(chatContentItem);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    emitter.tryOnError(e);
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                } else {
                    Logger.t(TAG).d("parseForGUI msgType = " + msgType + " msgContent = " + msgContent);
                }
            }
        });
    }

    private static boolean isTextMessage(WeChatMessage newWeChatMessage) {
        return newWeChatMessage.getMsgType() == WECHAT_MSG_TYPE_TEXT && newWeChatMessage.getSubMsgType() == WECHAT_MSG_SUB_TYPE_PURE_TEXT;
    }

    private static boolean isLocationMessage(WeChatMessage newWeChatMessage) {
        return newWeChatMessage.getMsgType() == WECHAT_MSG_TYPE_TEXT && newWeChatMessage.getSubMsgType() == WECHAT_MSG_SUB_TYPE_LOCATION;
    }

    private static boolean isVoiceMessage(WeChatMessage newWeChatMessage) {
        return newWeChatMessage.getMsgType() == WECHAT_MSG_TYPE_VOICE;
    }

    private static boolean isImageMessage(WeChatMessage newWeChatMessage) {
        return newWeChatMessage.getMsgType() == WECHAT_MSG_TYPE_GIF
                || newWeChatMessage.getMsgType() == WECHAT_MSG_TYPE_IMAGE;
    }

    private static boolean isVideoMessage(WeChatMessage newWeChatMessage) {
        return newWeChatMessage.getMsgType() == WECHAT_MSG_TYPE_VIDEO
                || newWeChatMessage.getMsgType() == WECHAT_MSG_TYPE_MINI_VIDEO;
    }

    private static boolean isRedPacket(WeChatMessage newWeChatMessage) {
        return newWeChatMessage.getMsgType() == WECHAT_MSG_TYPE_RED_PACKET;
    }

    private static boolean isGroupMessage(WeChatMessage newWeChatMessage) {
        Pattern pattern = Pattern.compile(RegexUtil.WECHAT_GROUP_MESSAGE_PREFIX);
        Matcher match = pattern.matcher(newWeChatMessage.getContent());
        return match.find();
    }
}