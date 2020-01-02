package com.tenke.library_wechat;

import androidx.lifecycle.MutableLiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.core.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Base64;

import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.tenke.lib_common.GlideConfig.GlideApp;
import com.tenke.lib_common.Okhttp.OkHttpClientManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function4;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class WeChatManager implements java.util.Observer {

    private static final String TAG = WeChatManager.class.getSimpleName();

    static final String WECHAT_SYNC_CHECK_RETURN_CODE_NORMAL = "0";
    static final String WECHAT_SYNC_CHECK_RETURN_CODE_LOGOUT = "1100";
    static final String WECHAT_SYNC_CHECK_RETURN_CODE_OTHER_LOGIN = "1101";
    static final String WECHAT_SYNC_CHECK_RETURN_CODE_QUIT_ON_PHONE = "1102";
    static final String WECHAT_SYNC_CHECK_RETURN_CODE_SERVER_ERROR = "500";
    private static final String WECHAT_SYNC_CHECK_SELECTOR_NORMAL = "0";
    private static final String WECHAT_SYNC_CHECK_SELECTOR_NEW_MESSAGE = "2";
    private static final String WECHAT_SYNC_CHECK_SELECTOR_UNKNOWN_3 = "3";
    private static final String WECHAT_SYNC_CHECK_SELECTOR_UNKNOWN_4 = "4";
    private static final String WECHAT_SYNC_CHECK_SELECTOR_UNKNOWN_6 = "6";
    public static final String WECHAT_SYNC_CHECK_SELECTOR_PLAY_IN_PHONE = "7";

    final static String ACTION_SERVICE_STARTED = "ACTION_SERVICE_STARTED";
    final static String ACTION_SERVICE_STOPPED = "ACTION_SERVICE_STOPPED";
    final static String ACTION_NEW_MESSAGE = "ACTION_NEW_MESSAGE";
    final static String ACTION_AUTH_FAILED = "ACTION_AUTH_FAILED";
    final static String MESSAGE_VO = "MESSAGE_VO";

    public static final String MSG_PARSE_DONE_ACTION = "MSG_PARSE_DONE_ACTION";
    public static final String MSG_CHAT_ID = "MSG_CHAT_ID";
    private static final String LOGIN_TIP_NOT_SCANNED = "1";
    private static final String LOGIN_TIP_SCANNED = "0";
    private static final String LOGIN_RETURN_CODE_SUCCESS = "200";
    private static final String LOGIN_RETURN_CODE_SCANNED = "201"; //scanned but not click login
    private static final String LOGIN_RETURN_CODE_RETRY = "408"; //timeout, continue loginWeChat
    private static final String LOGIN_RETURN_CODE_FAIL = "400"; //retry fail, refresh UUID and QRCode

    private static final int WECHAT_CONTACT_PERSONAL_DINGYUEHAO = 8;
    private static final int WECHAT_CONTACT_COMPANY_FUWUHAO = 24;
    private static final int WECHAT_CONTACT_WECHAT_OFFICIAL = 56;

    public static final int STATUS_LOGOUT = 1;
    public static final int STATUS_LOGIN_START = 2;
    public static final int STATUS_LOGIN_SUCCESS = 3;
    public static final int STATUS_LOGIN_FAIL = 4;

    private static final String WECHAT_MEDIA_STORAGE_PATH = Constants.STORAGE_PATH + "/wechat/media/";
    static final String WECHAT_MSG_IMAGE_TYPE_BIG = "big";
    static final String WECHAT_MSG_IMAGE_TYPE_SLAVE = "slave";

    private static WeChatManager INSTANCE = new WeChatManager();

    private Context mContext;
    private WeChatRepository mWeChatRepository;
    private WeChatLoginUser mWeChatLoginUser;
    private WeChatAuthenticationBean mWeChatAuthenticationBean;

    private WeChatRestService mWeChatRestService;
    private WeChatMsgReceiver mWeChatMsgReceiver = new WeChatMsgReceiver();

    private String mSyncCheckHost = "";
    private List<Bean> mReceiverCandidates = new ArrayList<>();

    private String mSenderUserName;

    private String mUUID;

    private final MutableLiveData<ChatData> newMsgItem = new MutableLiveData<>();
    private final MutableLiveData<Integer> loginStatus = new MutableLiveData<>();
    public final MutableLiveData<Bitmap> avatar = new MutableLiveData<>();

    private WeChatManager() {
    }

    public static WeChatManager getInstance() {
        return INSTANCE;
    }

    private void startWeChatListening(Context context) {
        mContext = context;
        mWeChatLoginUser = mWeChatRepository.getWeChatLoginUser();
        mWeChatAuthenticationBean = mWeChatRepository.getAuthBean();
        WeChatMessagePollService.start(mContext, mWeChatAuthenticationBean);
        mWeChatMsgReceiver.registerWeChatMsgReceiver(mContext);
    }

    private void parseMessages(WeChatMessageBean messageBean) {
        if (messageBean == null) return;
        final List<WeChatMessage> weChatMessages = messageBean.getAddMsgList();
        final WeChatMessage newWeChatMessage = weChatMessages.get(0);

        WeChatMessageParser.parseForGUI(mWeChatRepository, mWeChatAuthenticationBean, newWeChatMessage)
                .subscribe(new Observer<ChatContentItem>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ChatContentItem chatContentItem) {
                        parseDone(chatContentItem);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        boolean isFromMine = newWeChatMessage.getFromUserName().equals(getLoginUserName());
        if (isFromMine || !mWeChatRepository.isAutoPlayWeChatMessage())
            return;
        mSenderUserName = newWeChatMessage.getFromUserName();
        WeChatMessageParser.parseForVUI(mWeChatRepository, mWeChatAuthenticationBean, newWeChatMessage);
    }

    public boolean isLogin() {
        return mWeChatRepository.isLogin();
    }

    @Override
    public void update(java.util.Observable o, Object arg) {
//        final Bundle dataBundle = (Bundle) arg;
//        final VoiceIntent voiceIntent = VoiceIntent.valueOf(dataBundle.getString(ProtocolConstants.KEY_VOICE_INTENT));
//        if (voiceIntent == VoiceIntent.WECHAT_SEND_MESSAGE_CONFIRM) {
//            String messageContent = dataBundle.getString(VoiceConstants.TN_NLU_SLOT_WECHAT_MESSAGE_CONTENT);
//            String userName = dataBundle.getString(VoiceConstants.TN_NLU_SLOT_WECHAT_RECEIVER_ID);
//            if (Strings.isNullOrEmpty(userName)) {
//                //This is reply message, not send proactively
//                userName = mSenderUserName;
//            }
//            sendMessage(userName, messageContent)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new SingleObserver<ChatContentItem>() {
//                        @Override
//                        public void onSubscribe(Disposable d) {
//
//                        }
//
//                        @Override
//                        public void onSuccess(ChatContentItem chatContentItem) {
//                            parseDone(chatContentItem);
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            Logger.t(TAG).e("sendMessage onError", e);
//                            VoiceProxy.getInstance().wechatMessageSendFail();
//                        }
//                    });
//
//        } else if (voiceIntent == VoiceIntent.WECHAT_VALIDATE_RECEIVER) {
//            String receiverNickName = dataBundle.getString(VoiceConstants.TN_NLU_SLOT_WECHAT_TO_USER);
//            mReceiverCandidates = findReceiverCandidates(receiverNickName);
//            if (mReceiverCandidates.size() > 1) {
//                try {
//                    Intent intent = new Intent(IntentConstants.INTENT_WECHAT_CANDIDATES);
//                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//                    Bundle extra = new Bundle();
//                    extra.putSerializable(CandidatesActivity.KEY_DATA_CANDIDATES, (Serializable) mReceiverCandidates);
//                    extra.putString(CandidatesActivity.KEY_DATA_ORIGIN_KEYWORD, receiverNickName);
//                    intent.putExtras(extra);
//                    mContext.startActivity(intent);
//                } catch (Exception e) {
//                    Logger.t(TAG).e("start wechat candidates activity exception", e);
//                }
//            } else {
//                VoiceProxy.getInstance().findWechatCandidatesDone(getWeChatBeansIds(mReceiverCandidates));
//
//            }
//        } else if (voiceIntent == VoiceIntent.WECHAT_LOGIN_VALIDATION) {
//            final String fromIntent = dataBundle.getString(ProtocolConstants.WECHAT_DATA_KEY_FROM_INTENT);
//            if (!mWeChatRepository.isLogin()) {
//                CasaApp casaApp = new CasaApp(R.string.wechat, R.drawable.wechat_app, WeChatActivity.class.getName());
//                casaApp.launch(mContext);
//            }
//            VoiceProxy.getInstance().loginValidationDone(mWeChatRepository.isLogin(), fromIntent);
//        } else if (voiceIntent == VoiceIntent.WECHAT_LOGOUT) {
//            Logger.t(TAG).d("Voice WECHAT_LOGOUT");
//            logoutWeChat();
//        } else if (voiceIntent == VoiceIntent.WECHAT_STOP_AUTO_PLAY) {
//            mWeChatRepository.setAutoPlayWeChatMessage(false);
//        }
    }

    private @NonNull
    List<Bean> findReceiverCandidates(String nickName) {
        return null;
//        final int PERFECT_SCORE = 100;
//        List<ExtractedResult> searchResults = FuzzySearch.extractTop(nickName, mWeChatRepository.getNickNameList(), 5, 50);
//        List<Bean> candidates = new ArrayList<>();
//        boolean hasPerfectMatch = false;
//        for (ExtractedResult result : searchResults) {
//            if (result.getScore() == PERFECT_SCORE) {
//                hasPerfectMatch = true;
//            }
//            if (hasPerfectMatch && result.getScore() < PERFECT_SCORE) {
//                continue;
//            }
//            Collection<String> userNames = mWeChatRepository.getUserNameByNickOrRemarkName(result.getString());
//            for (String userName : userNames) {
//                Bean bean = mWeChatRepository.getContactBean(userName);
//                if (bean != null) {
//                    candidates.add(bean);
//                }
//            }
//        }
//
//        return candidates;
    }

    /**
     * Send WeChat Message
     *
     * @param userName The id of wechat user. WeChat json use 'userName' for the id.
     *                 Format as '@xxx' for normal user and '@@xxx' for group user.
     * @param message  The message reply through voice assistant
     */
    private Single<ChatContentItem> sendMessage(final String userName, final String message) {
        Logger.t(TAG).d("sendMessage start");
        if (Strings.isNullOrEmpty(mSyncCheckHost)) return null;
        String url = "https://" + mSyncCheckHost + "/cgi-bin/mmwebwx-bin/webwxsendmsg?pass_ticket=" + mWeChatAuthenticationBean.getPassTicket();
        Map<String, Object> msgMap = new HashMap<>();
        long time = System.currentTimeMillis();
        msgMap.put("Type", "1");
        msgMap.put("Content", message);
        msgMap.put("FromUserName", getLoginUserName());
        msgMap.put("ClientMsgId", time + "");
        msgMap.put("LocalID", time + "");
        msgMap.put("ToUserName", userName);

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("BaseRequest", mWeChatAuthenticationBean.getBaseRequest());
        bodyMap.put("Msg", msgMap);
        bodyMap.put("Scene", 0);
        RequestBody body = RequestBody.create(WeChatRestService.JSON, new Gson().toJson(bodyMap));
        Logger.t(TAG).d("sendMessage url = " + url + " body = " + new Gson().toJson(bodyMap));
        return mWeChatRestService.sendMessage(url, body)
                .map(new Function<String, ChatContentItem>() {
                    @Override
                    public ChatContentItem apply(final String s) throws Exception {
                        ChatContentItem contentItem = new ChatContentItem();
                        if (!TextUtils.isEmpty(s)) {
                            Logger.t(TAG).d("sendMessage message json = " + s);
                            SendMsgVo vo = new Gson().fromJson(s, SendMsgVo.class);
                            if (vo != null && vo.getBaseResponse().isSuccess()) {
                                Logger.t(TAG).d("sendMessage.onSuccess");
                                contentItem.setMsgType(ProtocolConstants.VALUE_TYPE_TEXT);
                                contentItem.setChatId(userName);
                                contentItem.setAvatarUri(mWeChatRepository.getAvatarUrlByUserName(getLoginUserName()));
                                contentItem.setMsgText(message);
                                contentItem.setMyMessage(true);
                            } else {
                                Logger.t(TAG).d("sendMessage response fail vo = " + vo);
                                return null;
                            }
                        } else {
                            Logger.t(TAG).d("sendMessage s null or empty");
                            return null;
                        }
                        return contentItem;
                    }
                }).single(new ChatContentItem());
    }

    public void logoutWeChat() {
        mWeChatLoginUser = null;
        WeChatMessagePollService.stop(mContext);
        mWeChatMsgReceiver.unRegisterWeChatMsgReceiver(mContext);
        mWeChatRepository.clearWeChatData();
        loginStatus.postValue(STATUS_LOGOUT);
        //ExternalLaunchUtil.wechatLogin(mContext, false);
        mWeChatAuthenticationBean = new WeChatAuthenticationBean();
    }

    /**
     * Load WeChat Login QRCode Bitmap.
     * <p>
     * Include two process of wechat login: getUUID and load QRCode
     *
     * @return Bitmap of the QRCode in Rxjava2 Observable
     */
    public Observable<Bitmap> loadQRCodeImage() {
        Logger.t(TAG).d("loadQRCodeImage start");
        return mWeChatRestService.getUUID("wx782c26e4c19acffb",
                "new", "zh_CN", "" + System.currentTimeMillis())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String rawUUID) {
                        if (rawUUID == null) {
                            throw new NullPointerException("raw uuid information is null");
                        }
                        Pattern pattern = Pattern.compile(RegexUtil.WECHAT_AUTHENTICATION_GET_UUID);
                        Matcher match = pattern.matcher(rawUUID);
                        String uuid = null;
                        while (match.find()) {
                            if ("200".equals(match.group(2))) {
                                uuid = match.group(3);
                            }
                        }
                        if (uuid == null) {
                            throw new NullPointerException("final uuid is null");
                        }
                        Logger.t(TAG).d("getUUID uuid = " + uuid);
                        mUUID = uuid;
                        return uuid;
                    }
                })
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String uuid) {
                        String url = WeChatRestService.WECHAT_LOGIN_END_POINT + "qrcode/" + uuid;
                        Logger.t(TAG).d("generate QRCode url = " + url);
                        return url;
                    }
                })
                .flatMap(new Function<String, ObservableSource<ResponseBody>>() {
                    @Override
                    public ObservableSource<ResponseBody> apply(String qrCodeUrl) {
                        Logger.t(TAG).d("load QRCode");
                        return mWeChatRestService.getQRCode(qrCodeUrl);
                    }
                })
                .flatMap(new Function<ResponseBody, ObservableSource<Bitmap>>() {
                    @Override
                    public ObservableSource<Bitmap> apply(ResponseBody responseBody) throws Exception {
                        Logger.t(TAG).d("parse QRCode Bitmap from responseBody");
                        byte[] bytes = responseBody.bytes();
                        final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        return new ObservableSource<Bitmap>() {
                            @Override
                            public void subscribe(Observer<? super Bitmap> observer) {
                                observer.onNext(bitmap);
                                Logger.t(TAG).d("loadQRCodeImage finish");
                            }
                        };
                    }
                });
    }

    /**
     * Start the wechat login process. The steps are as follows:
     * 1. Waiting user to scan QR code
     * 2. Waiting user click login button on phone wechat
     * 3. Fire multiple request to Wechat server to init and setup message connection
     */
    public Observable<String> loginWeChat() {
        Logger.t(TAG).d("loginWeChat start");
        return mWeChatRestService.waitMobileLogin(LOGIN_TIP_NOT_SCANNED, mUUID, String.valueOf(System.currentTimeMillis()), true)
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String rawResult) {
                        Pattern pattern = Pattern.compile(RegexUtil.WECHAT_AUTHENTICATION_WAIT_FOR_SCAN);
                        Matcher match = pattern.matcher(rawResult);
                        if (match.find()) {
                            String retCode = match.group(1);
                            final String avatarBase64Str = match.group(2);
                            if (LOGIN_RETURN_CODE_SCANNED.equals(retCode)) {
                                avatar.postValue(base64StringToBitmap(avatarBase64Str));
                                return waitingLoginOnPhone(mUUID);
                            } else if (LOGIN_RETURN_CODE_RETRY.equals(retCode)) {
                                return waitingLoginOnPhone(mUUID);
                            } else {
                                return Observable.error(new Throwable("unknown return code from waitMobileLogin " + retCode));
                            }
                        } else {
                            return Observable.error(new Throwable("waitMobileLogin failed rawResult = " + rawResult));
                        }
                    }
                });
    }

    /**
     * Waiting user click login on phone wechat
     *
     * @param uuid
     * @return
     */
    private Observable<String> waitingLoginOnPhone(final String uuid) {
        Logger.t(TAG).d("waitingLoginOnPhone start");
        return mWeChatRestService.waitMobileLogin(LOGIN_TIP_SCANNED, uuid, String.valueOf(System.currentTimeMillis()), false)
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String rawResult) {
                        Pattern pattern = Pattern.compile(RegexUtil.WECHAT_AUTHENTICATION_WAIT_FOR_LOGIN);
                        Matcher match = pattern.matcher(rawResult);
                        if (match.find()) {
                            String retCode = match.group(1);
                            if (LOGIN_RETURN_CODE_SUCCESS.equals(retCode)) {
                                Logger.t(TAG).d("waitingLoginOnPhone success.");
                                pattern = Pattern.compile(RegexUtil.WECHAT_AUTHENTICATION_WAIT_FOR_LOGIN_REDIRECT_URL);
                                match = pattern.matcher(rawResult);
                                if (match.find()) {
                                    String redirectUrl = match.group(1) + "&fun=new";
                                    String baseUrl = redirectUrl.substring(0, redirectUrl.lastIndexOf("/"));
                                    mWeChatAuthenticationBean.setRedirectUrl(redirectUrl);
                                    mWeChatAuthenticationBean.setBaseUrl(baseUrl);
                                    loginStatus.postValue(STATUS_LOGIN_START);
                                    return redirectNewLoginPage(redirectUrl);
                                }
                            } else if (LOGIN_RETURN_CODE_SCANNED.equals(retCode)) {
                                return waitingLoginOnPhone(uuid);
                            } else if (LOGIN_RETURN_CODE_RETRY.equals(retCode)) {
                                return waitingLoginOnPhone(uuid);
                            } else {
                                return Observable.error(new Throwable("unknown return code from waitingLoginOnPhone " + retCode));
                            }
                        }
                        return Observable.error(new Throwable("waitingLoginOnPhone error rawResult = " + rawResult));
                    }
                });
    }

    private Observable<String> redirectNewLoginPage(final String redirectUrl) {
        Logger.t(TAG).d("redirectNewLoginPage start");
        return mWeChatRestService.redirectNewLoginPage(redirectUrl)
                .map(new Function<String, WeChatAuthenticationBean>() {
                    @Override
                    public WeChatAuthenticationBean apply(String s) {
                        Logger.t(TAG).d("redirectNewLoginPage success.");
                        String skey = getLoginData(s, "skey");
                        String sid = getLoginData(s, "wxsid");
                        String uin = getLoginData(s, "wxuin");
                        String passTicket = getLoginData(s, "pass_ticket");
                        mWeChatAuthenticationBean.setSkey(skey);
                        mWeChatAuthenticationBean.setSid(sid);
                        mWeChatAuthenticationBean.setUin(Long.parseLong(uin));
                        mWeChatAuthenticationBean.setPassTicket(passTicket);
                        mWeChatAuthenticationBean.setBaseRequest();
                        return mWeChatAuthenticationBean;
                    }
                })
                .flatMap(new Function<WeChatAuthenticationBean, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(WeChatAuthenticationBean weChatAuthenticationBean) {
                        return weChatInit();
                    }
                });
    }

    private Observable<String> weChatInit() {
        mWeChatRepository.clearWeChatData();
        Logger.t(TAG).d("weChatInit start");
        String url = mWeChatAuthenticationBean.getBaseUrl() + "/webwxinit?pass_ticket=" + mWeChatAuthenticationBean.getPassTicket()
                + "lang=en_US&r=" + System.currentTimeMillis();
        HashMap<String, Object> map = new HashMap<>();
        map.put("BaseRequest", mWeChatAuthenticationBean.getBaseRequest());
        RequestBody body = RequestBody.create(WeChatRestService.JSON, new Gson().toJson(map));
        return mWeChatRestService.wechatInit(url, body)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) {
                        WechatUiDataBean wechatUiDataBean = new Gson().fromJson(s, WechatUiDataBean.class);
                        if (wechatUiDataBean.getBaseResponse().isSuccess()) {
                            Logger.t(TAG).d("weChatInit success. ");
                            WeChatUserBean userBean = wechatUiDataBean.getUser();
                            mWeChatAuthenticationBean.setUid(userBean.getUserName());
                            mWeChatAuthenticationBean.setSyncKey(wechatUiDataBean.getSyncKey());
                            mWeChatRepository.saveAuthBean(mWeChatAuthenticationBean);

                            loadRecentChatsFromServer(mWeChatAuthenticationBean, wechatUiDataBean);
                            mWeChatRepository.saveWeChatContacts(Arrays.asList(userBean), false);
                            WeChatLoginUser user = new WeChatLoginUser(userBean.getUin());
                            user.setWeChatUserBean(userBean);
                            mWeChatRepository.saveWeChatLoginUser(user);
                            Logger.t(TAG).d(userBean.getNickName() + ", welcome back");
                        } else {
                            throw new NullPointerException("weChatInit response failed");
                        }
                        return wechatUiDataBean.getChatSet();
                    }
                })
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String chatSet) {
                        return combinedRequestResources(chatSet);
                    }
                });
    }

    private Observable<String> combinedRequestResources(String chatSet) {
        final String baseUrl = mWeChatAuthenticationBean.getBaseUrl();
        String passTicket = mWeChatAuthenticationBean.getPassTicket();
        String uId = mWeChatAuthenticationBean.getUid();
        Map<String, String> baseRequest = mWeChatAuthenticationBean.getBaseRequest();
        String sKey = mWeChatAuthenticationBean.getSkey();
        String sId = mWeChatAuthenticationBean.getSid();
        long uIn = mWeChatAuthenticationBean.getUin();
        String deviceId = WeChatAuthenticationBean.createDeviceId();
        String syncKey = mWeChatAuthenticationBean.getSyncKey();

        return Observable.zip(webWxStatusNotify(baseUrl, passTicket, uId, baseRequest),
                fetchContacts(baseUrl, passTicket, sKey),
                batchGetGroupContacts(baseUrl, passTicket, baseRequest, chatSet),
                testSyncCheck(baseUrl, sId, sKey, uIn, deviceId, syncKey),
                new Function4<String, String, String, String, String>() {
                    @Override
                    public String apply(String statusNotifyResult, String fetchContactsResult, String getGroupContactsResult, String testSyncCheckResult) {

                        //webWxStatusNotify results ignored

                        //fetchContacts save
                        if (!TextUtils.isEmpty(fetchContactsResult)) {
                            WeChatContactVo contactVo = new Gson().fromJson(fetchContactsResult, WeChatContactVo.class);
                            if (contactVo.getBaseResponse().isSuccess()) {
                                Logger.t(TAG).d("getContacts success");
                                if (contactVo.getMemberCount() > 0) {
                                    mWeChatRepository.saveWeChatContacts(contactVo.getMemberList(), false);
                                }
                            } else {
                                throw new RuntimeException("getContacts response status fail");
                            }
                        } else {
                            throw new NullPointerException("getContacts response is null");
                        }

                        //batchGetGroupContacts save
                        if (TextUtils.isEmpty(getGroupContactsResult)) {
                            throw new NullPointerException("batchGetGroupContacts response is null");
                        }
                        WechatUiDataBean wechatUiDataBean = new Gson().fromJson(getGroupContactsResult, WechatUiDataBean.class);
                        if (wechatUiDataBean.getBaseResponse().isSuccess()) {
                            List<WeChatContactVo> weChatContactVos = wechatUiDataBean.getContactList();
                            mWeChatRepository.saveWeChatConversationContacts(weChatContactVos);
                            Logger.t(TAG).d("batchGetGroupContacts success.");
                        } else {
                            Logger.t(TAG).d("batchGetGroupContacts response ret != 0 contactList size = " + wechatUiDataBean.getContactList());
                        }

                        //testSyncCheck handle
                        if (TextUtils.isEmpty(testSyncCheckResult)) {
                            throw new NullPointerException("testSyncCheck raw result is null");
                        }
                        Pattern pattern = Pattern.compile(RegexUtil.WECHAT_NEW_MESSAGE_LOOP);
                        Matcher match = pattern.matcher(testSyncCheckResult);
                        if (match.find()) {
                            String retCode = match.group(1);
                            if (WECHAT_SYNC_CHECK_RETURN_CODE_NORMAL.equals(retCode)) {
                                HttpUrl httpUrl = HttpUrl.parse(baseUrl);
                                if (httpUrl == null)
                                    throw new NullPointerException("mSyncCheckHost is null");
                                mSyncCheckHost = httpUrl.host();
                                Logger.t(TAG).d("testSyncCheck success " + mSyncCheckHost);
                                String headImgUrl = "";
                                ArrayList<Parcelable> values;
                                boolean autoPlayWeChatMessage;
                                if (mWeChatRepository.getWeChatLoginUser() != null
                                        && mWeChatRepository.getWeChatLoginUser().getWechatUserBean() != null) {
                                    headImgUrl = mWeChatRepository.getWeChatLoginUser().getWechatUserBean().getHeadImgUrl();
                                    headImgUrl = mWeChatRepository.getAvatarUrl(headImgUrl);
                                }
                                mWeChatRepository.getChatDataList();
                                values = new ArrayList<Parcelable>(mWeChatRepository.getChatDataList());
                                autoPlayWeChatMessage = mWeChatRepository.isAutoPlayWeChatMessage();
                                //ExternalLaunchUtil.wechatLogin(mContext, headImgUrl, values, autoPlayWeChatMessage);
                                loginStatus.postValue(STATUS_LOGIN_SUCCESS);
                                startWeChatListening(mContext);
                            } else {
                                throw new IllegalArgumentException("testSyncCheck return code error " + retCode);
                            }
                        } else {
                            throw new IllegalArgumentException("testSyncCheck RE no match. result= " + testSyncCheckResult);
                        }
                        return "";
                    }
                });
    }

    private Observable<String> webWxStatusNotify(String baseUrl, String passTicket, String uId, Map<String, String> baseRequest) {
        Logger.t(TAG).d("webWxStatusNotify start");
        String url = baseUrl + "/webwxstatusnotify?lang=zh_CN&pass_ticket=" + passTicket;
        HashMap<String, Object> map = new HashMap<>();
        map.put("BaseRequest", baseRequest);
        map.put("Code", 3);
        map.put("FromUserName", uId);
        map.put("ToUserName", uId);
        map.put("ClientMsgId", System.currentTimeMillis());
        RequestBody body = RequestBody.create(WeChatRestService.JSON, new Gson().toJson(map));
        return mWeChatRestService.webWxStatusNotify(url, body);
    }

    private Observable<String> fetchContacts(String baseUrl, String passTicket, String sKey) {
        Logger.t(TAG).d("getContacts start");
        String url = baseUrl + "/webwxgetcontact";
        return mWeChatRestService.getContacts(url, passTicket,
                sKey,
                0,
                System.currentTimeMillis());
    }

    Observable<String> batchGetGroupContacts(String baseUrl, String passTicket, Map<String, String> baseRequest, String chatSet) {
        Logger.t(TAG).d("batchGetGroupContacts start");
        List<Map<String, String>> userNameList = new ArrayList<>();
        Pattern pattern = Pattern.compile(RegexUtil.WECHAT_CHAT_SET_GROUP_USER_NAME);
        Matcher match = pattern.matcher(chatSet);
        while (match.find()) {
            Map<String, String> map = new HashMap<>();
            map.put("UserName", chatSet.substring(match.start(), match.end() - 1));
            userNameList.add(map);
        }

        String url = baseUrl + "/webwxbatchgetcontact?type=ex&r=" + System.currentTimeMillis() + "&pass_ticket=" + passTicket;

        HashMap<String, Object> map = new HashMap<>();
        map.put("BaseRequest", baseRequest);
        map.put("Count", userNameList.size());
        map.put("List", userNameList);
        RequestBody body = RequestBody.create(WeChatRestService.JSON, new Gson().toJson(map));

        return mWeChatRestService.batchGetGroupContacts(url, body);
    }

    private Observable<String> testSyncCheck(String baseUrl, String sId, String sKey, long uIn, String deviceId, String syncKey) {
        Logger.t(TAG).d("testSyncCheck start");
        // for url: https://wx2.qq.com/cgi-bin/, get wx2.qq.com
        String url;
        final HttpUrl httpUrl = HttpUrl.parse(baseUrl);
        if (httpUrl != null) {
            url = httpUrl.scheme() + "://webpush." + httpUrl.host() + "/cgi-bin/mmwebwx-bin/synccheck";
        } else {
            Logger.t(TAG).d("testSyncCheck BaseUrl parse error. url= " + baseUrl);
            return Observable.error(new Throwable("testSyncCheck BaseUrl parse error"));
        }
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("sid", sId);
        queryMap.put("skey", sKey);
        queryMap.put("uin", String.valueOf(uIn));
        queryMap.put("deviceid", deviceId);
        queryMap.put("synckey", syncKey);
        queryMap.put("r", System.currentTimeMillis() + "");
        queryMap.put("_", System.currentTimeMillis() + "");
        return mWeChatRestService.testSyncCheck(url, queryMap);
    }

    void pollWeChatMessage(final WeChatAuthenticationBean authenticationBean, final WeChatMessageLoopCallback callback) {
        if (Strings.isNullOrEmpty(mSyncCheckHost)) {
            callback.onAuthFailed(WECHAT_SYNC_CHECK_RETURN_CODE_LOGOUT, "mSyncCheckHost = null");
            return;
        }
        String url = "https://" + mSyncCheckHost + "/cgi-bin/mmwebwx-bin/synccheck";
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("sid", authenticationBean.getSid());
        queryMap.put("skey", authenticationBean.getSkey());
        queryMap.put("uin", String.valueOf(authenticationBean.getUin()));
        queryMap.put("deviceid", WeChatAuthenticationBean.createDeviceId());
        queryMap.put("synckey", authenticationBean.getSyncKey());
        queryMap.put("r", System.currentTimeMillis() + "");
        queryMap.put("_", System.currentTimeMillis() + "");

        mWeChatRestService.pollWeChatMessage(url, queryMap)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String result) {
                        if (TextUtils.isEmpty(result)) {
                            callback.onAuthFailed(WECHAT_SYNC_CHECK_RETURN_CODE_SERVER_ERROR, "pollWeChatMessage result is Empty");
                        } else {
                            Pattern pattern = Pattern.compile(RegexUtil.WECHAT_NEW_MESSAGE_LOOP);
                            Matcher match = pattern.matcher(result);
                            while (match.find()) {
                                String retCode = match.group(1);
                                String selector = match.group(2);
                                if (WECHAT_SYNC_CHECK_RETURN_CODE_NORMAL.equals(retCode)) {
                                    if (WECHAT_SYNC_CHECK_SELECTOR_NEW_MESSAGE.equals(selector)) {
                                        messageSync(authenticationBean.getBaseUrl(), authenticationBean.getSid(), authenticationBean.getSkey(),
                                                authenticationBean.getPassTicket(), authenticationBean.getBaseRequest(), authenticationBean.getWechatSyncKeyBean(), callback);
                                    } else if (WECHAT_SYNC_CHECK_SELECTOR_NORMAL.equals(selector)) {
                                        callback.onPollEmpty();
                                    } else if (WECHAT_SYNC_CHECK_SELECTOR_UNKNOWN_4.equals(selector)
                                            || WECHAT_SYNC_CHECK_SELECTOR_UNKNOWN_6.equals(selector)
                                            || WECHAT_SYNC_CHECK_SELECTOR_UNKNOWN_3.equals(selector)) {
                                        callback.onAuthFailed(WECHAT_SYNC_CHECK_RETURN_CODE_LOGOUT, "Caution! Unknown selector = " + selector);
                                    } else {
                                        callback.onAuthFailed(retCode, "Unhandled selector " + selector);
                                    }
                                    return;
                                } else if (WECHAT_SYNC_CHECK_RETURN_CODE_LOGOUT.equals(retCode)) {
                                    callback.onAuthFailed(retCode, "You'v logged out");
                                } else if (WECHAT_SYNC_CHECK_RETURN_CODE_OTHER_LOGIN.equals(retCode)) {
                                    callback.onAuthFailed(retCode, "You login Web WeChat somewhere else");
                                } else if (WECHAT_SYNC_CHECK_RETURN_CODE_QUIT_ON_PHONE.equals(retCode)) {
                                    callback.onAuthFailed(retCode, "You'v logged out from mobile WeChat");
                                } else {
                                    callback.onAuthFailed(retCode, "Invalid retCode " + retCode);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.t(TAG).d("pollWeChatMessage.onError " + e);
                        callback.onAuthFailed(WECHAT_SYNC_CHECK_RETURN_CODE_SERVER_ERROR, "pollWeChatMessage onError");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void messageSync(String baseUrl, String sid, String skey, String passTicket, Map<String, String> baseRequest, WeChatSyncKeyBean syncKeyBean, final WeChatMessageLoopCallback callback) {
        String url = baseUrl + "/webwxsync?sid=" + sid + "&skey=" + skey + "&pass_ticket=" + passTicket;

        HashMap<String, Object> map = new HashMap<>();
        map.put("BaseRequest", baseRequest);
        map.put("SyncKey", syncKeyBean);
        map.put("rr", System.currentTimeMillis());
        RequestBody body = RequestBody.create(WeChatRestService.JSON, new Gson().toJson(map));

        mWeChatRestService.messageSync(url, body)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        if (!TextUtils.isEmpty(s)) {
                            WeChatMessageBean vo = new Gson().fromJson(s, WeChatMessageBean.class);
                            callback.onSuccess(vo);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onAuthFailed(WECHAT_SYNC_CHECK_RETURN_CODE_NORMAL, "messageSync onError " + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadRecentChatsFromServer(WeChatAuthenticationBean weChatAuthenticationBean, WechatUiDataBean wechatUiDataBean) {
        List<WeChatContactVo> weChatContactVos = wechatUiDataBean.getContactList();
        if (weChatContactVos == null) return;
        Logger.t(TAG).d("recent chats from server count " + weChatContactVos.size());
        int size = weChatContactVos.size();
        for (int i = size - 1; i > -1; i--) {
            final WeChatContactVo weChatContactVo = weChatContactVos.get(i);
            int verifyFlag = weChatContactVo.getVerifyFlag();
            if (verifyFlag == WECHAT_CONTACT_PERSONAL_DINGYUEHAO
                    || verifyFlag == WECHAT_CONTACT_COMPANY_FUWUHAO
                    || verifyFlag == WECHAT_CONTACT_WECHAT_OFFICIAL
                    || !weChatContactVo.getUserName().startsWith("@"))
                continue;
            ChatData chatData = new ChatData();
            chatData.setChatId(weChatContactVo.getUserName());
            chatData.setChatName(Strings.isNullOrEmpty(weChatContactVo.getRemarkName()) ?
                    weChatContactVo.getNickName() : weChatContactVo.getRemarkName());
            String headImageUrl = weChatContactVo.getHeadImgUrl();
            chatData.setAvatarUrl(mWeChatRepository.getAvatarUrl(headImageUrl));
            mWeChatRepository.saveNewChat(chatData);
        }
    }

    private String getLoginData(String xml, String key) {
        String header = "<" + key + ">";
        String footer = "</" + key + ">";
        return xml.substring(xml.indexOf(header) + header.length(), xml.indexOf(footer));
    }

    public void setContext(Context context) {
        this.mContext = context;
        mWeChatRepository = new WeChatRepository(context.getSharedPreferences("sp_wechat", Context.MODE_PRIVATE));
        OkHttpClient client = OkHttpClientManager.getInstance().getOkHttpClient()
                .newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36")
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    }
                })
                .cookieJar(new CookieJar() {
                    private final Map<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();
                    private HttpUrl firstUrl;

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        firstUrl = url;
                        cookieStore.put(url, cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(firstUrl);
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .build();

        Retrofit weChatLoginRetrofit = new Retrofit.Builder()
                .baseUrl(WeChatRestService.WECHAT_LOGIN_END_POINT)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();
        GlideApp.get(mContext).getRegistry().replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client));
        mWeChatRestService = weChatLoginRetrofit.create(WeChatRestService.class);

        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                mWeChatAuthenticationBean = mWeChatRepository.getAuthBean();
                if (mWeChatAuthenticationBean == null) {
                    mWeChatAuthenticationBean = new WeChatAuthenticationBean();
                } else {
                    weChatInit();
                }
            }
        });
    }

    public WeChatRepository getWeChatRepository() {
        return mWeChatRepository;
    }

    private void parseDone(ChatContentItem chatContentItem) {
        ChatData chatData = mWeChatRepository.saveNewMessage(chatContentItem);
        newMsgItem.postValue(chatData);
    }

    Observable<File> getImageMessage(final WeChatAuthenticationBean weChatAuthenticationBean, final String msgId, final String type) {
        return Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(final ObservableEmitter<File> emitter) {
                String url = weChatAuthenticationBean.getBaseUrl() + "/webwxgetmsgimg?msgid=" + msgId + "&skey="
                        + weChatAuthenticationBean.getSkey() + "&type=" + type;
                mWeChatRestService.downloadFile(url)
                        .subscribe(new Observer<ResponseBody>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(ResponseBody responseBody) {
                                File folder = new File(WECHAT_MEDIA_STORAGE_PATH);
                                if (!folder.exists()) {
                                    folder.mkdir();
                                }
                                File file = new File(WECHAT_MEDIA_STORAGE_PATH, "image_" + msgId);
                                emitter.onNext(inputStream2File(responseBody.byteStream(), file));
                                ;
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });
    }

    Observable<File> getVoiceMessage(final WeChatAuthenticationBean weChatAuthenticationBean, final String msgId) {
        return Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(final ObservableEmitter<File> emitter) {
                String url = weChatAuthenticationBean.getBaseUrl() + "/webwxgetvoice?msgid=" + msgId + "&skey="
                        + weChatAuthenticationBean.getSkey();
                mWeChatRestService.downloadFile(url)
                        .subscribe(new Observer<ResponseBody>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(ResponseBody responseBody) {
                                File folder = new File(WECHAT_MEDIA_STORAGE_PATH);
                                if (!folder.exists()) {
                                    folder.mkdir();
                                }
                                File file = new File(WECHAT_MEDIA_STORAGE_PATH, "voice_" + msgId);
                                emitter.onNext(inputStream2File(responseBody.byteStream(), file));
                                ;
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });
    }

    Observable<File> getLocationCover(final WeChatAuthenticationBean weChatAuthenticationBean, final String msgId) {
        return Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(final ObservableEmitter<File> emitter) {
                String url = weChatAuthenticationBean.getBaseUrl() + "/webwxgetpubliclinkimg?url=xxx&msgid=" + msgId + "&pictype=location";
                mWeChatRestService.downloadFile(url)
                        .subscribe(new Observer<ResponseBody>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(ResponseBody responseBody) {
                                File folder = new File(WECHAT_MEDIA_STORAGE_PATH);
                                if (!folder.exists()) {
                                    folder.mkdir();
                                }
                                File file = new File(WECHAT_MEDIA_STORAGE_PATH, "location_" + msgId);
                                emitter.onNext(inputStream2File(responseBody.byteStream(), file));
                                ;
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });
    }

    Observable<File> getVideoMessage(final WeChatAuthenticationBean weChatAuthenticationBean, final String msgId) {
        return Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(final ObservableEmitter<File> emitter) {
                String url = weChatAuthenticationBean.getBaseUrl() + "/webwxgetvideo?msgid=" + msgId + "&skey="
                        + weChatAuthenticationBean.getSkey();
                mWeChatRestService.downloadFile(url, "bytes=0-")
                        .subscribe(new Observer<ResponseBody>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(ResponseBody responseBody) {
                                File folder = new File(WECHAT_MEDIA_STORAGE_PATH);
                                if (!folder.exists()) {
                                    folder.mkdir();
                                }
                                File file = new File(WECHAT_MEDIA_STORAGE_PATH, "video_" + msgId);
                                emitter.onNext(inputStream2File(responseBody.byteStream(), file));
                                ;
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });
    }

    private static File inputStream2File(InputStream ins, File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (IOException e) {
            return null;
        }
        return file;
    }

    @NonNull
    String getLoginUserName() {
        String loginUserName = null;
        if (mWeChatLoginUser != null && mWeChatLoginUser.getWechatUserBean() != null) {
            loginUserName = mWeChatLoginUser.getWechatUserBean().getUserName();
        }
        return Strings.nullToEmpty(loginUserName);
    }

    public interface WeChatMessageLoopCallback {
        void onSuccess(WeChatMessageBean vo);

        void onPollEmpty();

        void onAuthFailed(String retCode, String errorMessage);
    }

    class WeChatMsgReceiver extends BroadcastReceiver {
        public void registerWeChatMsgReceiver(Context context) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_SERVICE_STARTED);
            intentFilter.addAction(ACTION_SERVICE_STOPPED);
            intentFilter.addAction(ACTION_NEW_MESSAGE);
            intentFilter.addAction(ACTION_AUTH_FAILED);
            LocalBroadcastManager.getInstance(context).registerReceiver(this, intentFilter);
        }

        public void unRegisterWeChatMsgReceiver(Context context) {
            Logger.t(TAG).d("unRegisterWeChatMsgReceiver");
            LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;
            switch (action) {
                case ACTION_AUTH_FAILED: {
                    break;
                }
                case ACTION_NEW_MESSAGE: {
                    WeChatMessageBean vo = (WeChatMessageBean) intent.getSerializableExtra(MESSAGE_VO);
                    parseMessages(vo);
                    break;
                }
                case ACTION_SERVICE_STARTED: {
                    break;
                }
                case ACTION_SERVICE_STOPPED: {
                    break;
                }
            }
        }
    }

    private static Bitmap base64StringToBitmap(String string) {
        if (Strings.isNullOrEmpty(string)) return null;
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            Logger.t(TAG).e("base64StringToBitmap error", e);
        }
        return bitmap;
    }

    public MutableLiveData<ChatData> getNewMsgItem() {
        return newMsgItem;
    }

    public MutableLiveData<Integer> getLoginStatus() {
        return loginStatus;
    }

    public MutableLiveData<Bitmap> getAvatar() {
        return avatar;
    }

    @NonNull
    public ArrayList<Parcelable> getWeChatBeansIds(List<Bean> beans) {
        ArrayList<Parcelable> candidatesIds = new ArrayList<>();
        WeChatRepository weChatRepository = WeChatManager.getInstance().getWeChatRepository();
        for (Bean bean : beans) {
            ChatData chatData = new ChatData();
            if (bean instanceof WeChatUserBean) {
                chatData.setAvatarUrl(weChatRepository.getAvatarUrl(((WeChatUserBean) bean).getHeadImgUrl()));
                chatData.setChatName(((WeChatUserBean) bean).getUserName());
                candidatesIds.add(chatData);
            } else if (bean instanceof WeChatContactVo) {
                chatData.setAvatarUrl(weChatRepository.getAvatarUrl(((WeChatContactVo) bean).getHeadImgUrl()));
                chatData.setChatName(((WeChatContactVo) bean).getUserName());
                candidatesIds.add(chatData);
            }
        }
        return candidatesIds;
    }
}
